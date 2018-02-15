package com.busparser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class BusParser {

    String urlToBuses;

    public static void main(String[] args) {
        new BusParser().parse();
    }

    public void parse() {
        try {
            LoadProperties();
            WebParser webParser = new WebParser();
            Map<String, String> linksToBuses = webParser.retrieveLinks(urlToBuses, ".*lista.htm");
            for (Map.Entry<String, String> entry : linksToBuses.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                System.out.println(key + " " + value);
            }
            /*
            File file = new File("401.pdf");
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
            /*System.out.println("SOBOTY");
            System.out.println(stripper.getTextForRegion("column2"));
            System.out.println("NIEDZIELE I ŚWIĘTA");
            System.out.println(stripper.getTextForRegion("column3"));*/

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void LoadProperties() {
        try {
            Properties prop = new Properties();
            InputStream input = getClass().getResourceAsStream("/config.properties");
            prop.load(input);
            urlToBuses = prop.getProperty("urlToBuses");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
