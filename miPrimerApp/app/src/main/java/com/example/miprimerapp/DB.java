package com.example.miprimerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "producto.db";
    private static final int DATABASE_VERSION = 5;

    private static final String SQLdb =
            "CREATE TABLE producto (" +
                    "idAmigo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "idProducto TEXT, " +
                    "codigo TEXT, " +
                    "descripcion TEXT, " +
                    "marca TEXT, " +
                    "presentacion TEXT, " +
                    "precio TEXT, " +
                    "urlFoto TEXT, " +
                    "costo TEXT, " +
                    "stock TEXT, " +
                    "ganancia TEXT)";

    public DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLdb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS producto");
        onCreate(db);
    }

    public String administrar_amigos(String accion, String[] datos) {

        try {

            SQLiteDatabase db = getWritableDatabase();
            ContentValues valores = new ContentValues();

            switch (accion) {

                case "nuevo":

                    valores.put("idProducto", datos[0]);
                    valores.put("codigo", datos[1]);
                    valores.put("descripcion", datos[2]);
                    valores.put("marca", datos[3]);
                    valores.put("presentacion", datos[4]);
                    valores.put("precio", datos[5]);
                    valores.put("urlFoto", datos[6]);
                    valores.put("costo", datos[7]);
                    valores.put("stock", datos[8]);
                    valores.put("ganancia", datos[9]);

                    db.insert("producto", null, valores);
                    break;

                case "modificar":

                    valores.put("codigo", datos[1]);
                    valores.put("descripcion", datos[2]);
                    valores.put("marca", datos[3]);
                    valores.put("presentacion", datos[4]);
                    valores.put("precio", datos[5]);
                    valores.put("urlFoto", datos[6]);
                    valores.put("costo", datos[7]);
                    valores.put("stock", datos[8]);
                    valores.put("ganancia", datos[9]);

                    db.update(
                            "producto",
                            valores,
                            "idProducto=?",
                            new String[]{datos[0]}
                    );

                    break;

                case "eliminar":

                    db.delete(
                            "producto",
                            "idProducto=?",
                            new String[]{datos[0]}
                    );

                    break;
            }

            db.close();
            return "ok";

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Cursor lista_amigos() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM producto",
                null
        );
    }
}