package com.busparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WebParser {
    Document doc;

    public Map<String,String> retrieveLinks(String url, String pattern){
        Map<String,String> linksMap = new HashMap<String, String>();
        downloadPage(url);
        Elements links = doc.select("a");
        for(Element link : links){
            if(Pattern.matches(pattern,link.attr("href"))){
                linksMap.put(getFullLink(url,link.attr("href")),link.html());
            }
        }
        return linksMap;
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
        String full = url.substring(0,url.lastIndexOf('/')+1);
        full+=link;
        return full;
    }
}
