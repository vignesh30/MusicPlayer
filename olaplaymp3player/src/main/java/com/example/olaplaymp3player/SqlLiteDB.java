package com.example.olaplaymp3player;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SqlLiteDB {
    myDbHelper myhelper;
    public SqlLiteDB(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    public Cursor getPlayLists()
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String query = "select * from PlayLists";
        Cursor c= db.rawQuery(query, null);
        return c;
    }

    public void addToPlayLists(String img, String tit, String art, String url){
        deleteFromPlayLists(img);
        SQLiteDatabase db=myhelper.getWritableDatabase();
        String ROW1 = "insert into PlayLists (image,title, artists, url) values ('"+img+"','"+tit+"','"+art+"','"+url+"')";
        db.execSQL(ROW1);
    }

    public void deleteFromPlayLists(String image)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String ROW1 = "delete from PlayLists where image ='"+image+"'";
        db.execSQL(ROW1);
    }

    public Cursor getHistory()
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String query = "select * from History";
        Cursor c= db.rawQuery(query, null);
        return c;
    }

    public void addToHistory(String img, String tit, String art, String url){
        SQLiteDatabase db=myhelper.getWritableDatabase();
        String ROW1 = "insert into History (image,title, artists, url) values ('"+img+"','"+tit+"','"+art+"','"+url+"')";
        db.execSQL(ROW1);
    }


    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "OlaPlay";    // Database Name
        private static final String TABLE_NAME = "PlayLists";   // Table Name
        private static final int DATABASE_Version = 1;  // Database Version

        private static final String CREATE_TABLE = "CREATE TABLE PlayLists (image TEXT,title TEXT, artists TEXT, url TEXT)";
        private static final String CREATE_TABLE_history = "CREATE TABLE History (image TEXT,title TEXT, artists TEXT, url TEXT)";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
                db.execSQL(CREATE_TABLE_history);
            } catch (Exception e) {

            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {

                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {

            }
        }
    }
}