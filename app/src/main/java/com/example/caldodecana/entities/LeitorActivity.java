package com.example.caldodecana.entities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caldodecana.MainActivity;
import com.example.caldodecana.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LeitorActivity extends AppCompatActivity {
    private String dataSelecionada;
    TextView txtTotalDia, txtTotalVendas;
    Button btnExportar;
    String nomePasta;

    TextView txtResultado;
    private TextView txtLeitura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leitor);

        txtTotalVendas = findViewById(R.id.totalVendas);
        txtTotalDia = findViewById(R.id.txtTotalDia);
        btnExportar = findViewById(R.id.btnExportar);

        nomePasta = getIntent().getStringExtra("arquivo");

        dataSelecionada = nomePasta;


        btnExportar.setOnClickListener(v -> exportarWhatsApp(nomePasta));

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btn = findViewById(R.id.btnVotarHistorico);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoricoActivity.class);
            startActivity(intent);
        });

    }

    private void carregarComprovantesDoDia() {
        try {
            File pasta = new File(getFilesDir(), dataSelecionada);

            if (!pasta.exists() || !pasta.isDirectory()) {
                txtTotalDia.setText("Nenhuma venda encontrada.");
                return;
            }

            File[] arquivos = pasta.listFiles();

            if (arquivos == null || arquivos.length == 0) {
                txtTotalDia.setText("Nenhuma venda encontrada.");
                return;
            }

            StringBuilder conteudo = new StringBuilder();
            double totalDia = 0.0;

            for (File arq : arquivos) {
                conteudo.append("==== COMPROVANTE ====\n");

                BufferedReader br = new BufferedReader(new FileReader(arq));
                String linha;
                double totalVenda = 0.0;

                while ((linha = br.readLine()) != null) {
                    conteudo.append(linha).append("\n");

                    if (linha.contains("Total valor: R$")) {
                        try {
                            String v = linha.replace("Total valor: R$", "").trim();
                            totalVenda = Double.parseDouble(v);



                        } catch (Exception ignored) {}
                    }
                }

                totalDia += totalVenda;


                br.close();
                conteudo.append("\n-----------------------------\n\n");
            }

            conteudo.append("\nTOTAL DO DIA: R$ ").append(String.format("%.2f", totalDia));

            txtTotalDia.setText(conteudo.toString());

        } catch (Exception e) {
            txtTotalDia.setText("Erro ao ler vendas: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarComprovantesDoDia();
    }

    private void exportarWhatsApp(String dataSelecionada) {
        try {

            File pasta = new File(getFilesDir(), dataSelecionada);

            if (!pasta.exists() || !pasta.isDirectory()) {
                Toast.makeText(this, "Pasta não encontrada!", Toast.LENGTH_SHORT).show();
                return;
            }

            File[] arquivos = pasta.listFiles();

            if (arquivos == null || arquivos.length == 0) {
                Toast.makeText(this, "Nenhuma venda encontrada!", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder conteudo = new StringBuilder();

            double totalDia = 0.0;

            for (File arq : arquivos) {
                if (arq.isFile()) {

                    conteudo.append("==== COMPROVANTE ====\n");

                    BufferedReader br = new BufferedReader(new FileReader(arq));
                    String linha;

                    while ((linha = br.readLine()) != null) {
                        conteudo.append(linha).append("\n");

                        if (linha.contains("Valor:")) {
                            try {
                                String v = linha.replace("Valor:", "").trim();
                                totalDia += Double.parseDouble(v);
                            } catch (Exception ignored) {}
                        }
                    }

                    br.close();

                    conteudo.append("\n-----------------------------\n\n");
                }
            }

            conteudo.append("\nTOTAL DO DIA: R$ ").append(String.format("%.2f", totalDia));

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, conteudo.toString());
            sendIntent.setType("text/plain");

            startActivity(Intent.createChooser(sendIntent, "Enviar vendas"));

        } catch (Exception e) {
            txtResultado.setText("Erro: " + e.getMessage());
        }
    }

}
