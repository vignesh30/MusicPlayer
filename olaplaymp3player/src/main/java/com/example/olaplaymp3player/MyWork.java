package com.example.olaplaymp3player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MyWork extends AppCompatActivity {
    private Button portfolio1, portfolio2, portfolio3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_work);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        portfolio1 = (Button) findViewById(R.id.portfolio1_install);
        portfolio2 = (Button) findViewById(R.id.portfolio2_install);
        portfolio3 = (Button) findViewById(R.id.portfolio3_install);

        portfolio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tamil.memes")));
            }
        });
        portfolio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.isunapps.routeking" )));
            }
        });
        portfolio3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.appatite.ofo" )));
            }
        });
    }

}
