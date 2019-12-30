package com.example.gift4you.model;

public class Evento {

    private String Año;
    private String Mes;
    private String Dia;
    private String Recordatorio;
    private String Tipo_Evento;
    private String Referente_A;
    private String Parentesco;
    private String Uid;

    public String getRecordatorio() {
        return Recordatorio;
    }

    public void setRecordatorio(String recordatorio) {
        Recordatorio = recordatorio;
    }

    public String getAño() {
        return Año;
    }

    public void setAño(String año) {
        Año = año;
    }

    public String getMes() {
        return Mes;
    }

    public void setMes(String mes) {
        Mes = mes;
    }

    public String getDia() {
        return Dia;
    }

    public void setDia(String dia) {
        Dia = dia;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getTipo_Evento() {
        return Tipo_Evento;
    }

    public void setTipo_Evento(String tipo_Evento) {
        Tipo_Evento = tipo_Evento;
    }

    public String getReferente_A() {
        return Referente_A;
    }

    public void setReferente_A(String referente_A) {
        Referente_A = referente_A;
    }

    public String getParentesco() {
        return Parentesco;
    }

    public void setParentesco(String parentesco) {
        Parentesco = parentesco;
    }
}
