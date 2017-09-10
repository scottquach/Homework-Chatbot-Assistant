package com.scottquach.homeworkchatbotassistant;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Scott Quach on 9/10/2017.
 */

public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public RecyclerChatAdapter() {
        
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{

        public TextView messageViewSent;

        public SentViewHolder(View itemView) {
            super(itemView);
            messageViewSent = itemView.findViewById(R.id.view_message_sent);
        }
    }

    public class ReceivedViewHolder extends RecyclerView.ViewHolder {

        public TextView messageViewReceived;

        public ReceivedViewHolder(View itemView) {
            super(itemView);
            messageViewReceived = itemView.findViewById(R.id.view_message_received);
        }
    }
}
