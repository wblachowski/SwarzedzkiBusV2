package com.busparser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;

public class BusParser {

    String urlToBuses;
    WebParser webParser;
    FileManager fileManager;

    public static void main(String[] args) {
        new BusParser().begin();
    }

    public void begin() {
        try {
            loadProperties();
            webParser = new WebParser();
            fileManager = new FileManager();
            Map<String, String> linksToBuses = webParser.retrieveLinks(urlToBuses, ".*lista.htm");
            for (Map.Entry<String, String> entry : linksToBuses.entrySet()) {
                Map<String, String> linksToStops = webParser.retrieveLinks(entry.getKey(), ".*\\.pdf");
                for (Map.Entry<String, String> stopEntry : linksToStops.entrySet()) {
                    System.out.println(stopEntry.getKey() + " " + stopEntry.getValue());
                    handlePDF(stopEntry.getKey(), stopEntry.getValue());
                }
            }
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
            parseFile(fileManager.getFile());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            fileManager.cleanUp();
        }
    }

    private void parseFile(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        Rectangle rect1 = new Rectangle(35, 95, 120, 250);
        Rectangle rect2 = new Rectangle(155, 95, 120, 250);
        Rectangle rect3 = new Rectangle(275, 95, 120, 250);
        stripper.addRegion("column1", rect1);
        stripper.addRegion("column2", rect2);
        stripper.addRegion("column3", rect3);
        PDPage firstPage = (PDPage) document.getPages().get(0);
        stripper.extractRegions(firstPage);
        System.out.println("DNI ROBOCZE");
        System.out.println(stripper.getTextForRegion("column1"));
        System.out.println("SOBOTY");
        System.out.println(stripper.getTextForRegion("column2"));
        System.out.println("NIEDZIELE I ŚWIĘTA");
        System.out.println(stripper.getTextForRegion("column3"));
        
        document.close();
    }
}
