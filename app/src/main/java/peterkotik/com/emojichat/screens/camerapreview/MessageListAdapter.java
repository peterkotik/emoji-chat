package peterkotik.com.emojichat.screens.camerapreview;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import peterkotik.com.emojichat.R;
import peterkotik.com.emojichat.models.EmojiMessage;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private static final int layoutId = R.layout.emoji_message_item;

    List<EmojiMessage> dataset;

    public MessageListAdapter(List<EmojiMessage> dataset) {
        this.dataset = dataset;
    }

    public void setDataset(List<EmojiMessage> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }

    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.emoji_message_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(dataset.get(position).message);
        holder.imageView.setImageBitmap(dataset.get(position).bitmap);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public FrameLayout frameLayout;
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.cardview);
            frameLayout = v.findViewById(R.id.content_container);
            textView = v.findViewById(R.id.message_text);
            imageView = v.findViewById(R.id.message_bitmap);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textView.getVisibility() == View.VISIBLE) {
                        textView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        textView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
