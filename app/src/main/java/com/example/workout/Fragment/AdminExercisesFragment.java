package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.AdminExerciseAdapter;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Enum.MuscleEnum;
import com.example.myapplication.R;
import com.example.myapplication.Service.ExerciseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminExercisesFragment extends Fragment {
    private RecyclerView exercisesRecyclerView;
    private Button addExerciseButton;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private AdminExerciseAdapter adapter;
    private List<Exercise> exerciseList;
    private ExerciseService exerciseService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_exercises, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exercisesRecyclerView = view.findViewById(R.id.exercisesRecyclerView);
        addExerciseButton = view.findViewById(R.id.addExerciseButton);
        progressBar = view.findViewById(R.id.exercisesProgressBar);
        emptyTextView = view.findViewById(R.id.exercisesEmptyTextView);

        exerciseService = new ExerciseService();
        exerciseList = new ArrayList<>();

        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminExerciseAdapter(exerciseList, new AdminExerciseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Exercise exercise) {
                // View details
            }

            @Override
            public void onEditClick(Exercise exercise) {
                showExerciseDialog(exercise);
            }

            @Override
            public void onDeleteClick(Exercise exercise) {
                showDeleteConfirmDialog(exercise);
            }
        });
        exercisesRecyclerView.setAdapter(adapter);

        addExerciseButton.setOnClickListener(v -> showExerciseDialog(null));

        loadExercises();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExercises();
    }

    private void loadExercises() {
        showLoading(true);
        exerciseService.GetAll(new ExerciseService.ListExerciseDataListener() {
            @Override
            public void onExerciseLoaded(List<Exercise> exercises) {
                showLoading(false);
                exerciseList.clear();
                exerciseList.addAll(exercises);
                adapter.updateList(exerciseList);
                updateEmptyState();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        exercisesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState() {
        emptyTextView.setVisibility(exerciseList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showExerciseDialog(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_exercise_form, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        EditText videoUrlEditText = dialogView.findViewById(R.id.videoUrlEditText);
        Spinner muscle1Spinner = dialogView.findViewById(R.id.muscle1Spinner);
        Spinner muscle2Spinner = dialogView.findViewById(R.id.muscle2Spinner);
        Spinner muscle3Spinner = dialogView.findViewById(R.id.muscle3Spinner);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView errorTextView = dialogView.findViewById(R.id.errorTextView);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Setup muscle spinners
        List<MuscleEnum> muscles = Arrays.asList(MuscleEnum.values());
        List<String> muscleNames = new ArrayList<>();
        muscleNames.add("None");
        for (MuscleEnum muscle : muscles) {
            muscleNames.add(muscle.getDisplayName());
        }

        ArrayAdapter<String> muscleAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, muscleNames);
        muscleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscle1Spinner.setAdapter(muscleAdapter);
        muscle2Spinner.setAdapter(muscleAdapter);
        muscle3Spinner.setAdapter(muscleAdapter);

        boolean isEdit = exercise != null;
        if (isEdit) {
            dialogTitle.setText("Edit Exercise");
            nameEditText.setText(exercise.getName());
            descriptionEditText.setText(exercise.getDescription());
            videoUrlEditText.setText(exercise.getVideoUrl());
            if (exercise.getTargetMuscle1() != null) {
                int position = muscles.indexOf(exercise.getTargetMuscle1()) + 1;
                muscle1Spinner.setSelection(position);
            }
            if (exercise.getTargetMuscle2() != null) {
                int position = muscles.indexOf(exercise.getTargetMuscle2()) + 1;
                muscle2Spinner.setSelection(position);
            }
            if (exercise.getTargetMuscle3() != null) {
                int position = muscles.indexOf(exercise.getTargetMuscle3()) + 1;
                muscle3Spinner.setSelection(position);
            }
        } else {
            dialogTitle.setText("Add Exercise");
        }

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String videoUrl = videoUrlEditText.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty()) {
                errorTextView.setText("Please fill required fields");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            errorTextView.setVisibility(View.GONE);

            // Get selected muscles
            MuscleEnum muscle1 = null;
            MuscleEnum muscle2 = null;
            MuscleEnum muscle3 = null;

            int pos1 = muscle1Spinner.getSelectedItemPosition();
            if (pos1 > 0) {
                muscle1 = muscles.get(pos1 - 1);
            }

            int pos2 = muscle2Spinner.getSelectedItemPosition();
            if (pos2 > 0) {
                muscle2 = muscles.get(pos2 - 1);
            }

            int pos3 = muscle3Spinner.getSelectedItemPosition();
            if (pos3 > 0) {
                muscle3 = muscles.get(pos3 - 1);
            }

            Exercise exerciseToSave;
            if (isEdit) {
                exerciseToSave = exercise;
                exerciseToSave.setName(name);
                exerciseToSave.setDescription(description);
                exerciseToSave.setVideoUrl(videoUrl);
                exerciseToSave.setTargetMuscle1(muscle1);
                exerciseToSave.setTargetMuscle2(muscle2);
                exerciseToSave.setTargetMuscle3(muscle3);
            } else {
                // Create new exercise with proper constructor
                exerciseToSave = new Exercise(0, name, videoUrl, description, muscle1, muscle2, muscle3);
            }

            if (isEdit) {
                exerciseService.updateExercise(exerciseToSave, new ExerciseService.ExerciseDataListener() {
                    @Override
                    public void onExerciseLoaded(Exercise updatedExercise) {
                        Toast.makeText(getContext(), "Exercise updated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadExercises();
                    }

                    @Override
                    public void onError(String message) {
                        errorTextView.setText(message);
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                exerciseService.createExercise(exerciseToSave, new ExerciseService.ExerciseDataListener() {
                    @Override
                    public void onExerciseLoaded(Exercise createdExercise) {
                        Toast.makeText(getContext(), "Exercise created successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadExercises();
                    }

                    @Override
                    public void onError(String message) {
                        errorTextView.setText(message);
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmDialog(Exercise exercise) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Exercise")
                .setMessage("Are you sure you want to delete exercise: " + exercise.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    exerciseService.deleteExercise(exercise.getId(), new ExerciseService.ExerciseDataListener() {
                        @Override
                        public void onExerciseLoaded(Exercise deletedExercise) {
                            // Success - deletedExercise will be null, reload list
                            Toast.makeText(getContext(), "Exercise deleted successfully", Toast.LENGTH_SHORT).show();
                            loadExercises();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

