package com.ryangordon.collegetracker.app;

import com.ryangordon.collegetracker.model.Task;
import com.ryangordon.collegetracker.service.TaskManager;
import com.ryangordon.collegetracker.storage.TaskStorage;
import com.ryangordon.collegetracker.ui.MainFrame;
import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.setProperty("apple.awt.application.name", "College Tracker");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        com.ryangordon.collegetracker.ui.theme.Theme.install();

        TaskManager manager = new TaskManager();

        Path filePath = Path.of(System.getProperty("user.home"),
                "CollegeAssignmentTracker",
                "tasks.csv"
        );

        TaskStorage storage = new TaskStorage(filePath);

        SwingUtilities.invokeLater(() -> {
            new MainFrame(manager, storage).setVisible(true);
        });

        // Load saved tasks
        List<Task> loaded = storage.load();
        for (Task t : loaded) {
            manager.addTask(t);
        }
    }
}