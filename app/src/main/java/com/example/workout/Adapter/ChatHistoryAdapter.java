package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Domain.ChatHistory;
import com.example.myapplication.R;

import java.util.List;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {
    private List<ChatHistory> historyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChatHistory history);
    }

    public ChatHistoryAdapter(List<ChatHistory> historyList, OnItemClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public void updateList(List<ChatHistory> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView previewTextView;
        private TextView dateTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            previewTextView = itemView.findViewById(R.id.previewTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(historyList.get(position));
                }
            });
        }

        void bind(ChatHistory history) {
            previewTextView.setText(history.getPreview());
            dateTextView.setText(history.getFormattedDate());
        }
    }
}

