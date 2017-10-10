package com.scottquach.homeworkchatbotassistant.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scottquach.homeworkchatbotassistant.MessageType;
import com.scottquach.homeworkchatbotassistant.R;
import com.scottquach.homeworkchatbotassistant.models.MessageModel;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Scott Quach on 9/10/2017.
 */

public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageModel> messageModels;
    private Context context;

    public RecyclerChatAdapter(List<MessageModel> messageModels,Context context) {
        this.context = context;
        this.messageModels = messageModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MessageType.SENT:
                View view = LayoutInflater.from(context).inflate(R.layout.row_message_sent, parent, false);
                return new SentViewHolder(view);
            case MessageType.RECEIVED:
                View view1 = LayoutInflater.from(context).inflate(R.layout.row_message_received, parent, false);
                return new ReceivedViewHolder(view1);
            default:
                View view2 = LayoutInflater.from(context).inflate(R.layout.row_message_sent, parent, false);
                return new SentViewHolder(view2);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case MessageType.SENT:
                ((SentViewHolder) holder).messageViewSent.setText(messageModels.get(position).getMessage());
                break;
            case MessageType.RECEIVED:
                ((ReceivedViewHolder) holder).messageViewReceived.setText(messageModels.get(position).getMessage());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch ((int) messageModels.get(position).getType()) {
            case MessageType.SENT:
                return MessageType.SENT;
            case MessageType.RECEIVED:
                return MessageType.RECEIVED;
            default:
                Timber.d("couldn't get view type, resorting to default");
                return MessageType.SENT;
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public void addMessage(MessageModel model) {
        this.messageModels.add(model);
        notifyItemInserted(getItemCount() - 1);
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
