package com.example.workout.Service;

import com.example.workout.Domain.Exercise;
import com.example.workout.Domain.Plan;
import com.example.workout.Domain.Session;
import com.example.workout.Domain.Set;
import com.example.workout.Domain.Workout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutService {
    private final com.example.workout.Service.ExerciseService exerciseService;
//    private final PlanService planService;
//    private final SessionService sessionService;
//    private final SetService setService;

    private ArrayList<Exercise> listExercise;
    private ArrayList<Plan> listPlan;
    private ArrayList<Session> listSession;
    private ArrayList<Set> listSet;

    public interface ListWorkoutDataListener {
        void onWorkoutsLoaded(List<Workout> workouts);
        void onError(String message);
    }

    public interface WorkoutDataListener {
        void onWorkoutLoaded(Workout workout);
        void onError(String message);
    }

    public WorkoutService() {
        exerciseService = new com.example.workout.Service.ExerciseService();
//        planService = new PlanService();
//        sessionService = new SessionService();
//        setService = new SetService();

        listExercise = new ArrayList<>();
//        listPlan = new ArrayList<>();
//        listSession = new ArrayList<>();
//        listSet = new ArrayList<>();
    }

    public void createWorkout(Workout workout, WorkoutDataListener listener) {
        // TODO: Implement when SessionService is available
        listener.onError("Function not implemented yet");
    }

    public void createListWorkout(List<Workout> workouts, ListWorkoutDataListener listener) {
        // TODO: Implement when other services are available
        listener.onWorkoutsLoaded(new ArrayList<>());
    }

    public void updateWorkout(Workout workout, WorkoutDataListener listener) {
        // TODO: Implement when SessionService is available
        listener.onError("Function not implemented yet");
    }

    public void deleteWorkout(Workout workout, WorkoutDataListener listener) {
        // TODO: Implement when SetService is available
        listener.onError("Function not implemented yet");
    }

    public void getByUserId(int userId, ListWorkoutDataListener listener) {
        // For now, just return empty list since we need to implement other services first
        exerciseService.GetAll(new com.example.workout.Service.ExerciseService.ListExerciseDataListener() {
            @Override
            public void onExerciseLoaded(List<Exercise> exercises) {
                // Temporarily return empty list until other services are implemented
                listener.onWorkoutsLoaded(new ArrayList<>());
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }



    private List<Workout> buildWorkouts(List<Exercise> exercises, List<Plan> plans, List<Session> sessions, List<Set> sets) {
        List<Workout> result = new ArrayList<>();

        for (Session session : sessions) {
            // Get the plan for this session
            Plan plan = null;
            for (Plan p : plans) {
                if (p.getId() == session.getPlanId()) {
                    plan = p;
                    break;
                }
            }

            if (plan == null) continue;

            // Group sets by exerciseId for this session
            Map<Integer, ArrayList<Set>> exerciseSetMap = new HashMap<>();
            for (Set set : sets) {
                if (set.getSessionId() == session.getId()) {
                    exerciseSetMap
                            .computeIfAbsent(set.getExerciseId(), k -> new ArrayList<>())
                            .add(set);
                }
            }

            // For each exerciseId used in this session, build a Workout
            for (Map.Entry<Integer, ArrayList<Set>> entry : exerciseSetMap.entrySet()) {
                int exerciseId = entry.getKey();
                ArrayList<Set> exerciseSets = entry.getValue();

                // Get the exercise object
                Exercise exercise = null;
                for (Exercise ex : exercises) {
                    if (ex.getId() == exerciseId) {
                        exercise = ex;
                        break;
                    }
                }

                if (exercise == null) continue;

                Workout workout = new Workout(
                        plan.getId(),
                        session.getId(),
                        exerciseId,
                        plan.getName(),
                        session.getDate(),
                        exercise,
                        exerciseSets
                );

                result.add(workout);
            }
        }

        return result;
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
