package br.ufc.awarenessteste;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class TestActivity extends AppCompatActivity {

    Button btnService, btnNotification;
    final String FENCE_RECEIVER_ACTION = "fence_receiver_action";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //iniciar servico do AwarenessTestService
        startService(new Intent(this, AwarenessTestService.class));
        btnService = findViewById(R.id.btn_start_service);
        btnNotification = findViewById(R.id.btn_sample_notification);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushNotification();
            }
        });

        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = getApplicationContext();
                //configurar o pending intent que chama o broadcastreceiver do servico

                Intent i = new Intent(FENCE_RECEIVER_ACTION);
                PendingIntent pi = PendingIntent.getBroadcast(ctx,123,i,PendingIntent.FLAG_CANCEL_CURRENT);
                //setar fence de headphone
                AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

                Awareness.getFenceClient(ctx).updateFences(new FenceUpdateRequest.Builder()
                        .addFence("headphoneFenceKey",headphoneFence, pi).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(getApplicationContext(),"Headphone", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(),"Erro", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void pushNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:")); // only email apps should handle this
        i.putExtra(Intent.EXTRA_TEXT, "Estou em ");
        i.putExtra(Intent.EXTRA_SUBJECT, "Estou em ");
        PendingIntent pi = PendingIntent.getActivity(this, 777, i, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(pi)
                .setContentText("Teste de notificacao")
                .setContentTitle("AwarenessTeste")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Teste de Pending intent")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        Notification not = builder.build();
        NotificationManagerCompat.from(this).notify(777,not);
    }
}
