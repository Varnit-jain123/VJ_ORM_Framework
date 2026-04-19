package com.vj.orm.config;

import com.google.gson.annotations.SerializedName;

public class DBConfig {
    @SerializedName("jdbc-driver")
    private String jdbcDriver;
    
    @SerializedName("connection-url")
    private String connectionUrl;
    
    private String username;
    private String password;

    // Getters
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
