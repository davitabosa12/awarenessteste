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

        Button btnHeadphone, btnGeo, btnGeo2;

        btnHeadphone = findViewById(R.id.btn_headphone);

        btnGeo = findViewById(R.id.btn_geo);

        btnGeo2 = findViewById(R.id.btn_service);


        btnGeo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RingerActivity.class );
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
