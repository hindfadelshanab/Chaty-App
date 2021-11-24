package com.example.chatingapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chatingapp.R;
import com.example.chatingapp.activites.ChatActivity;
import com.example.chatingapp.models.User;
import com.example.chatingapp.utilites.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "chat_channel";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FMC", "Token : " + token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.d("FMC", "Message : " + remoteMessage.getNotification().getBody());
        createNotificationChannel();
        User user = new User();
        user.id = remoteMessage.getData().get(Constants.KEY_USER_ID);
        user.name = remoteMessage.getData().get(Constants.KEY_NAME);
        user.token = remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

//        int notificationId = new Random().nextInt();
//        // String channelId = "chat_channel";
//
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.putExtra(Constants.KEY_USER, user);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
//        builder.setSmallIcon(R.drawable.ic_round_notifications_24);
//        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
//        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get(Constants.KEY_MESSAGE)));
//        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        builder.setContentIntent(pendingIntent);
//        builder.setAutoCancel(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence channelName = "Chat Channel";
//            String channelDescription = "This notifications channel is used for chat mesage";
//            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription(channelDescription);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(notificationId, builder.build());


        Intent intent2 = new Intent(getApplicationContext(), ChatActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent2.putExtra(Constants.KEY_USER, user);

        PendingIntent pendingIntentt = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);

        int notificationId = new Random().nextInt();

        NotificationCompat.Builder builderr = new NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_round_notifications_24);
        builderr.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
        builderr.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get(Constants.KEY_MESSAGE)));
        builderr.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builderr.setContentIntent(pendingIntentt);
        builderr.setAutoCancel(true);
        builderr.setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builderr.build());


        }

private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        CharSequence name="Channel Notification";
        String description="description";
        int importance=NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,name,importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager=getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        }
        }


        }
