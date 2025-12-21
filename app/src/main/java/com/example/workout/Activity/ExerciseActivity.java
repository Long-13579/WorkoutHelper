package com.example.workout.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.workout.Domain.Exercise;
import com.example.workout.Domain.Plan;
import com.example.workout.Domain.Set;
import com.example.workout.Domain.Workout;
import com.example.workout.Enum.MuscleEnum;
import com.example.workout.R;
import com.example.workout.Service.PlanService;
import com.example.workout.Service.WorkoutService;
import com.example.workout.databinding.ActivityExerciseBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_SELECTED_DAY = "selectedDay";
    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private ActivityExerciseBinding binding;
    private Exercise exercise;
    private final ArrayList<Plan> planList = new ArrayList<>();

    private PlanService planService;
    private WorkoutService workoutService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        initServices();
        initData();
        initUI();
    }

    /* ================= INIT ================= */

    private void initServices() {
        planService = new PlanService();
        workoutService = new WorkoutService();
    }

    private void initData() {
        exercise = (Exercise) getIntent().getSerializableExtra("object");
        userId = getUserId();
        fetchPlans();
    }

    private void initUI() {
        loadExerciseImage();
        setupTexts();
        setupTargetMuscles();
        setupActions();
    }

    /* ================= UI ================= */

    private void loadExerciseImage() {
        int resId = getResources().getIdentifier(
                exercise.getImageUrl(),
                "drawable",
                getPackageName()
        );
        Glide.with(this).load(resId).into(binding.pic);
    }

    private void setupTexts() {
        binding.titleTxt.setText(exercise.getName());
        binding.levelTxt.setText("Intermediate");
        binding.descriptionTxt.setText(
                exercise.getDescription() != null
                        ? exercise.getDescription()
                        : "No description available"
        );
    }

    private void setupTargetMuscles() {
        addMuscle(exercise.getTargetMuscle1());
        addMuscle(exercise.getTargetMuscle2());
        addMuscle(exercise.getTargetMuscle3());
    }

    private void setupActions() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.startBtn.setOnClickListener(v -> showCreateWorkoutDialog());

        binding.watchTutorialLayout.setOnClickListener(v -> {
            if (exercise.getVideoUrl() != null) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(exercise.getVideoUrl())
                ));
            }
        });
    }

    private void addMuscle(MuscleEnum muscle) {
        if (muscle == null) return;

        TextView tv = new TextView(this);
        tv.setText(muscle.toString());
        tv.setTextColor(getColor(android.R.color.white));
        tv.setTextSize(12);
        tv.setPadding(12, 4, 12, 4);

        binding.targetMuscleLayout.addView(tv);
    }

    /* ================= DATA ================= */

    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return Integer.parseInt(prefs.getString(KEY_USER_ID, "-1"));
    }

    private void fetchPlans() {
        planService.getPlansByUserId(userId, new PlanService.ListPlanDataListener() {
            @Override
            public void onPlansLoaded(List<Plan> plans) {
                planList.clear();
                planList.addAll(plans);
            }

            @Override
            public void onError(String message) {
                toast(message);
            }
        });
    }

    private void createWorkout(Workout workout) {
        workoutService.createWorkout(workout, new WorkoutService.WorkoutDataListener() {
            @Override
            public void onWorkoutLoaded(Workout workout) {
                toast("Workout created successfully");
            }

            @Override
            public void onError(String message) {
                toast(message);
            }
        });
    }

    /* ================= DIALOG ================= */

    private void showCreateWorkoutDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_create_workout, null);

        Spinner spinnerPlan = view.findViewById(R.id.spinnerPlan);
        EditText editDate = view.findViewById(R.id.editDate);
        EditText editSets = view.findViewById(R.id.editSets);
        EditText editReps = view.findViewById(R.id.editReps);
        EditText editVolume = view.findViewById(R.id.editVolume);
        EditText editRest = view.findViewById(R.id.editRestTime);
        TextView txtExercise = view.findViewById(R.id.textviewExercise);

        txtExercise.setText(exercise.getName());
        editDate.setText(getDefaultDate());

        spinnerPlan.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        planList
                )
        );

        editDate.setOnClickListener(v -> showDatePicker(editDate));

        new AlertDialog.Builder(this)
                .setTitle("Add to Plan")
                .setView(view)
                .setPositiveButton("Save", (d, w) ->
                        handleSave(
                                spinnerPlan,
                                editDate,
                                editSets,
                                editReps,
                                editVolume,
                                editRest
                        )
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleSave(
            Spinner spinnerPlan,
            EditText editDate,
            EditText editSets,
            EditText editReps,
            EditText editVolume,
            EditText editRest
    ) {
        if (!isValid(editSets, editReps, editVolume, editRest)) {
            toast("Please fill in all fields");
            return;
        }

        Plan plan = (Plan) spinnerPlan.getSelectedItem();
        Date date = parseDate(editDate.getText().toString());

        int sets = Integer.parseInt(editSets.getText().toString());
        int reps = Integer.parseInt(editReps.getText().toString());
        int volume = Integer.parseInt(editVolume.getText().toString());
        int rest = Integer.parseInt(editRest.getText().toString());

        Workout workout = new Workout(
                plan.getId(),
                0,
                exercise.getId(),
                plan.getName(),
                date,
                exercise,
                generateSets(sets, reps, volume, rest)
        );

        createWorkout(workout);
    }

    /* ================= UTIL ================= */

    private boolean isValid(EditText... fields) {
        for (EditText e : fields) {
            if (e.getText().toString().trim().isEmpty()) return false;
        }
        return true;
    }

    private ArrayList<Set> generateSets(int count, int reps, int volume, int rest) {
        ArrayList<Set> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(new Set(0, 0, exercise.getId(), volume, reps, rest));
        }
        return result;
    }

    private void showDatePicker(EditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (v, y, m, d) -> {
                    c.set(y, m, d);
                    target.setText(
                            new SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                                    .format(c.getTime())
                    );
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private Date parseDate(String value) {
        try {
            return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(value);
        } catch (ParseException e) {
            return new Date();
        }
    }

    private String getDefaultDate() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getString(
                KEY_SELECTED_DAY,
                new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date())
        );
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}