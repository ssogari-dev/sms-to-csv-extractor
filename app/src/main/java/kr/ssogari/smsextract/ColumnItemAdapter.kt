package kr.ssogari.smsextract

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ColumnItemAdapter(
    val columnItems: MutableList<ColumnItem>,
    val onItemClick: (ColumnItem) -> Unit
) : RecyclerView.Adapter<ColumnItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_column, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = columnItems[position]
        holder.bind(item, onItemClick)

        // 다크 모드와 라이트 모드에 맞게 배경 색과 텍스트 색을 설정합니다.
        val context = holder.itemView.context
        val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.darkModeCapsuleText))
        } else {
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.lightModeCapsuleText))
        }
    }

    override fun getItemCount(): Int = columnItems.size

    fun addItem(item: ColumnItem) {
        columnItems.add(item)
        notifyItemInserted(columnItems.size - 1)
    }

    fun removeItem(item: ColumnItem): Boolean {
        val position = columnItems.indexOf(item)
        if (position >= 0) {
            columnItems.removeAt(position)
            notifyItemRemoved(position)
            return true
        }
        return false
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = columnItems.removeAt(fromPosition)
        columnItems.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.columnTextView)

        fun bind(item: ColumnItem, onItemClick: (ColumnItem) -> Unit) {
            textView.text = item.name
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
