package com.busparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;

public class WebParser {
    Document doc;

    public Map<String,String> retrieveLinks(String url, String pattern){
        Map<String,String> linksMap = new LinkedHashMap<>();
        downloadPage(url);
        Elements links = doc.select("a");
        for(Element link : links){
            if(Pattern.matches(pattern,link.attr("href"))){
                linksMap.put(getFullLink(url,link.attr("href")),link.text());
            }
        }
        return linksMap;
    }

    public Map<String,String> retrieveLeftLinks(String url,String pattern){
        return retrieveTabularLinks(url,pattern,0);
    }

    public Map<String,String> retrieveRightLinks(String url,String pattern){
        return retrieveTabularLinks(url,pattern,1);
    }

    private Map<String,String> retrieveTabularLinks(String url,String pattern, int column){
        Map<String,String> linksMap=new LinkedHashMap<>();
        downloadPage(url);
        Elements rows = doc.select("tr");
        for(Element row : rows){
            int linkNO=0;
            Elements cells = row.select("td");
            cells.removeIf(cell->{return Integer.parseInt(cell.attr("width").replace("%",""))<30;});
            Elements links = cells.get(column).select("a");
            links.removeIf(link->{return !link.attr("href").contains(".pdf");});
            if(links.size()>0){
                linksMap.put(getFullLink(url,links.get(0).attr("href")),links.get(0).text());
            }
        }
        return linksMap;
    }
    public String getBusName(String url){
        String busname="";
        downloadPage(url);
        String html=doc.outerHtml();
        int index=html.indexOf("LINIA ");
        if(index>=0){
            index=index+"LINIA ".length();
            while(index<html.length() && Character.isLetterOrDigit(html.charAt(index))){
                busname+=html.charAt(index);
                index++;
            }
        }
        return busname;
    }

    private void downloadPage(String urlString) {
        try {
            doc = Jsoup.connect(urlString).get();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private String getFullLink(String url,String link){
        if(isValidUrl(link)){
            return link;
        }
        String full = url.substring(0,url.lastIndexOf('/')+1);
        full+=link;
        return full;
    }

    private boolean isValidUrl(String link){
        try{
            new URL(link).toURI();
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }
}
