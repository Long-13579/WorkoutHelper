package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.R;

import java.util.List;

public class AdminExerciseAdapter extends RecyclerView.Adapter<AdminExerciseAdapter.ViewHolder> {
    private List<Exercise> exerciseList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
        void onEditClick(Exercise exercise);
        void onDeleteClick(Exercise exercise);
    }

    public AdminExerciseAdapter(List<Exercise> exerciseList, OnItemClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exerciseList != null ? exerciseList.size() : 0;
    }

    public void updateList(List<Exercise> newList) {
        this.exerciseList = newList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView musclesTextView;
        private TextView editButton;
        private TextView deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            musclesTextView = itemView.findViewById(R.id.musclesTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(exerciseList.get(position));
                }
            });

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClick(exerciseList.get(position));
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(exerciseList.get(position));
                }
            });
        }

        void bind(Exercise exercise) {
            nameTextView.setText(exercise.getName());
            descriptionTextView.setText(exercise.getDescription() != null && exercise.getDescription().length() > 50 
                ? exercise.getDescription().substring(0, 50) + "..." 
                : exercise.getDescription());
            
            StringBuilder muscles = new StringBuilder();
            if (exercise.getTargetMuscle1() != null) {
                muscles.append(exercise.getTargetMuscle1().getDisplayName());
            }
            if (exercise.getTargetMuscle2() != null) {
                if (muscles.length() > 0) muscles.append(", ");
                muscles.append(exercise.getTargetMuscle2().getDisplayName());
            }
            if (exercise.getTargetMuscle3() != null) {
                if (muscles.length() > 0) muscles.append(", ");
                muscles.append(exercise.getTargetMuscle3().getDisplayName());
            }
            musclesTextView.setText(muscles.length() > 0 ? muscles.toString() : "N/A");
        }
    }
}

