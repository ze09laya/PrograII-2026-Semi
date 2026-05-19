package com.example.miprimerapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;

public class ListaDibujosActivity extends Activity {

    GridView gridDibujos;

    EditText txtBuscar;

    DB db;

    ArrayList<String> dibujos =
            new ArrayList<>();

    ArrayList<String> dibujosOriginal =
            new ArrayList<>();

    AdaptadorDibujos adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_lista_dibujos
        );

        db = new DB(this);

        // GRID
        gridDibujos =
                findViewById(R.id.gridDibujos);

        // BUSCADOR
        txtBuscar =
                findViewById(R.id.txtBuscar);

        // CARGAR
        cargarDibujos();

        // FILTRAR
        txtBuscar.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {

                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filtrar(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {

                    }
                }
        );
    }

    private void cargarDibujos() {

        dibujos.clear();

        dibujosOriginal.clear();

        Cursor cursor =
                db.obtenerDibujos();

        while (cursor.moveToNext()) {

            String ruta =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "rutaDibujo"
                            )
                    );

            dibujos.add(ruta);

            dibujosOriginal.add(ruta);
        }

        cursor.close();

        adaptador =
                new AdaptadorDibujos(
                        this,
                        dibujos
                );

        gridDibujos.setAdapter(
                adaptador
        );
    }

    private void filtrar(String texto) {

        dibujos.clear();

        for (String ruta : dibujosOriginal) {

            if (ruta.toLowerCase()
                    .contains(
                            texto.toLowerCase()
                    )) {

                dibujos.add(ruta);
            }
        }

        adaptador.notifyDataSetChanged();
    }
}