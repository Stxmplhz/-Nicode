package com.example.nicode.InsideCommunity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nicode.User.ConversionListener;
import com.example.nicode.databinding.ItemContainerRecentchatBinding;

import java.util.List;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.ConversationViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentChatAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(ItemContainerRecentchatBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
         holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentchatBinding binding;

        ConversationViewHolder(ItemContainerRecentchatBinding itemContainerRecentchatBinding){
            super(itemContainerRecentchatBinding.getRoot());
            binding = itemContainerRecentchatBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.imageProfile.setImageBitmap(getConversationImage(chatMessage.conversation_image));
            binding.textName.setText(chatMessage.conversation_name);
            binding.textRecenttext.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v ->{
                User user = new User();
                user.id = chatMessage.conversationID;
                user.username = chatMessage.conversation_name;
                user.image = chatMessage.conversation_image;
                conversionListener.onConversionClicked(user);
            });
        }
    }
    private Bitmap getConversationImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
