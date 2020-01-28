package com.app.reporid;

import org.json.JSONException;
import org.json.JSONObject;

public class Person_Reporid {
    private String identificacion;
    private String identificador_persona;
    private String nombres;
    private String apellidos;
    private String correo_electronico;
    private String celular;
    private String directorio_perfil;

    public Person_Reporid(){

    }
    public Person_Reporid(JSONObject datos){
        try {
            identificador_persona = datos.getString("id_persona");
            nombres = datos.getString("nombres");
            apellidos = datos.getString("apellidos");
            correo_electronico = datos.getString("correo");
            celular = datos.getString("celular");
            identificacion = datos.getString("identificacion");
            directorio_perfil = datos.getString("directorio_perfil");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getIdentificador_persona() {
        return identificador_persona;
    }

    public void setIdentificador_persona(String identificador_persona) {
        this.identificador_persona = identificador_persona;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo_electronico() {
        return correo_electronico;
    }

    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }
}
