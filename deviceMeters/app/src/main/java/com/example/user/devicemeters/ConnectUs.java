package com.example.user.devicemeters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ConnectUs extends AppCompatActivity {
private Button homebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_us);

        homebtn = (Button)findViewById(R.id.button);
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent homeIn = new Intent();
//                homeIn.setClass(ConnectUs.this,MainActivity.class);
//                startActivity(homeIn);
//                homeIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });
    }
}
