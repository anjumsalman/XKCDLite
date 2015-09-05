package com.stellerapps.xkcdlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fav.db";
    private static final String DB_TABLE = "favTable";
    private static final String DB_CTABLE = "countTable";
    private static final String DB_STABLE = "saveTable";


    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_IMGURL = "imgurl";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_xkcd_TABLE = "CREATE TABLE " + DB_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NUMBER + " INTEGER,"
                + KEY_TITLE + " TEXT," + KEY_IMGURL + " TEXT"+ ")";
        db.execSQL(CREATE_xkcd_TABLE);
        String CREATE_count_TABLE = "CREATE TABLE " + DB_CTABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NUMBER + " INTEGER )";
        db.execSQL(CREATE_count_TABLE);
        String CREATE_save_TABLE = "CREATE TABLE " + DB_STABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NUMBER + " INTEGER," + KEY_TITLE+" TEXT )";
        db.execSQL(CREATE_save_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    /* ------DB_TABLE------ */
    public void addXKCD(XKCD xkcd) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NUMBER, xkcd.getXkcdNumber());
        values.put(KEY_TITLE, xkcd.getXkcdTitle());
        values.put(KEY_IMGURL, xkcd.getXkcdImgUrl());

        db.insert(DB_TABLE, null, values);
        db.close();
    }

    public XKCD getxkcd(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE, new String[] { KEY_ID,
                        KEY_NUMBER,KEY_TITLE, KEY_IMGURL,}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        XKCD xkcd = new XKCD(Integer.parseInt(cursor.getString(1)),cursor.getString(2),cursor.getString(3));
        return xkcd;
    }

     public List<XKCD> getAllxkcd() {

        List<XKCD> xkcdList = new ArrayList<XKCD>();
        String selectQuery = "SELECT  * FROM " + DB_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                XKCD xkcd = new XKCD();
                xkcd.setXkcdNumber(Integer.parseInt(cursor.getString(1)));
                xkcd.setXkcdTitle(cursor.getString(2));
                xkcd.setXkcdImgUrl(cursor.getString(3));
                xkcdList.add(xkcd);
            } while (cursor.moveToNext());
        }

        return xkcdList;
    }

    public void deletexkcd(XKCD xkcd) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, KEY_TITLE + " = ?",
                new String[] { String.valueOf(xkcd.getXkcdTitle()) });
        db.close();
    }

    public void deletexkcd(int number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, KEY_NUMBER + " = ?",
                new String[]{String.valueOf(number)});
        db.close();
    }

    public boolean isPresent(int number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ DB_TABLE +" WHERE "+ KEY_NUMBER + "=?",new String[]{String.valueOf(number)});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    /* ------DB_CTABLE------ */
    public void addCount(int number){
        if(!isPresentCTable(number)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(KEY_NUMBER, number);

            db.insert(DB_CTABLE, null, values);
            db.close();

            Log.d("CTABLE","Count Added!");
        }
    }

    public boolean isPresentCTable(int number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ DB_CTABLE +" WHERE "+ KEY_NUMBER + "=?",new String[]{String.valueOf(number)});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public int getCount(){
        String selectQuery = "SELECT  * FROM " + DB_CTABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }

    /* ------DB_STABLE------ */

    public void addSave(int number, String title){
        if(!isPresentSTable(number)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(KEY_NUMBER, number);
            values.put(KEY_TITLE, title);

            db.insert(DB_STABLE, null, values);
            db.close();

            Log.d("STABLE","Save Added!");
        }
    }

    public void deleteSave(int number){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_STABLE, KEY_NUMBER + " = ?",
                new String[]{String.valueOf(number)});
        db.close();
    }

    public boolean isPresentSTable(int number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ DB_STABLE +" WHERE "+ KEY_NUMBER + "=?",new String[]{String.valueOf(number)});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }


}