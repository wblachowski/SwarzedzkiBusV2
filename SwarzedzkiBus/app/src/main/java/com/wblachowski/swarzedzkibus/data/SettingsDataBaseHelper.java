package com.wblachowski.swarzedzkibus.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

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
        new File(DB_PATH+DB_NAME).delete();
        boolean dbExistring = new File(DB_PATH + DB_NAME).exists();
        myDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        if (!dbExistring) {
            initializeDataBase();
        }
    }

    private void initializeDataBase() {
        initalizeFavourites();
    }

    private void initalizeFavourites() {
        String query = "CREATE TABLE IF NOT EXISTS favourites(stop_id TEXT, direction TEXT, PRIMARY KEY(stop_id,direction))";
        myDataBase.execSQL(query);
    }

    public void insertIntoFavourites(String stopId, String direction) {
        try{
        String query = "INSERT INTO favourites(stop_id, direction) VALUES(?,?)";
        myDataBase.execSQL(query,new String[]{stopId,direction});}
        catch (Exception ex){
            //this stop is already in favourites
            return;
        }
    }

    public void deleteFromFavourites(String stopId, String direction){
        String query="DELETE FROM favourites WHERE stop_id = ? AND diretion = ?";
        myDataBase.execSQL(query,new String[]{stopId,direction});
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

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
