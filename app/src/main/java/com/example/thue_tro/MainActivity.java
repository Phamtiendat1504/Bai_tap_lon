package com.example.thue_tro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnBatDau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBatDau = findViewById(R.id.btn_bat_dau);
        if (btnBatDau != null) {
            btnBatDau.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, Dangnhap.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }
}