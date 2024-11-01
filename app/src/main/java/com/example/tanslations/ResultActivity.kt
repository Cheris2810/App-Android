package com.example.tanslations // Đảm bảo rằng tên package đúng chính tả

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.translations.api.ApiService
import com.example.translations.api.Vocabulary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

@Suppress("DEPRECATION")
class ResultActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    private lateinit var additionalDataTextView: TextView
    private lateinit var wordTextView: TextView
    private lateinit var pronunciationTextView: TextView
    private lateinit var definitionTextView: TextView
    private lateinit var translationTextView: TextView
    private lateinit var textToSpeech: TextToSpeech

    private val REQUEST_CODE = 100 // Mã yêu cầu cho quyền

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Kiểm tra quyền truy cập bộ nhớ
        checkPermissions()

        // Khởi tạo các TextView
        resultTextView = findViewById(R.id.resultTextView)
        additionalDataTextView = findViewById(R.id.additionalDataTextView)

        // Khởi tạo TextToSpeech
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale("zh", "CN")
                textToSpeech.setSpeechRate(0.9f)
                textToSpeech.setPitch(1.0f)
            }
        }

        // Nhận ký tự đã được dự đoán từ MainActivity qua Intent
        val predictedCharacter = intent.getStringExtra("PREDICTED_CHARACTER")

        // Hiển thị ký tự dự đoán
        resultTextView.text = predictedCharacter ?: "No result"

        // Nếu có ký tự dự đoán, gọi API để lấy dữ liệu từ bảng tuvung
        predictedCharacter?.let {
            fetchDataFromTuvung(it)
        } ?: Toast.makeText(this, "Không nhận diện được ký tự", Toast.LENGTH_SHORT).show()
    }

    // Kiểm tra quyền truy cập
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Quyền đã được cấp
            } else {
                // Quyền bị từ chối
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hàm hiển thị thông tin từ Vocabulary
    private fun displayVocabularyDetails(vocabulary: Vocabulary) {
        wordTextView.text = vocabulary.voc_word
        pronunciationTextView.text = vocabulary.voc_pronunciation
        definitionTextView.text = vocabulary.voc_definition
        translationTextView.text = vocabulary.voc_translation
    }

    // Phương thức xử lý sự kiện khi nút phát âm mới được nhấn (onNewPronounceButtonClicked)
    fun onNewPronounceButtonClicked(view: View) {
        val examplePinyin = additionalDataTextView.text.toString()
        if (examplePinyin.isNotEmpty()) {
            playAudio(examplePinyin)
        } else {
            Toast.makeText(this, "Không có Pinyin để phát âm.", Toast.LENGTH_SHORT).show()
        }
    }

    // Hàm gọi API để lấy dữ liệu từ bảng tuvung
    @SuppressLint("SetTextI18n")
    private fun fetchDataFromTuvung(character: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.getVocabulary().enqueue(object : Callback<List<Vocabulary>> {
            override fun onResponse(call: Call<List<Vocabulary>>, response: Response<List<Vocabulary>>) {
                if (response.isSuccessful) {
                    val vocabularyList = response.body()
                    vocabularyList?.let { vocabList ->
                        val matchingVocabulary = vocabList.find { it.voc_word == character }
                        if (matchingVocabulary != null) {
                            updateUIWithVocabulary(matchingVocabulary)
                        } else {
                            runOnUiThread {
                                additionalDataTextView.text = "Không tìm thấy dữ liệu cho ký tự: $character"
                                Log.d("Truy vấn CSDL", "Không tìm thấy dữ liệu cho ký tự: $character")
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        additionalDataTextView.text = "Không thể lấy dữ liệu từ máy chủ."
                        Log.d("Truy vấn CSDL", "Phản hồi không thành công: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<List<Vocabulary>>, t: Throwable) {
                runOnUiThread {
                    additionalDataTextView.text = "Gọi API thất bại: ${t.message}"
                    Log.d("Truy vấn CSDL", "Gọi API thất bại: ${t.message}")
                }
            }
        })
    }

    // Phương thức cập nhật giao diện người dùng
    private fun updateUIWithVocabulary(matchingVocabulary: Vocabulary) {
        additionalDataTextView.text = ""
        val pronunciationSpannable = SpannableString(" ${matchingVocabulary.voc_pronunciation}\n\n")
        val pronunciationIcon = ContextCompat.getDrawable(this, R.drawable.ic_volume_up)

        if (pronunciationIcon != null) {
            pronunciationIcon.setBounds(0, 0, pronunciationIcon.intrinsicWidth, pronunciationIcon.intrinsicHeight)
            val pronunciationClickSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    playAudio(matchingVocabulary.voc_pronunciation)
                }
            }
            pronunciationSpannable.setSpan(pronunciationClickSpan, 0, pronunciationSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            pronunciationSpannable.setSpan(ImageSpan(pronunciationIcon), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        additionalDataTextView.append(pronunciationSpannable)
        val stringBuilder = StringBuilder()
        stringBuilder.append("Từ: ${matchingVocabulary.voc_word}\n\n")
            .append("Dịch nghĩa: ${matchingVocabulary.voc_translation}\n\n")
            .append("Từ loại: ${matchingVocabulary.voc_definition}\n\n")
            .append("Ví dụ: ${matchingVocabulary.voc_example}\n")

        additionalDataTextView.append(stringBuilder.toString())

        val examplePinyin = matchingVocabulary.voc_example_pinyin
        val examplePinyinSpannable = SpannableString(" Phiên âm ví dụ: $examplePinyin\n\n")
        val examplePinyinIcon = ContextCompat.getDrawable(this, R.drawable.ic_volume_up)

        if (examplePinyinIcon != null) {
            examplePinyinIcon.setBounds(0, 0, examplePinyinIcon.intrinsicWidth, examplePinyinIcon.intrinsicHeight)
            val examplePinyinClickSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    playAudio(examplePinyin)
                }
            }
            examplePinyinSpannable.setSpan(examplePinyinClickSpan, 14, examplePinyinSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            examplePinyinSpannable.setSpan(ImageSpan(examplePinyinIcon), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        additionalDataTextView.append(examplePinyinSpannable)

        // Chuyển dòng này xuống dưới cùng
        additionalDataTextView.append("Nét bút: ${matchingVocabulary.voc_video}\n")

        additionalDataTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    // Phương thức phát âm
    private fun playAudio(text: String) {
        if (::textToSpeech.isInitialized) {
            val params = Bundle()
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
    // Phương thức xử lý sự kiện khi nút Back được nhấn
    fun onBackButtonClicked(view: View) {
        finish() // Trở về MainActivity
    }

}
