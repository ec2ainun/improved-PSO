package com.startup.ec2ainun.ipsotest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Random;

public class optimasi extends AppCompatActivity {
    Button optimasi;
    EditText nilaiberatSapi, nilaiproduksiSusu, nilaiberatPakan, nilaipopSize, nilaiiterasi;

    private ProgressDialog progressDialog;

    double beratSapi = 0,
            produksiSusu = 0,
            lemakSusu = 4.5;
    double kebutuhanTDN, kebutuhanNE, kebutuhanPK, kebutuhanBK;
    int beratPakan = 0, jenispakan = 10, partikelke;
    double rata2Fitness = 0, maksimal = 0;
    //BK, PK, TDN, harga
    double[][] kandunganGizi = new double[][]{
            {0.365, 0.128, 0.557, 1250},//rumput gajah
            {0.351, 0.088, 0.688, 1700},//tebon jagung
            {81.79, 16.89, 32.456, 3000},//bapro
            {0.8179, 0.1869, 0.32456, 4500},//pollard
            {20.66, 25.69, 73.625, 500},//ampas tahu
            {68.3, 15, 89.3, 250},//limbah roti
            {80.8, 3.9, 45.57, 500},//jerami padi
            {90.0, 13.9, 67.3, 250},//kulit kedelai
            {30, 32.12, 69.017, 2000},//daun lamtoro
            {90.6, 15.5, 84.8, 2500}//bekatul
    };

    double partikel[][];
    //    double partikel[][] = {
//            {8.34, 7.3, 2.21, 0},
//            {5.92, 3.43, 11.2, 0},
//            {4.34, 4.61, 11, 41, 0},
//            {2.62, 5.86, 11.64, 0},
//            {9.1, 1.14, 10.65, 0}};
    double partikelPBest[][], partikelGBest[] = new double[5];
    double velocity[][], vmax, vmin;
    int popSize = 5, iterMax = 100;
    double w1 = 1, w2 = 1;

    DecimalFormat belakangkoma = new DecimalFormat("#.########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimasi);

        nilaiberatPakan = (EditText)findViewById(R.id.nilaiBeratPakan);
        nilaiberatSapi = (EditText)findViewById(R.id.nilaiBeratSapi);
        nilaiproduksiSusu = (EditText)findViewById(R.id.nilaiProduksiSusu);
        nilaipopSize = (EditText) findViewById(R.id.nilaiPopsize);
        nilaiiterasi = (EditText)findViewById(R.id.nilaiIterasi);

        nilaiberatSapi.setText("450");
        nilaiberatPakan.setText("20");
        nilaiproduksiSusu.setText("15");
        progressDialog = new ProgressDialog(this);

        optimasi = (Button)findViewById(R.id.btnOptimasi);
        optimasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nilaiberatPakan.getText().toString()!=null
                        &&nilaiberatSapi.getText().toString()!=null
                        &&nilaiproduksiSusu.getText().toString()!=null
                        &&nilaipopSize.getText().toString()!=null
                        &&nilaiiterasi.getText().toString()!=null){
                    progressDialog.setMessage("Mengoptimalisasi Data...");
                    progressDialog.show();

                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            prosesOptimasi(Integer.parseInt(nilaiberatSapi.getText().toString()), Integer.parseInt(nilaiproduksiSusu.getText().toString()),
                                    Integer.parseInt(nilaiberatPakan.getText().toString()), Integer.parseInt(nilaipopSize.getText().toString()), Integer.parseInt(nilaiiterasi.getText().toString()));
                            progressDialog.dismiss();
                            Intent intent = new Intent(optimasi.this, hasil.class);

                            for (int j = 0; j <= jenispakan; j++) {
                                intent.putExtra("hasil"+j,belakangkoma.format(partikelGBest[j]));
                            }
                            startActivity(intent);
                        }
                    };
                    mThread.start();


                }else{
                    Toast.makeText(optimasi.this,"mohon isi semua nilai yang ada", Toast.LENGTH_LONG);
                }

            }
        });


    }

    void prosesOptimasi(int beratSapi, int produksiSusu, int beratPakan, int popSize, int iterasi){


        velocityClamping();
        this.beratSapi = beratSapi;
        this.produksiSusu = produksiSusu;
        this.beratPakan = beratPakan;
        this.popSize = popSize;
        this.iterMax = iterasi;
        partikel = new double[popSize][jenispakan + 1];
        partikelPBest = new double[popSize][jenispakan + 1];
        partikelGBest = new double[jenispakan + 1];
        velocity = new double[popSize][jenispakan + 1];
        proses();

    }

    void proses() {
        hitungKebutuhanGizi();
        inisialisasi();

        for (int iterasi = 0; iterasi < iterMax; iterasi++) {
            double totalf = 0;
            System.out.println("Iterasi : " + iterasi);
            updateVelocity(iterasi);
            updatePosisi();
            updateBest();
            for (int i = 0; i < popSize; i++) {
                totalf = totalf + partikelPBest[i][jenispakan];
            }
            rata2Fitness = totalf / popSize;
            //tampil();
        }
    }

    void inisialisasi() {
        Random r = new Random();
        for (int i = 0; i < popSize; i++) {
            for (int j = 0; j < jenispakan; j++) {
                partikel[i][j] = r.nextInt(beratPakan);
            }
            //nilai fitness disimpan pada indeks kolom ke jenis pakan
            partikel[i][jenispakan] = hitungFitness(partikel[i]);
        }
        updateBest();
    }

    void hitungKebutuhanGizi() {
        double NEhidup = 7.82;
        double TDNhidup = 3.44;
        double PKhidup = 0.403;
        double NEproduksi = produksiSusu * 0.78;
        double TDNproduksi = produksiSusu * 0.344;
        double PKproduksi = produksiSusu * 92;
        double NEtumbuh = 0.1 * NEhidup;
        double TDNtumbuh = 0;
        double PKtumbuh = 0;
        //kebutuhanNutrisi NE TDN PK BK
        kebutuhanNE = NEhidup + NEproduksi + NEtumbuh;
        kebutuhanTDN = TDNhidup + TDNproduksi + TDNtumbuh;
        kebutuhanPK = PKhidup + PKproduksi + PKtumbuh;
        kebutuhanBK = kebutuhanNE / 1.62;
    }

    double hitungFitness(double partikel[]) {
        double ketersediaanTDN = 0, ketersediaanNE = 0, ketersediaanPK = 0,ketersediaanBK = 0;
        //hitung total nutrisi
        //gizi : BK, PK, TDN, NEL
        ketersediaanNE = (0.0245 * kandunganGizi[0][2] / 100) - 0.12;

        for (int j = 0; j < (jenispakan-1); j++) {
            ketersediaanBK += partikel[j] * kandunganGizi[j][0];
            ketersediaanPK += partikel[j] * kandunganGizi[j][1];
            ketersediaanTDN += partikel[j] * kandunganGizi[j][2];
        }
        /*
         ketersediaanBK = partikel[0] * kandunganGizi[0][0] + partikel[1] * kandunganGizi[1][0] + partikel[2] * kandunganGizi[2][0];
         ketersediaanPK = partikel[0] * kandunganGizi[0][1] + partikel[1] * kandunganGizi[1][1] + partikel[2] * kandunganGizi[2][1];
         ketersediaanTDN = partikel[0] * kandunganGizi[0][2] + partikel[1] * kandunganGizi[1][2] + partikel[2] * kandunganGizi[2][2];
         */
        //hitung pinalti
        double pinalti = 0;
        pinalti = pinalti + Math.abs(kebutuhanNE - ketersediaanNE);
        pinalti = pinalti + Math.abs(kebutuhanPK - ketersediaanPK);
        pinalti = pinalti + Math.abs(kebutuhanTDN - ketersediaanTDN);
        pinalti = pinalti + Math.abs(kebutuhanBK - ketersediaanBK);

        double totalPinalti = pinalti * 5;
        //hitung total harga
        //harga disimpan di indeks ke 3 pada array "makanan"
        double totalHarga = 0;
        int j = 0;
        for (int x = 0; x < jenispakan; x++) {
            totalHarga = partikel[x] * kandunganGizi[x][3];
            //System.out.print(kromosom[x] + " * " + pakan[x][3] + ", ");
            //System.out.print(totalHarga + "\t");
        }
        //System.out.println("");
        double fitness = 100000 / (w1 * totalHarga + w2 * (totalPinalti * 100000));
        return fitness * 1000;
    }

    void updateVelocity(int iterasi) {
        double w, k = Math.cos(2*3.14 / iterMax * (iterasi - iterMax / 2)) + 2.428571;
        //hitung kecepatan

        for (int i = 0; i < popSize; i++) {
            for (int j = 0; j < jenispakan; j++) {
                //fitness
                if (partikelGBest[jenispakan] == partikel[i][jenispakan]) {
                    w = 0.857143;
                } else {
                    w = 0.857143 + (1 - 0.857143) * (1 - iterasi / iterMax);
                }
                if (iterasi < iterMax / 2) {
                    velocity[i][j] = w * velocity[i][j] + 2 * Math.random() * (partikelPBest[i][j] - partikel[i][j]) + 2 * Math.random() * (partikelGBest[j] - partikel[i][j]);
                } else {
                    velocity[i][j] = k * (0.7 * velocity[i][j] + 2 * Math.random() * (partikelPBest[i][j] - partikel[i][j]) + 2 * Math.random() * (partikelGBest[j] - partikel[i][j]));
                }

                if (velocity[i][j] <= vmin) {
                    velocity[i][j] = vmin;
                }
                if (velocity[i][j] >= vmax) {
                    velocity[i][j] = vmax;
                }
            }
        }
    }

    void updatePosisi() {
        for (int i = 0; i < popSize; i++) {
            for (int j = 0; j < jenispakan; j++) {
                partikel[i][j] = partikel[i][j] + velocity[i][j];
                if (partikel[i][j] < 0) {
                    partikel[i][j] = 0;
                }
                if (partikel[i][j] > beratPakan) {
                    partikel[i][j] = beratPakan;
                }
            }
            partikel[i][jenispakan] = hitungFitness(partikel[i]);
        }
        updateBest();
    }

    void updateBest() {
        for (int i = 0; i < popSize; i++) {
            if (partikel[i][jenispakan] > partikelPBest[i][jenispakan]) {
                partikelPBest[i] = partikel[i];
            }
        }

        //update GBest
        for (int i = 0; i < popSize; i++) {
            if (maksimal < partikelPBest[i][jenispakan]) {
                maksimal = partikelPBest[i][jenispakan];
                partikelGBest = partikelPBest[i];
                partikelke = i;
            }
        }
    }

    void velocityClamping() {
        double k = 0.6;
        vmax = k * (beratPakan - 0) / 2;
        vmin = -vmax;
    }

    void tampil() {

        System.out.println("fitur 1 \t fitur 2 \t fitur 3 \t fitur 4 \t fitur 5 \t  fitur 6 \t fitur 7 \t fitur 8 \t fitur 9 \t fitur 10 \t fitness");
        for (int i = 0; i < popSize; i++) {
            for (int j = 0; j <= jenispakan; j++) {
                System.out.print(belakangkoma.format(partikelPBest[i][j]) + " \t");
            }
            System.out.println("");
        }
        System.out.println("");

        System.out.println("Gbest");
        for (int j = 0; j <= jenispakan; j++) {
            System.out.print(belakangkoma.format(partikelGBest[j]) + " \t");
        }
        System.out.println("");
        System.out.println("partikel ke :" + (partikelke + 1));
        System.out.println("rata fitness : " + rata2Fitness);
        System.out.println("");
    }
}
