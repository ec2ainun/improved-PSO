package com.startup.ec2ainun.ipsotest;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class hasil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String [] namaKandungan = {
                "rumput gajah", "tebon jagung", "bapro", "pollard", "ampas tahu",
                "limbah roti", "jerami padi", "kulit kedelai", "daun lamtoro","bekatul"
        };

        String hasil[]= new String[11];
        for (int j = 0; j <= 10; j++) {
            hasil[j]=getIntent().getExtras().get("hasil"+j).toString();
        }

        TableLayout table = new TableLayout(this);
        table.setGravity(Gravity.CENTER_HORIZONTAL);
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        TableRow rowTitle = new TableRow(this);
        rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        TableRow empty = new TableRow(this);
        TextView emptyrow = new TextView(this);
        empty.addView(emptyrow);

        TextView title = new TextView(this);
        title.setText("Hasil Optimasi");
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 2;
        params.setMargins(10, 100, 10, 10);
        rowTitle.addView(title, params);

        TableRow[] rowData = new TableRow[namaKandungan.length];
        TableRow.LayoutParams data = new TableRow.LayoutParams();
        data.gravity = Gravity.RIGHT;

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

        TextView[] datahasil = new TextView[namaKandungan.length];
        for(int i = 0; i < namaKandungan.length; i++) {
            datahasil[i] = new TextView(this);
        }
        for (int baris = 0; baris < namaKandungan.length; baris++) {
            datahasil[baris].setText(" " +  hasil[baris]);
            datahasil[baris].setGravity(Gravity.CENTER_HORIZONTAL);
        }
        for (int baris = 0; baris < namaKandungan.length; baris++) {
            rowData[baris].addView(datahasil[baris]);
        }

        TableRow fitness = new TableRow(this);
        TextView hasilFitness = new TextView(this);
        hasilFitness.setText("Dengan nilai Fitness : "+hasil[10]);
        hasilFitness.setGravity(Gravity.CENTER);
        fitness.addView(hasilFitness, params);

        table.addView(rowTitle);
        table.addView(empty);
        for (int baris = 0; baris < namaKandungan.length; baris++) {
            table.addView(rowData[baris], data);
        }
        table.addView(fitness);

        setContentView(table);



    }
}
