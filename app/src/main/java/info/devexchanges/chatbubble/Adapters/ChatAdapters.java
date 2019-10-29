package info.devexchanges.chatbubble.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.devexchanges.chatbubble.Data.ChatModel;
import info.devexchanges.chatbubble.R;

public class ChatAdapters extends RecyclerView.Adapter<ChatAdapters.MyViewHolder> {

    LayoutInflater inflator;
    Context context;
    private List<ChatModel> mDataset;
    View v;

    public ChatAdapters(Context context , List<ChatModel> myDataset) {
        inflator = LayoutInflater.from(context);
        mDataset = myDataset;
        this.context = context;
    }

    @Override
    public ChatAdapters.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        v = inflator.inflate(R.layout.chat_layout, parent, false);
        ChatAdapters.MyViewHolder holder = new ChatAdapters.MyViewHolder(v, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ChatAdapters.MyViewHolder holder, final int position) {
        holder.MediaShared.setVisibility(View.GONE);
        holder.MessgeBox.setText(mDataset.get(position).getMessage());
        if(mDataset.get(position).getMedial()!=null) {
            holder.MediaShared.setImageBitmap(mDataset.get(position).getMedial());
            holder.MediaShared.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        Context context;
        public TextView MessgeBox;
        public ImageView MediaShared;

        public MyViewHolder(View itemView, final Context context) {
            super(itemView);
            this.context = context;
            MessgeBox = v.findViewById(R.id.edt_MessageBox);
            MediaShared = v.findViewById(R.id.media_shared);
        }
    }
}
