package com.busparser;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FileManager {

    File file;

    public void download(String urlString, String path) throws MalformedURLException, IOException {
        String fullpath = path + urlString.substring(urlString.lastIndexOf('/') + 1, urlString.length());
        file = new File(fullpath);
        URL url = new URL(urlString);
        FileUtils.copyURLToFile(url, file);
    }

    public void cleanUp() {
        if (file != null) {
            file.delete();
        }
    }

    public File getFile() {
        return file;
    }
}
