package com.example.caldodecana.entities;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileManager {

    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    public String getTodayFileName() {
        String day = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        return "vendas_" + day + ".txt";
    }

    public void saveToDailyFile(String text) {
        try {
            String fileName = getTodayFileName();
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write((text + "\n").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readDailyFile(String fileName) {
        ArrayList<String> linhas = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                linhas.add(line);
            }

            reader.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return linhas;
    }

    public double sumDailyValues(String fileName) {
        double total = 0;

        try {
            ArrayList<String> linhas = readDailyFile(fileName);

            for (String linha : linhas) {
                if (linha.contains("TOTAL:")) {
                    String valor = linha.substring(linha.indexOf("TOTAL:") + 6).trim();
                    total += Double.parseDouble(valor);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public ArrayList<String> listAllDailyFiles() {
        ArrayList<String> lista = new ArrayList<>();

        File folder = context.getFilesDir();
        File[] files = folder.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(".txt")) {
                    lista.add(f.getName());
                }
            }
        }

        return lista;
    }



}
