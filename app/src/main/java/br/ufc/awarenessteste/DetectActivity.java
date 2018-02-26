package br.ufc.awarenessteste;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceClient;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;

public class DetectActivity extends AppCompatActivity {

    FenceClient client;
    Button btnActivateFences;
    TextView txvActivity;
    boolean fencesActivated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        //Iniciar cliente awareness
        client = Awareness.getFenceClient(this);

         //Inicializar ui
        btnActivateFences = findViewById(R.id.btn_activate_fences);
        txvActivity = findViewById(R.id.txv_activity);

        btnActivateFences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ativarFences();
            }
        });

    }

    private void ativarFences() {
        if(!fencesActivated){
            //registre as fences
        }
        else{
            Log.d("ACIVITY DETECTOR","Fence already registered");
        }
    }

}
