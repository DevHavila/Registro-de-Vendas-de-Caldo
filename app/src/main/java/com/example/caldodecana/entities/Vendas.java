package com.example.caldodecana.entities;

public class Vendas {

    private double soma;
    private double saldo;
    private double caldo12;
    private double caldo7;
    private double litro;

    private int cont1;
    private int cont2;
    private int cont3;

    public Vendas(int caldo12, int caldo7, int litro){
        this.caldo12 = caldo12 * 12;
        this.caldo7 = caldo7 * 7;
        this.litro = litro * 16;

        calcularSoma();
        calcularSaldo();
    }

    public void calcularSoma(){
        this.soma = caldo12 + caldo7 + litro;
    }

    public  void calcularSaldo(){
        saldo += soma;
    }

    public void contador(int cont1,int cont2,int cont3){
        this.cont1 = cont1;
        this.cont2 = cont2;
        this.cont3 = cont3;
    }

    public double getSaldo() {
        return saldo;
    }

    @Override
    public String toString(){
        return "\nCaldo de R$ 12 Quantidade ("+cont1+") R$ " + String.format("%.2f", caldo12)
                + " \nCaldo de R$ 7 Quantidade ("+cont2+") R$ " + String.format("%.2f", caldo7)
                + " \nLitro de R$ 16 Quantidade ("+cont3 +") R$ "+ String.format("%.2f", litro) + "\n"
                + " \nTotal valor: R$ " + String.format("%.2f",getSaldo());
    }

}
