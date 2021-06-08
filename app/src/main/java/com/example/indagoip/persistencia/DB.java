package com.example.indagoip.persistencia;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.indagoip.modelo.Request;
import com.example.indagoip.modelo.RequestsContract;


public class DB {

    private static final String TAG = DB.class.getName();

    /** Database name. */
    public static final String DB_EXAMPLE = "DBExercise";

    /**
     * Database instance.
     */
    private static final DB db = new DB();

    /**
     * Constructor.
     */
    private DB() {
    }

    /**
     * Gest instance.
     *
     * @return database instance
     */
    public static DB getInstance() {
        return db;
    }

    /**
     * Insert request and save data.
     *
     * @param context
     * @param data
     */
    public void insertRequest(Context context, Request data) {
        DBSqliteHelper dbSqlite = null;
        SQLiteDatabase dbHelper = null;
        Cursor cursor = null;

        try {
            // se instancia la base de datos
            dbSqlite = new DBSqliteHelper(context, DB_EXAMPLE,
                    null, 1);

            // write mode
            dbHelper = dbSqlite.getWritableDatabase();
            dbHelper.beginTransaction();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            SimpleDateFormat sdf = new SimpleDateFormat(RequestsContract.Request.DATE_FORMAT);
            values.put(RequestsContract.Request.COLUMN_NAME_FECHA, sdf.format(data.getFechaHora()));
            values.put(RequestsContract.Request.COLUMN_NAME_URL, data.getUrl());

            // en caso de especificar el tipo, se añade
            if (data.getTipo() != null) {
                values.put(RequestsContract.Request.COLUMN_NAME_TIPO, data.getTipo());
            }

            // Insert the new row, returning the primary key value of the new row
            dbHelper.insert(RequestsContract.Request.TABLE_NAME, null, values);
            dbHelper.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Error in database insert: " + ex.getLocalizedMessage());
        } finally {
            // se finaliza la transacción y se cierra el cursor
            dbHelper.endTransaction();
            dbHelper.close();
            dbSqlite.close();
        }
    }

    /**
     * Query requests and load data.
     *
     * @param context
     *            current context
     * @param data
     *            current data from database
     */
    public void readRequests(Context context, List<Request> data) {
        DBSqliteHelper dbSqlite = null;
        SQLiteDatabase dbHelper = null;
        Cursor cursor = null;
        try {
            // se instancia la base de datos
            dbSqlite = new DBSqliteHelper(context, DB_EXAMPLE,
                    null, 1);

            // read mode
            dbHelper = dbSqlite.getReadableDatabase();
            dbHelper.beginTransaction();

            // Raw query
            cursor = dbHelper.rawQuery("SELECT " + RequestsContract.Request.COLUMN_NAME_FECHA + ", " + RequestsContract.Request.COLUMN_NAME_URL + ", " + RequestsContract.Request.COLUMN_NAME_TIPO +
                    " FROM " + RequestsContract.Request.TABLE_NAME + " ORDER BY " +  RequestsContract.Request.COLUMN_NAME_FECHA + " DESC", null);

            // Se recorre el cursor y se crean los objetos Request con la información recibida
            if (cursor.moveToFirst()) {

                do {
                    Date fechaHora = new SimpleDateFormat(RequestsContract.Request.DATE_FORMAT).parse(cursor.getString(0));
                    String url = cursor.getString(1);
                    Integer tipo = cursor.getInt(2);
                    data.add(new Request(tipo, fechaHora, url));
                } while (cursor.moveToNext());
            }
            dbHelper.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Error in database access: " + ex.getLocalizedMessage());
        } finally {
            // se finaliza la transacción y se cierra el cursor
            dbHelper.endTransaction();
            cursor.close();
            dbHelper.close();
            dbSqlite.close();
        }
    }
}

