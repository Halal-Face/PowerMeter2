package com.halalface.powermeter2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MasterDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DataBaseHelper";
    private static final String COL2 = "name";
    private static final String COL1 = "ID";
    private  final String TABLE_NAME;
    private Context context;

    public MasterDbHelper(Context context, String name) {
        super(context, name, null, 1);
        TABLE_NAME = name;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 +" TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean addData(String item){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME +" WHERE " + COL2 + " = '" + item + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.moveToNext()){
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return (result ==-1)? false :true;

    }

    //return iterator for all the data
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemID(String item){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + item + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public boolean updateItem(String newItem, int id, String oldItem){
        SQLiteDatabase db = this.getWritableDatabase();

        String queryCheck = "SELECT " + COL2 + " FROM " + TABLE_NAME +" WHERE " + COL2 + " = '" + newItem + "'";
        Cursor data = db.rawQuery(queryCheck, null);
        if(data.moveToNext()){
            return false;
        }

        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newItem +"' WHERE " + COL1 + " = '" +
                id + "' AND " + COL2 + " = '" + oldItem + "'";
        db.execSQL(query);
        return true;
    }
    public void deleteItem(int id, String item){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " +
                 COL1 + " = '" + id +"'" + " AND " + COL2 +
                " = '" + item + "'";
        db.execSQL(query);
        //Log.d(TAG, "QUERY DELETE: " + query);
    }

}
