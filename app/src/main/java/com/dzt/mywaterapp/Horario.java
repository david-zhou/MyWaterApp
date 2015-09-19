package com.dzt.mywaterapp;

import java.util.Date;

public class Horario {
    private Date fechaInicial;
    private Date fechaFinal;

    private String sFechaInicial;
    private String sFechaFinal;

    public Horario(Date fInicial, Date fFinal) {
        this.fechaInicial = fInicial;
        this.fechaFinal = fFinal;
    }
    public Horario(String fInicial, String fFinal) {
        this.sFechaInicial = fInicial;
        this.sFechaFinal = fFinal;
    }
    public Horario(Horario horario) {
        this.fechaInicial = horario.getfechaInicial();
        this.fechaFinal = horario.getfechaFinal();
    }
    public Date getfechaInicial() {
        return fechaInicial;
    }

    public String getStringfechaInicial() {
        return sFechaInicial;
    }
    public String getStringsfechaFinal() {
        return sFechaFinal;
    }
    public Date getfechaFinal() {
        return fechaFinal;
    }
    public void setFechaInicial(Date nuevaFecha) {
        this.fechaInicial = nuevaFecha;
    }

    public void setFechaFinal(Date nuevaFecha) {
        this.fechaFinal = nuevaFecha;
    }

}
