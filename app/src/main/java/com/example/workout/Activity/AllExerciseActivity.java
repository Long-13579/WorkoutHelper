package com.example.workout.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workout.Adapter.SmallExerciseAdapter;
import com.example.workout.Domain.Exercise;
import com.example.workout.Enum.MuscleEnum;
import com.example.workout.Service.ExerciseService;
import com.example.workout.databinding.ActivityAllExerciseBinding;

import java.util.ArrayList;
import java.util.List;

public class AllExerciseActivity extends AppCompatActivity {
    private ActivityAllExerciseBinding binding;
    private ArrayList<Exercise> listExercise;
    private ArrayList<Exercise> filteredListExercise;
    private ExerciseService exerciseService;
    private SmallExerciseAdapter exerciseAdapter;
    private boolean[] checkedItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAllExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listExercise = new ArrayList<>();
        filteredListExercise = new ArrayList<>();
        exerciseService = new ExerciseService();

        MuscleEnum[] muscles = MuscleEnum.values();
        checkedItems = new boolean[muscles.length];

        binding.allExerciseRecyclerView.setLayoutManager(new LinearLayoutManager(AllExerciseActivity.this, LinearLayoutManager.VERTICAL, false));
        exerciseAdapter = new SmallExerciseAdapter(filteredListExercise);
        binding.allExerciseRecyclerView.setAdapter(exerciseAdapter);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.filterBtn.setOnClickListener(v -> showFilterDialog());

        fetchListExercise();
    }

    private void fetchListExercise() {
        exerciseService.GetAll(new ExerciseService.ListExerciseDataListener() {
            @Override
            public void onExerciseLoaded(List<Exercise> exercises) {
                listExercise.clear();
                listExercise.addAll(exercises);
                filteredListExercise.addAll(listExercise);
                exerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AllExerciseActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFilterDialog() {
        MuscleEnum[] muscles = MuscleEnum.values();
        String[] muscleNames = new String[muscles.length];
        boolean[] newCheckedItems = new boolean[muscles.length];
        System.arraycopy(checkedItems, 0, newCheckedItems, 0, newCheckedItems.length);

        for (int i = 0; i < muscles.length; i++) {
            muscleNames[i] = muscles[i].getDisplayName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Muscle Types")
                .setMultiChoiceItems(muscleNames, newCheckedItems, (dialog, which, isChecked) -> {
                    // this is just used to update the checkedItems array in real time
                })
                .setPositiveButton("Filter", (dialog, which) -> {
                    List<MuscleEnum> selectedMuscles = new ArrayList<>();
                    System.arraycopy(newCheckedItems, 0, checkedItems, 0, newCheckedItems.length);
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            selectedMuscles.add(muscles[i]);
                        }
                    }
                    filterExercisesByMuscles(selectedMuscles);
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Clear Filter", (dialog, which) -> {
                    checkedItems = new boolean[muscles.length];
                    filteredListExercise.clear();
                    filteredListExercise.addAll(listExercise);
                    exerciseAdapter.notifyDataSetChanged();
                })
                .show();
    }

    public void filterExercisesByMuscles(List<MuscleEnum> selectedMuscles) {
        filteredListExercise.clear();

        for (Exercise ex : listExercise) {
            if (selectedMuscles.contains(ex.getTargetMuscle1()) ||
                selectedMuscles.contains(ex.getTargetMuscle2()) ||
                selectedMuscles.contains(ex.getTargetMuscle3())) {
                filteredListExercise.add(ex);
            }
        }

        exerciseAdapter.notifyDataSetChanged();
    }

}
