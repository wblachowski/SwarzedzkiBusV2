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

    public static void main(String[] args) {
        new BusParser().begin();
    }

    public void begin() {
        try {
            java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
            loadProperties();
            webParser = new WebParser();
            fileManager = new FileManager();
            pdfManager = new PDFmanager();
            /*Map<String, String> linksToBuses = webParser.retrieveLinks(urlToBuses, ".*lista.htm");
            for (Map.Entry<String, String> entry : linksToBuses.entrySet()) {
                Map<String, String> linksToStops = webParser.retrieveLinks(entry.getKey(), ".*\\.pdf");
                for (Map.Entry<String, String> stopEntry : linksToStops.entrySet()) {
                    System.out.println(stopEntry.getKey() + " " + stopEntry.getValue());
                    //if(stopEntry.getKey().endsWith("S4_S007_1.pdf"))
                        handlePDF(stopEntry.getKey(), stopEntry.getValue());
                }
                System.out.println();
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            fileManager.cleanUp();
        }
    }

}
