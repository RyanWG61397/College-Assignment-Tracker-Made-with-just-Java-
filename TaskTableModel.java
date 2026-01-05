package com.ryangordon.collegetracker.ui;

import com.ryangordon.collegetracker.model.Task;
import com.ryangordon.collegetracker.service.TaskManager;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {

    private final TaskManager taskManager;

    // Column indexes (keeps the switch statements clean + prevents magic numbers)
    public static final int COL_DONE = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_COURSE = 2;
    public static final int COL_DUE = 3;
    public static final int COL_URGENCY = 4;
    public static final int COL_DELETE = 5;

    private final String[] columns = {
            "Completed", "Title", "Course", "Due", "Urgency", ""
    };

    private List<Task> rows = new ArrayList<>();

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }


    public TaskTableModel(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = rows.get(rowIndex);

        switch (columnIndex) {
            case COL_DONE:
                return task.isCompleted();
            case COL_TITLE:
                return task.getTitle();
            case COL_COURSE:
                return task.getCourse();
            case COL_DUE:
                return task.getDueDate();
            case COL_URGENCY:
                return taskManager.computeUrgency(task);
            case COL_DELETE:
                return "DELETE";
            default:
                return null;
        }
    }

    public void setRows(List<Task> tasks) {
        this.rows = new ArrayList<>(tasks);
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_DONE) return Boolean.class;
        if (columnIndex == COL_DUE) return LocalDate.class;

        // Title/Course/View Details are strings (View Details will be rendered as a button)
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Only the checkbox + the View Details "button" should be clickable/editable
        return columnIndex == COL_DONE || columnIndex == COL_URGENCY || columnIndex == COL_DELETE;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        if (columnIndex != COL_DONE) return;
        Task task = rows.get(rowIndex);
        task.setCompleted((Boolean) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public Task getTaskAt(int row) {
        return rows.get(row);
    }
}