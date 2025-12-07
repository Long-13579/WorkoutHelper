package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activity.ExerciseActivity;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Enum.MuscleEnum;
import com.example.myapplication.databinding.ViewholderExerciseSmallBinding;

import java.util.ArrayList;
import java.util.List;

public class SmallExerciseAdapter extends RecyclerView.Adapter<SmallExerciseAdapter.Viewholder> {
    private ArrayList<Exercise> list;
    private Context context;

    public SmallExerciseAdapter(ArrayList<Exercise> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderExerciseSmallBinding binding = ViewholderExerciseSmallBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Exercise item = list.get(position);
        holder.binding.titleTxt.setText(item.getName());
        int resId=context.getResources().getIdentifier(list.get(position).getImageUrl(),"drawable",context.getPackageName());

        // Load image if available
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(resId)
                    .into(holder.binding.pic);
        }

        List<MuscleEnum> muscles = new ArrayList<>();
        if (item.getTargetMuscle1() != null) muscles.add(item.getTargetMuscle1());
        if (item.getTargetMuscle2() != null) muscles.add(item.getTargetMuscle2());
        if (item.getTargetMuscle3() != null) muscles.add(item.getTargetMuscle3());

        StringBuilder muscleTxt = new StringBuilder();
        for (int i = 0; i < muscles.size(); i++) {
            muscleTxt.append(muscles.get(i).getDisplayName());

            if (i < muscles.size() - 1) {
                muscleTxt.append(" • ");
            }
        }

        holder.binding.targetMuscleLayout.setText(muscleTxt.toString());

        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(context, ExerciseActivity.class);
            intent.putExtra("object", item);  // gửi object
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderExerciseSmallBinding binding;

        public Viewholder(ViewholderExerciseSmallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
