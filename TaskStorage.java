package com.ryangordon.collegetracker.storage;

import com.ryangordon.collegetracker.model.Task;
import com.ryangordon.collegetracker.model.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {

    private final Path filePath;

    public TaskStorage(Path filePath) {
        this.filePath = filePath;
    }

    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) return tasks;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // CSV format:
                // title, course, type, dueDate, completed, gradeWeight, notes
                String [] parts = splitCsvLine(line);

                String title = unescape(parts[0]);
                String course = unescape(parts[1]);
                TaskType type = TaskType.valueOf(parts[2]);
                LocalDate dueDate = LocalDate.parse(parts[3]);
                boolean completed = Boolean.parseBoolean(parts[4]);
                double gradeWeight = Double.parseDouble(parts[5]);
                String notes = unescape(parts[6]);

                Task t = new Task(title, type, dueDate, course);
                t.setCompleted(completed);
                t.setGradeWeight(gradeWeight);
                t.setNotes(notes);

                tasks.add(t);
            }
        } catch (Exception e) {
            // If file is corrupted, we don't want to crash the whole app
            e.printStackTrace();
        }

        return tasks;
    }

    public void save(List<Task> tasks) {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Task t : tasks) {
                String line = toCsvLine(t);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toCsvLine(Task t) {

        return escape(t.getTitle()) + "," +
                escape(t.getCourse()) + "," +
                t.getType().name() + "," +
                t.getDueDate() + "," +
                t.isCompleted() + "," +
                t.getGradeWeight() + "," +
                escape(t.getNotes());
    }

    private String escape(String s) {
        if (s == null) return "\"\"";
        String escaped = s.replace("\'", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String unescape(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() -1);
        }
        return s.replace("\"\"", "\"");
    }

    // Splits a CSV line where every text field is quoted
    private String [] splitCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                // handle escaped quote
                if (inQuotes && i + i < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                    current.append('"');
                }
            } else if (ch == ',' && !inQuotes) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        parts.add(current.toString());

        // We expect 7 columns
        while (parts.size() < 7) parts.add("\"\"");
        return parts.toArray(new String[0]);
    }
}