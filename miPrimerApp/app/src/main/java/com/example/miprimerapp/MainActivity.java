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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        btn = findViewById(R.id.btnMonedasConvertir);
        btn.setOnClickListener(v->convertirMonedas());

        btn = findViewById(R.id.btnLongitudConvertir);
        btn.setOnClickListener(v->convertirLongitud());
    }

    private void convertirLongitud(){
        spn = findViewById(R.id.spnLongitudDe);
        int de = spn.getSelectedItemPosition();

        spn = findViewById(R.id.spnLongitudA);
        int a = spn.getSelectedItemPosition();

        tempVal = findViewById(R.id.txtLongitudCantidad);
        double cantidad = Double.parseDouble(tempVal.getText().toString());
        double respuesta = conversorLongitud(de, a, cantidad);

        tempVal = findViewById(R.id.lblLongitudRespuesta);
        tempVal.setText("Respuesta: "+ respuesta);
    }

    private void convertirMonedas(){
        spn = findViewById(R.id.spnMonedasDe);
        int de = spn.getSelectedItemPosition();

        spn = findViewById(R.id.spnMonedasA);
        int a = spn.getSelectedItemPosition();

        tempVal = findViewById(R.id.txtMonedasCantidad);
        double cantidad = Double.parseDouble(tempVal.getText().toString());
        double respuesta = conversor(de, a, cantidad);

        tempVal = findViewById(R.id.lblMonedasRespuesta);
        tempVal.setText("Respuesta: "+ respuesta);
    }

    double conversor(int de, int a, double cantidad){
        return valores[a]/valores[de] * cantidad;
    }

    double conversorLongitud(int de, int a, double cantidad){
        return longitudes[a]/longitudes[de] * cantidad;
    }
}
