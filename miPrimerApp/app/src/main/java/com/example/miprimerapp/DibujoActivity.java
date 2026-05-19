package com.example.miprimerapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class DibujoActivity extends Activity {

    Lienzo lienzo;

    LinearLayout btnGuardar,
            btnLimpiar,
            btnSalir,
            btnVerDibujos;

    TextView txtDibujos,
            txtArtista;

    DB db;

    int cantidadDibujos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dibujo);

        db = new DB(this);

        // CONTENEDOR
        FrameLayout contenedor =
                findViewById(R.id.contenedorLienzo);

        // BOTONES
        btnGuardar =
                findViewById(R.id.btnGuardar);

        btnLimpiar =
                findViewById(R.id.btnLimpiar);

        btnSalir =
                findViewById(R.id.btnSalir);

        btnVerDibujos =
                findViewById(R.id.btnVerDibujos);

        // TEXTOS
        txtDibujos =
                findViewById(R.id.txtDibujos);

        txtArtista =
                findViewById(R.id.txtArtista);

        // CREAR LIENZO
        lienzo = new Lienzo(this);

        contenedor.addView(lienzo);

        // TOTAL DIBUJOS
        cantidadDibujos =
                db.obtenerDibujos().getCount();

        txtDibujos.setText(
                "♥ " + cantidadDibujos + " dibujos"
        );

        actualizarNivel();

        // LIMPIAR
        btnLimpiar.setOnClickListener(v -> {
            lienzo.limpiar();
        });

        // SALIR
        btnSalir.setOnClickListener(v -> {
            finish();
        });

        // GUARDAR
        btnGuardar.setOnClickListener(v -> {
            guardarDibujo();
        });

        // VER DIBUJOS
        btnVerDibujos.setOnClickListener(v -> {

            Intent i = new Intent(
                    DibujoActivity.this,
                    ListaDibujosActivity.class
            );

            startActivity(i);
        });

        // COLORES
        findViewById(R.id.colorRojo)
                .setOnClickListener(v ->
                        lienzo.cambiarColor(
                                Color.parseColor("#F44336")
                        ));

        findViewById(R.id.colorNaranja)
                .setOnClickListener(v ->
                        lienzo.cambiarColor(
                                Color.parseColor("#FF9800")
                        ));

        findViewById(R.id.colorAmarillo)
                .setOnClickListener(v ->
                        lienzo.cambiarColor(
                                Color.parseColor("#FFD600")
                        ));

        findViewById(R.id.colorVerde)
                .setOnClickListener(v ->
                        lienzo.cambiarColor(
                                Color.parseColor("#4CAF50")
                        ));

        findViewById(R.id.colorMorado)
                .setOnClickListener(v ->
                        lienzo.cambiarColor(
                                Color.parseColor("#7C4DFF")
                        ));
    }

    private void guardarDibujo() {

        try {

            if (lienzo == null) {

                Toast.makeText(
                        this,
                        "No hay lienzo",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            // CREAR BITMAP
            lienzo.setDrawingCacheEnabled(true);

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            lienzo.getDrawingCache()
                    );

            lienzo.setDrawingCacheEnabled(false);

            // CARPETA
            File carpeta =
                    getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES
                    );

            if (carpeta != null &&
                    !carpeta.exists()) {

                carpeta.mkdirs();
            }

            // NOMBRE ARCHIVO
            String nombre =
                    "dibujo_" +
                            System.currentTimeMillis()
                            + ".png";

            File archivo =
                    new File(carpeta, nombre);

            // GUARDAR PNG
            FileOutputStream output =
                    new FileOutputStream(archivo);

            bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    output
            );

            output.flush();
            output.close();

            // GUARDAR EN SQLITE
            db.guardarDibujo(
                    archivo.getAbsolutePath()
            );

            // ACTUALIZAR CONTADOR
            cantidadDibujos =
                    db.obtenerDibujos().getCount();

            txtDibujos.setText(
                    "♥ " +
                            cantidadDibujos +
                            " dibujos"
            );

            actualizarNivel();

            // INTERNET
            detectarinternet di =
                    new detectarinternet(this);

            if (di.hayConexionInternet()) {

                try {

                    JSONObject json =
                            new JSONObject();

                    json.put(
                            "tipo",
                            "dibujo"
                    );

                    json.put(
                            "ruta",
                            archivo.getAbsolutePath()
                    );

                    enviarDatosServidor enviar =
                            new enviarDatosServidor(this);

                    enviar.execute(
                            json.toString(),
                            "POST",
                            utilidades.url_mto
                    );

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            Toast.makeText(
                    this,
                    "Dibujo guardado 🎨",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // NIVEL ARTISTA
    private void actualizarNivel() {

        if (cantidadDibujos >= 15) {

            txtArtista.setText(
                    "🏆 Maestro artista"
            );

        } else if (cantidadDibujos >= 10) {

            txtArtista.setText(
                    "⭐⭐ Super artista"
            );

        } else if (cantidadDibujos >= 5) {

            txtArtista.setText(
                    "⭐ Gran artista"
            );

        } else {

            txtArtista.setText(
                    "★ Artista novato"
            );
        }
    }
}