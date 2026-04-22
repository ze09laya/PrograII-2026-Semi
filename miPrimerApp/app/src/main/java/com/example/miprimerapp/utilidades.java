package com.example.miprimerapp;

import java.util.Base64;
public class utilidades {
    static String url_consulta = "http://192.168.1.9:5984/tienda/_design/tienda/_view/tienda";
    static String url_mto = "http://192.168.1.9:5984/tienda"; //CRUD, Insertar, Actualizar, Borrar, y Buscar
    static String user = "michelle";
    static String passwd = "12345";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user +":"+ passwd).getBytes());
    public String generarUnicoId(){
        return java.util.UUID.randomUUID().toString();
    }
}
