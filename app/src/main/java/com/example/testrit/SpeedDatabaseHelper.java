package com.example.testrit;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SpeedDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SpeedReader.db";
    private static final String TABLE_SPEED_NAME = "speedTable";
    private static final String SUM_SPEED_COLUMN_NAME = "sum_speed";
    private static final String COUNT_SPEED_COLUMN_NAME = "count";
    private static final String PATH_COLUMN_NAME = "path";
    private static final String TYPE_HUD_COLUMN_NAME = "type_hud";
    private static final String TYPE_SPEEDOMETR_COLUMN_NAME = "type_speedometr";

    private static final String CREATE_COMMAND_SPEED_TABLE = "create table "+TABLE_SPEED_NAME+" ("
            + SUM_SPEED_COLUMN_NAME +" double ,"
            + COUNT_SPEED_COLUMN_NAME +" integer, "
            + PATH_COLUMN_NAME + " integer, "
            + TYPE_HUD_COLUMN_NAME + " integer, "
            + TYPE_SPEEDOMETR_COLUMN_NAME +" integer );";

    private static final String DELETE_COMMAND_SPEED_TABLE = "DROP TABLE IF EXISTS " + TABLE_SPEED_NAME;
    public SpeedDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COMMAND_SPEED_TABLE);
        if(getCount(db)==0){
            insertSpeedData(db);
        }

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_COMMAND_SPEED_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    private long getCount(SQLiteDatabase db){
        long count = DatabaseUtils.queryNumEntries(db, TABLE_SPEED_NAME);
        return count;
    }
    public void insertSpeedData(SQLiteDatabase db){
        String insertCommand = "INSERT INTO " + TABLE_SPEED_NAME + " (" + SUM_SPEED_COLUMN_NAME + ", " + COUNT_SPEED_COLUMN_NAME + ", " + PATH_COLUMN_NAME + "," + TYPE_HUD_COLUMN_NAME + ", " + TYPE_SPEEDOMETR_COLUMN_NAME + ")" +
                "VALUES (0,0,0,0,0);";
        db.execSQL(insertCommand);
    }
    public SaveDataFromDB getSaveData(SQLiteDatabase db) {
        SaveDataFromDB saveData = null;
        Cursor c = db.query(TABLE_SPEED_NAME, null, null, null, null, null, null);
        while (c.moveToNext()) {
            saveData = new SaveDataFromDB(c.getDouble(0),c.getInt(1),c.getInt(2),c.getInt(3)==1,c.getInt(4)==1);
        }
        c.close();
        return saveData;
    }
    public void saveSpeedData(SQLiteDatabase db,SaveDataFromDB saveData){
        int rot = saveData.isRotate()?1:0;
        int spdType = saveData.isSpdType()?1:0;
        String updateCommand = "Update " + TABLE_SPEED_NAME + " set "
                + SUM_SPEED_COLUMN_NAME + " = " + saveData.getSpeed()
                +", "+ COUNT_SPEED_COLUMN_NAME + " = "+ saveData.getCountSpeed()
                +", "+ PATH_COLUMN_NAME + " = "+ saveData.getPath()
                +", "+ TYPE_HUD_COLUMN_NAME + " = "+ rot
                +", "+ TYPE_SPEEDOMETR_COLUMN_NAME + " = " + spdType;
        Log.v("tagSave","="+spdType);
        db.execSQL(updateCommand);
    }

}
