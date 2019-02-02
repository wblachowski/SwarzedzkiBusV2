package com.busparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WebParser {
    Document doc;

    public Map<String, String> retrieveLinks(String url, String pattern) {
        Map<String, String> linksMap = new LinkedHashMap<>();
        downloadPage(url);
        Elements links = doc.select("a");
        for (Element link : links) {
            if (Pattern.matches(pattern, link.attr("href"))) {
                linksMap.put(getFullLink(url, link.attr("href")), link.text());
            }
        }
        return linksMap;
    }

    public Map<String, String> retrieveLeftLinks(String url) {
        return retrieveTabularLinks(url, 0);
    }

    public Map<String, String> retrieveRightLinks(String url) {
        return retrieveTabularLinks(url, 1);
    }

    private Map<String, String> retrieveTabularLinks(String url, int column) {
        Map<String, String> linksMap = new LinkedHashMap<>();
        downloadPage(url);
        Elements rows = doc.select("tr");
        for (Element row : rows) {
            Elements cells = row.select("td");
            cells.removeIf(cell -> !hasCorrectWidth(cell));
            if (cells.size() > column) {
                Elements links = cells.get(column).select("a");
                links.removeIf(link -> !link.attr("href").contains(".pdf"));
                for (Element link : links) {
                    if (!link.text().isEmpty()) {
                        linksMap.put(getFullLink(url, link.attr("href")), link.text());
                    }
                }
            }
        }
        return linksMap;
    }

    public String getBusName(String url) {
        String busname = "";
        downloadPage(url);
        String html = doc.outerHtml();
        int index = html.indexOf("LINIA ");
        if (index >= 0) {
            index = index + "LINIA ".length();
            while (index < html.length() && Character.isLetterOrDigit(html.charAt(index))) {
                busname += html.charAt(index);
                index++;
            }
        }
        return busname;
    }

    private void downloadPage(String urlString) {
        try {
            if (urlString.contains("S8lista")) {
                doc = Jsoup.parse(new URL(urlString).openStream(), "unicode", urlString);
            } else {
                doc = Jsoup.connect(urlString).get();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String getFullLink(String url, String link) {
        if (isValidUrl(link)) {
            return link;
        }
        String full = url.substring(0, url.lastIndexOf('/') + 1);
        full += link;
        return full;
    }

    private boolean isValidUrl(String link) {
        try {
            new URL(link).toURI();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean hasCorrectWidth(Element element) {
        try {
            return Integer.parseInt(element.attr("width").replace("%", "")) >= 30;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
