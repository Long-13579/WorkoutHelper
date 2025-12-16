package com.example.myapplication.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.SetAdapter;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Set;
import com.example.myapplication.Domain.Workout;
import com.example.myapplication.R;
import com.example.myapplication.Service.SetService;
import com.example.myapplication.databinding.ActivityWorkoutBinding;

import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {
    ActivityWorkoutBinding binding;
    private Workout workout;
    private ArrayList<Set> listSet;
    private SetService setService;
    private SetAdapter setAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWorkoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getObject();
        setVariable();

    }

    private void getObject() {
        workout= (Workout) getIntent().getSerializableExtra("object");

        setService = new SetService();

        listSet = new ArrayList<>();
        fetchListSet();
    }

    private void setVariable() {
        Exercise itemExercise = workout.getExercise();

        int resId=getResources().getIdentifier(itemExercise.getImageUrl(),"drawable",getPackageName());
        Glide.with(this)
                .load(resId)
                .into(binding.pic);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.titleTxt.setText(itemExercise.getName());
        binding.descriptionTxt.setText(itemExercise.getDescription());

        binding.watchTutorialLayout.setOnClickListener(v -> {
            String videoUrl = itemExercise.getVideoUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            startActivity(intent);
        });

        binding.view3.setLayoutManager(new LinearLayoutManager(WorkoutActivity.this,LinearLayoutManager.VERTICAL,false));
        setAdapter = new SetAdapter(listSet, WorkoutActivity.this, new SetAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Set item) {
                showEditSetDialog(item);
            }

            @Override
            public void onDelete(Set item) {
                showDeleteSet(item);
            }

            @Override
            public void onFinishChanged(Set item) {
                updateSet(item);
            }
        });
        binding.view3.setAdapter(setAdapter);

        binding.btnAddSet.setOnClickListener(v -> showCreateSetDialog());
    }

    private void showCreateSetDialog() {
        View dialogView = LayoutInflater.from(WorkoutActivity.this).inflate(R.layout.dialog_edit_set, null);

        EditText editReps = dialogView.findViewById(R.id.editReps);
        EditText editWeight = dialogView.findViewById(R.id.editWeight);
        EditText editRestTime = dialogView.findViewById(R.id.editRestTime);

        new AlertDialog.Builder(WorkoutActivity.this)
                .setTitle("Create Set")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    int reps = Integer.parseInt(editReps.getText().toString());
                    int weight = Integer.parseInt(editWeight.getText().toString());
                    int restTime = Integer.parseInt(editRestTime.getText().toString());

                    Set set = new Set(0, workout.getSessionId(), workout.getExerciseId(), weight, reps, restTime);

                    // Save changes (update in list, database, etc.)
                    createSet(set);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditSetDialog(Set set) {
        View dialogView = LayoutInflater.from(WorkoutActivity.this).inflate(R.layout.dialog_edit_set, null);

        EditText editReps = dialogView.findViewById(R.id.editReps);
        EditText editWeight = dialogView.findViewById(R.id.editWeight);
        EditText editRestTime = dialogView.findViewById(R.id.editRestTime);

        // Pre-fill current values
        editReps.setText(String.valueOf(set.getReps()));
        editWeight.setText(String.valueOf(set.getWeight()));
        editRestTime.setText(String.valueOf(set.getRestTime()));

        new AlertDialog.Builder(WorkoutActivity.this)
                .setTitle("Edit Set")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    int reps = Integer.parseInt(editReps.getText().toString());
                    int weight = Integer.parseInt(editWeight.getText().toString());
                    int restTime = Integer.parseInt(editRestTime.getText().toString());

                    set.setReps(reps);
                    set.setWeight(weight);
                    set.setRestTime(restTime);

                    // Save changes (update in list, database, etc.)
                    updateSet(set);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteSet(Set item) {
        new AlertDialog.Builder(WorkoutActivity.this)
                .setTitle("Delete Set")
                .setMessage("Are you sure you want to delete this set?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteSet(item);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchListSet() {
        setService.getBySessionId(workout.getSessionId(), new SetService.ListSetDataListener() {
            @Override
            public void onSetsLoaded(List<Set> sets) {
                listSet.clear();
                for (Set set : sets) {
                    if (set.getExerciseId() == workout.getExerciseId()) {
                        listSet.add(set);
                    }
                }
                setAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void createSet(Set set) {
        setService.createSet(set, new SetService.SetDataListener() {
            @Override
            public void onSetLoaded(Set set) {
                fetchListSet();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(WorkoutActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSet(Set set) {
        setService.updateSet(set, new SetService.SetDataListener() {
            @Override
            public void onSetLoaded(Set set) {
                fetchListSet();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(WorkoutActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSet(Set set) {
        setService.deleteSet(set.getId(), new SetService.ListSetDataListener() {
            @Override
            public void onSetsLoaded(List<Set> sets) {
                fetchListSet();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(WorkoutActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}