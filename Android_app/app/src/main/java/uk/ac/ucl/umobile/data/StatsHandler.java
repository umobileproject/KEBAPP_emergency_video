package uk.ac.ucl.umobile.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ucl.umobile.utils.G;

/**
 * Created by srenevic on 24/08/17.
 *
 */

public class StatsHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UbiCdnstats";

    // Contacts table name
    private static final String TABLE_CONTENT = "stats";

    // Contacts Table Columns names
    private static final String m_btStatus = "btstatus";
    private static final String m_btDiscoveries = "btdiscoveries";
    private static final String m_btConnections = "btconnections";
    private static final String m_dStatus = "status";
    private static final String m_hsSSID = "ssid";
    private static final String m_discoveries = "discoveries";
    private static final String m_connections = "connections";
    private static final String m_hsClients = "clients";
    private static final String m_linkStatus = "linkStatus";


    public StatsHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTENT + "("
                + "id" + " INT PRIMARY KEY NOT NULL, "
                + m_btStatus + " TEXT,"
                + m_btDiscoveries + " INT,"
                + m_btConnections + " INT,"
                + m_dStatus + " TEXT,"
                + m_hsSSID + " TEXT, "
                + m_discoveries + " INT,"
                + m_connections + " INT, "
                + m_hsClients + " INT,"
                + m_linkStatus + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        ContentValues values = new ContentValues();
        values.put("id", "1");
        db.insert(TABLE_CONTENT, null, values);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);

        // Create tables again
        onCreate(db);
    }

    public int reset()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_btStatus, "");
        values.put(m_btDiscoveries, 0);
        values.put(m_btConnections, 0);
        values.put(m_dStatus, "");
        values.put(m_hsSSID, "");
        values.put(m_discoveries, 0);
        values.put(m_connections, 0);
        values.put(m_hsClients, 0);
        values.put(m_linkStatus, "");

        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});
        // updating row
        return result;
    }

    public String getBtStatus(){

        String m_btStatus="";
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                m_btStatus = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return m_btStatus;
    }


    public int setBtStatus(String btStatus)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_btStatus, btStatus);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});
        G.Log("setBtStatus "+btStatus +" "+result);

        // updating row
        return result;
    }

    public int getBtConnections(){

        int conn=0;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                conn = cursor.getInt(3);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return conn;
    }


    public int setBtConnections(int btConnections)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_btConnections, btConnections);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});

        // updating row
        return result;
    }




    public int getBtDiscoveries(){
        int conn=0;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                conn = cursor.getInt(2);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return conn;
    }

    public int setBtDiscoveries(int btDiscoveries)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_btDiscoveries, btDiscoveries);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});

        // updating row
        return result;
    }



    public String getHsSSID(){
        String m_btStatus="";
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                m_btStatus = cursor.getString(5);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return m_btStatus;
    }

    public int setHsSSID(String SSID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_hsSSID, SSID);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});
        // updating row
        return result;
    }

    public int setDiscoveryStatus(String st)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_dStatus, st);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});
        // updating row
        return result;
    }

    public String getDiscoveryStatus()
    {
        String m_btStatus="";
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                m_btStatus = cursor.getString(4);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return m_btStatus;
    }

    public int getDiscoveries(){
        int conn=0;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                conn = cursor.getInt(6);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return conn;
    }

    public int setDiscoveries(int discoveries)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_discoveries, discoveries);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});

        // updating row
        return result;
    }

    public int getConnections(){
        int conn=0;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                conn = cursor.getInt(7);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return conn;
    }

    public int setConnections(int connections)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_connections, connections);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});

        // updating row
        return result;
    }

    public int getHsClients(){
        int conn=0;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                conn = cursor.getInt(8);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return conn;
    }

    public int setHsClients(int clients)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_hsClients, clients);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});

        // updating row
        return result;
    }

    public String getLinkStatus(){
        String m_btStatus="";
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT + " WHERE id=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                m_btStatus = cursor.getString(9);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Closing database connection
        // return contact list
        return m_btStatus;
    }

    public int setLinkStatus(String status)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(m_linkStatus, status);
        int result = db.update(TABLE_CONTENT, values, "id= ?",
                new String[] {String.valueOf(1)});

        // updating row
        return result;
    }




}
