package com.example.caldodecana.entities;

public class Vendas {

    private double soma;
    private double saldo;
    private double caldo12;
    private double caldo7;
    private double litro;

    public Vendas(int caldo12, int caldo7, int litro){
        this.caldo12 = caldo12;
        this.caldo7 = caldo7;
        this.litro = litro;

        calcularSoma();
        calcularSaldo();
    }

    public void calcularSoma(){
        this.soma = caldo12 + caldo7 + litro;
    }

    public  void calcularSaldo(){
        saldo += soma;
    }

    @Override
    public String toString(){
        return "Caldo de 12: R$" + String.format("%.2f", caldo12)
                + " \nCaldo de 7: R$" + String.format("%.2f", caldo7)
                + " \nLitro: R$" + String.format("%.2f", litro)
                + " \nTotal valor: R$" + String.format("%.2f", saldo);
    }

}
