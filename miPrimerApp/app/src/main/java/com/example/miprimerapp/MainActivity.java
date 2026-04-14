package com.example.miprimerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    DB db;
    Button btn;
    TextView tempVal;

    String accion = "nuevo";
    String idAmigo = "";
    String urlFoto = "";
    String id = "";
    String rev = "";

    Intent tomarFotoIntent;
    FloatingActionButton fab;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.imgFotoAmigo);
        btn = findViewById(R.id.btnGuardarAmigo);
        fab = findViewById(R.id.fabListaAmigo);

        db = new DB(this);

        img.setOnClickListener(v -> tomarFoto());
        btn.setOnClickListener(v -> guardarAmigo());
        fab.setOnClickListener(v -> regresarListaAmigos());

        mostrarDatosAmigos();
    }

    private void mostrarDatosAmigos() {
        try {
            Bundle parametros = getIntent().getExtras();

            if (parametros != null) {
                accion = parametros.getString("accion");

                if (accion.equals("modificar")) {

                    JSONObject datos = new JSONObject(parametros.getString("amigos"));

                    id = datos.getString("_id");
                    rev = datos.getString("_rev");
                    idAmigo = datos.getString("idAmigo");

                    tempVal = findViewById(R.id.txtNombreAmigos);
                    tempVal.setText(datos.getString("nombre"));

                    tempVal = findViewById(R.id.txtDireccionAmigos);
                    tempVal.setText(datos.getString("direccion"));

                    tempVal = findViewById(R.id.txtTelefonoAmigos);
                    tempVal.setText(datos.getString("telefono"));

                    tempVal = findViewById(R.id.txtEmailAmigos);
                    tempVal.setText(datos.getString("email"));

                    tempVal = findViewById(R.id.txtDuiAmigos);
                    tempVal.setText(datos.getString("dui"));

                    urlFoto = datos.getString("urlFoto");

                    img.setImageURI(Uri.parse(urlFoto));
                }
            }

        } catch (Exception e) {
            mostrarMsg("Error al mostrar datos: " + e.getMessage());
        }
    }

    private void tomarFoto() {
        try {
            tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File fotoAmigo = crearImgAmigo();

            if (fotoAmigo != null) {

                Uri uriFoto = FileProvider.getUriForFile(
                        this,
                        "com.example.miprimerapp.fileprovider",
                        fotoAmigo
                );

                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);

                startActivityForResult(tomarFotoIntent, 1);
            }

        } catch (Exception e) {
            mostrarMsg("Error al tomar foto: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            img.setImageURI(Uri.parse(urlFoto));
        }
    }

    private File crearImgAmigo() throws Exception {

        String fechaHora = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String nombreArchivo = "foto_" + fechaHora;

        File directorio = getExternalFilesDir(Environment.DIRECTORY_DCIM);

        if (!directorio.exists()) {
            directorio.mkdir();
        }

        File imagen = File.createTempFile(nombreArchivo, ".jpg", directorio);

        urlFoto = imagen.getAbsolutePath();

        return imagen;
    }

    private void guardarAmigo() {

        try {

            tempVal = findViewById(R.id.txtNombreAmigos);
            String nombre = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDireccionAmigos);
            String direccion = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtTelefonoAmigos);
            String telefono = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtEmailAmigos);
            String email = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDuiAmigos);
            String dui = tempVal.getText().toString();

            String[] datos = {
                    idAmigo,
                    nombre,
                    direccion,
                    telefono,
                    email,
                    dui,
                    urlFoto
            };

            db.administrar_amigos(accion, datos);

            JSONObject datosAmigos = new JSONObject();

            if (accion.equals("modificar")) {
                datosAmigos.put("_id", id);
                datosAmigos.put("_rev", rev);
            }

            datosAmigos.put("idAmigo", idAmigo);
            datosAmigos.put("nombre", nombre);
            datosAmigos.put("direccion", direccion);
            datosAmigos.put("telefono", telefono);
            datosAmigos.put("email", email);
            datosAmigos.put("dui", dui);
            datosAmigos.put("urlFoto", urlFoto);

            mostrarMsg("Registro guardado con éxito");

            regresarListaAmigos();

        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void regresarListaAmigos() {
        Intent intent = new Intent(this, lista_amigos.class);
        startActivity(intent);
    }
}