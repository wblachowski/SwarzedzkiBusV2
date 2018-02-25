package com.wblachowski.swarzedzkibus.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wblachowski on 2/23/2018.
 */

public class SettingsDataBaseHelper extends SQLiteOpenHelper {

    private static SettingsDataBaseHelper instance;
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.wblachowski.swarzedzkibus/databases/";

    private static String DB_NAME = "serttings.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private SettingsDataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public static synchronized SettingsDataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsDataBaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void createDataBase() {
        myDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        initializeDataBase();
    }

    private void initializeDataBase() {
        initalizeFavourites();
        initializeAboutInfo();
    }

    private void initalizeFavourites() {
        String query = "CREATE TABLE IF NOT EXISTS favourites(stop_id TEXT, direction TEXT, PRIMARY KEY(stop_id,direction))";
        myDataBase.execSQL(query);
    }

    private void initializeAboutInfo() {
        String query = "CREATE TABLE IF NOT EXISTS about(key TEXT PRIMARY KEY, value TEXT)";
        myDataBase.execSQL(query);
        insertDBcreateDate();
    }

    private void insertDBcreateDate() {
        String query = "INSERT INTO about(key,value) VALUES('database_date',?)";
        String date = getDBfileDate();
        myDataBase.execSQL(query, new String[]{date});
    }

    private String getDBfileDate() {
        String path = MainDataBaseHelper.getInstance(myContext).getDbPath();
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        Long lastModified = file.lastModified();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        Date date=new Date();
        date.setTime(lastModified);
        return dateFormat.format(date);
    }

    public void insertIntoFavourites(String stopId, String direction) {
        try {
            String query = "INSERT INTO favourites(stop_id, direction) VALUES(?,?)";
            myDataBase.execSQL(query, new String[]{stopId, direction});
        } catch (Exception ex) {
            //this stop is already in favourites
            return;
        }
    }

    public void deleteFromFavourites(String stopId, String direction) {
        try {
            String query = "DELETE FROM favourites WHERE stop_id = ? AND direction = ?";
            myDataBase.execSQL(query, new String[]{stopId, direction});
        } catch (Exception ex) {
            //log info
            return;
        }
    }

    public String getFavouritesString() {

        try {
            String query = "SELECT GROUP_CONCAT('''' || stop_id || ' ' || direction || '''') as list from favourites";
            Cursor cursor = myDataBase.rawQuery(query, null);
            cursor.moveToFirst();
            String list = cursor.getString(cursor.getColumnIndex("list"));
            cursor.close();
            return list == null ? "" : list;
        } catch (Exception ex) {
            return "";
        }
    }

    public String getLastUpdateString(){
        try{
            String query="SELECT value FROM about WHERE key='database_date'";
            Cursor cursor = myDataBase.rawQuery(query,null);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("value"));
        }
        catch(Exception ex){
            return "";
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
