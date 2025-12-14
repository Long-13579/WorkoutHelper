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
import androidx.recyclerview.widget.RecyclerView;
import com.example.workout.Activity.AllExerciseActivity;
import com.example.workout.Adapter.WorkoutScheduleAdapter;
import com.example.workout.Domain.Exercise;
import com.example.workout.Domain.Plan;
import com.example.workout.Domain.Set;
import com.example.workout.Domain.Workout;
import com.example.workout.Enum.MuscleEnum;
import com.example.workout.R;
import com.example.workout.Service.PlanService;
import com.example.workout.Service.WorkoutService;
import com.example.workout.databinding.FragmentCalendarBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;

    private ArrayList<Plan> listPlans;
    private ArrayList<Workout> allWorkouts;  // This list contains all the workouts
    private ArrayList<Workout> selectedDayWorkouts;  // This list contains workouts of the selected day
    private ArrayList<String> planNames;
    private Date selectedDate;
    private int userId;
    private PlanService planService;
    private WorkoutService workoutService;

    // Initialize the adapter and RecyclerView
    private RecyclerView recyclerView;
    private WorkoutScheduleAdapter workoutAdapter;
    private ArrayAdapter<String> spinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        workoutService = new WorkoutService();
        planService = new PlanService();

        // Initialize lists and RecyclerView
        planNames = new ArrayList<>();
        listPlans = new ArrayList<>();
        allWorkouts = new ArrayList<>();
        selectedDayWorkouts = new ArrayList<>();
        selectedDate = new Date();

        getUserId();
        //fetchListPlan();
        //fetchListWorkout();

        workoutAdapter = new WorkoutScheduleAdapter(selectedDayWorkouts, new WorkoutScheduleAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Workout item) {
                editWorkoutListener(item);
            }

            @Override
            public void onDelete(Workout item) {
                deleteWorkoutListener(item);
            }
        });

        binding.calendarWorkoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.calendarWorkoutRecyclerView.setAdapter(workoutAdapter);

        //filterWorkoutsForSelectedDay();

        // Listen to calendar view date selection
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = getDateFromCalendar(year, month, dayOfMonth);
            filterWorkout();
        });

        //Plan Spinner
        Spinner planSpinner = view.findViewById(R.id.planSpinner);
        spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item_white,
                planNames
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planSpinner.setAdapter(spinnerAdapter);

        planSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // "All" selected: show all workouts
                    filterWorkoutsForSelectedDay();
                } else {
                    // A specific plan selected
                    Plan selectedPlan = listPlans.get(position - 1); // subtract 1 due to "All"
                    filterWorkoutsByPlan(selectedPlan.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        binding.addWorkout.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String selectedDayStr = sdf.format(selectedDate);

            SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("selectedDay", selectedDayStr);
            editor.apply();

            Intent intent = new Intent(requireContext(), AllExerciseActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchListPlan();
        fetchListWorkout();
    }

    private void filterWorkoutsForSelectedDay() {
        selectedDayWorkouts.clear();
        for (Workout workout : allWorkouts) {
            if (isSameDay(workout.getDate(), selectedDate)) {
                selectedDayWorkouts.add(workout);
            }
        }
        workoutAdapter.notifyDataSetChanged();
    }

    private void filterWorkoutsByPlan(int planId) {
        selectedDayWorkouts.clear();
        for (Workout workout : allWorkouts) {
            boolean isSameDay = isSameDay(workout.getDate(), selectedDate);
            boolean isSamePlan = workout.getPlanId() == planId;

            if (isSamePlan && isSameDay) {
                selectedDayWorkouts.add(workout);
            }
        }
        workoutAdapter.notifyDataSetChanged();
    }

    private void filterWorkout() {
        int selectedPosition = binding.planSpinner.getSelectedItemPosition();
        if (selectedPosition < 0) selectedPosition = 0;
        if (selectedPosition == 0) {
            filterWorkoutsForSelectedDay();
        } else {
            Plan selectedPlan = listPlans.get(selectedPosition - 1);
            filterWorkoutsByPlan(selectedPlan.getId());
        }
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

    private Date getDateFromCalendar(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        // Set the year, month, and day of the calendar instance
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0); // Clear milliseconds for consistency

        // Return the Date object corresponding to the selected date
        return calendar.getTime();
    }

    private void editWorkoutListener(Workout workout) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_workout, null);

        Spinner spinnerPlan = dialogView.findViewById(R.id.spinnerPlan);
        TextView textExercise = dialogView.findViewById(R.id.textviewExercise);
        EditText editDate = dialogView.findViewById(R.id.editDate);

        // Set up Plan Spinner
        ArrayAdapter<Plan> planAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listPlans);
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlan.setAdapter(planAdapter);

        // Set up Exercise Text view
        textExercise.setText(workout.getExercise().getName());

        // Pre-select existing values if editing
        if (workout != null) {
            for (int i = 0; i < listPlans.size(); i++) {
                if (listPlans.get(i).getId() == workout.getPlanId()) {
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

    public void getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", 0);
        String currentUserId = prefs.getString("user_id", "-1");
        userId = Integer.parseInt(currentUserId);
    }

    public void fetchListPlan() {
        planService.getPlansByUserId(userId, new PlanService.ListPlanDataListener() {
            @Override
            public void onPlansLoaded(List<Plan> plans) {
                listPlans.clear();
                listPlans.addAll(plans);

                planNames.clear();
                planNames.add("All"); // Default option
                for (Plan plan : listPlans) {
                    planNames.add(plan.getName());
                }
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchListWorkout() {
        workoutService.getByUserId(userId, new WorkoutService.ListWorkoutDataListener() {
            @Override
            public void onWorkoutsLoaded(List<Workout> workouts) {
                allWorkouts.clear();
                allWorkouts.addAll(workouts);
                filterWorkout();
                workoutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateWorkout(Workout workout) {
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

    public void deleteWorkout(Workout workout) {
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

    private ArrayList<Workout> getListWorkout() {
        ArrayList<Exercise> listExercise = getListExercise();

        ArrayList<Set> sets1 = new ArrayList<>();
        sets1.add(new Set(1, 101, 1, 0, 15, 60));
        sets1.add(new Set(2, 101, 1, 0, 12, 60));

        ArrayList<Set> sets2 = new ArrayList<>();
        sets2.add(new Set(3, 102, 2, 50, 10, 90));
        sets2.add(new Set(4, 102, 2, 60, 8, 90));

        ArrayList<Set> sets3 = new ArrayList<>();
        sets3.add(new Set(5, 103, 3, 0, 10, 120));
        sets3.add(new Set(6, 103, 3, 0, 8, 120));

        ArrayList<Workout> workoutList = new ArrayList<>();
        workoutList.add(new Workout(1, 101, 1, "Loss Weight", new Date(), listExercise.get(0), sets1));
        workoutList.add(new Workout(1, 102, 2, "Loss Weight", new Date(), listExercise.get(1), sets2));
        workoutList.add(new Workout(2, 103, 3, "Build muscle", new Date(), listExercise.get(2), sets3));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Move to next day
        Date tomorrow = calendar.getTime(); // Get the Date object for tomorrow

        workoutList.add(new Workout(1, 104, 3, "Loss Weight", tomorrow, listExercise.get(2), sets3));


        return workoutList;
    }

    private ArrayList<Exercise> getListExercise() {
        ArrayList<Exercise> exerciseList = new ArrayList<>();

        exerciseList.add(new Exercise(
                1,
                "Seated Dumbbell Shoulder Press",
                "seated_dumbbell_shoulder_press",
                "A strength exercise that targets the shoulders using dumbbells while seated.",
                MuscleEnum.SHOULDERS,
                MuscleEnum.TRICEPS,
                null
        ));

        exerciseList.add(new Exercise(
                2,
                "Band Chest Fly",
                "band_chest_fly",
                "An isolation chest movement using resistance bands to target the pectoral muscles.",
                MuscleEnum.CHEST,
                MuscleEnum.SHOULDERS,
                null
        ));

        exerciseList.add(new Exercise(
                3,
                "Yoga Warrior I",
                "yoga_warrior_i",
                "A foundational yoga pose that strengthens the legs, opens the hips, and improves balance.",
                MuscleEnum.QUADS,
                MuscleEnum.GLUTES,
                MuscleEnum.CALVES
        ));

        return exerciseList;
    }

    private ArrayList<Plan> getListPlan() {
        ArrayList<Plan> result = new ArrayList<>();

        result.add(new Plan(1, 1, "Lost weight"));
        result.add(new Plan(2, 1, "Build muscle"));

        return result;
    }
}


