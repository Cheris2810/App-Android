package com.example.tanslations

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

class ResultSearch : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var word1TextView: TextView
    private lateinit var pronunciationTextView: TextView
    private lateinit var wordTextView: TextView
    private lateinit var translationTextView: TextView
    private lateinit var definitionTextView: TextView
    private lateinit var exampleTextView: TextView
    private lateinit var examplepinyinTextView: TextView
    private lateinit var videoTextView: TextView
    private lateinit var tts: TextToSpeech

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary_search)

        // Khởi tạo Text-to-Speech
        tts = TextToSpeech(this, this)

        // Khởi tạo các TextView
        word1TextView = findViewById(R.id.word1TextView)
        pronunciationTextView = findViewById(R.id.pronunciationTextView)
        wordTextView = findViewById(R.id.wordTextView)
        translationTextView = findViewById(R.id.translationTextView)
        definitionTextView = findViewById(R.id.definitionTextView)
        exampleTextView = findViewById(R.id.exampleTextView)
        examplepinyinTextView = findViewById(R.id.examplepinyinTextView)
        videoTextView = findViewById(R.id.videoTextView)

        // Lấy dữ liệu từ Intent
        val word = intent.getStringExtra("WORD") ?: ""
        val pronunciation = intent.getStringExtra("PRONUNCIATION") ?: ""
        val definition = intent.getStringExtra("DEFINITION") ?: ""
        val translation = intent.getStringExtra("TRANSLATION") ?: ""
        val example = intent.getStringExtra("EXAMPLE") ?: ""
        val examplepinyin = intent.getStringExtra("EXAMPLEPINYIN") ?: ""
        val video = intent.getStringExtra("VIDEO") ?: ""

        // Đặt dữ liệu vào các TextView
        word1TextView.text = word
        wordTextView.text = word
        translationTextView.text = translation
        definitionTextView.text = definition
        exampleTextView.text = example
        videoTextView.text = video

        // Thiết lập Spannable với biểu tượng và sự kiện nhấn để phát âm
        setSpannableWithIcon(pronunciationTextView, pronunciation, R.drawable.ic_volume_up)
        setSpannableWithIcon(examplepinyinTextView, examplepinyin, R.drawable.ic_volume_up)
    }

    // Hàm để tạo Spannable kèm biểu tượng và sự kiện nhấn
    private fun setSpannableWithIcon(textView: TextView, text: String, iconResId: Int) {
        // Thêm khoảng trống phía trước cho biểu tượng
        val spannableString = SpannableString("  $text")
        val icon: Drawable? = ContextCompat.getDrawable(this, iconResId)
        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        val imageSpan = ImageSpan(icon!!, ImageSpan.ALIGN_BOTTOM)

        // Đặt biểu tượng ở vị trí đầu tiên (vị trí 0)
        spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        // Thêm ClickableSpan để phát âm khi nhấn vào biểu tượng
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Phát âm đoạn văn bản
                speakOut(text)
            }
        }

        // Đặt sự kiện nhấn vào khoảng trống chứa biểu tượng
        spannableString.setSpan(clickableSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance() // Đảm bảo TextView nhận sự kiện nhấn
    }

    // Hàm phát âm văn bản
    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Hàm xử lý khi nhấn nút quay lại
    fun onBackButtonClicked(view: View) {
        finish() // Kết thúc Activity để quay lại MainActivity
    }

    // Khởi tạo Text-to-Speech
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Thiết lập ngôn ngữ cho TTS
            val result = tts.setLanguage(Locale.US) // Đổi thành ngôn ngữ bạn muốn

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Ngôn ngữ không được hỗ trợ", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Khởi tạo Text-to-Speech thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    // Giải phóng tài nguyên TTS khi Activity bị hủy
    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
