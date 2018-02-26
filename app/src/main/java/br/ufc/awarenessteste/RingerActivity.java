package br.ufc.awarenessteste;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceClient;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

public class RingerActivity extends AppCompatActivity {

    Time tempo = new Time(0, 0, 0);
    FenceClient client;
    ArrayList<String> registeredFences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringer);
        EditText edtHora = findViewById(R.id.edt_hora);
        Button btnAddFence = findViewById(R.id.btn_add_fence);
        Button btnRemoveFence = findViewById(R.id.btn_remove_all);
        client = Awareness.getFenceClient(this);


        //focus do edt
        edtHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int nowH = Calendar.getInstance().getTime().getHours();
                int nowM = Calendar.getInstance().getTime().getMinutes();
                    TimePickerDialog tpd = new TimePickerDialog(RingerActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override

                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            EditText edt = findViewById(R.id.edt_hora);
                            edt.setText(i + ":" + i1);
                            tempo = new Time(i, i1, 0);

                        }
                    }, nowH, nowM, true);
                    tpd.show();

            }
        });

        btnAddFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                long horaInicio = tempo.getTime();
                Spinner ringerMode = findViewById(R.id.spinner);
                String mode = ringerMode.getSelectedItem().toString();
                String uuid = UUID.randomUUID().toString();
                Log.d("Spinner",mode + "");
                Log.d("Tempo",tempo.getTime() + "");
                registerFence(uuid,ringerMode(mode),horaInicio,horaInicio + (3600 * 1000));

            }
        });
        btnRemoveFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //Pega o texto do spinner e retorna o numero apropriado para uso no AudioManager
    public int ringerMode(String text) {
        if (text.equals("Vibrar e Tocar")) {
            return AudioManager.RINGER_MODE_NORMAL;
        } else if (text.equals("Vibrar")) {
            return AudioManager.RINGER_MODE_VIBRATE;
        } else {
            return AudioManager.RINGER_MODE_SILENT;
        }
    }

    public void registerFence(final String uuid, int ringer, long horaInicio, long horaFim) {
        //Pegar a TimeZone do usuario
        TimeZone tz = Calendar.getInstance().getTimeZone();

        //intent
        Intent i = new Intent("fence_receiver_action");
        PendingIntent pi = PendingIntent.getBroadcast(this, 12, i, PendingIntent.FLAG_CANCEL_CURRENT);
        i.putExtra("ringer", ringer);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FenceState fenceState = FenceState.extract(intent);
                if (fenceState.getCurrentState() == FenceState.TRUE) {
                    int ringer = intent.getIntExtra("ringer", AudioManager.RINGER_MODE_NORMAL);
                    //qual o metodo?
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.setRingerMode(ringer);
                    Log.d("FENCE", "Fence executada com sucesso");

                } else if ((fenceState.getCurrentState() == FenceState.FALSE)) {
                    Log.d("FENCE", "Fence false");
                }
            };
        };
            registerReceiver(receiver, new IntentFilter("fence_receiver_action"));


        //registrar fence
        final AwarenessFence timeFence = TimeFence.inDailyInterval(null, horaInicio, horaFim);
        client.updateFences(new FenceUpdateRequest.Builder().addFence(uuid, timeFence, pi).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    registeredFences.add(uuid);
                    Log.d("OnComplete", "Registrado com Sucesso");

                }
                else{
                    Log.e("OnComplete","Failed");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.d("FAIL", "onFailure: " + e.getCause() + " " + e.getMessage());
            }
        });


    }
}