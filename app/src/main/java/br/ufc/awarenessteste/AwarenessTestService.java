package br.ufc.awarenessteste;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.awareness.fence.FenceState;

public class AwarenessTestService extends Service {
    public AwarenessTestService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Test service", "on start command");
        final String FENCE_RECEIVER_ACTION = "fence_receiver_action";

        Intent i = new Intent(FENCE_RECEIVER_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FenceState state = FenceState.extract(intent);
                if(state.getCurrentState() == FenceState.TRUE)
                    pushNotification();
                else
                    Log.d("Service test", "False");
            }
        };
        registerReceiver(receiver,new IntentFilter(FENCE_RECEIVER_ACTION));
        return START_STICKY;
    }

    private void pushNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder .setContentText("Teste de notificacao")
                .setContentTitle("Service Awareness")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Teste de Serviço")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        Notification not = builder.build();
        NotificationManagerCompat.from(this).notify(777,not);
    }
}
