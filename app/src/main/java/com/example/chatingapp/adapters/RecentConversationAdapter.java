package com.example.chatingapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatingapp.databinding.ItemContainerRecentConversionBinding;
import com.example.chatingapp.listeners.ConversationListener;
import com.example.chatingapp.models.ChatMessage;
import com.example.chatingapp.models.User;

import java.util.List;

public class RecentConversationAdapter extends  RecyclerView.Adapter<RecentConversationAdapter.ConversationViewHolder> {
    private  final List<ChatMessage> chatMessageList;
    private ConversationListener conversationListener ;

    public RecentConversationAdapter(List<ChatMessage> chatMessageList , ConversationListener conversationListener) {
        this.chatMessageList = chatMessageList;
        this.conversationListener = conversationListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                ItemContainerRecentConversionBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(chatMessageList.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }


    class ConversationViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConversionBinding binding;

        ConversationViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;

        }


        void setData(ChatMessage chatMessage){
            binding.textName.setText(chatMessage.converstionName);
            binding.imageProfile.setImageBitmap(getConversationImage(chatMessage.converstionImage));
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(view -> {

                User user = new User();
                user.id = chatMessage.converstionId;
                user.image = chatMessage.converstionImage;
                user.name = chatMessage.converstionName;
                conversationListener.onConversationClicked(user);
            });

        }
    }

        private Bitmap getConversationImage(String encodedImage) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }


}
