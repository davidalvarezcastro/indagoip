package com.example.indagoip.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.indagoip.modelo.RequestsContract;


public class DBSqliteHelper extends SQLiteOpenHelper {

    // DDL SQL statements
    public static final String sqlCreateContactos = "CREATE TABLE " + RequestsContract.Request.TABLE_NAME + " ("
            + RequestsContract.Request.COLUMN_NAME_ID + " INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + RequestsContract.Request.COLUMN_NAME_TIPO + " INTEGER NOT NULL DEFAULT " + RequestsContract.Request.TIPO_REQUEST_HOST + ","
            + RequestsContract.Request.COLUMN_NAME_FECHA + " DATE  NULL,"
            + RequestsContract.Request.COLUMN_NAME_URL + " VARCHAR(200)  NULL)";

    /**
     * Constructor
     *
     * @param context context
     * @param name name
     * @param factory factory
     * @param version version
     */
    public DBSqliteHelper(Context context, String name,
                          CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    /**
     * On create database.
     *
     * @param db database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creación de la tablas que gestiona las peticiones (tabla vacía)
        db.execSQL(sqlCreateContactos);

        // Insert data examples (fake data)
        db.execSQL("INSERT INTO " + RequestsContract.Request.TABLE_NAME + " (" + RequestsContract.Request.COLUMN_NAME_FECHA + ", "+  RequestsContract.Request.COLUMN_NAME_URL + ") "
                + "VALUES ('2021-06-03 15:00:00', 'www.ejemplo.log.es')");
    }

    /**
     * On upgrade database.
     *
     * @param db database
     * @param previousVersion previous version number
     * @param newVersion new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int previousVersion,
                          int newVersion) {
        // NOTE: easy solution dropping tables and creating again

        // Remove tables...
        db.execSQL("DROP TABLE IF EXISTS Contactos");
        // Create new versions... (only create tables again in this solution)
        db.execSQL(sqlCreateContactos);
    }
}
