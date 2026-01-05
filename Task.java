package com.ryangordon.collegetracker.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Task {
    String title;
    TaskType type;
    LocalDate dueDate;
    String course;
    String notes;
    double gradeWeight;
    private boolean completed;
    String difficulty;
    String priority;

    // Helper methods
    public Task(String title, TaskType type, LocalDate dueDate, String course) {
        this.title = title;
        this.type = type;
        this.dueDate = dueDate;
        this.course = course;

        this.notes = "";
        this.gradeWeight = 0.0;
        this.completed = false;
    }

    public long daysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    public boolean isOverdue() {
        return !completed && daysLeft() < 0;
    }

    public boolean isDueToday() {
        return daysLeft() == 0;
    }

    public boolean isDueSoon() {
        return daysLeft() >= 0 && daysLeft() <= 3;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getCourse() {
        return course;
    }

    public String getTitle() {
        return title;
    }

    public TaskType getType() {
        return type;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public double getGradeWeight() {
        return gradeWeight;
    }

    public void setGradeWeight(double gradeWeight) {
        this.gradeWeight = gradeWeight;
    }
}
