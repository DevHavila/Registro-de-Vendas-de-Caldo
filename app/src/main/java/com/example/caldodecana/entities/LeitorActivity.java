package com.example.caldodecana.entities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caldodecana.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class LeitorActivity extends AppCompatActivity {

    private String dataSelecionada;

    TextView txtTotalSomado;
    Button btnExportar, btnApagarPasta;

    private ListView listaComprovantes;
    private ArrayList<File> listaArquivos = new ArrayList<>();
    private ArrayList<String> listaConteudos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leitor);

        txtTotalSomado = findViewById(R.id.txtTotalSomado);
        btnExportar = findViewById(R.id.btnExportar);
        btnApagarPasta = findViewById(R.id.btnApagarPasta);
        listaComprovantes = findViewById(R.id.listaComprovantes);

        Button btnVoltar = findViewById(R.id.btnVotarHistorico);
        btnVoltar.setOnClickListener(v -> finish());

        dataSelecionada = getIntent().getStringExtra("arquivo");

        btnExportar.setOnClickListener(v -> exportarWhatsApp());
        btnApagarPasta.setOnClickListener(v -> confirmarExclusaoPasta());

        // 👆 Clique normal → mostrar comprovante
        listaComprovantes.setOnItemClickListener((parent, view, position, id) -> {
            File arquivo = listaArquivos.get(position);
            mostrarComprovante(arquivo);
        });

        // 👇 Clique longo → apagar diretamente
        listaComprovantes.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmarExclusaoArquivo(listaArquivos.get(position));
            return true;
        });
    }


    // ============================================================
    // MOSTRAR COMPROVANTE
    // ============================================================
    private void mostrarComprovante(File arquivo) {
        try {
            StringBuilder conteudo = new StringBuilder();

            BufferedReader br = new BufferedReader(new FileReader(arquivo));
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
            br.close();

            new AlertDialog.Builder(this)
                    .setTitle("Comprovante")
                    .setMessage(conteudo.toString())
                    .setPositiveButton("Apagar", (d, w) -> apagarArquivo(arquivo))
                    .setNegativeButton("Fechar", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarExclusaoArquivo(File arquivo) {
        new AlertDialog.Builder(this)
                .setTitle("Apagar comprovante?")
                .setMessage("Deseja apagar este comprovante?")
                .setPositiveButton("Sim", (d, w) -> apagarArquivo(arquivo))
                .setNegativeButton("Não", null)
                .show();
    }

    private void apagarArquivo(File arquivo) {
        if (arquivo.delete()) {
            Toast.makeText(this, "Comprovante apagado!", Toast.LENGTH_SHORT).show();
            carregarComprovantesDoDia();
        } else {
            Toast.makeText(this, "Erro ao apagar arquivo!", Toast.LENGTH_SHORT).show();
        }
    }


    // ============================================================
    // CARREGAR COMPROVANTES DO DIA
    // ============================================================
    private void carregarComprovantesDoDia() {
        try {
            listaArquivos.clear();
            listaConteudos.clear();

            File pasta = new File(getFilesDir(), dataSelecionada);
            if (!pasta.exists()) return;

            File[] arquivos = pasta.listFiles();

            double soma = 0.0;

            if (arquivos != null) {
                for (File f : arquivos) {
                    if (f.isFile()) {

                        // Salva arquivo
                        listaArquivos.add(f);

                        // Lê conteúdo para mostrar na lista
                        StringBuilder texto = new StringBuilder();
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        String linha;

                        while ((linha = br.readLine()) != null) {
                            texto.append(linha).append("\n");

                            if (linha.contains("Total valor: R$")) {
                                String v = linha.replace("Total valor: R$", "")
                                        .replace(",", ".").trim();
                                soma += Double.parseDouble(v);
                            }
                        }
                        br.close();

                        listaConteudos.add(texto.toString());
                    }
                }
            }

            // Mostra conteúdo dos comprovantes na lista
            listaComprovantes.setAdapter(
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaConteudos)
            );

            txtTotalSomado.setText("R$ " + String.format("%.2f", soma));

        } catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        carregarComprovantesDoDia();
    }


    // ============================================================
    // EXPORTAR PARA WHATSAPP
    // ============================================================
    private void exportarWhatsApp() {
        try {
            File pasta = new File(getFilesDir(), dataSelecionada);
            File[] arquivos = pasta.listFiles();

            if (arquivos == null || arquivos.length == 0) {
                Toast.makeText(this, "Nenhuma venda encontrada!", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder conteudo = new StringBuilder();
            double totalDia = 0.0;

            for (File arq : arquivos) {
                conteudo.append("==== COMPROVANTE ====\n");
                BufferedReader br = new BufferedReader(new FileReader(arq));

                String linha;
                while ((linha = br.readLine()) != null) {
                    conteudo.append(linha).append("\n");

                    if (linha.contains("Total valor: R$")) {
                        String v = linha.replace("Total valor: R$", "")
                                .replace(",", ".").trim();
                        totalDia += Double.parseDouble(v);
                    }
                }
                br.close();
                conteudo.append("\n-----------------------------\n\n");
            }

            conteudo.append("\nTOTAL DO DIA: R$ ").append(String.format("%.2f", totalDia));

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, conteudo.toString());
            sendIntent.setType("text/plain");

            startActivity(Intent.createChooser(sendIntent, "Enviar vendas"));

        } catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // ============================================================
    // EXCLUIR PASTA INTEIRA
    // ============================================================
    private void confirmarExclusaoPasta() {
        new AlertDialog.Builder(this)
                .setTitle("Apagar todas as vendas?")
                .setMessage("Isso vai apagar TODOS os comprovantes do dia.")
                .setPositiveButton("Sim", (d, w) -> apagarPasta())
                .setNegativeButton("Não", null)
                .show();
    }

    private void apagarPasta() {
        File pasta = new File(getFilesDir(), dataSelecionada);

        boolean sucesso = deletarPastaRecursivamente(pasta);

        if (sucesso) {
            Toast.makeText(this, "Pasta apagada!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao apagar!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deletarPastaRecursivamente(File file) {
        if (file.isDirectory()) {
            File[] filhos = file.listFiles();
            if (filhos != null) {
                for (File f : filhos) {
                    if (!deletarPastaRecursivamente(f)) return false;
                }
            }
        }
        return file.delete();
    }
}
