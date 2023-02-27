package com.example.notificationexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText text_id;
    Button btn_id;

    int NOTIFICATION_ID = 1;

    String inputString;

    private static final String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_id = findViewById(R.id.text_id);
        btn_id = findViewById(R.id.btn_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My_notification", "Notify", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        btn_id.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showNotification();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void showNotification() {

        handleIntent();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("Data", text_id.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(MainActivity.this, 1, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground,
                        getString(R.string.reply_label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        //Notification Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My_notification");
        builder.setSmallIcon(R.drawable.baseline_message_24);
        builder.setContentTitle("Notification");
        builder.setContentText(text_id.getText().toString());
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.addAction(action);


        //notification tap
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    private void handleIntent() {
        Intent intent = this.getIntent();

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if (remoteInput != null) {


            inputString = remoteInput.getCharSequence(
                    KEY_TEXT_REPLY).toString();


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification repliedNotification = new Notification.Builder(this, "My_notification")
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info)
                        .setContentText("Reply received")
                        .build();


                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.notify(NOTIFICATION_ID,
                        repliedNotification);
            }
        }
    }


}