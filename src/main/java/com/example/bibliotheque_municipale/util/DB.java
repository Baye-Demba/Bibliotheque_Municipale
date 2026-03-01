package com.example.bibliotheque_municipale.util;

import java.sql.*;

public class DB {

    private Connection conn = null;
    private PreparedStatement pstm = null;
    private ResultSet rs = null;

    public Connection getConnexion() {
        try {
            if (conn == null || conn.isClosed()) {
                String url  = "jdbc:mysql://127.0.0.1:3306/bibliotheque_municipale?useSSL=false&allowPublicKeyRetrieval=true";
                String user = "root";
                String mdp  = "";
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, mdp);
            }
        } catch (Exception ex) {
            System.out.println("Erreur connexion : " + ex.getMessage());
        }
        return conn;
    }

    // executer un SELECT et retourner directement les resultats
    public ResultSet select(String sql, Object... params) {
        try {
            pstm = getConnexion().prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstm.setObject(i + 1, params[i]);
            }
            rs = pstm.executeQuery();
        } catch (Exception ex) {
            System.out.println("Erreur select : " + ex.getMessage());
        }
        return rs;
    }

    // executer un INSERT / UPDATE / DELETE
    public int maj(String sql, Object... params) {
        try {
            pstm = getConnexion().prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstm.setObject(i + 1, params[i]);
            }
            return pstm.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Erreur maj : " + ex.getMessage());
        }
        return 0;
    }

    public void fermer() {
        try {
            if (rs   != null) rs.close();
            if (pstm != null) pstm.close();
            if (conn != null) conn.close();
        } catch (Exception ex) {
            System.out.println("Erreur fermeture : " + ex.getMessage());
        }
    }
}
