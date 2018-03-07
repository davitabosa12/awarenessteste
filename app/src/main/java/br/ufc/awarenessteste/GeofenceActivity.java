package br.ufc.awarenessteste;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.internal.PlaceEntity;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;

public class GeofenceActivity extends AppCompatActivity {

    EditText edtPlace;
    Place lugarEscolhido;
    private static final int PLACE_REQUEST_CODE = 797;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PLACE_REQUEST_CODE){
            switch(resultCode){
                case RESULT_OK : {
                    lugarEscolhido = PlaceAutocomplete.getPlace(this,data);
                    edtPlace.setText(lugarEscolhido.getName());
                    Log.d("onActivityResult", "lugar escolhido :" + lugarEscolhido.getName());
                    Log.d("onActivityResult", "endereco :" + lugarEscolhido.getAddress());
                    break;
                }
                case PlaceAutocomplete.RESULT_ERROR : {
                    break;
                }
                case RESULT_CANCELED : {
                    break;
                }

            }
        }

        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        setTitle("Geofence");

        final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 21;

        edtPlace = findViewById(R.id.edt_place);


        edtPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPlaceAutocomplete();

            }
        });




        Button start = findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            setUpGeofence();

            }
        });

    }
    private void callPlaceAutocomplete(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent,PLACE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Solucionar o erro.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Solucionar o erro.
        }
    }

    //setup awareness callbacks
    public void setUpGeofence(){
        Log.d("setup geofence","Inicio da funcao");
        final String FENCE_RECEIVER_ACTION = "fence_receiver_action";
        Intent i = new Intent(FENCE_RECEIVER_ACTION);
        i.putExtra("place",lugarEscolhido.getName());
        Log.d("setup geofence","Lugar escolhido:" + lugarEscolhido.getName());
        PendingIntent pi = PendingIntent.getBroadcast(this, 10, i, 0);
        GeofenceReceiver receiver = new GeofenceReceiver(this);
        registerReceiver(receiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        //Awareness
        AwarenessFence geo;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }else {
            LatLng latLng = lugarEscolhido.getLatLng();
            geo = LocationFence.entering(latLng.latitude,latLng.longitude,500);
            Awareness.getFenceClient(this).updateFences(new FenceUpdateRequest.Builder()
                    .addFence("place",geo,pi).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        Log.d("setup geofence", "Fence registrada com sucesso");
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                        Log.d("setup geofence", "Registro falhou.");
                    }
                }
            });
        }


    }
}
class GeofenceReceiver extends BroadcastReceiver{

    Context ctx;

    public GeofenceReceiver(Context context){
        ctx = context;
    }

        @Override
        public void onReceive(Context context, Intent intent) {

        Intent outra  = intent;
        Bundle extrar = outra.getExtras();
        //tratar resposta do awareness
            FenceState fenceState = FenceState.extract(intent);
            if(TextUtils.equals("place", fenceState.getFenceKey())) {

                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d("GeofenceReceiver", "Broadcast chegou aqui");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        //pegar o nome do local
                        String nomeLocal = intent.getStringExtra("place");
                        Log.d("GeofenceReceiver", "Lugar escolhido:" + nomeLocal);

                        //intent para abrir o email
                        Intent i = new Intent(Intent.ACTION_SENDTO);
                        i.setData(Uri.parse("mailto:")); // only email apps should handle this
                        i.putExtra(Intent.EXTRA_TEXT, "Estou em " + nomeLocal);
                        i.putExtra(Intent.EXTRA_SUBJECT, "Estou em " + nomeLocal);
                        PendingIntent pi = PendingIntent.getActivity(context, 777, i, PendingIntent.FLAG_CANCEL_CURRENT);

                        //construtor de notificacao
                        builder.setContentIntent(pi)
                                .setContentText("Voce esta em " + nomeLocal)
                                .setContentTitle("AwarenessTeste")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setTicker("Novo local")
                                .setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL);
                        Notification not = builder.build();

                        //notificar
                        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
                        nm.notify(777, not);

                        break;
                    case FenceState.FALSE:
                        break;
                    case FenceState.UNKNOWN: {
                        break;
                    }
                }
            }

    }

}
