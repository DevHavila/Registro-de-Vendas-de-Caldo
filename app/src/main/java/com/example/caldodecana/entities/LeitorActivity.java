package com.example.caldodecana.entities;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caldodecana.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class LeitorActivity extends AppCompatActivity {

    TextView txtConteudo, txtTotalDia;
    Button btnExportar;
    String nomeArquivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leitor);

        txtConteudo = findViewById(R.id.txtConteudo);
        txtTotalDia = findViewById(R.id.txtTotalDia);
        btnExportar = findViewById(R.id.btnExportar);

        nomeArquivo = getIntent().getStringExtra("arquivo");

        lerArquivo(nomeArquivo);
        somarTotalDoDia(nomeArquivo);

        btnExportar.setOnClickListener(v -> exportarWhatsApp(nomeArquivo));
    }

    private void lerArquivo(String nomeArquivo) {
        try {
            FileInputStream fis = openFileInput(nomeArquivo);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            StringBuilder sb = new StringBuilder();
            String linha;

            while ((linha = reader.readLine()) != null) {
                sb.append(linha).append("\n");
            }

            txtConteudo.setText(sb.toString());
            fis.close();

        } catch (Exception e) {
            txtConteudo.setText("Erro: " + e);
        }
    }

    // SOMA TOTAL DO ARQUIVO
    private void somarTotalDoDia(String nomeArquivo) {
        double total = 0;

        try {
            FileInputStream fis = openFileInput(nomeArquivo);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String linha;

            while ((linha = reader.readLine()) != null) {
                // TOTAL ESTÁ DEPOIS DE "total="
                if (linha.contains("total=")) {
                    String[] partes = linha.split("total=");
                    double valor = Double.parseDouble(partes[1]);
                    total += valor;
                }
            }

            txtTotalDia.setText("Total do dia: R$ " + String.format("%.2f", total));
            fis.close();

        } catch (Exception e) {
            txtTotalDia.setText("Erro ao somar: " + e);
        }
    }

    // EXPORTAR VIA WHATSAPP
    private void exportarWhatsApp(String nomeArquivo) {
        try {
            File arquivo = new File(getFilesDir(), nomeArquivo);

            // tornar o arquivo exportável via FileProvider
            File cacheArquivo = new File(getExternalCacheDir(), nomeArquivo);
            copiarArquivo(arquivo, cacheArquivo);

            Uri uri = Uri.fromFile(cacheArquivo);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, "Relatório do dia: " + nomeArquivo);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setPackage("com.whatsapp");

            startActivity(intent);

        } catch (Exception e) {
            txtConteudo.setText("Erro ao exportar: " + e);
        }
    }

    private void copiarArquivo(File origem, File destino) throws Exception {
        FileWriter fw = new FileWriter(destino);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(origem)));

        String linha;
        while ((linha = br.readLine()) != null) {
            fw.write(linha + "\n");
        }

        br.close();
        fw.close();
    }
}