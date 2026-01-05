package com.ryangordon.collegetracker.service;

import java.util.Comparator;
import com.ryangordon.collegetracker.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

/*
This class:
 owns the list of tasks
 decides how tasks are sorted
 handles filtering (today / week / overdue)
 calculates progress + workload
 works with Swing

 */
public class TaskManager {

    private List<Task> tasks;

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        tasks.add(task);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public void sortByDueDate() {
        tasks.sort(Comparator.comparing(
                Task::getDueDate,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));
    }

    public void sortByCourse() {
        tasks.sort(Comparator.comparing(t ->
                t.getCourse() == null ? "" : t.getCourse().toLowerCase()
        ));
    }

    public void sortByType() {
        tasks.sort(Comparator.comparing(t ->
                t.getType() == null ? "" : t.getType().toString()
        ));
    }

    public String computeUrgency(Task task) {
        if (task == null || task.getDueDate() == null) return "";

        java.time.LocalDate today = java.time.LocalDate.now();
        long days = java.time.temporal.ChronoUnit.DAYS.between(today, task.getDueDate());

        if (days < 0) return "OVERDUE";          // already overdue
        if (days <= 2) return "URGENT";         // due soon
        if (days <= 7) return "MEDIUM";         // coming up
        return "LOW";                     // plenty of time
    }

    private int urgencyRank(Task t) {
        // If your Task has a method like getUrgencyLabel() or getUrgency(),
        // we’ll adjust this in the next message if needed.
        String u = computeUrgency(t);  // <-- if this line errors, tell me what your urgency getter is called

        if (u == null) return 999;

        return switch (u) {
            case "OVERDUE" -> 0;
            case "URGENT"  -> 1;
            case "MEDIUM"  -> 2;
            case "LOW"     -> 3;
            default        -> 999;
        };
    }

    public void sortByUrgency() {
        tasks.sort(Comparator
                .comparingInt(this::urgencyRank)
                .thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );
    }

    public void markCompleted(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Invalid task index: " + index);
        }
        tasks.get(index).setCompleted(true);
    }

    public Task getNextDueTask() {
        return getTasksSortedByDueDate().isEmpty() ? null : getTasksSortedByDueDate().get(0);
    }

    public List<Task> getOverdueTasks() {
        return tasks.stream()
                .filter(Task::isOverdue)
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksDueToday() {
        LocalDate today = LocalDate.now();
        return tasks.stream()
                .filter(t -> !t.isCompleted())
                .filter(t -> t.getDueDate().isEqual(today))
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksDueThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(7);

        return tasks.stream()
                .filter(t -> !t.isCompleted())
                .filter(t -> !t.getDueDate().isBefore(today) && !t.getDueDate().isAfter(end))
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks() {
        return tasks.stream()
                .filter(Task::isCompleted)
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    public double getCompletionRate() {
        if (tasks.isEmpty()) return 0.0;

        long completedCount = tasks.stream().filter(Task::isCompleted).count();
        return (double) completedCount / tasks.size();
    }

    public int getCompletionPercent() {
        return (int) Math.round(getCompletionRate() * 100);
    }

    public int getWorkloadScore() {
        // Higher score = heavier workload
        // Only considers incomplete tasks due in the next 7 days (and overdue)
        int score = 0;

        for (Task t : tasks) {
            if (t.isCompleted()) continue;

            long d = t.daysLeft();

            if (d < 0) score += 6;
            else if (d <= 1) score += 5;
            else if (d <= 3) score += 3;
            else if (d <= 7) score += 1;
        }
        return score;
    }

    public String getWorkloadLevel() {
        int score = getWorkloadScore();
        if (score >= 12) return "HIGH";
        if (score >= 6) return "MEDIUM";
        return "LOW";
    }

    public void removeTask(Task task) {
        if (task == null) return;
        tasks.remove(task);
    }

    public List<Task> getTasksSortedByDueDate() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<Task> getTasksSortedByCourse() {
        return tasks.stream()
                .sorted(Comparator.comparing(t -> safeLower(t.getCourse())))
                .toList();
    }

    public List<Task> getTasksSortedByType() {
        return tasks.stream()
                .sorted(Comparator.comparing(t -> t.getType() == null ? "" : t.getType().toString()))
                .toList();
    }

    public List<Task> getTasksSortedByUrgency() {
        return tasks.stream()
                .sorted(Comparator
                        .comparingInt(this::urgencyRank)
                        .thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }
}
