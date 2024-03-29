package com.example.chatingapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatingapp.databinding.ItemContienerReceivedMessageBinding;
import com.example.chatingapp.databinding.ItemContinerSendMessageBinding;
import com.example.chatingapp.models.ChatMessage;

import java.util.List;

public class ChatAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    final List<ChatMessage> chatMessageList;
    private  Bitmap receiverProfileImage;
    private final String senderId ;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public void setReceiverProfileImage(Bitmap bitmap){
        receiverProfileImage =bitmap;
    }

    public ChatAdapter(List<ChatMessage> chatMessageList, Bitmap receiverProfileImage, String senderId) {
        this.chatMessageList = chatMessageList;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContinerSendMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
            );
        }else {
            return new ReceivedMessageViewHolder(
                    ItemContienerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessageList.get(position));
        }else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessageList.get(position),receiverProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageList.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }

    }


    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContinerSendMessageBinding binding ;
        SentMessageViewHolder(ItemContinerSendMessageBinding itemContinerSendMessageBinding){
            super(itemContinerSendMessageBinding.getRoot());
            binding = itemContinerSendMessageBinding ;
        }

        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContienerReceivedMessageBinding binding;
        ReceivedMessageViewHolder(ItemContienerReceivedMessageBinding itemContienerReceivedMessageBinding){
            super(itemContienerReceivedMessageBinding.getRoot());
            binding = itemContienerReceivedMessageBinding;
        }
        void setData(ChatMessage chatMessage,Bitmap receiverProfileImage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            if (receiverProfileImage !=null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
        }

    }

    }
