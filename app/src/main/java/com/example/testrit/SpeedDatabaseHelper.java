package com.example.testrit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SpeedDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SpeedReader.db";
    private static final String TABLE_SPEED_NAME = "speedTable";
    private static final String ID_SPEED_COLUMN_NAME = "id";
    private static final String SPEED_COLUMN_NAME = "speed";
    private static final String TABLE_PATH_NAME = "pathTable";
    private static final String ID_PATH_COLUMN_NAME = "id";
    private static final String PATH_COLUMN_NAME = "path";

    private static final String CREATE_COMMAND_SPEED_TABLE = "create table "+TABLE_SPEED_NAME+" ("
            + ID_SPEED_COLUMN_NAME+" integer primary key autoincrement,"
            + SPEED_COLUMN_NAME +" float "+");";
    private static final String CREATE_COMMAND_PATH_TALBE = "create table "+TABLE_PATH_NAME+" ("
            + ID_PATH_COLUMN_NAME+" integer primary key autoincrement, "
            + PATH_COLUMN_NAME +" float "+");";
    private static final String DELETE_COMMAND_SPEED_TABLE = "DROP TABLE IF EXISTS " + TABLE_SPEED_NAME;
    private static final String DELETE_COMMAND_PATH_TABLE = "DROP TABLE IF EXISTS " + TABLE_PATH_NAME;
    public SpeedDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COMMAND_SPEED_TABLE);
        db.execSQL(CREATE_COMMAND_PATH_TALBE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_COMMAND_SPEED_TABLE);
        db.execSQL(DELETE_COMMAND_PATH_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public void ClearSpeedTable(SQLiteDatabase db){
        db.delete(TABLE_SPEED_NAME, null, null);
    }
    public void insertSpeedData(SQLiteDatabase db, List<Float> floatList){
        StringBuilder insertCommand = new StringBuilder("INSERT INTO " + TABLE_SPEED_NAME + " (" + SPEED_COLUMN_NAME + ")" +
                "VALUES ");
        for (int i=0;i<floatList.size();i++){
            insertCommand.append("(").append(floatList.get(i)).append(")");
            if (i!=floatList.size()-1){
                insertCommand.append(",");
            }else{
                insertCommand.append(";");
            }
        }
        db.execSQL(insertCommand.toString());
    }
    public List<Float> getSpeedList(SQLiteDatabase db) {
        List<Float> list = new ArrayList<>();
        Cursor c = db.query(TABLE_SPEED_NAME, null, null, null, null, null, null);
        int speedColIndex = c.getColumnIndex(SPEED_COLUMN_NAME);
        while (c.moveToNext()) {
            list.add(c.getFloat(speedColIndex));
        }
        c.close();
        return list;
    }

    public void updatePathData(SQLiteDatabase db, float path){
        String updateCommand = "Update " + TABLE_PATH_NAME + " set " + PATH_COLUMN_NAME + " = " +
                path + " Where "+ID_PATH_COLUMN_NAME +" = "+ "(SELECT max("+ID_PATH_COLUMN_NAME+") FROM "+TABLE_PATH_NAME+")";
        db.execSQL(updateCommand);
    }
    public void newPath(SQLiteDatabase db) {
        float zero = 0;
        String insertCommand = ("INSERT INTO " + TABLE_PATH_NAME + " (" + PATH_COLUMN_NAME + ")" +
                " VALUES ("+zero+")");
        db.execSQL(insertCommand);
    }
    public float getLastPath(SQLiteDatabase db) {
        float lastPath = 0;
        String selectCommand = "SELECT "+ID_PATH_COLUMN_NAME+","+PATH_COLUMN_NAME+" FROM "+TABLE_PATH_NAME +" ORDER BY id DESC LIMIT 1";
        Cursor c = db.rawQuery(selectCommand,null);
        int pathColIndex = c.getColumnIndex(PATH_COLUMN_NAME);
        if (c.moveToFirst()){
            do{
                lastPath = c.getFloat(pathColIndex);
            }while (c.moveToNext());

        }else{
            newPath(db);
        }
        c.close();
        return lastPath;
    }
}
