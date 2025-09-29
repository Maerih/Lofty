package com.sciatafrica.lofty;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class ListProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.sciatafrica.Lofty.listProvider";
    static  final String URL = "content://" + PROVIDER_NAME + "/listItems";
    static final Uri CONTENT_URL = Uri.parse(URL);
    static final String _ID = "_id";
    static final String LISTITEM = "listitem";
    private static HashMap<String,String> LISTITEMS_PROJECTION_MAP;
    static final int LISTITEMS = 1;
    static final int LISTITEM_ID=2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"listitems",LISTITEMS);
        uriMatcher.addURI(PROVIDER_NAME,"listitems/#",LISTITEM_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "ListDB";
    static final String LISTITEM_TABLE_NAME = "listitems";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = " CREATE TABLE " + LISTITEM_TABLE_NAME +
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "listitem TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_DB_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + LISTITEM_TABLE_NAME);
            onCreate(db);
        }
    }
    @Override
    public boolean onCreate(){
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db==null) ? false:true;
    }
    @Override
    public Uri insert(Uri uri, ContentValues values){
        long rowID = db.insert(LISTITEM_TABLE_NAME,"",values);

        if (rowID >0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL,rowID);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }
        throw new SQLException("FAILED TO ADD A RECORD INTO"+ uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,String selection, String[] selectionArgs, String sortOrder){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(LISTITEM_TABLE_NAME);
        switch(uriMatcher.match(uri)){
            case LISTITEMS:
                qb.setProjectionMap(LISTITEMS_PROJECTION_MAP);
                break;
            case LISTITEM_ID:
                qb.appendWhere(_ID + "="+ uri.getPathSegments().get(1));
                break;
        }
        if(sortOrder==null || sortOrder==""){
            sortOrder = LISTITEM;
        }
        Cursor c = qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection,String[] selectionArgs){
        int count = 0;
        switch (uriMatcher.match(uri)){
            case LISTITEMS:
                count = db.delete(LISTITEM_TABLE_NAME,selection,selectionArgs);
                break;
            case LISTITEM_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(LISTITEM_TABLE_NAME,_ID+"="+ id + (!TextUtils.isEmpty(selection)? "AND ("+ selection+')':""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unkown URI"+ uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,String selection, String[] selectionArgs){
        int count =0;
        switch (uriMatcher.match(uri)){
            case LISTITEMS:
                count = db.update(LISTITEM_TABLE_NAME,values,selection,selectionArgs);
                break;
            case LISTITEM_ID:
                count = db.update(LISTITEM_TABLE_NAME,values,_ID + "="+ uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection)? " AND (" +selection + ')':""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unkown URI"+ uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
    @Override
    public String getType(Uri uri){
        switch (uriMatcher.match(uri)){
            case LISTITEMS:
                return "vnd.android.cursor.dir/vnd.flipcortex.listitems";
            case LISTITEM_ID:
                return "vnd.android.cursor.item/vnd.flipcortex.listitems";
            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
    }

}
