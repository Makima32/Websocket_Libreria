package com.websocket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConector {

    private String url;
    private String user;
    private String password;

    public DbConector(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    //Comprobar la conexión

    public void testConnection() throws SQLException {
        try (Connection con = connect()) {
            if (con == null || con.isClosed()) {
                throw new SQLException("No se pudo establecer la conexión a la base de datos.");
            }
        }
    }
    
    // Abrir conexión
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // Cerrar la conexión
    public void close(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
