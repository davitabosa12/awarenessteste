package br.ufc.awarenessteste;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;

public class GeofenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        setTitle("Geofence");

        final EditText edtEndereco = findViewById(R.id.edt_email);
        final EditText edtAssunto = findViewById(R.id.edt_assunto);
        final EditText edtMsg = findViewById(R.id.edt_msg);

        String endereco,assunto,msg;

        Button start = findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setUpGeofence(edtEndereco.getText().toString(),
                        edtAssunto.getText().toString(),
                        edtMsg.getText().toString());
            }
        });

    }
    //setup awareness callbacks
    public void setUpGeofence(String endereco, String assunto, String msg){
        final String FENCE_RECEIVER_ACTION = "fence_receiver_action";
        Intent i = new Intent(FENCE_RECEIVER_ACTION);
        i.putExtra("endereco",endereco.toString());
        i.putExtra("assunto", assunto.toString());
        i.putExtra("msg", msg.toString());
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
            geo = LocationFence.in(-3.7463548, -38.5762805, 1005,1);
            Awareness.getFenceClient(this).updateFences(new FenceUpdateRequest.Builder()
                    .addFence("dentroPici",geo,pi).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(),"Fail",Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
}
class GeofenceReceiver extends BroadcastReceiver{

    Context ctx;
    String endereco, msg, assunto;
    public GeofenceReceiver(Context context){
        ctx = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);
        endereco = intent.getStringExtra("endereco");
        assunto = intent.getStringExtra("assunto");
        msg = intent.getStringExtra("msg");

        if (TextUtils.equals(fenceState.getFenceKey(), "dentroPici")) {
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(TAG, "Dentro da UFC");
                    if(endereco != null || !endereco.isEmpty() ||
                        assunto != null || !assunto.isEmpty() ||
                        msg != null || !msg.isEmpty()){
                    mandaEmail(endereco, assunto, msg);
                }
                else{
                        Log.e("ERRO:", "DEU RUIM");
                }

                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fora da UFC");

                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "The fence is in an unknown state.");

                    break;
            }
        }
    }

    public void mandaEmail(String endereco, String assunto, String msg){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, endereco);
        intent.putExtra(Intent.EXTRA_SUBJECT, assunto);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            Toast.makeText(ctx,"Email enviado",Toast.LENGTH_SHORT).show();
            ctx.startActivity(intent);
        }

    }
}
