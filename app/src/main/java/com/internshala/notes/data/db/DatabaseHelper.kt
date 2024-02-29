package com.internshala.notes.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// DatabaseHelper.kt
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 1

        // Define table and column names
        const val TABLE_NAME = "notes"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    // Create table query
    private val CREATE_TABLE_QUERY =
        "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT , $COLUMN_TIMESTAMP TEXT)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
}
