
package com.example.miprimerapp;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;
    RadioGroup radioGroup;


    RadioButton opt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnCalcular);
        btn.setOnClickListener(v->calcular());
    }
    private void calcular(){
        tempVal = findViewById(R.id.txtNum1);
        Double num1 = Double.parseDouble(tempVal.getText().toString());

        tempVal = findViewById(R.id.txtNum2);
        Double num2 = Double.parseDouble(tempVal.getText().toString());

        double respuesta = 0;

        radioGroup = findViewById(R.id.optOpciones);
        if(radioGroup.getCheckedRadioButtonId()==R.id.optSuma) {
            respuesta = num1 + num2;
        }

        if(radioGroup.getCheckedRadioButtonId()==R.id.optResta) {
            respuesta = num1 - num2;
        }
        if(radioGroup.getCheckedRadioButtonId()==R.id.optMultiplicar)  {
            respuesta = num1 * num2;
        }

        if(radioGroup.getCheckedRadioButtonId()==R.id.optDividir) {
            respuesta = num1 / num2;
        }

        if(radioGroup.getCheckedRadioButtonId()==R.id.optFactorial) {

            long fact = 1;
            for(int i = 1; i <= num1; i++){
                fact = fact * i;
            }
            respuesta = fact;
        }

        if(radioGroup.getCheckedRadioButtonId()==R.id.optPorcentaje) {
            respuesta = (num1 * num2) / 100;
        }

        if(radioGroup.getCheckedRadioButtonId()==R.id.optExponenciacion) {

            double pot = 1;

            for (int i = 1; i <= num2; i++) {
                pot = pot * num1;
            }

            respuesta = pot;
        }

        if(radioGroup.getCheckedRadioButtonId()==R.id.optRaiz){

            if (num1 >= 0) {
                respuesta = Math.sqrt(num1);
            } else {
                tempVal.setText("No existe raíz de números negativos");
                return;
            }
        }

        tempVal = findViewById(R.id.lblRespuesta);
        tempVal.setText("Respuesta: "+ respuesta);
    }
}

