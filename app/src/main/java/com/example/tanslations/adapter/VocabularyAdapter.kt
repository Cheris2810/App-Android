package com.example.tanslations.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tanslations.R

data class VocabularyItem(
    val word: String,
    val pronunciation: String,
    val translation: String,
    val definition: String,
    val example: String,
    val examplepinyin: String,
    val video: String
)

class VocabularyAdapter(
    private var items: List<VocabularyItem>,
    private val onItemClickListener: (VocabularyItem) -> Unit // Thêm tham số trình lắng nghe nhấp chuột
) : RecyclerView.Adapter<VocabularyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordTextView: TextView = itemView.findViewById(R.id.wordTextView)
        private val pronunciationTextView: TextView = itemView.findViewById(R.id.pronunciationTextView)
        private val translationTextView: TextView = itemView.findViewById(R.id.translationTextView)
        private val definitionTextView: TextView = itemView.findViewById(R.id.definitionTextView)
        private val exampleTextView: TextView = itemView.findViewById(R.id.exampleTextView)
        private val examplepinyinTextView: TextView = itemView.findViewById(R.id.examplepinyinTextView)

        fun bind(vocabularyItem: VocabularyItem) {
            wordTextView.text = vocabularyItem.word
            pronunciationTextView.text = vocabularyItem.pronunciation
            translationTextView.text = vocabularyItem.translation
            definitionTextView.text = vocabularyItem.definition
            exampleTextView.text = vocabularyItem.example
            examplepinyinTextView.text = vocabularyItem.examplepinyin

            // Thiết lập sự kiện nhấp chuột
            itemView.setOnClickListener {
                onItemClickListener(vocabularyItem) // Truyền đối tượng đã nhấp cho trình lắng nghe
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vocabulary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Hàm cập nhật các mục mới
    fun updateItems(newItems: List<VocabularyItem>) {
        items = newItems
        notifyDataSetChanged() // Cập nhật RecyclerView
    }
}
