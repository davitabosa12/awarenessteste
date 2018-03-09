package br.ufc.awarenessteste;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;

public class HeadphoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headphone);
        setTitle("Headphone Fence");

        Intent i = new Intent(FenceFilters.HEADPHONE_FILTER);
        TextView texto = findViewById(R.id.txv_headphone);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        MyFenceReceiver receiver = new MyFenceReceiver(texto);
        registerReceiver(receiver,new IntentFilter(FenceFilters.HEADPHONE_FILTER));



        AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

        Awareness.getFenceClient(this).updateFences(new FenceUpdateRequest.Builder()
                .addFence("headphoneFenceKey",headphoneFence, pi).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Headphone", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
// Handle the callback on the Intent.
class MyFenceReceiver extends BroadcastReceiver {
    TextView tex;
    public MyFenceReceiver(){

    }
    public MyFenceReceiver(TextView t){
        tex = t;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFenceKey")) {
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(TAG, "Headphones are plugged in.");
                    tex.setText("Headphones plugados");

                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Headphones are NOT plugged in.");
                    tex.setText("Plugue seu headphone");
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "The headphone fence is in an unknown state.");
                    tex.setText("Estado desconhecido");
                    break;
            }
        }
    }
}

