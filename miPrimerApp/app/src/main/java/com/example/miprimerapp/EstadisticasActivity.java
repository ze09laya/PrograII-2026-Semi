package com.example.miprimerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EstadisticasActivity extends Activity {

    DB db;

    TextView txtEstado;
    TextView txtResumen;
    TextView txtPorcentaje;

    ProgressBar progressEmocion;

    LinearLayout btnEnviarCorreo;

    int felices = 0;
    int tranquilos = 0;
    int tristes = 0;

    int porcentajeFeliz = 0;

    boolean correoYaEnviado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        db = new DB(this);

        txtEstado =
                findViewById(R.id.txtEstado);

        txtResumen =
                findViewById(R.id.txtResumen);

        txtPorcentaje =
                findViewById(R.id.txtPorcentaje);

        progressEmocion =
                findViewById(R.id.progressEmocion);

        btnEnviarCorreo =
                findViewById(R.id.btnEnviarCorreo);

        cargarEstadisticas();

        btnEnviarCorreo.setOnClickListener(v -> {
            enviarCorreoPadre();
        });
    }

    private void cargarEstadisticas() {

        try {

            Cursor cursor =
                    db.obtenerDatos();

            if (cursor == null ||
                    cursor.getCount() == 0) {

                txtEstado.setText(
                        "Sin registros"
                );

                txtResumen.setText(
                        "No hay emociones registradas 😢"
                );

                txtPorcentaje.setText("0%");

                progressEmocion.setProgress(0);

                return;
            }

            while (cursor.moveToNext()) {

                String emocion =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "emocion"
                                )
                        );

                if (emocion == null)
                    emocion = "";

                // CONTAR EMOCIONES

                if (emocion.contains("😄")) {

                    felices++;

                } else if (emocion.contains("😐")) {

                    tranquilos++;

                } else {

                    tristes++;
                }
            }

            int total =
                    felices +
                            tranquilos +
                            tristes;

            porcentajeFeliz =
                    (felices * 100) / total;

            progressEmocion.setProgress(
                    porcentajeFeliz
            );

            txtPorcentaje.setText(
                    porcentajeFeliz + "%"
            );

            // ESTADO

            if (porcentajeFeliz >= 70) {

                txtEstado.setText(
                        "😄 Estado emocional excelente"
                );

            } else if (porcentajeFeliz >= 40) {

                txtEstado.setText(
                        "🙂 Estado emocional estable"
                );

            } else {

                txtEstado.setText(
                        "😢 Estado emocional bajo"
                );
            }

            txtResumen.setText(

                    "😄 Felices: " + felices +

                            "\n🙂 Tranquilos: " + tranquilos +

                            "\n😢 Tristes: " + tristes +

                            "\n\nPromedio emocional: "
                            + porcentajeFeliz + "%"
            );

            // ALERTA AUTOMATICA

            if (porcentajeFeliz < 40 &&
                    !correoYaEnviado) {

                correoYaEnviado = true;

                mostrarAlerta();

                enviarCorreoPadre();
            }

        } catch (Exception e) {

            txtResumen.setText(
                    "Error:\n" + e.getMessage()
            );
        }
    }

    private void mostrarAlerta() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle(
                "⚠ Advertencia emocional"
        );

        builder.setMessage(

                "Se detectó un estado emocional bajo.\n\n" +

                        "😢 Tristes: " + tristes +

                        "\n🙂 Tranquilos: " + tranquilos +

                        "\n😄 Felices: " + felices
        );

        builder.setPositiveButton(
                "Aceptar",
                null
        );

        builder.show();
    }

    private void enviarCorreoPadre() {

        try {

            Intent intent =
                    new Intent(Intent.ACTION_SENDTO);

            intent.setData(
                    Uri.parse("mailto:")
            );

            intent.putExtra(
                    Intent.EXTRA_EMAIL,
                    new String[]{
                            "padre@gmail.com"
                    }
            );

            intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "⚠ Advertencia emocional"
            );

            intent.putExtra(
                    Intent.EXTRA_TEXT,

                    "Hola.\n\n" +

                            "La aplicación detectó " +
                            "un estado emocional bajo.\n\n" +

                            "📊 RESULTADOS:\n\n" +

                            "😢 Tristes: " + tristes +

                            "\n🙂 Tranquilos: " + tranquilos +

                            "\n😄 Felices: " + felices +

                            "\n\nPromedio emocional: "
                            + porcentajeFeliz + "%"
            );

            startActivity(intent);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}