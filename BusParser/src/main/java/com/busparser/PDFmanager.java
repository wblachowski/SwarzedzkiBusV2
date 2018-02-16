package com.busparser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.Annotation;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PDFmanager {

    String column1;
    String column2;
    String column3;
    ArrayList<Remark> remarks;

    public void parse(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        Rectangle rect1 = new Rectangle(30, 95, 120, 250);
        Rectangle rect2 = new Rectangle(155, 95, 120, 250);
        Rectangle rect3 = new Rectangle(275, 95, 120, 250);
        Rectangle rectRemarks = new Rectangle(30,350,400,250);
        stripper.addRegion("column1", rect1);
        stripper.addRegion("column2", rect2);
        stripper.addRegion("column3", rect3);
        stripper.addRegion("remarks",rectRemarks);
        PDPage firstPage = document.getPages().get(0);
        stripper.extractRegions(firstPage);


        column1=stripper.getTextForRegion("column1");

        column2=stripper.getTextForRegion("column2");

        column3=stripper.getTextForRegion("column3");

        String remarksString = stripper.getTextForRegion("remarks");

        String all=column1+column2+column3;
        if(hasRemarks(all)){
            resolveRemarks(remarksString);
            System.out.println(all);
        }
        document.close();
    }

    private boolean hasRemarks(String text){
        String toCheck = text.replace("\r\n","").toUpperCase().replace("NIE KURSUJE","");
        return Pattern.matches(".*[a-zA-Z]+.*",toCheck);
    }

    private ArrayList<Remark> resolveRemarks(String text){
        remarks=new ArrayList<Remark>();
        text=text.replace(" / ","/");
        for(int i=0;i<text.length();i++){
            if(isMinus(text,i)){
                String remark="";
                String description="";
                int j=i;
                int y=i;
                while(j>=0 && (text.charAt(j)==' ' || isMinus(text,j)))j--;
                while(j>=0 && text.charAt(j)!=' ' && text.charAt(j)!='\n') {
                    remark = text.charAt(j) + remark;
                    j--;
                }
                while(y<text.length() && !Character.isLetter(text.charAt(y)))y++;
                while(y<text.length() && !isMinus(text,y)){
                    description+=text.charAt(y);
                    y++;
                }
                if(y<text.length() && isMinus(text,y)){
                    description=description.trim();
                    while(!description.equals("") && description.charAt(description.length()-1)!=' '){
                        description=description.substring(0,description.length()-1);
                    }
                }
                description=description.trim();
                description=description.replace(";","");
                if(description.endsWith(","))description=description.substring(0,description.length()-1);
                if(!remark.trim().equals("") && remark.length()<5){
                    remarks.add(new Remark(remark,description));
                }
            }
        }
        return remarks;
    }

    private boolean isMinus(String text, int i){
        if(text.charAt(i)!='-')return false;
        else return !isInBracket(text,i);
    }

    private boolean isInBracket(String text, int i){
        while(i>=0){
            if(text.charAt(i)==')')return false;
            if(text.charAt(i)=='(')return true;
            i--;
        }
        return false;
    }

    public String getColumn1() {
        return column1;
    }

    public String getColumn2() {
        return column2;
    }

    public String getColumn3() {
        return column3;
    }

    public ArrayList<Remark> getRemarks() {
        return remarks;
    }

    private class Remark{
        String title;
        String description;
        public Remark(String title, String description){
            this.title=title;
            this.description=description;
        }

        public String getDescription() {
            return description;
        }

        public String getTitle() {
            return title;
        }
    }

}
