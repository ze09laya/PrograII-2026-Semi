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
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    DB db;

    Button btnGuardar;
    ImageView imgFoto;
    FloatingActionButton fab;

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
        fab = findViewById(R.id.fabListaAmigo);

        imgFoto.setOnClickListener(v -> menuImagenes());
        btnGuardar.setOnClickListener(v -> guardarProducto());
        fab.setOnClickListener(v -> regresarLista());

        mostrarDatos();
    }

    //=================================================
    // MENÚ IMÁGENES
    //=================================================
    private void menuImagenes() {

        String[] opciones = {
                "Tomar nueva foto",
                "Elegir de galería",
                "Escoger foto tomada"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Imagen del producto");

        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) tomarFoto();
            if (which == 1) abrirGaleria();
            if (which == 2) elegirFotoTomada();
        });

        builder.show();
    }

    //=================================================
    // TOMAR FOTO
    //=================================================
    private void tomarFoto() {

        try {

            Intent intent =
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File archivo = crearArchivoFoto();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    archivo
            );

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, CAMERA_CODE);

        } catch (Exception e) {
            mostrarMsg("Error cámara: " + e.getMessage());
        }
    }

    private File crearArchivoFoto() throws Exception {

        String fecha =
                new SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(new Date());

        File carpeta =
                getExternalFilesDir(
                        Environment.DIRECTORY_DCIM
                );

        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        File archivo =
                File.createTempFile(
                        "IMG_" + fecha,
                        ".jpg",
                        carpeta
                );

        urlFoto = archivo.getAbsolutePath();

        return archivo;
    }

    //=================================================
    // GALERÍA
    //=================================================
    private void abrirGaleria() {

        Intent intent =
                new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("image/*");

        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );

        startActivityForResult(intent, GALERIA_CODE);
    }

    //=================================================
    // ESCOGER FOTO TOMADA
    //=================================================
    private void elegirFotoTomada() {

        if (fotosTomadas.size() == 0) {
            mostrarMsg("No hay fotos tomadas");
            return;
        }

        String[] lista =
                new String[fotosTomadas.size()];

        for (int i = 0; i < fotosTomadas.size(); i++) {
            lista[i] = "Foto " + (i + 1);
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Escoge una foto");

        builder.setItems(lista, (dialog, which) -> {

            urlFoto = fotosTomadas.get(which);

            imgFoto.setImageBitmap(
                    BitmapFactory.decodeFile(urlFoto)
            );
        });

        builder.show();
    }

    //=================================================
    // RESULTADO
    //=================================================
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {
        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        try {

            if (resultCode == RESULT_OK) {

                // CÁMARA
                if (requestCode == CAMERA_CODE) {

                    fotosTomadas.add(urlFoto);

                    imgFoto.setImageBitmap(
                            BitmapFactory.decodeFile(urlFoto)
                    );
                }

                // GALERÍA
                if (requestCode == GALERIA_CODE &&
                        data != null) {

                    Uri uri = data.getData();

                    if (uri != null) {

                        getContentResolver()
                                .takePersistableUriPermission(
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );

                        urlFoto =
                                guardarImagenGaleria(uri);

                        imgFoto.setImageBitmap(
                                BitmapFactory.decodeFile(urlFoto)
                        );
                    }
                }
            }

        } catch (Exception e) {
            mostrarMsg("Error imagen: " + e.getMessage());
        }
    }

    //=================================================
    // COPIAR IMAGEN GALERÍA
    //=================================================
    private String guardarImagenGaleria(Uri uri) {

        try {

            InputStream input =
                    getContentResolver()
                            .openInputStream(uri);

            Bitmap bitmap =
                    BitmapFactory.decodeStream(input);

            if (input != null) input.close();

            File archivo = new File(
                    getExternalFilesDir(
                            Environment.DIRECTORY_DCIM
                    ),
                    "GAL_" +
                            System.currentTimeMillis() +
                            ".jpg"
            );

            FileOutputStream output =
                    new FileOutputStream(archivo);

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    85,
                    output
            );

            output.flush();
            output.close();

            return archivo.getAbsolutePath();

        } catch (Exception e) {
            mostrarMsg("Error galería: " + e.getMessage());
            return "";
        }
    }

    //=================================================
    // MOSTRAR DATOS
    //=================================================
    private void mostrarDatos() {

        try {

            Bundle b = getIntent().getExtras();

            if (b != null) {

                accion =
                        b.getString(
                                "accion",
                                "nuevo"
                        );

                if (accion.equals("modificar")) {

                    JSONObject datos =
                            new JSONObject(
                                    b.getString("producto")
                            );

                    id = datos.optString("_id", "");
                    rev = datos.optString("_rev", "");
                    idProducto =
                            datos.optString(
                                    "idProducto",
                                    ""
                            );

                    ((TextView)findViewById(
                            R.id.txtcodigoAmigos))
                            .setText(
                                    datos.optString(
                                            "codigo",
                                            ""
                                    )
                            );

                    ((TextView)findViewById(
                            R.id.txtdescripcionAmigos))
                            .setText(
                                    datos.optString(
                                            "descripcion",
                                            ""
                                    )
                            );

                    ((TextView)findViewById(
                            R.id.txtmarcaAmigos))
                            .setText(
                                    datos.optString(
                                            "marca",
                                            ""
                                    )
                            );

                    ((TextView)findViewById(
                            R.id.txtpresentacionAmigos))
                            .setText(
                                    datos.optString(
                                            "presentacion",
                                            ""
                                    )
                            );

                    ((TextView)findViewById(
                            R.id.txtprecioAmigos))
                            .setText(
                                    datos.optString(
                                            "precio",
                                            ""
                                    )
                            );

                    urlFoto =
                            datos.optString(
                                    "foto",
                                    ""
                            );

                    if (!urlFoto.isEmpty()) {

                        File archivo =
                                new File(urlFoto);

                        if (archivo.exists()) {

                            imgFoto.setImageBitmap(
                                    BitmapFactory.decodeFile(urlFoto)
                            );

                        } else {

                            imgFoto.setImageResource(
                                    android.R.drawable.ic_menu_gallery
                            );
                        }
                    }
                }
            }

        } catch (Exception e) {
            mostrarMsg("Error mostrar datos");
        }
    }

    //=================================================
    // GUARDAR PRODUCTO
    //=================================================
    private void guardarProducto() {

        try {

            String codigo =
                    ((TextView)findViewById(
                            R.id.txtcodigoAmigos))
                            .getText().toString().trim();

            String descripcion =
                    ((TextView)findViewById(
                            R.id.txtdescripcionAmigos))
                            .getText().toString().trim();

            String marca =
                    ((TextView)findViewById(
                            R.id.txtmarcaAmigos))
                            .getText().toString().trim();

            String presentacion =
                    ((TextView)findViewById(
                            R.id.txtpresentacionAmigos))
                            .getText().toString().trim();

            String precio =
                    ((TextView)findViewById(
                            R.id.txtprecioAmigos))
                            .getText().toString().trim();

            if (codigo.isEmpty() ||
                    descripcion.isEmpty() ||
                    marca.isEmpty() ||
                    presentacion.isEmpty() ||
                    precio.isEmpty()) {

                mostrarMsg("Complete todos los campos");
                return;
            }

            if (urlFoto.isEmpty()) {
                mostrarMsg("Seleccione imagen");
                return;
            }

            //=================================================
            // SIEMPRE SQLITE
            //=================================================
            String[] datos = {
                    idProducto,
                    codigo,
                    descripcion,
                    marca,
                    presentacion,
                    precio,
                    urlFoto
            };

            db.administrar_amigos(
                    accion,
                    datos
            );

            //=================================================
            // INTERNET
            //=================================================
            detectarinternet di =
                    new detectarinternet(this);

            if (di.hayConexionInternet()) {

                JSONObject json =
                        new JSONObject();

                if (accion.equals("modificar")) {
                    json.put("_id", id);
                    json.put("_rev", rev);
                }

                json.put("idProducto", idProducto);
                json.put("codigo", codigo);
                json.put("descripcion", descripcion);
                json.put("marca", marca);
                json.put("presentacion", presentacion);
                json.put("precio", precio);
                json.put("foto", urlFoto);

                enviarDatosServidor enviar =
                        new enviarDatosServidor(this);

                String respuesta =
                        enviar.execute(
                                json.toString(),
                                "POST",
                                utilidades.url_mto
                        ).get();

                JSONObject resp =
                        new JSONObject(respuesta);

                id = resp.optString("id", id);
                rev = resp.optString("rev", rev);

                mostrarMsg(
                        "Guardado en servidor"
                );

            } else {

                mostrarMsg(
                        "Sin internet: guardado en SQLite"
                );
            }

            regresarLista();

        } catch (Exception e) {
            mostrarMsg(
                    "Error guardar: " +
                            e.getMessage()
            );
        }
    }

    //=================================================
    private void regresarLista() {

        startActivity(
                new Intent(
                        this,
                        lista_producto.class
                )
        );

        finish();
    }

    private void mostrarMsg(String msg) {

        Toast.makeText(
                this,
                msg,
                Toast.LENGTH_LONG
        ).show();
    }
}