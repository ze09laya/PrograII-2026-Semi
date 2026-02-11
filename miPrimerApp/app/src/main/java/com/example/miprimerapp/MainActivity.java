package com.example.miprimerapp;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    TextView tempVal;
    Button btn;
    Spinner spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnCalcular);
        btn.setOnClickListener(v -> calcular());
    }

    private void calcular() {

        tempVal = findViewById(R.id.txtNum1);
        double num1 = Double.parseDouble(tempVal.getText().toString());

        tempVal = findViewById(R.id.txtNum2);
        double num2 = Double.parseDouble(tempVal.getText().toString());

        double respuesta = 0;

        spn = findViewById(R.id.cboOpciones);

        switch (spn.getSelectedItemPosition()) {

            case 0: // suma
                respuesta = num1 + num2;
                break;

            case 1: // Resta
                respuesta = num1 - num2;
                break;

            case 2: // Multiplicacion
                respuesta = num1 * num2;
                break;

            case 3: // division
                respuesta = num1 / num2;
                break;

            case 4: // Factorial
                int n = (int) num1;
                long fact = 1;
                for (int i = 1; i <= n; i++) {
                    fact *= i;
                }
                respuesta = fact;
                break;

            case 5: // Porcentaje
                respuesta = (num1 * num2) / 100;
                break;

            case 6: // Exponenciación
                respuesta = Math.pow(num1, num2);
                break;

            case 7: // Raíz
                if (num1 >= 0) {
                    respuesta = Math.sqrt(num1);
                } else {
                    tempVal = findViewById(R.id.lblRespuesta);
                    tempVal.setText("No existe raíz de números negativos");
                    return;
                }
                break;
        }

        tempVal = findViewById(R.id.lblRespuesta);
        tempVal.setText("Respuesta: " + respuesta);
    }
}
