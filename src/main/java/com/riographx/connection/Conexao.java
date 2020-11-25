package com.riographx.connection;
  
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
  
public final class Conexao {

    /**
     * @return the senha
     */
    public static String getSenha() {
        return senha;
    }

    /**
     * @return the url
     */
    public static String getUrl() {
        return url;
    }

    /**
     * @return the usuario
     */
    public static String getUsuario() {
        return usuario;
    }
  
    private static final String usuario = "";
    private static final String senha = "";
    private static final String url = "jdbc:postgresql://0.0.0.0:5432/graphx";
    
    //Amazon
//    private static final String usuario = "postgres";
//    private static final String senha = "R10gr4phx";
//    private static final String url = "jdbc:postgresql://graphx.ciwhoshkg4uy.us-east-2.rds.amazonaws.com:5432/postgres";
  
    public static Connection open() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(getUrl(), getUsuario(), getSenha());
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
  
    public static void close(ResultSet rs, Statement st, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
  
    public static void close(Statement st, Connection conn) {
        close(null, st, conn);
    }
  
    public static void close(Connection conn) {
        close(null, null, conn);
    }
}