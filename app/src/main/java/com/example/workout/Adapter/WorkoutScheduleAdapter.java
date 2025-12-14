package com.example.workout.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.workout.Activity.WorkoutActivity;
import com.example.workout.Domain.Workout;
import com.example.workout.R;
import com.example.workout.databinding.ViewholderWorkoutScheduleBinding;

import java.util.ArrayList;

public class WorkoutScheduleAdapter extends RecyclerView.Adapter<WorkoutScheduleAdapter.Viewholder> {
    private final ArrayList<Workout> list;
    private Context context;
    private final OnItemActionListener actionListener;

    public WorkoutScheduleAdapter(ArrayList<Workout> list, OnItemActionListener listener) {
        this.list = list;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderWorkoutScheduleBinding binding = ViewholderWorkoutScheduleBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Workout workout = list.get(position);

        holder.binding.titleTxt.setText(workout.getExercise().getName());
        holder.binding.planNameTxt.setText("Plan: " + workout.getPlanName());
        holder.binding.numberOfSetTxt.setText(workout.getNumberOfSet() + " sets");

        int resId = context.getResources().getIdentifier(
                workout.getExercise().getImageUrl(), "drawable", context.getPackageName());

        Glide.with(context)
                .load(resId)
                .into(holder.binding.pic);

        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkoutActivity.class);
            intent.putExtra("object", workout);
            context.startActivity(intent);
        });

        holder.binding.btnMore.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, holder.binding.btnMore);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.item_menu, popup.getMenu()); // you'll need to define this menu

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.edit) {
                    actionListener.onEdit(workout);
                    return true;
                } else if (menuItem.getItemId() == R.id.delete) {
                    actionListener.onDelete(workout);
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

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderWorkoutScheduleBinding binding;

        public Viewholder(ViewholderWorkoutScheduleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemActionListener {
        void onEdit(Workout item);
        void onDelete(Workout item);
    }
}
