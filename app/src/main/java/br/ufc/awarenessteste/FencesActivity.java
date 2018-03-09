package br.ufc.awarenessteste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fences);
        setTitle("Fences");

        Button btnHeadphone, btnGeo, btnActivityDetection;

        btnHeadphone = findViewById(R.id.btn_headphone);

        btnGeo = findViewById(R.id.btn_geo);

        btnActivityDetection = findViewById(R.id.btn_service);


        btnActivityDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DetectActivity.class );
                startActivity(i);
            }
        });

        btnGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GeofenceActivity.class );
                startActivity(i);
            }
        });

        btnHeadphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HeadphoneActivity.class );
                startActivity(i);
            }
        });


    }
}
