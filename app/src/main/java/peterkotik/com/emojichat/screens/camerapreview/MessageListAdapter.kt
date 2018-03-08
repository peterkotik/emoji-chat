package peterkotik.com.emojichat.screens.camerapreview

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import peterkotik.com.emojichat.R
import peterkotik.com.emojichat.models.EmojiMessage

class MessageListAdapter(private var dataset: List<EmojiMessage>) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    fun setDataset(dataset: List<EmojiMessage>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MessageListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.emoji_message_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataset[position].message
        holder.imageView.setImageBitmap(dataset[position].bitmap)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cardView: CardView
        var frameLayout: FrameLayout
        var textView: TextView
        var imageView: ImageView

        init {
            cardView = v.findViewById(R.id.cardview)
            frameLayout = v.findViewById(R.id.content_container)
            textView = v.findViewById(R.id.message_text)
            imageView = v.findViewById(R.id.message_bitmap)

            cardView.setOnClickListener {
                if (textView.visibility == View.VISIBLE) {
                    textView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                }
            }
        }
    }

    companion object {

        private val layoutId = R.layout.emoji_message_item
    }
}
