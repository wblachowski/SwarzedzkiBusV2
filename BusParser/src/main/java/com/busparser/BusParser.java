package com.busparser;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Properties;

public class BusParser {

    String urlToBuses;
    String dbPath;
    String dbFile;
    String dbTimeFile;
    WebParser webParser;
    FileManager fileManager;
    PdfManager pdfManager;
    DataBaseManager dataBaseManager;

    public static void main(String[] args) {
        new BusParser().begin();
    }

    public void begin() {
        try {
            initialize();
            Map<String, String> linksToBuses = webParser.retrieveLinks(urlToBuses, ".*lista.htm");
            for (Map.Entry<String, String> entry : linksToBuses.entrySet()) {
                Map<String, String> linksLeftStops = webParser.retrieveLeftLinks(entry.getKey());
                Map<String, String> linksRightStops = webParser.retrieveRightLinks(entry.getKey());
                String busName = webParser.getBusName(entry.getKey());
                dataBaseManager.insertBus(busName);
                for (Map.Entry<String, String> stopEntry : linksLeftStops.entrySet()) {
                    System.out.println("LEFT: " + stopEntry.getKey() + " " + stopEntry.getValue());
                    handlePDF(stopEntry.getKey(), stopEntry.getValue());
                    dataBaseManager.insertStop(stopEntry.getKey(), stopEntry.getValue());
                }
                for (Map.Entry<String, String> stopEntry : linksRightStops.entrySet()) {
                    System.out.println("RIGHT: " + stopEntry.getKey() + " " + stopEntry.getValue());
                    handlePDF(stopEntry.getKey(), stopEntry.getValue());
                    dataBaseManager.insertStop(stopEntry.getKey(), stopEntry.getValue());
                }
                dataBaseManager.insertRoute(busName, linksLeftStops);
                dataBaseManager.insertRoute(busName, linksRightStops);
                System.out.println();
            }
            dataBaseManager.setFinishTime();

            updateIfNeeded();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialize() {
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
        loadProperties();
        webParser = new WebParser();
        fileManager = new FileManager();
        pdfManager = new PdfManager();
        dataBaseManager = new DataBaseManager();
        dataBaseManager.createNewDatabase();
    }

    private void loadProperties() {
        try {
            Properties prop = new Properties();
            InputStream input = getClass().getResourceAsStream("/config.properties");
            prop.load(input);
            urlToBuses = prop.getProperty("urlToBuses");
            dbPath = prop.getProperty("databasePath");
            dbFile = prop.getProperty("databaseFile");
            dbTimeFile = prop.getProperty("databaseTimeFile");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handlePDF(String url, String stopName) {
        try {
            fileManager.download(url, "");
            pdfManager.parse(fileManager.getFile());
            dataBaseManager.insertRemarks(url, pdfManager.getRemarks());
            dataBaseManager.insertTimes(url, pdfManager.getColumn1(), 0);
            dataBaseManager.insertTimes(url, pdfManager.getColumn2(), 1);
            dataBaseManager.insertTimes(url, pdfManager.getColumn3(), 2);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            fileManager.cleanUp();
        }
    }

    private void updateIfNeeded() {
        if (isUpdateNeeded()) {
            Path newDbPath = new File(dbFile).toPath();
            Path newTimePath = new File(dbTimeFile).toPath();
            Path toDbPath = Paths.get(dbPath + dbFile);
            Path toTimePath = Paths.get(dbPath + dbTimeFile);
            try {
                Files.copy(newDbPath, toDbPath, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(newTimePath, toTimePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdateNeeded() {
        try {
            File newFile = new File(dbFile);
            File oldFile = new File(dbPath + dbFile);
            return !FileUtils.contentEquals(newFile, oldFile);
        } catch (Exception ex) {
            return false;
        }
    }
}
