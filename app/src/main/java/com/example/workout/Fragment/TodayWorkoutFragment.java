package com.example.workout.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.workout.Activity.AllExerciseActivity;
import com.example.workout.Adapter.WorkoutScheduleAdapter;
import com.example.workout.Domain.Plan;
import com.example.workout.Domain.Workout;
import com.example.workout.R;
import com.example.workout.Service.PlanService;
import com.example.workout.Service.WorkoutService;
import com.example.workout.databinding.FragmentTodayWorkoutBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TodayWorkoutFragment extends Fragment {

    private FragmentTodayWorkoutBinding binding;
    private ArrayList<Workout> listWorkout;
    private ArrayList<Plan> listPlan;
    private WorkoutScheduleAdapter workoutScheduleAdapter;
    private WorkoutService workoutService;
    private PlanService planService;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTodayWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listWorkout = new ArrayList<>();
        listPlan = new ArrayList<>();
        workoutService = new WorkoutService();
        planService = new PlanService();

        binding.todayWorkoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        workoutScheduleAdapter = new WorkoutScheduleAdapter(listWorkout, new WorkoutScheduleAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Workout item) {
                editWorkoutListener(item);
            }

            @Override
            public void onDelete(Workout item) {
                deleteWorkoutListener(item);
            }
        });
        binding.todayWorkoutRecyclerView.setAdapter(workoutScheduleAdapter);

        binding.btnAddWorkout.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("selectedDay", null);
            editor.apply();

            Intent intent = new Intent(requireContext(), AllExerciseActivity.class);
            startActivity(intent);
        });

        getUserId();
        //fetchListWorkout();
        //fetchListPlan();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchListPlan();
        fetchListWorkout();
    }

    private void editWorkoutListener(Workout workout) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_workout, null);

        Spinner spinnerPlan = dialogView.findViewById(R.id.spinnerPlan);
        TextView textExercise = dialogView.findViewById(R.id.textviewExercise);
        EditText editDate = dialogView.findViewById(R.id.editDate);

        // Set up Plan Spinner
        ArrayAdapter<Plan> planAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listPlan);
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlan.setAdapter(planAdapter);

        // Set up Exercise Text view
        textExercise.setText(workout.getExercise().getName());

        // Pre-select existing values if editing
        if (workout != null) {
            for (int i = 0; i < listPlan.size(); i++) {
                if (listPlan.get(i).getId() == workout.getPlanId()) {
                    spinnerPlan.setSelection(i);
                    break;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            editDate.setText(sdf.format(workout.getDate()));
        }

        // Date picker
        editDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (workout.getDate() != null) {
                calendar.setTime(workout.getDate());
            }
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                editDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Show dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Workout")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    Plan selectedPlan = (Plan) spinnerPlan.getSelectedItem();
                    Date selectedDate = null;
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date displayDate = displayFormat.parse(editDate.getText().toString());

                        SimpleDateFormat storageFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String storageDateStr = storageFormat.format(displayDate);

                        selectedDate = storageFormat.parse(storageDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Update the workout object
                    if (workout != null) {
                        workout.setPlanId(selectedPlan.getId());
                        workout.setPlanName(selectedPlan.getName());
                        workout.setDate(selectedDate); // if needed
                        // Notify adapter if you're updating a list
                        //Reload today workout list
                        updateWorkout(workout);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteWorkoutListener(Workout item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete this workout?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteWorkout(item);
                    Toast.makeText(requireContext(), "Plan deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchListPlan() {
        planService.getPlansByUserId(userId, new PlanService.ListPlanDataListener() {
            @Override
            public void onPlansLoaded(List<Plan> plans) {
                listPlan.clear();
                listPlan.addAll(plans);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchListWorkout() {
        workoutService.getByUserId(userId, new WorkoutService.ListWorkoutDataListener() {
            @Override
            public void onWorkoutsLoaded(List<Workout> workouts) {
                listWorkout.clear();
                Date today = new Date();

                for (Workout workout : workouts) {
                    if (isSameDay(workout.getDate(), today)) {
                        listWorkout.add(workout);
                    }
                }

                workoutScheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWorkout(Workout workout) {
        workoutService.updateWorkout(workout, new WorkoutService.WorkoutDataListener() {
            @Override
            public void onWorkoutLoaded(Workout workout) {
                fetchListWorkout();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWorkout(Workout workout) {
        workoutService.deleteWorkout(workout, new WorkoutService.WorkoutDataListener() {
            @Override
            public void onWorkoutLoaded(Workout workout) {
                fetchListWorkout();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", 0);
        String currentUserId = prefs.getString("user_id", "-1");
        userId = Integer.parseInt(currentUserId);
    }

    private boolean isSameDay(Date date1, Date date2) {
        // Implement a method to check if two Date objects represent the same day
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }
}
