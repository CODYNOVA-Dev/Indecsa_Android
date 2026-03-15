package com.example.indecsa.models;

public class LoginResponse {

    private boolean success;
    private String message;

    // Para Capital Humano
    private CapitalHumano capitalHumano;

    // Para Admin
    private Admin admin;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    public CapitalHumano getCapitalHumano() { return capitalHumano; }
    public Admin getAdmin() { return admin; }

    // ------------------ MODELO CAPITAL HUMANO ------------------
    public static class CapitalHumano {
        private int idCapHum;
        private String correoCapHum;

        public int getIdCapHum() { return idCapHum; }
        public String getCorreoCapHum() { return correoCapHum; }
    }

    // ------------------ MODELO ADMIN ------------------
    public static class Admin {
        private int idAdmin;
        private String correoAdmin;

        public int getIdAdmin() { return idAdmin; }
        public String getCorreoAdmin() { return correoAdmin; }
    }
}
