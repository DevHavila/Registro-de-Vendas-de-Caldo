package com.example.caldodecana;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caldodecana.entities.HistoricoActivity;
import com.example.caldodecana.entities.LeitorActivity;
import com.example.caldodecana.entities.Vendas;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText editCaldo12, editCaldo7, editLitro, editSoma;
    TextView txtResultado;

    Vendas caldo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editCaldo12 = findViewById(R.id.caldo12);
        editCaldo7 = findViewById(R.id.caldo7);
        editLitro = findViewById(R.id.litro);

        int caldo12 = safePaserInt(editCaldo12.getText().toString());
        int caldo7 = safePaserInt(editCaldo7.getText().toString());
        int litro = safePaserInt(editLitro.getText().toString());

        txtResultado = findViewById(R.id.txtResultado);

    }

        public void svBotton(View view){
             try {
                 int caldo12 = safePaserInt(editCaldo12.getText().toString()) * 12;
                 int caldo7 = safePaserInt(editCaldo7.getText().toString()) * 7;
                 int litro = safePaserInt(editLitro.getText().toString()) * 16;

                 caldo = new Vendas(caldo12,caldo7,litro);

                 txtResultado.setText(caldo.toString());

                 String registro = caldo.toString();

                 salvarVendaDiaria(registro);

             } catch (RuntimeException e){
                 txtResultado.setText("Unexpectded error:" + e);
             }

        }

    public int safePaserInt(String value){
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException e){
            return 0;
        }
    }

    private String getNomeArquivo(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date()) + "txt";
    }

    private void salvarVendaDiaria(String texto) {
        try {
            String nome = getNomeArquivo();
            FileOutputStream fos = openFileOutput(nome, MODE_PRIVATE);
            fos.write((texto + "\n").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirHistorico(View view) {
        Intent intent = new Intent(this, HistoricoActivity.class);
        startActivity(intent);
    }

}