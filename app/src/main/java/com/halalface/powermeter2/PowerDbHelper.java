package com.halalface.powermeter2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

public class PowerDbHelper extends SQLiteOpenHelper {
    private final String TAG = "Power Database";
    private final String COL5 = "CHANGELOG";
    private final String COL4 = "NOTES";
    private final String COL3 = "DATE";
    private final String COL2 = "POWER";
    private final String COL1 = "ID";
    private final String TABLE_NAME;
    private Context context;

    public PowerDbHelper(Context context, String name) {
        super(context, name, null, 1);
            TABLE_NAME = "_" + name;

            //TABLE_NAME = name;

        Log.d(TAG, "NAME: "+TABLE_NAME);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " INT, " +
                COL3 + " INT, " +
                COL4 + " TEXT, "+
                COL5 + " TEXT)";
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
            String updateQuery = "UPDATE " + TABLE_NAME + " SET " +
                    COL2 + " = '" + newPower +"' AND " +
                    COL4 + " = 'No Notes.'" + " WHERE " +
                    COL3 + " = '" + date + "' AND " +
                    COL2 + " = '" + oldPower + "'";
            db.execSQL(updateQuery);
            return true;
        }
        else{
            contentValues.put(COL2, item);
            contentValues.put(COL3, date);
            contentValues.put(COL4, "No Notes.");
            long result = db.insert(TABLE_NAME, null, contentValues);
            return (result ==-1)? false :true;
        }
    }
    public boolean addData(int item, int date, String notes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME +" WHERE " + COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.moveToNext()){
            int oldPower = data.getInt(0);
            int newPower = oldPower + item;
            db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                    COL2 + " = '" + newPower + "' WHERE " +
                    COL3 + " = '" + date +"'");
            if(!notes.isEmpty()||!notes.matches("")) {
                db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                        COL2 + " = '" + newPower + "' WHERE " +
                        COL3 + " = '" + date + "'");
                db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                        COL4 + " = '" + notes + "' WHERE " +
                        COL3 + " = '" + date + "'");

            }
            Log.d(TAG, "QUERY UPDATE Add: ");
            return true;
        }
        else{
            contentValues.put(COL2, item);
            contentValues.put(COL3, date);
            if(notes.isEmpty()||notes.matches("")){
                contentValues.put(COL4, "No Notes.");
            }else{
                contentValues.put(COL4, notes);
            }
            Log.d(TAG, "QUERY Add: Added " + item + " " + date + " "+ notes);
            long result = db.insert(TABLE_NAME, null, contentValues);
            return (result ==-1)? false :true;
        }
    }

    public boolean updateChangeLog(int weight, int rep, int set, int date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String query = "SELECT " + COL5 + " FROM " + TABLE_NAME +" WHERE " + COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.moveToNext()) {
            String changelog = data.getString(0);
            if(changelog==null){
                db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                        COL5 + " = '" + " Weight: "+ weight + " Rep: " + rep + " Set: "+ set + "\n" + "' WHERE " +
                        COL3 + " = '" + date + "'");
            }
            else{
                db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                        COL5 + " = '" + changelog + " Weight: "+ weight + " Rep: " + rep + " Set: "+ set + "\n" + "' WHERE " +
                        COL3 + " = '" + date + "'");
            }
            Log.d(TAG, "Change Log updated");
            return true;
        }
        return false;
    }
    public boolean updateChangeLog(int new_power, int date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String query = "SELECT " + COL5 + " FROM " + TABLE_NAME +" WHERE " + COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.moveToNext()) {
            String changelog = data.getString(0);
            if(changelog==null){
                db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                        COL5 + " = '" + " UPpdate: "+ new_power + "\n" + "' WHERE " +
                        COL3 + " = '" + date + "'");
            }
            else{
                db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                        COL5 + " = '" + changelog + " UPDATE: "+ new_power + "\n" + "' WHERE " +
                        COL3 + " = '" + date + "'");
            }
            Log.d(TAG, "Change Log updated");
            return true;
        }
        return false;
    }
    public String getChangeLog(int date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String query = "SELECT " + COL5 + " FROM " + TABLE_NAME +" WHERE " + COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.moveToNext()) {
            String changelog = data.getString(0);
           return changelog;
        }else{
            return "Nothing found. ";
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

    //update via ID
    public void updateItem(int newItem, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newItem +"' WHERE " + COL1 + " = '" + id +"'";
        db.execSQL(query);
        Log.d(TAG, "QUERY UPDATE POWER VIA DATE: " + query);

    }

    //update notes
    public boolean updateItem(String newNotes, int date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL4 +
                " = '" + newNotes +"' WHERE " + COL3 + " = '" + date +"'";
        db.execSQL(query);
        Log.d(TAG, "QUERY UPDATE Notes: " + query);

        return  true;
    }
    public boolean updateItem(int old_date, int new_date, String newNotes){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL4 +
                " = '" + newNotes +"' WHERE " + COL3 + " = '" + old_date +"'");

        db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                COL3 + " = '" + new_date +
                "' WHERE " + COL3 + " = '" + old_date +"'");



        return  true;
    }

    //update power and date
    public boolean updateItem(int newPower, int old_date, int new_date){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryCheck = "SELECT " + COL2 + " FROM " + TABLE_NAME +" WHERE " + COL3 + " = '" + old_date + "'";
        Cursor data = db.rawQuery(queryCheck, null);
        if(data.moveToNext()){
            db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                    COL2 + " = '" + newPower +
                    "' WHERE " + COL3 + " = '" + old_date +"'");

            db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                    COL3 + " = '" + new_date +
                    "' WHERE " + COL3 + " = '" + old_date +"'");

            return true;
        }
        return false;
    }

    //update everything
    public boolean updateItem(int new_power, int old_date, int new_date, String new_notes){
        SQLiteDatabase db = this.getWritableDatabase();

        String queryCheck = "SELECT " + COL2 + " FROM " + TABLE_NAME +" WHERE " + COL3 + " = '" + old_date + "'";
        Cursor data = db.rawQuery(queryCheck, null);
        if(data.moveToNext()){
            //Update Power

//            Log.d(TAG, "QUERY UPDATE ALL: " + query);
            db.execSQL("UPDATE " + TABLE_NAME + " SET " +
                    COL2 + " = '" + new_power +"' WHERE " +
                    COL3 + " = '" + old_date +"'");

            //Update new Note if it isn't Empty
            if(!new_notes.matches("")||!new_notes.isEmpty()){
                Log.d(TAG,"UPDATE " + TABLE_NAME + " SET "+
                        COL4 + " = '" + new_notes + "' WHERE " +
                        COL3 + " = '" + old_date + "'" );

                db.execSQL( "UPDATE " + TABLE_NAME + " SET "+
                        COL4 + " = '" + new_notes + "' WHERE " +
                        COL3 + " = '" + old_date + "'" );
            }
            //Update Date
            Log.d(TAG,"UPDATE " + TABLE_NAME + " SET " +
                    COL3 + " = '" + new_date + "' WHERE "+
                    COL2 + " = '" + new_power +"'");
            db.execSQL( "UPDATE " + TABLE_NAME + " SET " +
                    COL3 + " = '" + new_date + "' WHERE "+
                    COL3 + " = '" + old_date +"'");
            return true;
        }
        return false;
    }

    //delete via ID
    public void deleteItem(int id, int item){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " +
                COL1 + " = '" + id +"'" + " AND " + COL2 +
                " = '" + item + "'";
        db.execSQL(query);
        //Log.d(TAG, "QUERY DELETE: " + query);
    }

    //delete via date
    public void deleteItem(int date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " +
                COL3 + " = '" + date+"'";
        db.execSQL(query);
        //Log.d(TAG, "QUERY DELETE: " + query);
    }

    public ArrayList<Integer> getXData(){
        //Query all x data and insert into an ArrayList
        ArrayList<Integer> xNewData = new ArrayList<Integer>();
        //SELECT name,somedata FROM my_table ORDER BY name
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME + " ORDER BY " + COL3 + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            xNewData.add(cursor.getInt(cursor.getColumnIndex(COL1)));
        }
        cursor.close();
        //Can optimize this with Binary Tree?
        Collections.sort(xNewData, Collections.reverseOrder());
        return xNewData;
    }
    public ArrayList<Integer> getYData(){
//        //Query all y data and insert into an ArrayList
        ArrayList<Integer> yNewData = new ArrayList<>();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME + " ORDER BY " + COL3 + " DESC";
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
    public void exportDB() {

        PowerDbHelper mPowerDbHelper = new PowerDbHelper(context, TABLE_NAME);
        File exportDir = new File(Environment.getExternalStorageDirectory(), "/Power_Meter");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, TABLE_NAME+"_Export.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = mPowerDbHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2), curCSV.getString(3) , curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }
    }

    public String getTABLE_NAME(){
        return TABLE_NAME;
    }

}
