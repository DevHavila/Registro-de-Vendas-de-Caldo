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
    TextView txtTotalDia, txtTotalVendas, txtTotalSomado;
    Button btnExportar;
    String nomePasta;
    TextView txtResultado;
    private TextView txtLeitura;
    Button btnApagarPasta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leitor);

        txtTotalSomado = findViewById(R.id.txtTotalSomado);
        txtTotalDia = findViewById(R.id.txtTotalDia);
        btnExportar = findViewById(R.id.btnExportar);

        btnApagarPasta = findViewById(R.id.btnApagarPasta);
        btnApagarPasta.setOnClickListener(v -> confirmarExclusao());

        nomePasta = getIntent().getStringExtra("arquivo");

        dataSelecionada = nomePasta;

        btnExportar.setOnClickListener(v -> exportarWhatsApp(nomePasta));

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btn = findViewById(R.id.btnVotarHistorico);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoricoActivity.class);
            startActivity(intent);
        });

    }
    private void confirmarExclusao() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Apagar pasta?")
                .setMessage("Tem certeza que deseja apagar a pasta " + dataSelecionada + "?")
                .setPositiveButton("Sim", (dialog, which) -> apagarPasta())
                .setNegativeButton("Não", null)
                .show();
    }

    private void apagarPasta() {
        try {
            File pasta = new File(getFilesDir(), dataSelecionada);

            if (!pasta.exists()) {
                Toast.makeText(this, "Pasta não encontrada!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean sucesso = deletarPastaRecursivamente(pasta);

            if (sucesso) {
                Toast.makeText(this, "Pasta apagada com sucesso!", Toast.LENGTH_LONG).show();
                finish(); // Volta para a tela anterior
            } else {
                Toast.makeText(this, "Erro ao apagar a pasta!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deletarPastaRecursivamente(File file) {
        if (file.isDirectory()) {
            File[] filhos = file.listFiles();
            if (filhos != null) {
                for (File f : filhos) {
                    if (!deletarPastaRecursivamente(f)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
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
                            String v = linha.replace("Total valor:", "")
                                    .replace("R$", "")
                                    .replace(" ", "")
                                    .replace(",", ".")
                                    .trim();

                            totalVenda = Double.parseDouble(v);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                totalDia += totalVenda;

                br.close();
                conteudo.append("\n-----------------------------\n\n");
            }

            conteudo.append("\nTOTAL DO DIA: R$ ").append(String.format("%.2f", totalDia));

            txtTotalDia.setText(conteudo.toString());
            txtTotalSomado.setText(String.format("%.2f", totalDia));

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

                        if (linha.contains("Total valor: R$")) {
                            try {
                                String v = linha.replace("Total valor: R$", "").trim().replace(",", ".");
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
