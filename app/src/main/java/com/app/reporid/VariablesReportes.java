package com.app.reporid;

public class VariablesReportes {
    private String tipoReporte;
    private String fechaReporte;
    private String imagen;
    private Double longitud;
    private Double latitud;
    private String descripcionReporte;


    public VariablesReportes(Double latitud, Double longitud, String imagen, String tipoReporte, String descripcionReporte, String fechaReporte) {
        this.tipoReporte = tipoReporte;
        this.fechaReporte = fechaReporte;
        this.imagen = imagen;
        this.longitud = longitud;
        this.latitud = latitud;
        this.descripcionReporte = descripcionReporte;
    }

    public VariablesReportes(String tipoReporte, String fechaReporte, String descripcionReporte) {
        this.tipoReporte = tipoReporte;
        this.fechaReporte = fechaReporte;
        this.descripcionReporte = descripcionReporte;
    }

    public VariablesReportes(String imagen, String tipoReporte, String fechaReporte, String descripcionReporte) {
        this.tipoReporte = tipoReporte;
        this.fechaReporte = fechaReporte;
        this.imagen = imagen;
        this.descripcionReporte = descripcionReporte;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(String fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getDescripcionReporte() {
        return descripcionReporte;
    }

    public void setDescripcionReporte(String descripcionReporte) {
        this.descripcionReporte = descripcionReporte;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

}
