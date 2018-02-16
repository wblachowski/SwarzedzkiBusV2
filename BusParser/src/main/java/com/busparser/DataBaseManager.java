package com.busparser;

import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DataBaseManager {

    String filename;
    String url;

    public void createNewDatabase() {
        loadProperties();
        url = "jdbc:sqlite:" + filename;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                initalizeTables();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertBus(String busName) {
        int region = resolveRegion(busName);
        String sql="INSERT INTO buses VALUES (?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, busName);
            pstmt.setInt(2, region);
            pstmt.executeUpdate();
            System.out.println("Bus " + busName + " inserted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertStop(String url, String stopName) {

    }

    private void createTable(String sql) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initalizeTables() {
        initalizeRegions();
        initalizeBuses();
    }

    private void initalizeRegions() {
        String sql = "CREATE TABLE IF NOT EXISTS regions (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL\n"
                + ");";
        createTable(sql);
        populateRegions();
    }

    private void initalizeBuses() {
        String sql = "CREATE TABLE IF NOT EXISTS buses (\n"
                + "	name TEXT PRIMARY KEY,\n"
                + "	region_id INTEGER,\n"
                +" FOREIGN KEY(region_id) REFERENCES regions(id)\n"
                + ");";
        createTable(sql);
    }

    private void populateRegions() {
        String sql = "INSERT INTO regions(id,name) VALUES(?,?)";
        int[] ids = {0, 1, 2};
        String[] names = {"Linie do Poznania", "Linie na terenie Gminy Swarzędz", "Linie międzygminne"};
        for (int i = 0; i < ids.length; i++) {
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ids[i]);
                pstmt.setString(2, names[i]);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int resolveRegion(String busName){
        if(busName.startsWith("49"))return 1;
        if(busName.startsWith("48"))return 2;
        return 0;
    }

    private void loadProperties() {
        try {
            Properties prop = new Properties();
            InputStream input = getClass().getResourceAsStream("/config.properties");
            prop.load(input);
            filename = prop.getProperty("databaseFile");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
