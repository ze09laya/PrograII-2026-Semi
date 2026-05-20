package com.example.miprimerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class AdaptadorDibujos extends BaseAdapter {

    Context context;

    ArrayList<String> dibujos;

    DB db;

    public AdaptadorDibujos(
            Context context,
            ArrayList<String> dibujos
    ) {

        this.context = context;

        this.dibujos = dibujos;

        db = new DB(context);
    }

    @Override
    public int getCount() {

        return dibujos.size();
    }

    @Override
    public Object getItem(int position) {

        return dibujos.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        View vista =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_dibujo,
                                parent,
                                false
                        );

        ImageView img =
                vista.findViewById(R.id.imgDibujo);

        TextView txtNombre =
                vista.findViewById(R.id.txtNombre);

        Button btnEliminar =
                vista.findViewById(R.id.btnEliminar);

        Button btnEditar =
                vista.findViewById(R.id.btnEditar);

        String ruta =
                dibujos.get(position);

        File archivo =
                new File(ruta);

        // VALIDAR EXISTE
        if (archivo.exists()) {

            img.setImageBitmap(
                    BitmapFactory.decodeFile(ruta)
            );

        } else {

            img.setImageResource(
                    android.R.drawable.ic_menu_report_image
            );
        }

        // NOMBRE
        txtNombre.setText(
                archivo.getName()
        );

        // ELIMINAR
        btnEliminar.setOnClickListener(v -> {

            new AlertDialog.Builder(context)

                    .setTitle("Eliminar")

                    .setMessage(
                            "¿Deseas eliminar este dibujo?"
                    )

                    .setPositiveButton(
                            "Sí",
                            (dialog, which) -> {

                                try {

                                    // BORRAR ARCHIVO
                                    if (archivo.exists()) {

                                        archivo.delete();
                                    }

                                    // BORRAR SQLITE
                                    db.eliminarDibujo(ruta);

                                    // BORRAR LISTA
                                    dibujos.remove(position);

                                    // ACTUALIZAR
                                    notifyDataSetChanged();

                                    Toast.makeText(
                                            context,
                                            "Dibujo eliminado 🗑",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } catch (Exception e) {

                                    Toast.makeText(
                                            context,
                                            "Error eliminando",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    )

                    .setNegativeButton(
                            "Cancelar",
                            null
                    )

                    .show();
        });

        // EDITAR
        btnEditar.setOnClickListener(v -> {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);

            builder.setTitle("Editar nombre");

            EditText txt =
                    new EditText(context);

            txt.setText(
                    archivo.getName()
                            .replace(".png", "")
            );

            builder.setView(txt);

            builder.setPositiveButton(
                    "Guardar",
                    (dialog, which) -> {

                        try {

                            String nuevo =
                                    txt.getText()
                                            .toString()
                                            .trim();

                            // VALIDAR
                            if (nuevo.isEmpty()) {

                                Toast.makeText(
                                        context,
                                        "Nombre vacío",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            File nuevoArchivo =
                                    new File(
                                            archivo.getParent(),
                                            nuevo + ".png"
                                    );

                            // RENOMBRAR
                            boolean renombrado =
                                    archivo.renameTo(
                                            nuevoArchivo
                                    );

                            if (renombrado) {

                                // SQLITE
                                db.eliminarDibujo(ruta);

                                db.guardarDibujo(
                                        nuevoArchivo.getAbsolutePath()
                                );

                                // ACTUALIZAR LISTA
                                dibujos.set(
                                        position,
                                        nuevoArchivo.getAbsolutePath()
                                );

                                notifyDataSetChanged();

                                Toast.makeText(
                                        context,
                                        "Nombre actualizado ✏",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        context,
                                        "No se pudo editar",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        } catch (Exception e) {

                            Toast.makeText(
                                    context,
                                    "Error editando",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

            builder.setNegativeButton(
                    "Cancelar",
                    null
            );

            builder.show();
        });

        return vista;
    }
}