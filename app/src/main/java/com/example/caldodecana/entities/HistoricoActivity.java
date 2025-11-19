package com.example.caldodecana.entities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caldodecana.MainActivity;
import com.example.caldodecana.R;

import java.io.File;

public class HistoricoActivity extends AppCompatActivity {

    GridView gridArquivo;
   @SuppressLint("MissingInflatedId")
   protected void onCreat(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico);

        gridArquivo = findViewById(R.id.gridArquivo);

        File pasta = getFilesDir();
        File[] arquivos =  pasta.listFiles();

        String[] nomes =  new String[arquivos.length];
        for(int i = 0; i < arquivos.length; i++){
            nomes[i] = arquivos[i].getName();
        }

       ArrayAdapter <String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomes);

        gridArquivo.setAdapter(adapter);

        gridArquivo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String nomeArquivo = nomes[position];

               Intent intent = new Intent(HistoricoActivity.this, LeitorActivity.class);
               intent.putExtra("arquivo", nomeArquivo);
               startActivity(intent);
           }
       });
    }

    public void voltarHistorico(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}