package com.halalface.powermeter2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class PowerDbHelper extends SQLiteOpenHelper {
    private final String TAG = "Power Database";
    private final String COL3 = "DATE";
    private final String COL2 = "POWER";
    private final String COL1 = "ID";
    private final String TABLE_NAME;
    private Context context;

    public PowerDbHelper(Context context, String name) {
        super(context, name, null, 1);
        TABLE_NAME = name;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 +" INT, " + COL3 + " INT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(int item, int date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME +" WHERE " + COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.moveToNext()){
            int oldPower = data.getInt(0);
            int newPower = oldPower + item;
            String updateQuery = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                    " = '" + newPower +"' WHERE " + COL3 + " = '" +
                    date + "' AND " + COL2 + " = '" + oldPower + "'";
            db.execSQL(updateQuery);
            return true;
        }
        else{
            contentValues.put(COL2, item);
            contentValues.put(COL3, date);
            long result = db.insert(TABLE_NAME, null, contentValues);
            return (result ==-1)? false :true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemID(int date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public void updateItem(int newItem, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newItem +"' WHERE " + COL1 + " = '" + id +"'";
        db.execSQL(query);
    }
    public boolean updateItem(int newPower, int old_date, int new_date){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryCheck = "SELECT " + COL2 + " FROM " + TABLE_NAME +" WHERE " + COL3 + " = '" + old_date + "'";
        Cursor data = db.rawQuery(queryCheck, null);
        if(data.moveToNext()){
            String query = "UPDATE " + TABLE_NAME + " SET " +
                    COL2 + " = '" + newPower + "' AND " +
                    COL3 + " = '" + new_date +
                    "' WHERE " + COL3 + " = '" + old_date +"'";
            db.execSQL(query);
            return true;
        }
        return false;
    }


    public void deleteItem(int id, int item){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " +
                COL1 + " = '" + id +"'" + " AND " + COL2 +
                " = '" + item + "'";
        db.execSQL(query);
        //Log.d(TAG, "QUERY DELETE: " + query);
    }
    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " +
                COL1 + " = '" + id+"'";
        db.execSQL(query);
        //Log.d(TAG, "QUERY DELETE: " + query);
    }

    public ArrayList<Integer> getXData(){
        //Query all x data and insert into an ArrayList
        ArrayList<Integer> xNewData = new ArrayList<Integer>();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            xNewData.add(cursor.getInt(cursor.getColumnIndex(COL1)));
        }
        cursor.close();
        return xNewData;
    }
    public ArrayList<Integer> getYData(){
        //Query all y data and insert into an ArrayList
        ArrayList<Integer> yNewData = new ArrayList<>();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            yNewData.add(cursor.getInt(cursor.getColumnIndex(COL2)));
        }
        cursor.close();
        return yNewData;
    }

    public void updateDbName(String newName){
        SQLiteDatabase db = this.getWritableDatabase();
        PowerDbHelper newDb = new PowerDbHelper(context, newName);
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        while(data.moveToNext()){
            newDb.addData(data.getInt(1), data.getInt(2) );
        }
    }
    public String getTABLE_NAME(){
        return TABLE_NAME;
    }

}
