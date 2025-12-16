package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Domain.Set;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ViewholderSetBinding;

import java.util.ArrayList;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.Viewholder> {
    private final ArrayList<Set> list;
    private final Context context;
    private final OnItemActionListener actionListener;

    public SetAdapter(ArrayList<Set> list, Context context, OnItemActionListener listener) {
        this.list = list;
        this.context = context;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public SetAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderSetBinding binding = ViewholderSetBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SetAdapter.Viewholder holder, int position) {
        Set set = list.get(position);

        holder.binding.titleTxt.setText("Set " + (position + 1));
        holder.binding.durationTxt.setText(set.getReps() + " reps | " + set.getWeight() + " kg | Rest: " + set.getRestTime() + " seconds");

        if (set.isFinish()) {
            holder.binding.getRoot().setBackgroundResource(R.drawable.gray_bg);
        } else {
            holder.binding.getRoot().setBackgroundResource(R.drawable.blue_bg);
        }

        // Optional: handle clicks, remove/edit logic here
        holder.binding.btnMore.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.binding.btnMore, Gravity.END);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.item_menu, popup.getMenu()); // you'll need to define this menu

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.edit) {
                    actionListener.onEdit(set);
                    return true;
                } else if (menuItem.getItemId() == R.id.delete) {
                    actionListener.onDelete(set);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        holder.binding.checkBox.setChecked(set.isFinish());
        holder.binding.checkBox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            set.setIsFinish(isChecked);
            actionListener.onFinishChanged(set);
        }));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderSetBinding binding;

        public Viewholder(ViewholderSetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemActionListener {
        void onEdit(Set item);
        void onDelete(Set item);
        void onFinishChanged(Set item);
    }
}
