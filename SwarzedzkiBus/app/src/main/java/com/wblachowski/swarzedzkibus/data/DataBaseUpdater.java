package com.wblachowski.swarzedzkibus.data;

import android.content.Context;

import com.wblachowski.swarzedzkibus.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Created by wblachowski on 2/25/2018.
 */

public class DataBaseUpdater {

    Context myContext;

    public DataBaseUpdater(Context myContext) {
        this.myContext = myContext;
    }

    public boolean isUpdateAvailable() {
        Calendar lastUpdateTime = getLastUpdateTime();
        Calendar currentDbTime = getCurrentDbTime();
        boolean upd = lastUpdateTime.before(currentDbTime);
        return lastUpdateTime.before(currentDbTime);
    }

    public boolean update() {
        try {
            URL website = new URL(myContext.getResources().getString(R.string.database_url));
            File targetFile = new File(MainDataBaseHelper.getInstance(myContext).getDbPath());
            try (InputStream in = website.openStream(); OutputStream outStream = new FileOutputStream(targetFile)) {
                copyStream(in, outStream);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Calendar getLastUpdateTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = SettingsDataBaseHelper.getInstance(null).getLastUpdateString();
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            return cal;
        }
        return cal;
    }

    private Calendar getCurrentDbTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = downloadDbDate();
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            return cal;
        }
        return cal;
    }

    private String downloadDbDate() {
        try {
            URL website = new URL(myContext.getResources().getString(R.string.database_update_url));
            try (InputStream in = website.openStream()) {
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";
                return result;
            }
        } catch (Exception ex) {
            return "";
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024*1024*5];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
