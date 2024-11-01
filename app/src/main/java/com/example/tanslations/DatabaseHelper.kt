package com.example.tanslations

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "androidapp.db" // Tên cơ sở dữ liệu của bạn
        private const val DATABASE_VERSION = 1 // Phiên bản cơ sở dữ liệu
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tạo bảng tuvung nếu chưa tồn tại
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS tuvung (
                voc_id INTEGER PRIMARY KEY,
                voc_word TEXT,
                voc_definition TEXT,
                voc_pronunciation TEXT,
                voc_translation TEXT,
                voc_example TEXT,
                voc_example_pinyin TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Xóa bảng cũ nếu nó tồn tại
        db.execSQL("DROP TABLE IF EXISTS tuvung")
        onCreate(db) // Tạo bảng mới
    }
}
