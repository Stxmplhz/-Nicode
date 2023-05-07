package com.example.nicode.InsideCommunity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.R;
import com.example.nicode.User.ConversionListener;
import com.example.nicode.databinding.FragmentRecentchatBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentChatActivity extends AppCompatActivity implements ConversionListener {

    private PreferenceManager preferenceManager;
    private FragmentRecentchatBinding binding;
    private List<ChatMessage> conversations;
    private FirebaseFirestore Fdatabase;
    private RecentChatAdapter recentChatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentRecentchatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        init();
        listenRecentChat();

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.startrandomchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        conversations = new ArrayList<>();
        recentChatAdapter = new RecentChatAdapter(conversations, this);
        binding.recentchatRecycler.setAdapter(recentChatAdapter);
        Fdatabase = FirebaseFirestore.getInstance();
    }

    private void listenRecentChat(){
        Fdatabase.collection(Constants.KEY_COLLECTION_CONVERSATIONS).whereEqualTo(Constants.KEY_SENDERID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        Fdatabase.collection(Constants.KEY_COLLECTION_CONVERSATIONS).whereEqualTo(Constants.KEY_RECEIVERID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null){
            return;
        } if (value != null){
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderID = documentChange.getDocument().getString(Constants.KEY_SENDERID);
                    String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVERID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = senderID;
                    chatMessage.receiverID = receiverID;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderID)) {
                        chatMessage.conversation_image = documentChange.getDocument().getString(Constants.KEY_RECEIVERIMAGE);
                        chatMessage.conversation_name = documentChange.getDocument().getString(Constants.KEY_RECEIVERNAME);
                        chatMessage.conversationID = documentChange.getDocument().getString(Constants.KEY_RECEIVERID);
                    } else {
                        chatMessage.conversation_image = documentChange.getDocument().getString(Constants.KEY_SENDERIMAGE);
                        chatMessage.conversation_name = documentChange.getDocument().getString(Constants.KEY_SENDERNAME);
                        chatMessage.conversationID = documentChange.getDocument().getString(Constants.KEY_SENDERID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LASTMESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderID = documentChange.getDocument().getString(Constants.KEY_SENDERID);
                        String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVERID);
                        if (conversations.get(i).senderID.equals(senderID) && conversations.get(i).receiverID.equals(receiverID)){
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LASTMESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations,(obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            recentChatAdapter.notifyDataSetChanged();
            binding.recentchatRecycler.smoothScrollToPosition(0);
            binding.recentchatRecycler.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

}
