package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Domain.BodyTrack;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemBodyTrackBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BodyTrackAdapter extends RecyclerView.Adapter<BodyTrackAdapter.ViewHolder> {

    private final List<BodyTrack> list;
    private final Context context;
    private final OnItemActionListener actionListener;

    public BodyTrackAdapter(Context context, List<BodyTrack> list, OnItemActionListener listener) {
        this.context = context;
        this.list = list;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public BodyTrackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBodyTrackBinding binding = ItemBodyTrackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BodyTrackAdapter.ViewHolder holder, int position) {
        BodyTrack item = list.get(position);
        holder.binding.titleTxt.setText("Weight: " + item.Weight + "kg, Height: " + item.Height + "cm");

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(item.Date);
        holder.binding.dateTxt.setText(formattedDate);

        // Handle 3-dot menu
        holder.binding.btnMore.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, holder.binding.btnMore);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.item_menu, popup.getMenu()); // you'll need to define this menu

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.edit) {
                    actionListener.onEdit(item);
                    return true;
                } else if (menuItem.getItemId() == R.id.delete) {
                    actionListener.onDelete(item);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemBodyTrackBinding binding;

        public ViewHolder(ItemBodyTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Define an interface to handle edit/delete
    public interface OnItemActionListener {
        void onEdit(BodyTrack item);
        void onDelete(BodyTrack item);
    }
}
