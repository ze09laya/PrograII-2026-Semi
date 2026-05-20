package com.example.miprimerapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EstadisticasActivity extends Activity {

    DB db;
    LinearLayout btnSalir;

    TextView txtEstado;
    TextView txtResumen;
    TextView txtPorcentaje;

    ProgressBar progressEmocion;

    LinearLayout btnEnviarCorreo;

    int felices = 0;
    int tranquilos = 0;
    int tristes = 0;

    int porcentajeFeliz = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // 1. Inicializar la base de datos
        db = new DB(this);

        // 2. Enlazar los componentes del XML
        txtEstado = findViewById(R.id.txtEstado);
        txtResumen = findViewById(R.id.txtResumen);
        txtPorcentaje = findViewById(R.id.txtPorcentaje);
        progressEmocion = findViewById(R.id.progressEmocion);
        btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        btnSalir = findViewById(R.id.btnSalir); // <-- Enlazamos el nuevo botón X

        // 3. Cargar los datos de la base de datos
        cargarEstadisticas();

        // 4. Configurar la acción del botón Enviar Correo
        btnEnviarCorreo.setOnClickListener(v -> {
            enviarCorreoPadre();
        }); // <-- Aquí cierra correctamente el botón de correo

        // 5. Configurar la acción del botón Salir (X)
        btnSalir.setOnClickListener(v -> {
            finish(); // Cierra esta actividad y regresa a la anterior
        });
    }


    private void cargarEstadisticas() {
        try {
            SQLiteDatabase database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM producto", null);

            if (cursor == null || cursor.getCount() == 0) {
                if (cursor != null) cursor.close();
                txtEstado.setText("Sin registros");
                txtResumen.setText("No hay datos 😢");
                txtPorcentaje.setText("0%");
                progressEmocion.setProgress(0);
                return;
            }

            // Reiniciamos los contadores antes de recorrer el bucle
            felices = 0;
            tranquilos = 0;
            tristes = 0;

            while (cursor.moveToNext()) {
                double ganancia = 0;

                try {
                    // CORRECCIÓN: Leemos directamente como double desde SQLite
                    // Esto evita problemas si los datos se guardaron como texto o números crudos.
                    ganancia = cursor.getDouble(cursor.getColumnIndexOrThrow("ganancia"));
                } catch (Exception e) {
                    ganancia = 0;
                }

                // CALCULAR EMOCIÓN (Basado en la lógica de ganancia/positividad)
                if (ganancia >= 50) {
                    felices++;
                } else if (ganancia >= 20) {
                    tranquilos++;
                } else {
                    tristes++;
                }
            }
            cursor.close();

            int total = felices + tranquilos + tristes;

            // CORRECCIÓN: Casteo a double para evitar la división entera truncada a cero
            if (total > 0) {
                porcentajeFeliz = (int) Math.round(((double) felices / total) * 100);
            } else {
                porcentajeFeliz = 0;
            }

            progressEmocion.setProgress(porcentajeFeliz);
            txtPorcentaje.setText(porcentajeFeliz + "%");

            // EVALUACIÓN DEL ESTADO GENERAL
            if (porcentajeFeliz >= 70) {
                txtEstado.setText("😄 Excelente");
            } else if (porcentajeFeliz >= 40) {
                txtEstado.setText("🙂 Estable");
            } else {
                txtEstado.setText("😢 Bajo");
            }

            txtResumen.setText(
                    "😄 Felices: " + felices +
                            "\n🙂 Tranquilos: " + tranquilos +
                            "\n😢 Tristes: " + tristes +
                            "\n\n📊 Positividad: " + porcentajeFeliz + "%"
            );

        } catch (Exception e) {
            txtResumen.setText("ERROR:\n" + e.getMessage());
        }
    }

    private void enviarCorreoPadre() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"padre@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "⚠️ Advertencia emocional");
            intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "😄 Felices: " + felices +
                            "\n🙂 Tranquilos: " + tranquilos +
                            "\n😢 Tristes: " + tristes +
                            "\n\n📊 Positividad: " + porcentajeFeliz + "%"
            );

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}