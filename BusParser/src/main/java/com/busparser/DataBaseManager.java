package com.busparser;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class DataBaseManager {

    String filename;
    String filenameTime;
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
        String sql = "INSERT INTO buses VALUES (?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, busName);
            pstmt.setInt(2, region);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static int routeID = 0;

    public void insertRoute(String busName, Map<String, String> stops) {
        String sql = "INSERT INTO routes VALUES(?,?,?)";
        int i = 0;
        for (Map.Entry<String, String> stopEntry : stops.entrySet()) {
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, routeID);
                pstmt.setInt(2, i++);
                pstmt.setString(3, resolveStopName(stopEntry.getKey()));
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        if (stops.size() > 0) {
            insertBusesRoutes(busName, routeID);
            routeID++;
        }
    }

    private void insertBusesRoutes(String busName, int route_id) {
        String sql = "INSERT INTO buses_routes VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, busName);
            pstmt.setInt(2, route_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setFinishTime(){
        Long time = System.currentTimeMillis();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filenameTime), "utf-8"))) {
            writer.write(time.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertStop(String stopUrl, String stopName) {
        String id = resolveStopName(stopUrl);
        String sql = "INSERT INTO stops VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, stopName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertRemarks(String urlStop, ArrayList<PDFmanager.Remark> remarks) {
        String stopId = resolveStopName(urlStop);
        String sql = "INSERT INTO remarks(stop_id,symbol,description) VALUES(?,?,?)";
        for (PDFmanager.Remark remark : remarks) {
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, stopId);
                pstmt.setString(2, remark.getTitle());
                pstmt.setString(3, remark.getDescription());
                pstmt.executeUpdate();
                System.out.println("REMARK " + remark.getTitle() + " WITH DESCRIPTION " + remark.getDescription() + " INSERTED");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void insertTimes(String urlStop, String times, int type) {
        String stopId = resolveStopName(urlStop);
        for (String line : times.split("\\r?\\n")) {
            String[] numbers = line.split(" ");
            int hour = -1;
            for (String number : numbers) {
                try {
                    int numberInt = Integer.parseInt(number);
                    if (hour < 0) {
                        hour = numberInt;
                    } else {
                        insertTime(stopId, type, hour, numberInt);
                    }
                } catch (NumberFormatException e) {
                    //we have to connect latest time with our remark
                    insertTimeRemark(number);
                }
            }
        }
    }

    private void insertTime(String stopId, int type, int hour, int minute) {
        String sql = "INSERT INTO time_tables(stop_id,type,hour,minute) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stopId);
            pstmt.setInt(2, type);
            pstmt.setInt(3, hour);
            pstmt.setInt(4, minute);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertTimeRemark(String remark) {
        if(remark.toUpperCase().contains("KURSUJE") || remark.toUpperCase().contains("NIE"))return;
        String sql = "UPDATE time_tables SET remark=COALESCE(remark, '') || ? || ' ' WHERE id=(SELECT MAX(id) FROM time_tables)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, remark);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
        initializeBuses();
        initializeStops();
        initializeRoutes();
        initializeBusesRoutes();
        initalizeTimeTables();
        initializeRemarks();
    }

    private void initalizeRegions() {
        String sql = "CREATE TABLE IF NOT EXISTS regions (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL\n"
                + ");";
        createTable(sql);
        populateRegions();
    }

    private void initializeBuses() {
        String sql = "CREATE TABLE IF NOT EXISTS buses (\n"
                + "	name TEXT PRIMARY KEY,\n"
                + "	region_id INTEGER,\n"
                + " FOREIGN KEY(region_id) REFERENCES regions(id)\n"
                + ");";
        createTable(sql);
    }

    private void initializeStops() {
        String sql = "CREATE TABLE IF NOT EXISTS stops (\n"
                + " id TEXT PRIMARY KEY, \n"
                + " name TEXT \n"
                + ");";
        createTable(sql);
    }

    private void initializeBusesRoutes() {
        String sql = "CREATE TABLE IF NOT EXISTS buses_routes (\n"
                + " bus_name TEXT, \n"
                + " route_id INTEGER, \n"
                + " PRIMARY KEY (bus_name,route_id), \n"
                + " FOREIGN KEY(bus_name) REFERENCES buses(name), \n"
                + " FOREIGN KEY(route_id) REFERENCES routes(id) \n"
                + ");";
        createTable(sql);
    }

    private void initializeRoutes() {
        String sql = "CREATE TABLE IF NOT EXISTS routes (\n"
                + " id INTEGER, \n"
                + " stop_order INTEGER, \n"
                + " stop_id INTEGER, \n"
                + " PRIMARY KEY(id,stop_order), \n"
                + " FOREIGN KEY(stop_id) REFERENCES stops(id) \n"
                + ");";
        createTable(sql);
    }

    private void initalizeTimeTables() {
        String sql = "CREATE TABLE IF NOT EXISTS time_tables (\n"
                + " id INTEGER PRIMARY KEY, \n"
                + " stop_id TEXT, \n"
                + " type INTEGER, \n"
                + " hour INTEGER, \n"
                + " minute INTEGER, \n"
                + " remark TEXT, \n"
                + " FOREIGN KEY(stop_id) REFERENCES stops(id) \n"
                + ");";
        createTable(sql);
    }

    private void initializeRemarks() {
        String sql = " CREATE TABLE IF NOT EXISTS remarks (\n"
                + " id INTEGER PRIMARY KEY, \n"
                + " stop_id TEXT, \n"
                + " symbol TEXT, \n"
                + " description TEXT, \n"
                + " FOREIGN KEY(stop_id) REFERENCES stops(id) \n"
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

    private int resolveRegion(String busName) {
        if (busName.startsWith("49")) return 1;
        if (busName.startsWith("48")) return 2;
        return 0;
    }

    private String resolveStopName(String stopUrl) {
        return stopUrl.substring(stopUrl.lastIndexOf("/") + 1, stopUrl.length());
    }

    private void loadProperties() {
        try {
            Properties prop = new Properties();
            InputStream input = getClass().getResourceAsStream("/config.properties");
            prop.load(input);
            filename = prop.getProperty("databaseFile");
            filenameTime = prop.getProperty("databaseTimeFile");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
