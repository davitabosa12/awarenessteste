package br.ufc.awarenessteste;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceClient;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class DetectActivity extends AppCompatActivity {

    FenceClient fenceClient;
    Button btnActivateFences;
    TextView txvActivity;
    SnapshotClient snapshotClient;
    BroadcastReceiver receiver;
    boolean fencesActivated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        //Inicializar ui
        btnActivateFences = findViewById(R.id.btn_activate_fences);
        txvActivity = findViewById(R.id.txv_activity);

        //setar Receiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FenceState fenceState = FenceState.extract(intent);
                switch (fenceState.getCurrentState()){
                    case FenceState.TRUE:{
                        String activity = fenceState.getFenceKey();
                        txvActivity.setText(activity);
                        break;
                    }
                    case FenceState.FALSE:{
                        break;
                    }
                    case FenceState.UNKNOWN:{
                        break;
                    }
                }
            }
        };

        registerReceiver(receiver,new IntentFilter("whatever"));




        //Iniciar cliente awareness
        fenceClient = Awareness.getFenceClient(this);
        snapshotClient = Awareness.getSnapshotClient(this);
        snapshotClient.getDetectedActivity().addOnCompleteListener(new OnCompleteListener<DetectedActivityResponse>() {
            @Override
            public void onComplete(@NonNull Task<DetectedActivityResponse> task) {
                if(task.isSuccessful()){
                    //Pega, dentro do Task o DetectedActivityResponse, que dentro tem o resultado, que dentro tem a atividade mais provavel e transforma em string.
                    //seta o txv da atividade detectada pela API
                    txvActivity.setText(task.getResult().getActivityRecognitionResult().getMostProbableActivity().toString());
                }
            }
        });




        btnActivateFences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ativarFences();
            }
        });

    }

    private void ativarFences() {




        if(!fencesActivated){
            Intent i = new Intent("whatever");
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),777,i,PendingIntent.FLAG_CANCEL_CURRENT );
            //registre as fences
            AwarenessFence fence_still = DetectedActivityFence.during(DetectedActivityFence.STILL);
            AwarenessFence fence_walking = DetectedActivityFence.during(DetectedActivityFence.WALKING);
            AwarenessFence fence_on_foot = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
            AwarenessFence fence_in_vehicle = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
            AwarenessFence fence_running = DetectedActivityFence.during(DetectedActivityFence.RUNNING);


            fenceClient.updateFences(new FenceUpdateRequest.Builder()
                    .addFence("still",fence_still,pi)
                    .addFence("walking",fence_walking,pi)
                    .addFence("on_foot",fence_on_foot,pi)
                    .addFence("in_vehicle",fence_in_vehicle,pi)
                    .addFence("running",fence_running,pi)
                    .build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                fencesActivated = true;
                                Toast.makeText(getApplicationContext(),"Fences registradas",Toast.LENGTH_LONG).show();
                                Log.d("Fences","Fences registradas");
                            }
                        }
                    });



        }
        else{
            Log.d("ACIVITY DETECTOR","Fence already registered");
        }
    }

}
