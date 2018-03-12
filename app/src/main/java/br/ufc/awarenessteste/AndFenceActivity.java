package br.ufc.awarenessteste;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceClient;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AndFenceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_and_fence);
        FenceClient client = Awareness.getFenceClient(this);

        AwarenessFence headphone_fence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
        AwarenessFence walking_or_running_fence = AwarenessFence.or(DetectedActivityFence.starting(DetectedActivityFence.ON_FOOT),
                                                                    DetectedActivityFence.starting(DetectedActivityFence.WALKING),
                                                                    DetectedActivityFence.starting(DetectedActivityFence.RUNNING));
        AwarenessFence and_fence = AwarenessFence.and(headphone_fence,walking_or_running_fence);

        PendingIntent pi = PendingIntent.getBroadcast(this,787,new Intent(FenceFilters.AND_FILTER),PendingIntent.FLAG_CANCEL_CURRENT);

        client.updateFences(new FenceUpdateRequest.Builder()
                                .addFence("and_fence",and_fence,pi)
                                .build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Fence registrada",Toast.LENGTH_SHORT).show();
                            Log.d("AND FENCE","Fence registrada");
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Erro no registro",Toast.LENGTH_SHORT).show();
                            Log.e("AND FENCE",task.getException().getMessage());
                        }
                    }
                });
    }
}
