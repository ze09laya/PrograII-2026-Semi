package com.example.miprimerapp;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Button;
import androidx.activity.EdgeToEdge;


public class MainActivity extends AppCompatActivity {

    TabHost tbh;
    TextView lbl;
    Spinner spn;
    Button btn;


    Double longitudes[] = {1.0, 1000.0, 100.0, 39.3701, 3.28084, 1.1963, 1.09361};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbh = findViewById(R.id.tbhConversores);
        tbh.setup();

        tbh.addTab(tbh.newTabSpec("Monedas").setContent(R.id.tabmonedas).setIndicator("", getDrawable(R.drawable.monedas)));
        tbh.addTab(tbh.newTabSpec("Longitud").setContent(R.id.tablongitud).setIndicator("", getResources().getDrawable(R.drawable.longitud)));
        tbh.addTab(tbh.newTabSpec("Volumen").setContent(R.id.tabvolumen).setIndicator("", getDrawable(R.drawable.volumen)));
        tbh.addTab(tbh.newTabSpec("Masa").setContent(R.id.tabmasa).setIndicator("", getDrawable(R.drawable.masa)));
        btn = findViewById(R.id.btnLongitudConvertir);
        btn.setOnClickListener(v -> convertirLongitud());
    }

    private void convertirLongitud() {

        spn = findViewById(R.id.spnLongitudDe);
        int de = spn.getSelectedItemPosition();

        spn = findViewById(R.id.spnLongitudA);
        int a = spn.getSelectedItemPosition();

        edt = findViewById(R.id.txtLongitudCantidad);

        if (edt.getText().toString().isEmpty()) return;

        double cantidad = Double.parseDouble(edt.getText().toString());
        double respuesta = longitudes[a] / longitudes[de] * cantidad;

        lbl = findViewById(R.id.lblLongitudRespuesta);
        lbl.setText("Respuesta: " + respuesta);
    }
}
