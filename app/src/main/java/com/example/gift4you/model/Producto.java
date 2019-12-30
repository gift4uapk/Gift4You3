package com.example.gift4you.model;

public class Producto {

    private String Uid;
    private String Descripcion;
    private String Imagen_Principal;
    private String Precio;
    private String Numero;
    private String Titulo;
    private String SubTitulo;


    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getImagen_Principal() {
        return Imagen_Principal;
    }

    public void setImagen_Principal(String imagen_Principal) {
        Imagen_Principal = imagen_Principal;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getSubTitulo() {
        return SubTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        SubTitulo = subTitulo;
    }
}
