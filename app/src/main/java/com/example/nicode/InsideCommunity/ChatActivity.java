package com.example.nicode.InsideCommunity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.nicode.Activity.BaseActivity;
import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.Api.ApiClient;
import com.example.nicode.Api.ApiService;
import com.example.nicode.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private User receiverUser;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore Fdatabase;
    private String conversationID = null;
    private Boolean isReceiverAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.sendbtn.setOnClickListener(v -> sendMessage());
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        loadReceiverDetails();
        chatlayout();
        listenMessages();
    }

    private void chatlayout(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages,getBitmapFromEncodedString(receiverUser.image),preferenceManager.getString(Constants.KEY_USER_ID));
        binding.chatrecyclerview.setAdapter(chatAdapter);
        Fdatabase = FirebaseFirestore.getInstance();
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null){
            byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }else {
            return null;
        }

    }


    private void sendMessage(){
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDERID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVERID,receiverUser.id);
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        Fdatabase.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversationID != null){
            updateConversion(binding.inputMessage.getText().toString());
        }else {
            HashMap<String ,Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDERID,preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDERNAME, preferenceManager.getString(Constants.KEY_USERNAME));
            conversion.put(Constants.KEY_SENDERIMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVERID, receiverUser.id);
            conversion.put(Constants.KEY_RECEIVERNAME, receiverUser.username);
            conversion.put(Constants.KEY_RECEIVERIMAGE, receiverUser.image);
            conversion.put(Constants.KEY_LASTMESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        if (!isReceiverAvailable){
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_USERNAME, preferenceManager.getString(Constants.KEY_USERNAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MESSAGE_DATA, data);
                body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens);

                sendNotifications(body.toString());
            } catch (Exception exception){
                showToast(exception.getMessage());
            }
        }
        binding.inputMessage.setText(null);
    }

    private void listenAvailabilityOfReceiver(){
        Fdatabase.collection(Constants.KEY_COLLECTION_USERS).document(receiverUser.id)
                .addSnapshotListener(ChatActivity.this,(value, error) -> {
                    if (error != null){
                        return;
                    }
                    if (value != null){
                        if (value.getLong(Constants.KEY_AVAILABILITY) != null){
                            int availability = Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABILITY)).intValue();
                            isReceiverAvailable = availability == 1;
                        } receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                        if (receiverUser.image == null){
                            receiverUser.image = value.getString(Constants.KEY_IMAGE);
                            chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.image));
                            chatAdapter.notifyItemRangeChanged(0,chatMessages.size());
                        }
                    }
                    //if (isReceiverAvailable){}
                });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotifications(String messagebody){
        ApiClient.getClient().create(ApiService.class).sendMessage(Constants.getRemotemessageheaders(), messagebody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                         if (response.isSuccessful()) {
                             try {
                                 if (response.body() != null){
                                     JSONObject responseJSON = new JSONObject(response.body());
                                     JSONArray results = responseJSON.getJSONArray("results");
                                     if (responseJSON.getInt("failure") == 1){
                                         JSONObject error = (JSONObject) results.get(0);
                                         showToast(error.getString("error"));
                                         return;
                                     }
                                 }
                             }catch (JSONException e){
                                 e.printStackTrace();
                             }
                         }else {
                         }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        showToast(t.getMessage());
                    }
                });
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.username);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if (value != null){
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = documentChange.getDocument().getString(Constants.KEY_SENDERID);
                    chatMessage.receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVERID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.datetime = getDatetime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, Comparator.comparing(obj -> obj.dateObject));
            if (count == 0){
                chatAdapter.notifyDataSetChanged();
            }else {
                chatAdapter.notifyItemRangeChanged(chatMessages.size(),chatMessages.size());
                binding.chatrecyclerview.smoothScrollToPosition(chatMessages.size() - 1);
            }binding.progressBar.setVisibility(View.VISIBLE);
        }binding.progressBar.setVisibility(View.GONE);
        if (conversationID == null) {
            checkForConversion();
        }
    };

    private String getDatetime(Date date){
        return new SimpleDateFormat("dd MMMM, - hh:mm", Locale.getDefault()).format(date);
    }

    private void listenMessages() {
        Fdatabase.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDERID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVERID, receiverUser.id)
                .addSnapshotListener(eventListener);
        Fdatabase.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDERID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVERID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private void addConversion(HashMap<String ,Object> conversion) {
        Fdatabase.collection(Constants.KEY_COLLECTION_CONVERSATIONS).add(conversion)
                .addOnSuccessListener(documentReference -> conversationID = documentReference.getId());
    }

    private void updateConversion(String message) {
        DocumentReference documentReference = Fdatabase.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationID);
        documentReference.update(Constants.KEY_LASTMESSAGE, message, Constants.KEY_TIMESTAMP, new Date());
    }

    private void checkForConversion(){
        if (chatMessages.size() != 0){
            checkForConversionRemotely(preferenceManager.getString(Constants.KEY_USER_ID),receiverUser.id);
            checkForConversionRemotely(receiverUser.id, preferenceManager.getString(Constants.KEY_USER_ID));
        }
    }

    private void checkForConversionRemotely(String senderID, String receiverID){
        Fdatabase.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDERID, senderID).whereEqualTo(Constants.KEY_RECEIVERID, receiverID)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationID = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}