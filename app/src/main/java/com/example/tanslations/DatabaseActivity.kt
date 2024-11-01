package com.example.tanslations

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class DatabaseActivity : AppCompatActivity() {
    private lateinit var additionalDataTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        additionalDataTextView = findViewById(R.id.additionalDataTextView)

        // Nhận dữ liệu từ intent
        val predictedCharacter = intent.getStringExtra("PREDICTED_CHARACTER")

        // Lấy dữ liệu từ cơ sở dữ liệu
        fetchAdditionalData(predictedCharacter)
    }

    @SuppressLint("Range", "SetTextI18n")
    private fun fetchAdditionalData(predictedCharacter: String?) {
        if (predictedCharacter != null) {
            val dbHelper = DatabaseHelper(this)
            val database = dbHelper.readableDatabase

            // Truy vấn dữ liệu từ bảng tuvung
            val cursor = database.rawQuery(
                "SELECT * FROM tuvung WHERE voc_word LIKE ?",
                arrayOf("%$predictedCharacter%")
            )

            if (cursor.count > 0) {
                val stringBuilder = StringBuilder()
                while (cursor.moveToNext()) {
                    val vocId = cursor.getString(cursor.getColumnIndex("voc_id"))
                    val vocWord = cursor.getString(cursor.getColumnIndex("voc_word"))
                    val vocDefinition = cursor.getString(cursor.getColumnIndex("voc_definition"))
                    val vocPronunciation =
                        cursor.getString(cursor.getColumnIndex("voc_pronunciation"))
                    val vocTranslation = cursor.getString(cursor.getColumnIndex("voc_translation"))
                    val vocExample = cursor.getString(cursor.getColumnIndex("voc_example"))
                    val vocExamplePinyin =
                        cursor.getString(cursor.getColumnIndex("voc_example_pinyin"))

                    // Thêm dữ liệu vào stringBuilder
                    stringBuilder.append("ID: $vocId\n")
                        .append("Word: $vocWord\n")
                        .append("Definition: $vocDefinition\n")
                        .append("Pronunciation: $vocPronunciation\n")
                        .append("Translation: $vocTranslation\n")
                        .append("Example: $vocExample\n")
                        .append("Example Pinyin: $vocExamplePinyin\n\n")
                }
                additionalDataTextView.text = stringBuilder.toString()
            } else {
                additionalDataTextView.text = "No data found for: $predictedCharacter"
            }

            cursor.close()
            database.close()

        }
    }
}
