package com.example.miprimerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    DB db;

    Button btnGuardar;
    FloatingActionButton fabRegresar;
    ImageView imgFoto;

    String accion = "nuevo";
    String idProducto = "";
    String id = "";
    String rev = "";
    String urlFoto = "";

    ArrayList<String> fotosTomadas = new ArrayList<>();

    final int CAMERA_CODE = 1;
    final int GALERIA_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);

        imgFoto = findViewById(R.id.imgFotoAmigo);
        btnGuardar = findViewById(R.id.btnGuardarAmigo);
        fabRegresar = findViewById(R.id.fabListaAmigo);


        fabRegresar.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, lista_producto.class);
            startActivity(i);
            finish();
        });

        imgFoto.setOnClickListener(v -> menuImagenes());
        btnGuardar.setOnClickListener(v -> guardarProducto());


        mostrarDatos();
    }


    private String generarId() {
        return String.valueOf(System.currentTimeMillis());
    }


    private void menuImagenes() {

        String[] opciones = {
                "Tomar nueva foto",
                "Elegir de galería",
                "Escoger foto tomada"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Imagen");

        builder.setItems(opciones, (d, which) -> {
            if (which == 0) tomarFoto();
            else if (which == 1) abrirGaleria();
            else if (which == 2) elegirFotoTomada();
        });

        builder.show();
    }


    private void tomarFoto() {

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File archivo = crearArchivoFoto();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    archivo
            );

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, CAMERA_CODE);

        } catch (Exception e) {
            mostrarMsg(e.getMessage());
        }
    }

    private File crearArchivoFoto() throws Exception {

        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File carpeta = getExternalFilesDir(Environment.DIRECTORY_DCIM);

        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        File archivo = File.createTempFile("IMG_" + fecha, ".jpg", carpeta);

        urlFoto = archivo.getAbsolutePath();

        return archivo;
    }


    private void abrirGaleria() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, GALERIA_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (resultCode == RESULT_OK) {

                if (requestCode == CAMERA_CODE) {

                    if (urlFoto != null) {
                        fotosTomadas.add(urlFoto);
                        imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
                    }
                }

                if (requestCode == GALERIA_CODE && data != null) {

                    Uri uri = data.getData();
                    if (uri == null) return;

                    InputStream input = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    if (input != null) input.close();

                    File archivo = new File(
                            getExternalFilesDir(Environment.DIRECTORY_DCIM),
                            "GAL_" + System.currentTimeMillis() + ".jpg"
                    );

                    FileOutputStream output = new FileOutputStream(archivo);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output);
                    output.flush();
                    output.close();

                    urlFoto = archivo.getAbsolutePath();

                    imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
                }
            }

        } catch (Exception e) {
            mostrarMsg(e.getMessage());
        }
    }


    private void guardarProducto() {

        try {

            String codigo = ((EditText)findViewById(R.id.txtcodigoAmigos)).getText().toString().trim();
            String descripcion = ((EditText)findViewById(R.id.txtdescripcionAmigos)).getText().toString().trim();
            String marca = ((EditText)findViewById(R.id.txtmarcaAmigos)).getText().toString().trim();
            String presentacion = ((EditText)findViewById(R.id.txtpresentacionAmigos)).getText().toString().trim();
            String precio = ((EditText)findViewById(R.id.txtprecioAmigos)).getText().toString().trim();
            String costo = ((EditText)findViewById(R.id.txtcostoAmigos)).getText().toString().trim();
            String stock = ((EditText)findViewById(R.id.txtstockAmigos)).getText().toString().trim();

            if (codigo.isEmpty() || descripcion.isEmpty() || marca.isEmpty()
                    || presentacion.isEmpty() || precio.isEmpty()) {
                mostrarMsg("Complete campos");
                return;
            }

            if (urlFoto == null || urlFoto.isEmpty()) {
                mostrarMsg("Seleccione imagen");
                return;
            }

            detectarinternet di = new detectarinternet(this);

            if (idProducto == null || idProducto.isEmpty()) {
                idProducto = generarId();
            }

            String[] datos = {
                    idProducto, codigo, descripcion, marca,
                    presentacion, precio, urlFoto, costo, stock
            };

            JSONObject json = new JSONObject();
            json.put("idProducto", idProducto);
            json.put("codigo", codigo);
            json.put("descripcion", descripcion);
            json.put("marca", marca);
            json.put("presentacion", presentacion);
            json.put("precio", precio);
            json.put("foto", urlFoto);
            json.put("costo", costo);
            json.put("stock", stock);

            if (accion.equals("modificar")) {
                json.put("_id", id);
                json.put("_rev", rev);
            }

            // OFFLINE
            if (!di.hayConexionInternet()) {

                db.administrar_amigos(accion, datos);

                mostrarMsg("Guardado offline (SQLite)");
                regresarLista();
                return;
            }


            enviarDatosServidor enviar = new enviarDatosServidor(this);

            enviar.execute(
                    json.toString(),
                    "POST",
                    utilidades.url_mto
            );

            mostrarMsg("Enviando al servidor...");
            regresarLista();

        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }


    private void elegirFotoTomada() {

        if (fotosTomadas.isEmpty()) {
            mostrarMsg("No hay fotos");
            return;
        }

        String[] lista = new String[fotosTomadas.size()];

        for (int i = 0; i < fotosTomadas.size(); i++) {
            lista[i] = "Foto " + (i + 1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escoger foto");

        builder.setItems(lista, (d, which) -> {

            urlFoto = fotosTomadas.get(which);
            imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
        });

        builder.show();
    }


    private void mostrarDatos() {

        try {

            Bundle b = getIntent().getExtras();
            if (b == null) return;

            accion = b.getString("accion", "nuevo");

            if (!accion.equals("modificar")) return;

            String jsonStr = b.getString("producto", "{}");
            JSONObject datos = new JSONObject(jsonStr);

            id = datos.optString("_id", "");
            rev = datos.optString("_rev", "");
            idProducto = datos.optString("idProducto", "");

            ((EditText)findViewById(R.id.txtcodigoAmigos)).setText(datos.optString("codigo", ""));
            ((EditText)findViewById(R.id.txtdescripcionAmigos)).setText(datos.optString("descripcion", ""));
            ((EditText)findViewById(R.id.txtmarcaAmigos)).setText(datos.optString("marca", ""));
            ((EditText)findViewById(R.id.txtpresentacionAmigos)).setText(datos.optString("presentacion", ""));
            ((EditText)findViewById(R.id.txtprecioAmigos)).setText(datos.optString("precio", ""));
            ((EditText)findViewById(R.id.txtcostoAmigos)).setText(datos.optString("costo", ""));
            ((EditText)findViewById(R.id.txtstockAmigos)).setText(datos.optString("stock", ""));

            urlFoto = datos.optString("foto", "");

            if (urlFoto != null && !urlFoto.isEmpty()) {
                File file = new File(urlFoto);
                if (file.exists()) {
                    imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
                }
            }

        } catch (Exception e) {
            mostrarMsg("Error cargar datos: " + e.getMessage());
        }
    }


    private void regresarLista() {
        startActivity(new Intent(this, lista_producto.class));
        finish();
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}