package com.example.heydude;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="MyDBName.db";
    public static final String TABLE_NAME="diary";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_DATE="date";
    public static final String COLUMN_ENTRY="entry";

    public DBHelper(Context context) {
        super(context, TABLE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+"(id integer primary key autoincrement, date text, entry text)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertEntry(String date, String entry){

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();

        contentValues.put("date",date);
        contentValues.put("entry",entry);

        long result=db.insert("diary",null, contentValues);

        if(result==1)
            return true;
        else
            return false;
    }

    public ArrayList<String> getAllEntries() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from diary", null);
        res.moveToFirst();

        while(res.isAfterLast()== false ){
            array_list.add(res.getString(res.getColumnIndex(COLUMN_ENTRY)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllDates() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from diary", null);
        res.moveToFirst();

        while(res.isAfterLast()== false ){
            array_list.add(res.getString(res.getColumnIndex(COLUMN_DATE)));
            res.moveToNext();
        }
        return array_list;
    }

}
