package com.example.nicode.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nicode.Activity.Constants;
import com.example.nicode.InsideCommunity.ChatActivity;
import com.example.nicode.InsideCommunity.User;
import com.example.nicode.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessageService extends FirebaseMessagingService{

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        User user = new User();
        user.id = remoteMessage.getData().get(Constants.KEY_USER_ID);
        user.username = remoteMessage.getData().get(Constants.KEY_USERNAME);
        user.token = remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

        int notificationID = new Random().nextInt();
        String ChannelID = "chatmessage";

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER, user);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,ChannelID);

        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle(user.username);
        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get(Constants.KEY_MESSAGE)));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence ChannelName = "Chat Message";
            String Channeldescription = "This notification channel used for chat message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(ChannelID,ChannelName,importance);
            channel.setDescription(Channeldescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationID,builder.build());
    }
}
