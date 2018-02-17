package com.busparser;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class BusParser {

    String urlToBuses;
    WebParser webParser;
    FileManager fileManager;
    PDFmanager pdfManager;
    DataBaseManager dataBaseManager;

    public static void main(String[] args) {
        new BusParser().begin();
    }

    public void begin() {
        try {
            initialize();
            Map<String, String> linksToBuses = webParser.retrieveLinks(urlToBuses, ".*lista.htm");
            for (Map.Entry<String, String> entry : linksToBuses.entrySet()) {
                Map<String, String> linksLeftStops = webParser.retrieveLeftLinks(entry.getKey(), ".*\\.pdf");
                Map<String,String> linksRightStops = webParser.retrieveRightLinks(entry.getKey(),".*\\.pdf");
                String busName=webParser.getBusName(entry.getKey());
                dataBaseManager.insertBus(busName);
                for (Map.Entry<String, String> stopEntry : linksLeftStops.entrySet()) {
                    System.out.println("LEFT: " + stopEntry.getKey() + " " + stopEntry.getValue());
                    //if(stopEntry.getKey().endsWith("S4_S007_1.pdf"))
                        handlePDF(stopEntry.getKey(), stopEntry.getValue());
                    dataBaseManager.insertStop(stopEntry.getKey(),stopEntry.getValue());
                }
                for(Map.Entry<String,String> stopEntry : linksRightStops.entrySet()){
                    System.out.println("RIGHT: " + stopEntry.getKey() + " " + stopEntry.getValue());
                    dataBaseManager.insertStop(stopEntry.getKey(),stopEntry.getValue());
                }
                dataBaseManager.insertRoute(busName,linksLeftStops);
                dataBaseManager.insertRoute(busName,linksRightStops);
                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialize(){
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
        loadProperties();
        webParser = new WebParser();
        fileManager = new FileManager();
        pdfManager = new PDFmanager();
        dataBaseManager = new DataBaseManager();
        dataBaseManager.createNewDatabase();
    }

    private void loadProperties() {
        try {
            Properties prop = new Properties();
            InputStream input = getClass().getResourceAsStream("/config.properties");
            prop.load(input);
            urlToBuses = prop.getProperty("urlToBuses");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handlePDF(String url, String stopName) {
        try {
            fileManager.download(url, "");
            pdfManager.parse(fileManager.getFile());
            dataBaseManager.insertRemarks(url,pdfManager.getRemarks());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            fileManager.cleanUp();
        }
    }
}
