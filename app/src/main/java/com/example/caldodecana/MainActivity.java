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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText editCaldo12, editCaldo7, editLitro, edtProduto, edtQuantidade, edtValor;
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
                 int caldo12 = safePaserInt(editCaldo12.getText().toString());
                 int caldo7 = safePaserInt(editCaldo7.getText().toString());
                 int litro = safePaserInt(editLitro.getText().toString());

                 caldo = new Vendas(caldo12,caldo7,litro);
                 caldo.contador(caldo12,caldo7,litro);

                 txtResultado.setText(caldo.toString());

                 String registro = caldo.toString();

                 salvarVendaDiaria(registro);

                 editCaldo12.setText("");
                 editCaldo7.setText("");
                 editLitro.setText("");

                 editCaldo12.requestFocus();

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

    private void salvarVendaDiaria(String texto){
        try{
            File pastaDia = getPastaDoDia();

            String nomeArquivo = getProximoNomeArquivo(pastaDia);

            File arquivo = new File(pastaDia, nomeArquivo);

            FileOutputStream fos = new FileOutputStream(arquivo, false);
            fos.write((texto + "\n").getBytes());
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void abrirHistorico(View view) {
        Intent intent = new Intent(this, HistoricoActivity.class);
        startActivity(intent);
    }

    private String getProximoNomeArquivo(File pastaDia) {
        File[] arquivos = pastaDia.listFiles();

        int maiorNumero = 0;

        if (arquivos != null) {
            for (File arq : arquivos) {
                String nome = arq.getName();

                if (nome.startsWith("venda_") && nome.endsWith(".txt")) {
                    try {
                        int num = Integer.parseInt(nome.replace("venda_", "").replace(".txt", ""));
                        if (num > maiorNumero) maiorNumero = num;
                    } catch (Exception ignored) {}
                }
            }
        }

        return "venda_" + (maiorNumero + 1) + ".txt";
    }

    private File getPastaDoDia() {
        String data = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        File pastaDia = new File(getFilesDir(), data);

        if (!pastaDia.exists()) {
            pastaDia.mkdirs(); // cria a pasta se não existir
        }

        return pastaDia;
    }

}