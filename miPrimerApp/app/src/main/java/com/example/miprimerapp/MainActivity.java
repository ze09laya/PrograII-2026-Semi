package com.example.miprimerapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    DB db;
    Button btnGuardar;
    TextView tempVal;
    ImageView imgFoto;
    FloatingActionButton fabLista;

    String accion = "nuevo";
    String idProducto = "";
    String urlFoto = "";
    String id = "";
    String rev = "";

    private static final int PERMISSIONS_CODE = 100;
    private static final int CAMERA_CODE = 1;
    private static final int GALERIA_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);

        imgFoto = findViewById(R.id.imgFotoAmigo);
        btnGuardar = findViewById(R.id.btnGuardarAmigo);
        fabLista = findViewById(R.id.fabListaAmigo);

        imgFoto.setOnClickListener(v -> mostrarOpcionesFoto());
        btnGuardar.setOnClickListener(v -> guardarProducto());
        fabLista.setOnClickListener(v -> regresarLista());

        pedirPermisos();
        mostrarDatosProducto();
    }

    private void pedirPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES
            }, PERMISSIONS_CODE);
        }
    }

    private void mostrarDatosProducto() {
        try {
            Bundle parametros = getIntent().getExtras();

            if (parametros != null) {
                accion = parametros.getString("accion");

                if ("modificar".equals(accion)) {

                    JSONObject datos = new JSONObject(parametros.getString("amigos"));

                    id = datos.getString("_id");
                    rev = datos.getString("_rev");
                    idProducto = datos.getString("idAmigo");

                    ((TextView) findViewById(R.id.txtcodigoAmigos))
                            .setText(datos.getString("codigo"));

                    ((TextView) findViewById(R.id.txtdescripcionAmigos))
                            .setText(datos.getString("descripcion"));

                    ((TextView) findViewById(R.id.txtmarcaAmigos))
                            .setText(datos.getString("marca"));

                    ((TextView) findViewById(R.id.txtpresentacionAmigos))
                            .setText(datos.getString("presentacion"));

                    ((TextView) findViewById(R.id.txtprecioAmigos))
                            .setText(datos.getString("precio"));

                    urlFoto = datos.getString("foto");

                    if (urlFoto != null && !urlFoto.isEmpty()) {
                        imgFoto.setImageURI(Uri.parse(urlFoto));
                    }
                }
            }

        } catch (Exception e) {
            mostrarMsg("Error mostrar datos: " + e.getMessage());
        }
    }

    private void mostrarOpcionesFoto() {
        String[] opciones = {"Tomar Foto", "Elegir de Galería"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción");

        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) abrirCamara();
            if (which == 1) abrirGaleria();
        });

        builder.show();
    }

    private void abrirCamara() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File archivo = crearArchivoFoto();

            Uri fotoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    archivo
            );

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);

            startActivityForResult(intent, CAMERA_CODE);

        } catch (Exception e) {
            mostrarMsg("Error cámara: " + e.getMessage());
        }
    }

    private File crearArchivoFoto() throws Exception {
        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombre = "foto_" + fecha;

        File carpeta = getExternalFilesDir(Environment.DIRECTORY_DCIM);

        File imagen = File.createTempFile(nombre, ".jpg", carpeta);

        urlFoto = imagen.getAbsolutePath();

        return imagen;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALERIA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_CODE) {
                imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
            }

            if (requestCode == GALERIA_CODE && data != null) {
                Uri uri = data.getData();

                imgFoto.setImageURI(uri);

                urlFoto = guardarImagenGaleria(uri);
            }
        }
    }

    // 🔥 GUARDAR IMAGEN OPTIMIZADA
    private String guardarImagenGaleria(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (inputStream != null) inputStream.close();

            String nombre = "img_" + System.currentTimeMillis() + ".jpg";
            File archivo = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), nombre);

            FileOutputStream outputStream = new FileOutputStream(archivo);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);

            outputStream.flush();
            outputStream.close();

            return archivo.getAbsolutePath();

        } catch (Exception e) {
            mostrarMsg("Error imagen: " + e.getMessage());
            return "";
        }
    }

    // ✅ GUARDAR CON ERROR CORREGIDO (NO USA "ok")
    private void guardarProducto() {
        try {
            String codigo = ((TextView) findViewById(R.id.txtcodigoAmigos))
                    .getText().toString().trim();

            String descripcion = ((TextView) findViewById(R.id.txtdescripcionAmigos))
                    .getText().toString().trim();

            String marca = ((TextView) findViewById(R.id.txtmarcaAmigos))
                    .getText().toString().trim();

            String presentacion = ((TextView) findViewById(R.id.txtpresentacionAmigos))
                    .getText().toString().trim();

            String precio = ((TextView) findViewById(R.id.txtprecioAmigos))
                    .getText().toString().trim();

            // VALIDACIONES
            if (codigo.isEmpty() || descripcion.isEmpty() ||
                    marca.isEmpty() || presentacion.isEmpty() || precio.isEmpty()) {
                mostrarMsg("Todos los campos son obligatorios");
                return;
            }

            try {
                double p = Double.parseDouble(precio);
                if (p <= 0) {
                    mostrarMsg("El precio debe ser mayor a 0");
                    return;
                }
            } catch (Exception e) {
                mostrarMsg("Precio inválido");
                return;
            }

            if (urlFoto == null || urlFoto.isEmpty()) {
                mostrarMsg("Debe seleccionar una imagen");
                return;
            }

            String[] datos = {
                    idProducto,
                    codigo,
                    descripcion,
                    marca,
                    presentacion,
                    precio,
                    urlFoto
            };

            db.administrar_amigos(accion, datos);

            JSONObject datosProducto = new JSONObject();

            if ("modificar".equals(accion)) {
                datosProducto.put("_id", id);
                datosProducto.put("_rev", rev);
            }

            datosProducto.put("idAmigo", idProducto);
            datosProducto.put("codigo", codigo);
            datosProducto.put("descripcion", descripcion);
            datosProducto.put("marca", marca);
            datosProducto.put("presentacion", presentacion);
            datosProducto.put("precio", precio);
            datosProducto.put("foto", urlFoto);

            enviarDatosServidor enviar = new enviarDatosServidor(this);

            String respuesta = enviar.execute(
                    datosProducto.toString(),
                    "POST",
                    utilidades.url_mto
            ).get();

            JSONObject json = new JSONObject(respuesta);

            // 🔥 CORRECCIÓN DEFINITIVA DEL ERROR "ok"
            if (json.has("id")) {
                id = json.getString("id");
            }

            if (json.has("rev")) {
                rev = json.getString("rev");
            }

            mostrarMsg("Producto guardado con éxito");
            regresarLista();

        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }

    private void regresarLista() {
        Intent intent = new Intent(this, lista_producto.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}