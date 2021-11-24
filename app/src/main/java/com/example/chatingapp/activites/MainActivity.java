package com.example.chatingapp.activites;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.chatingapp.R;
import com.example.chatingapp.adapters.RecentConversationAdapter;
import com.example.chatingapp.databinding.ActivityMainBinding;
import com.example.chatingapp.listeners.ConversationListener;
import com.example.chatingapp.models.ChatMessage;
import com.example.chatingapp.models.User;
import com.example.chatingapp.utilites.Constants;
import com.example.chatingapp.utilites.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversationListener {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    private List<ChatMessage> conversions;
    private RecentConversationAdapter recentConversationAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListener();
        listenConversion();

    }

    private void setListener() {
        binding.imageSignOut.setOnClickListener(view -> signOut());
        binding.fabNewChat.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), UserActivity.class)));
    }

    private void init() {
        conversions = new ArrayList<>();
        recentConversationAdapter = new RecentConversationAdapter(conversions,this);
        binding.conversionRecycleView.setAdapter(recentConversationAdapter);
        database = FirebaseFirestore.getInstance();

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void  listenConversion(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String recevierId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = recevierId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {

                        chatMessage.converstionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.converstionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.converstionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                    } else {
                        chatMessage.converstionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.converstionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.converstionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);

                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversions.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {

                    for (int i = 0; i < conversions.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String recevierId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversions.get(i).senderId.equals(senderId) && conversions.get(i).receiverId.equals(recevierId)){
                          conversions.get(i).message=documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                          conversions.get(i).dateObject= documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                          break;
                        }
                    }
                }

            }

            Collections.sort(conversions,(obj1,obj2)-> obj2.dateObject.compareTo(obj1.dateObject));
            recentConversationAdapter.notifyDataSetChanged();
            binding.conversionRecycleView.smoothScrollToPosition(0);
            binding.conversionRecycleView.setVisibility(View.VISIBLE);
            binding.avi.hide();



        }
    };

    public void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token).addOnSuccessListener(unsend -> showToast("Token update Successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));


    }

    public void signOut() {
        showToast("signing out ....");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unsend -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }).addOnFailureListener(e -> showToast("unable to sign out"));

    }

    @Override
    public void onConversationClicked(User user) {
        Intent i = new Intent(this,ChatActivity.class);
        i.putExtra(Constants.KEY_USER,user);
        startActivity(i);

    }
}