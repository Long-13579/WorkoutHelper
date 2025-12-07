package com.example.workout.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
// TODO: Create ExerciseActivity
//import com.example.workout.Activity.ExerciseActivity;
import com.example.workout.Domain.Exercise;
import com.example.workout.Enum.MuscleEnum;
import com.example.workout.databinding.ViewholderExerciseBinding;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.Viewholder> {
    private final ArrayList<Exercise> list;
    private Context context;

    public ExerciseAdapter(ArrayList<Exercise> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ExerciseAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderExerciseBinding binding = ViewholderExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.Viewholder holder, int position) {
        Exercise item = list.get(position);
        holder.binding.titleTxt.setText(item.getName());
        int resId=context.getResources().getIdentifier(list.get(position).getImageUrl(),"drawable",context.getPackageName());

        // Load image if available
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(resId)
                    .into(holder.binding.pic);
        }

        setMuscleTargetLayout(holder, item);


        // TODO: Implement ExerciseActivity navigation
        /*holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(context, ExerciseActivity.class);
            intent.putExtra("object", item);  // gửi object
            context.startActivity(intent);
        });*/
    }

    private void setMuscleTargetLayout(@NonNull ExerciseAdapter.Viewholder holder, Exercise item) {
        holder.binding.targetMuscleLayout.removeAllViews();

        List<MuscleEnum> muscles = new ArrayList<>();
        if (item.getTargetMuscle1() != null) muscles.add(item.getTargetMuscle1());
        if (item.getTargetMuscle2() != null) muscles.add(item.getTargetMuscle2());
        if (item.getTargetMuscle3() != null) muscles.add(item.getTargetMuscle3());

        for (int i = 0; i < muscles.size(); i++) {
            MuscleEnum muscle = muscles.get(i);

            TextView tv = new TextView(context);
            tv.setText(muscle.getDisplayName());
            tv.setTextColor(ContextCompat.getColor(context, com.example.workout.R.color.white));
            tv.setTextSize(12);
            tv.setPadding(8, 0, 8, 0);

            holder.binding.targetMuscleLayout.addView(tv);

            if (i < muscles.size() - 1) {
                TextView dot = new TextView(context);
                dot.setText(" • ");
                dot.setTextColor(ContextCompat.getColor(context, com.example.workout.R.color.orange));
                dot.setTextSize(14);
                holder.binding.targetMuscleLayout.addView(dot);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderExerciseBinding binding;

        public Viewholder(ViewholderExerciseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
