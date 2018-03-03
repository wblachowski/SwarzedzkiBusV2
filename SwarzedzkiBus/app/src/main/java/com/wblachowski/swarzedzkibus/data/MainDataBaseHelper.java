package com.wblachowski.swarzedzkibus.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.wblachowski.swarzedzkibus.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class MainDataBaseHelper extends SQLiteOpenHelper {

    private static MainDataBaseHelper instance;
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.wblachowski.swarzedzkibus/databases/";

    private static String DB_NAME = "buses.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */

    public static synchronized MainDataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MainDataBaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private MainDataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    public String getDbPath() {
        return DB_PATH + DB_NAME;
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    public Cursor getAllBusesCursor() {
        String query = "SELECT bus_name, region_id, regions.name as region_name, routes.id as ROUTE_ID_A ,min(stop_order), stops.name as START_STOP, END_STOP FROM buses_routes join routes on buses_routes.route_id=routes.id join stops on stops.id=routes.stop_id join buses on buses.name=bus_name join regions on regions.id=region_id\n" +
                " join\n" +
                " (SELECT routes.id as ROUTE_ID_B, max(stop_order),name as END_STOP FROM buses_routes join routes on buses_routes.route_id=routes.id join stops on stops.id=routes.stop_id group by routes.id) on ROUTE_ID_A=ROUTE_ID_B\n" +
                " group by routes.id\n" +
                " order by region_id";
        try {
            return myDataBase.rawQuery(query, null);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public Cursor getStopsCursor(String routeId) {
        boolean isTimes = PreferenceManager.getDefaultSharedPreferences(myContext).getBoolean(myContext.getResources().getString(R.string.key_departure_time), true);
        if (isTimes) {
            return getStopsCursorWithTimes(routeId);
        } else {
            return getStopsCursorWithoutTimes(routeId);
        }
    }

    private Cursor getStopsCursorWithoutTimes(String routeId) {
        String query = "SELECT routes.rowid _id, bus_name, routes.id, stop_order, stop_id,stops.name, " +
                " (stop_id || ' ' || lastStop.name) in(" + SettingsDataBaseHelper.getInstance(myContext).getFavouritesString() + ") as favourite, lastStop.name as LAST_STOP  " +
                " from buses_routes join routes on buses_routes.route_id=routes.id join stops on stops.id=stop_id \n" +
                " join (SELECT max(stop_order), stops.name FROM routes JOIN stops ON routes.stop_id=stops.id WHERE routes.id = ?) lastStop\n" +
                " WHERE routes.id = ? order by routes.id, stop_order";
        try {
            return myDataBase.rawQuery(query, new String[]{routeId, routeId});
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private Cursor getStopsCursorWithTimes(String routeId) {
        String query = "SELECT routes.rowid _id, bus_name, routes.id, stop_order, routes.stop_id,stops.name,  (routes.stop_id || ' ' || lastStop.name) in(" + SettingsDataBaseHelper.getInstance(myContext).getFavouritesString() + ") as favourite, lastStop.name as LAST_STOP, nextTime.hour, nextTime.minute   from buses_routes join routes on buses_routes.route_id=routes.id join stops on stops.id=routes.stop_id \n" +
                " join (SELECT max(stop_order), stops.name FROM routes JOIN stops ON routes.stop_id=stops.id WHERE routes.id = @routeId) lastStop\n" +
                " left outer join (  select time_tables.stop_id,type,hour,minute,(hour*60+minute) - (@hour*60+@minute) as diff,\n" +
                "case when (hour*60+minute) - (@hour*60+@minute)>0 then (hour*60+minute) - (@hour*60+@minute) else 24*60-(@hour*60+@minute)+(hour*60+minute) end as diff_abs,\n" +
                "min(case when (hour*60+minute) - (@hour*60+@minute)>0 then (hour*60+minute) - (@hour*60+@minute) else 24*60-(@hour*60+@minute)+(hour*60+minute) end) as minimum\n" +
                " from time_tables join routes on time_tables.stop_id=routes.stop_id\n" +
                " where  routes.id=@routeId and ((type= @todayType and diff>0)or (type=@tommorowType and diff<0))group by time_tables.stop_id ) nextTime on nextTime.stop_id=routes.stop_id\n" +
                " WHERE routes.id = @routeId order by routes.id, stop_order";
        int todayType = getDayType(0);
        int tommorowType = getDayType(1);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        try {
            return myDataBase.rawQuery(query, new String[]{routeId, String.valueOf(currentHour), String.valueOf(currentMinute), String.valueOf(todayType), String.valueOf(tommorowType)});
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private int getDayType(int offset) {
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        dayOfWeek += offset;
        if (dayOfWeek == 8) dayOfWeek = 1;
        if (dayOfWeek >= 2 && dayOfWeek <= 6) {
            return 0;
        } else if (dayOfWeek == 7) {
            return 1;
        } else {
            return 2;
        }
    }

    public Cursor getStopsTimes(String stopId, int type) {
        String query = "SELECT DISTINCT stop_id, type, hour,minute,remark FROM time_tables WHERE stop_id=? AND type=?";
        try {
            return myDataBase.rawQuery(query, new String[]{stopId, new Integer(type).toString()});
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public Cursor getRemarksByStop(String stopId) {
        String query = "SELECT DISTINCT symbol,description FROM remarks WHERE stop_id=?";
        try {
            return myDataBase.rawQuery(query, new String[]{stopId});
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public Cursor getStopsByName(String name){
        boolean isTimes = PreferenceManager.getDefaultSharedPreferences(myContext).getBoolean(myContext.getResources().getString(R.string.key_departure_time), true);
        if (isTimes) {
            return getStopsByNameWithTime(name);
        } else {
            return getStopsByNameWithoutTime(name);
        }
    }

    private Cursor getStopsByNameWithoutTime(String name) {
        String query = "SELECT  routes.id as _id, buses_routes.bus_name, \n" +
                "stops.name as STOP, \n" +
                "replace(replace(replace(replace(replace(replace(replace(replace(replace(lower(stops.name),'ą','a'),'ć','c'),'ę','e'),'ł','l'),'ń','n'),'ó','o'),'ś','s'),'ź','z'),'ż','z') as STOP_ASCII,\n" +
                "lastStops.name as FINAL_STOP, stops.id FROM stops join routes on stops.id=routes.stop_id join buses_routes on buses_routes.route_id=routes.id\n" +
                "join\n" +
                "(SELECT bus_name, stops.name, stops.id, routes.id AS ROUTE_ID, max(routes.stop_order) FROM stops join routes on stops.id=routes.stop_id join buses_routes on buses_routes.route_id=routes.id group by routes.id) lastStops\n" +
                "on routes.id=lastStops.ROUTE_ID\n" +
                "where\n" +
                "stop_ASCII like '%' || replace(replace(replace(replace(replace(replace(replace(replace(replace(lower(?),'ą','a'),'ć','c'),'ę','e'),'ł','l'),'ń','n'),'ó','o'),'ś','s'),'ź','z'),'ż','z') || '%'";
        try {
            return myDataBase.rawQuery(query, new String[]{name});
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
            return null;
        }
    }

    private Cursor getStopsByNameWithTime(String name) {
        String query =" SELECT  routes.id as _id, buses_routes.bus_name, \n" +
                "stops.name as STOP, \n" +
                "replace(replace(replace(replace(replace(replace(replace(replace(replace(lower(stops.name),'ą','a'),'ć','c'),'ę','e'),'ł','l'),'ń','n'),'ó','o'),'ś','s'),'ź','z'),'ż','z') as STOP_ASCII,\n" +
                "lastStops.name as FINAL_STOP, stops.id, nextTime.hour, nextTime.minute FROM stops join routes on stops.id=routes.stop_id join buses_routes on buses_routes.route_id=routes.id\n" +
                "join\n" +
                "(SELECT bus_name, stops.name, stops.id, routes.id AS ROUTE_ID, max(routes.stop_order) FROM stops join routes on stops.id=routes.stop_id join buses_routes on buses_routes.route_id=routes.id group by routes.id) lastStops\n" +
                "on routes.id=lastStops.ROUTE_ID\n" +
                "left outer join(select time_tables.stop_id,type,hour,minute,(hour*60+minute) - (@hour*60+@minute) as diff,\n" +
                "case when (hour*60+minute) - (@hour*60+@minute)>0 then (hour*60+minute) - (@hour*60+@minute) else 24*60-(@hour*60+@minute)+(hour*60+minute) end as diff_abs,\n" +
                "min(case when (hour*60+minute) - (@hour*60+@minute)>0 then (hour*60+minute) - (@hour*60+@minute) else 24*60-(@hour*60+@minute)+(hour*60+minute) end) as minimum\n" +
                "from time_tables \n" +
                "where  ((type= @todayType and diff>0)or (type=@tommorowType and diff<0))group by time_tables.stop_id) nextTime on nextTime.stop_id=stops.id\n" +
                "where\n" +
                "stop_ASCII like '%' || replace(replace(replace(replace(replace(replace(replace(replace(replace(lower(@name),'ą','a'),'ć','c'),'ę','e'),'ł','l'),'ń','n'),'ó','o'),'ś','s'),'ź','z'),'ż','z') || '%'";
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int todayType = getDayType(0);
        int tommorowType = getDayType(1);
        try {
            return myDataBase.rawQuery(query, new String[]{String.valueOf(hour), String.valueOf(minute), String.valueOf(todayType), String.valueOf(tommorowType),name});
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
            return null;
        }
    }

    public Cursor getFavouriteStops() {
        boolean isTimes = PreferenceManager.getDefaultSharedPreferences(myContext).getBoolean(myContext.getResources().getString(R.string.key_departure_time), true);
        if (isTimes) {
            return getFavouriteStopsWithTime();
        } else {
            return getFavouriteStopsWithoutTime();
        }
    }

    private Cursor getFavouriteStopsWithoutTime() {
        String query = "SELECT  routes.id as _id, buses_routes.bus_name, \n" +
                "stops.name as STOP, lastStops.name as FINAL_STOP, stops.id FROM stops join routes on stops.id=routes.stop_id join buses_routes on buses_routes.route_id=routes.id\n" +
                "join\n" +
                "(SELECT bus_name, stops.name, stops.id, routes.id AS ROUTE_ID, max(routes.stop_order) FROM stops join routes on stops.id=routes.stop_id join buses_routes on buses_routes.route_id=routes.id group by routes.id) lastStops\n" +
                "on routes.id=lastStops.ROUTE_ID\n" +
                "WHERE (stops.id || ' ' || FINAL_STOP) in (" +
                SettingsDataBaseHelper.getInstance(myContext).getFavouritesString() + ")";
        try {
            return myDataBase.rawQuery(query, null);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private Cursor getFavouriteStopsWithTime() {
        String query = "SELECT  routes.id as _id, buses_routes.bus_name, \n" +
                "stops.name as STOP, lastStops.name as FINAL_STOP, stops.id, nextTime.hour, nextTime.minute FROM stops join routes on stops.id=routes.stop_id join buses_routes on buses_routes.route_id=routes.id\n" +
                "join\n" +
                "(SELECT bus_name, stops.name, stops.id, routes.id AS ROUTE_ID, max(routes.stop_order) FROM stops join routes on stops.id=routes.stop_id join buses_routes on buses_routes.route_id=routes.id group by routes.id) lastStops\n" +
                "on routes.id=lastStops.ROUTE_ID\n" +
                "left outer join(select time_tables.stop_id,type,hour,minute,(hour*60+minute) - (@hour*60+@minute) as diff,\n" +
                "case when (hour*60+minute) - (@hour*60+@minute)>0 then (hour*60+minute) - (@hour*60+@minute) else 24*60-(@hour*60+@minute)+(hour*60+minute) end as diff_abs,\n" +
                "min(case when (hour*60+minute) - (@hour*60+@minute)>0 then (hour*60+minute) - (@hour*60+@minute) else 24*60-(@hour*60+@minute)+(hour*60+minute) end) as minimum\n" +
                "from time_tables \n" +
                "where  ((type=@todayType and diff>0)or (type=@tommorowType and diff<0))group by time_tables.stop_id) nextTime on nextTime.stop_id=stops.id\n" +
                "WHERE (stops.id || ' ' || FINAL_STOP) in (" +
                SettingsDataBaseHelper.getInstance(myContext).getFavouritesString() + ")";
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int todayType = getDayType(0);
        int tommorowType = getDayType(1);
        try {
            return myDataBase.rawQuery(query, new String[]{String.valueOf(hour), String.valueOf(minute), String.valueOf(todayType), String.valueOf(tommorowType)});
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

}
