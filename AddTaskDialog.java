package com.ryangordon.collegetracker.ui;

import com.ryangordon.collegetracker.model.Task;
import com.ryangordon.collegetracker.model.TaskType;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class AddTaskDialog extends JDialog {

    private Task createdTask = null;

    private final JTextField titleField = new JTextField();
    private final JTextField courseField = new JTextField();
    private final JComboBox<TaskType> typeBox = new JComboBox<>(TaskType.values());
    private final JTextField dueDateField = new JTextField(); // YYYY-MM-DD
    private final JTextArea notesArea = new JTextArea(4, 20);
    private final JTextField weightField = new JTextField(); // optional number

    public AddTaskDialog(JFrame owner) {
        super(owner, "Add Task", true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(owner);

        setLayout(new BorderLayout());
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JComponent buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(6, 0, 6, 10);

        panel.add(new JLabel("Title"), c);
        c.gridy++;
        panel.add(new JLabel("Course"), c);
        c.gridy++;
        panel.add(new JLabel("Type"), c);
        c.gridy++;
        panel.add(new JLabel("Due Date (YYYY-MM-DD)"), c);
        c.gridy++;
        panel.add(new JLabel("Weight % (optional)"), c);
        c.gridy++;
        panel.add(new JLabel("Notes (optional)"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);

        panel.add(titleField, c);
        c.gridy++;
        panel.add(courseField, c);
        c.gridy++;
        panel.add(typeBox, c);
        c.gridy++;

        dueDateField.setToolTipText("Example: 2025-12-27");
        panel.add(dueDateField, c);
        c.gridy++;

        panel.add(weightField, c);
        c.gridy++;

        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        panel.add(notesScroll, c);

        return panel;
    }

    private JComponent buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(0, 12, 12, 12));

        JButton cancel = new JButton("Cancel");
        JButton add = new JButton("Add");

        cancel.addActionListener(e -> {
            createdTask = null;
            dispose();
        });

        add.addActionListener(e -> onAdd());

        panel.add(cancel);
        panel.add(add);
        return panel;
    }

    private void onAdd() {
        String title = titleField.getText().trim();
        String course = courseField.getText().trim();
        TaskType type = (TaskType) typeBox.getSelectedItem();
        String dueText = dueDateField.getText().trim();

        if (title.isEmpty() || course.isEmpty() || dueText.isEmpty() || type == null) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in Title, Course, Type, and Due Date.",
                    "Missing fields",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueText); // expects YYYY-MM-DD
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Due Date must be in the format YYYY-MM-DD (example: 2025-12-27).",
                    "Invalid date",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        createdTask = new Task(title, type, dueDate, course);

        // Optional fields
        String notes = notesArea.getText().trim();
        if (!notes.isEmpty()) {
            createdTask.setNotes(notes);
        }

        String weightText = weightField.getText().trim();
        if (!weightText.isEmpty()) {
            try {
                double w = Double.parseDouble(weightText);
                createdTask.setGradeWeight(w);
            } catch (NumberFormatException ignored) {
                JOptionPane.showMessageDialog(this,
                        "Weight must be a number (example: 10 or 15.5).",
                        "Invalid weight",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        dispose();
    }

    public Task getCreatedTask() {
        return createdTask;
    }
}