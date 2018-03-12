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
import android.text.TextUtils;
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

        /// headphone broadcast receiver configuration =============================================
        BroadcastReceiver headphoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FenceState state = FenceState.extract(intent);
                if (state.getCurrentState() == FenceState.TRUE) {
                    Log.d("Service test", "Headphone connected");
                    pushNotification("Headphone conectado","Service Awareness","Headphone conectado");
                }
                else
                    Log.d("Service test", "False");
            }
        };
        registerReceiver(headphoneReceiver, new IntentFilter(FenceFilters.HEADPHONE_FILTER));
        /// geofence broadcast receiver configuration ==============================================
        BroadcastReceiver geofenceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FenceState fenceState = FenceState.extract(intent);
                    switch (fenceState.getCurrentState()) {
                        case FenceState.TRUE:
                            Log.d("GeofenceReceiver", "Broadcast chegou aqui");
                            //pegar o nome do local que é a chave da fence
                            String nomeLocal = fenceState.getFenceKey();
                            Log.d("GeofenceReceiver", "Lugar escolhido:" + nomeLocal);
                            pushEmailNotification(nomeLocal);
                            break;
                        case FenceState.FALSE:
                            Log.d("Service test geofence", "FALSE");


                    }
            }
        };
        registerReceiver(geofenceReceiver,new IntentFilter(FenceFilters.GEOFENCE_FILTER));
        /// andfence broadcast receiver configuration ==============================================

        BroadcastReceiver andReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FenceState fenceState = FenceState.extract(intent);
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        pushNotification("Pronto para ouvir musicas?","AwareApp","Pronto para ouvir musicas?");
                        break;
                    case FenceState.FALSE:
                        Log.d("Service test andfence", "FALSE");


                }
            }
        };
        registerReceiver(andReceiver,new IntentFilter(FenceFilters.AND_FILTER));

        return START_STICKY;
    }

    private void pushNotification(String contentText, String contentTitle, String ticker){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder .setContentText(contentText)
                .setContentTitle(contentTitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(ticker)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        Notification not = builder.build();
        NotificationManagerCompat.from(this).notify(777,not);
    }

    /**
     * Empurra uma Notification que diz o local onde o usuario esta, e possui uma action para mandar um email.
     * @param placeChosen Lugar que o usuario esta
     */
    private void pushEmailNotification(String placeChosen){
        //intent para abrir o email
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:")); // only email apps should handle this
        i.putExtra(Intent.EXTRA_TEXT, "Estou em " + placeChosen);
        i.putExtra(Intent.EXTRA_SUBJECT, "Estou em " + placeChosen);
        PendingIntent pi = PendingIntent.getActivity(this, 777, i, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder .setContentText("Email Sender")
                .setContentTitle("Você chegou em " + placeChosen)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Você chegou em " + placeChosen)
                .addAction(R.mipmap.ic_launcher_round,"Enviar Email", pi)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        Notification not = builder.build();
        NotificationManagerCompat.from(this).notify(777,not);

    }


}
