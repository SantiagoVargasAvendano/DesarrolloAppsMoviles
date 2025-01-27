package com.example.reto8;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Reto8.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_URL = "url";
    public static final String CONTACTS_COLUMN_PAS = "prod_serv";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    public static final String CONTACTS_COLUMN_CLASSIF = "classification";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, url text, prod_serv text, classification text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact (String name, String phone, String email, String url,String prod_serv, String classification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("url", url);
        contentValues.put("prod_serv", prod_serv);
        contentValues.put("classification", classification);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String url,String prod_serv, String classification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("url", url);
        contentValues.put("prod_serv", prod_serv);
        contentValues.put("classification", classification);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    @SuppressLint("Range")
    public ArrayList<String> getAllContacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    @SuppressLint("Range")
    public ArrayList<String> filters(String name, Boolean cons , Boolean desa, Boolean fabr) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from contacts";
        if(!name.equals("")){
            query +=  " where name=\"" + name + "\"";
            if(cons) query +=  " and classification=\"consultoría\"";
            if(desa) query +=  " or classification=\"desarrollo a la medida\"";
            if(fabr) query +=  " or classification=\"fábrica de software\"";
        }else{
            if(cons){
                query +=  " where classification=\"consultoría\"";
                if(desa) query +=  " or classification=\"desarrollo a la medida\"";
                if(fabr) query +=  " or classification=\"fábrica de software\"";
            }else{
                if(desa){
                    query +=  " where classification=\"desarrollo a la medida\"";
                    if(fabr) query +=  " or classification=\"fábrica de software\"";
                }else{
                    if(fabr) query +=  " where classification=\"fábrica de software\"";
                }
            }
        }

        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}