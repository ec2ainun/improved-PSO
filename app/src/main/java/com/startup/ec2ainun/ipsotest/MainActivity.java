package com.startup.ec2ainun.ipsotest;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        String [] namaKandungan = {
                "rumput gajah", "tebon jagung", "bapro", "pollard", "ampas tahu",
                "limbah roti", "jerami padi", "kulit kedelai", "daun lamtoro","bekatul"
        };
        String [] infoLabel = {"Kandungan", "BK", "PK", "TDN", "Harga"};

        //buat table
        TableLayout table = new TableLayout(this);
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        TableRow rowTitle = new TableRow(this);
        rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        TableRow empty = new TableRow(this);
        TextView emptyrow = new TextView(this);
        empty.addView(emptyrow);

        //definisi title
        TextView title = new TextView(this);
        title.setText("Informasi Komposisi Pakan Sapi Perah");
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 6;
        rowTitle.addView(title, params);

        TableRow optimasi = new TableRow(this);
        optimasi.setGravity(Gravity.CENTER_HORIZONTAL);
        Button ke = new Button(this);
        ke.setPadding(10, 10, 10, 10);
        ke.setText("Optimasi Kandungan Pakan");
        ke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, optimasi.class);
                startActivity(intent);

            }
        });
        params.setMargins(10, 100, 10, 10);
        optimasi.addView(ke, params);

        //label
        TableRow infoLabels = new TableRow(this);
        TextView[] info = new TextView[infoLabel.length];
        for(int i = 0; i < infoLabel.length; i++) {
            info[i] = new TextView(this);
        }
        for (int kolom = 0; kolom < info.length; kolom++) {
            info[kolom].setText(" "+infoLabel[kolom]);
            info[kolom].setGravity(Gravity.CENTER_HORIZONTAL);
            info[kolom].setTypeface(Typeface.DEFAULT_BOLD);
        }
        for(int i = 0; i < infoLabel.length; i++) {
            infoLabels.addView(info[i]);
        }

        //data
        TableRow[] rowData = new TableRow[namaKandungan.length];
        for(int i = 0; i < namaKandungan.length; i++) {
            rowData[i] = new TableRow(this);
        }

        //nama kandungan
        TextView[] namaKandunganLabel = new TextView[namaKandungan.length];
        for(int i = 0; i < namaKandungan.length; i++) {
            namaKandunganLabel[i] = new TextView(this);
        }
        for (int baris = 0; baris < namaKandungan.length; baris++) {
            namaKandunganLabel[baris].setText(" "+namaKandungan[baris]);
            namaKandunganLabel[baris].setGravity(Gravity.CENTER_HORIZONTAL);
        }

        for (int baris = 0; baris < namaKandungan.length; baris++) {
            rowData[baris].addView(namaKandunganLabel[baris]);
        }

        //nilai kandungan
        for(int kolom = 0; kolom < kandunganGizi[0].length; kolom++) {
            TextView[][] dataKandunganLabel = new TextView[kandunganGizi.length][kandunganGizi[0].length];
            for(int i = 0; i < kandunganGizi.length; i++) {
                dataKandunganLabel[i][kolom] = new TextView(this);
            }
            for (int baris = 0; baris < kandunganGizi.length; baris++) {
                dataKandunganLabel[baris][kolom].setText(" " + kandunganGizi[baris][kolom]);
                dataKandunganLabel[baris][kolom].setGravity(Gravity.CENTER_HORIZONTAL);
            }
            for (int baris = 0; baris < kandunganGizi.length; baris++) {
                rowData[baris].addView(dataKandunganLabel[baris][kolom]);
            }
        }

        table.addView(rowTitle);
        table.addView(empty);
        table.addView(infoLabels);
        for (int baris = 0; baris < namaKandungan.length; baris++) {
            table.addView(rowData[baris]);
        }
        table.addView(optimasi);

        setContentView(table);
    }
}
