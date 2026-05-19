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

        View vista = LayoutInflater.from(context)
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

        String ruta = dibujos.get(position);

        img.setImageBitmap(
                BitmapFactory.decodeFile(ruta)
        );

        File archivo = new File(ruta);

        txtNombre.setText(
                archivo.getName()
        );

        // ELIMINAR
        btnEliminar.setOnClickListener(v -> {

            archivo.delete();

            dibujos.remove(position);

            notifyDataSetChanged();
        });

        // EDITAR
        btnEditar.setOnClickListener(v -> {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);

            builder.setTitle("Editar nombre");

            EditText txt =
                    new EditText(context);

            txt.setText(archivo.getName());

            builder.setView(txt);

            builder.setPositiveButton(
                    "Guardar",
                    (dialog, which) -> {

                        String nuevo =
                                txt.getText().toString();

                        File nuevoArchivo =
                                new File(
                                        archivo.getParent(),
                                        nuevo + ".png"
                                );

                        archivo.renameTo(nuevoArchivo);

                        dibujos.set(
                                position,
                                nuevoArchivo.getAbsolutePath()
                        );

                        notifyDataSetChanged();
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