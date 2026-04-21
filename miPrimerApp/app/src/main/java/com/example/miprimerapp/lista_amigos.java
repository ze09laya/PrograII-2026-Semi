package com.example.miprimerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_amigos extends Activity {

    Bundle parametros = new Bundle();
    DB db;
    FloatingActionButton fab;
    ListView ltsAmigos;
    Cursor cAmigos;

    final ArrayList<amigos> alAmigos = new ArrayList<>();
    final ArrayList<amigos> alAmigosCopia = new ArrayList<>();

    JSONArray jsonArray;
    JSONObject jsonObject;

    int posicion = 0;
    amigos misAmigos;

    detectarinternet di;
    obtenerDatosServidor datosServidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigos);

        parametros.putString("accion", "nuevo");

        db = new DB(this);
        di = new detectarinternet(this);

        fab = findViewById(R.id.fabAgregarAmigos);
        ltsAmigos = findViewById(R.id.ltsAmigos);

        fab.setOnClickListener(v -> abrirActivity());

        buscarAmigos();
        obtenerAmigos();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        try {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;

            posicion = info.position;

            menu.setHeaderTitle(
                    jsonArray.getJSONObject(posicion)
                            .getJSONObject("value")
                            .getString("nombre")
            );

        } catch (Exception e) {
            mostrarMsg("Error menú: " + e.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        try {

            if (item.getItemId() == R.id.mnxAgregar) {
                abrirActivity();

            } else if (item.getItemId() == R.id.mnxModificar) {

                parametros.putString("accion", "modificar");
                parametros.putString(
                        "amigos",
                        jsonArray.getJSONObject(posicion)
                                .getJSONObject("value")
                                .toString()
                );

                abrirActivity();

            } else if (item.getItemId() == R.id.mnxEliminar) {
                borrarAmigo();
            }

            return true;

        } catch (Exception e) {
            mostrarMsg("Error menú item: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }

    private void borrarAmigo() {

        try {

            String nombre = jsonArray.getJSONObject(posicion)
                    .getJSONObject("value")
                    .getString("nombre");

            AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);

            confirmacion.setTitle("¿Eliminar?");
            confirmacion.setMessage(nombre);

            confirmacion.setPositiveButton("SI", (dialog, which) -> {

                try {

                    String respuesta = db.administrar_amigos(
                            "eliminar",
                            new String[]{
                                    jsonArray.getJSONObject(posicion)
                                            .getJSONObject("value")
                                            .getString("idAmigo")
                            });

                    if (respuesta.equals("ok")) {
                        mostrarMsg("Registro eliminado");
                        obtenerAmigos();
                    }

                } catch (Exception e) {
                    mostrarMsg(e.getMessage());
                }

            });

            confirmacion.setNegativeButton("NO",
                    (dialog, which) -> dialog.dismiss());

            confirmacion.create().show();

        } catch (Exception e) {
            mostrarMsg("Error eliminar: " + e.getMessage());
        }
    }

    private void buscarAmigos() {

        TextView txtBuscar = findViewById(R.id.txtBuscarAmigos);

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                alAmigos.clear();

                String buscar = txtBuscar.getText().toString()
                        .trim()
                        .toLowerCase();

                if (buscar.length() == 0) {

                    alAmigos.addAll(alAmigosCopia);

                } else {

                    for (amigos item : alAmigosCopia) {

                        if (item.getNombre().toLowerCase().contains(buscar)
                                || item.getDui().contains(buscar)
                                || item.getEmail().toLowerCase().contains(buscar)) {

                            alAmigos.add(item);
                        }
                    }
                }

                ltsAmigos.setAdapter(
                        new AdaptadorAmigos(lista_amigos.this, alAmigos)
                );
            }
        });
    }

    private void abrirActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(parametros);
        startActivity(intent);
    }

    private void obtenerAmigos() {

        try {

            if (di.hayConexionInternet()) {

                datosServidor = new obtenerDatosServidor() {

                    @Override
                    protected void onPostExecute(String respuesta) {

                        try {

                            jsonObject = new JSONObject(respuesta);
                            jsonArray = jsonObject.getJSONArray("rows");

                            mostrarAmigos();

                        } catch (Exception e) {
                            mostrarMsg(e.getMessage());
                        }
                    }
                };

                datosServidor.execute();

            } else {

                cAmigos = db.lista_amigos();

                if (cAmigos.moveToFirst()) {

                    jsonArray = new JSONArray();

                    do {

                        jsonObject = new JSONObject();

                        jsonObject.put("value", new JSONObject()
                                .put("idAmigo", cAmigos.getString(0))
                                .put("nombre", cAmigos.getString(1))
                                .put("direccion", cAmigos.getString(2))
                                .put("telefono", cAmigos.getString(3))
                                .put("email", cAmigos.getString(4))
                                .put("dui", cAmigos.getString(5))
                                .put("foto", cAmigos.getString(6)));

                        jsonArray.put(jsonObject);

                    } while (cAmigos.moveToNext());

                    mostrarAmigos();

                } else {
                    mostrarMsg("No hay registros");
                }
            }

        } catch (Exception e) {
            mostrarMsg(e.getMessage());
        }
    }

    private void mostrarAmigos() {

        try {

            alAmigos.clear();
            alAmigosCopia.clear();

            for (int i = 0; i < jsonArray.length(); i++) {

                jsonObject = jsonArray.getJSONObject(i)
                        .getJSONObject("value");

                misAmigos = new amigos(
                        jsonObject.getString("idAmigo"),
                        jsonObject.getString("nombre"),
                        jsonObject.getString("direccion"),
                        jsonObject.getString("telefono"),
                        jsonObject.getString("email"),
                        jsonObject.getString("dui"),
                        jsonObject.getString("foto")
                );

                alAmigos.add(misAmigos);
            }

            alAmigosCopia.addAll(alAmigos);

            ltsAmigos.setAdapter(
                    new AdaptadorAmigos(this, alAmigos)
            );

            registerForContextMenu(ltsAmigos);

        } catch (Exception e) {
            mostrarMsg(e.getMessage());
        }
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}