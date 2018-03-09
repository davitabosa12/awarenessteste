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
        setTitle("Email sender");

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
            setUpGeofence(lugarEscolhido.getName().toString());

            }
        });

        Button btnRemoveAllFences = findViewById(R.id.btn_remove_fences);
        btnRemoveAllFences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllLocationFences();
            }
        });

    }
    private void callPlaceAutocomplete(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent,PLACE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    //setup awareness callbacks
    public void setUpGeofence(String placeChosen){
        Log.d("setup geofence","Inicio da funcao");

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
            PendingIntent pi = PendingIntent.getBroadcast(this,92,new Intent(FenceFilters.GEOFENCE_FILTER),PendingIntent.FLAG_UPDATE_CURRENT);
            Awareness.getFenceClient(this).updateFences(new FenceUpdateRequest.Builder()
                    .addFence(placeChosen,geo,pi).build()) //a "key" da fence e' o lugar escolhido do usuario.
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void removeAllLocationFences(){
        PendingIntent pi = PendingIntent.getBroadcast(this,92,new Intent(FenceFilters.GEOFENCE_FILTER),PendingIntent.FLAG_UPDATE_CURRENT);
        Awareness.getFenceClient(this).updateFences(new FenceUpdateRequest.Builder().removeFence(pi).build())
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"All fences Removed.", Toast.LENGTH_SHORT).show();
                    Log.d("Remove Fences", "All fences removes successfully");
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error removing fences", Toast.LENGTH_SHORT).show();
                    Log.d("Remove Fences", "Error removing fences");
                    task.getException().printStackTrace();
                }
            }
        });
    }
}
