package com.busparser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class PDFmanager {

    public void parse(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        Rectangle rect1 = new Rectangle(30, 95, 120, 250);
        Rectangle rect2 = new Rectangle(155, 95, 120, 250);
        Rectangle rect3 = new Rectangle(275, 95, 120, 250);
        stripper.addRegion("column1", rect1);
        stripper.addRegion("column2", rect2);
        stripper.addRegion("column3", rect3);
        PDPage firstPage = (PDPage) document.getPages().get(0);
        stripper.extractRegions(firstPage);

        String column1,column2,column3;

        // System.out.println("DNI ROBOCZE");
        column1=stripper.getTextForRegion("column1");
        //System.out.println(column1);

        // System.out.println("SOBOTY");
        column2=stripper.getTextForRegion("column2");
        //System.out.println(column2);

        //System.out.println("NIEDZIELE I ŚWIĘTA");
        column3=stripper.getTextForRegion("column3");
        //System.out.println(column3);

        String all=column1+column2+column3;
        if(hasAnnotataions(all)){
            System.out.println(all);
        }
        document.close();
    }

    private boolean hasAnnotataions(String text){
        String toCheck = text.replace("\r\n","").toUpperCase().replace("NIE KURSUJE","");
        return Pattern.matches(".*[a-zA-Z]+.*",toCheck);
    }
}
