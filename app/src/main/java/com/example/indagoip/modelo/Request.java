package com.example.indagoip.modelo;

import java.util.Date;

public class Request {

    // se guarda la petición y la fecha
    private Integer idRequest;
    private Integer tipo;
    private Date fechaHora;
    private String url;

    public Request(Integer tipo, Date fechaHora, String url) {
        this.setTipo(tipo);
        this.setFechaHora(fechaHora);
        this.setUrl(url);
    }

    public Request(Date fechaHora, String url) {
        this.setFechaHora(fechaHora);
        this.setUrl(url);
    }

    // Getters y Setters

    public Integer getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(Integer idRequest) {
        this.idRequest = idRequest;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
