package com.example.caldodecana.entities;

import android.app.AppComponentFactory;
import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caldodecana.R;

import java.io.File;

public class HistoricoActivity extends AppCompatActivity {


    protected void onCreat(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico);




    }




}