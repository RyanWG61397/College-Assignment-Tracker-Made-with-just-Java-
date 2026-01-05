package com.ryangordon.collegetracker.ui;

import com.ryangordon.collegetracker.model.Task;
import com.ryangordon.collegetracker.service.TaskManager;
import com.ryangordon.collegetracker.storage.TaskStorage;
import com.ryangordon.collegetracker.ui.theme.PremiumPanel;
import com.ryangordon.collegetracker.ui.theme.SidebarButton;
import com.ryangordon.collegetracker.ui.theme.Theme;
import com.ryangordon.collegetracker.ui.util.TrashIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private final TaskManager taskManager;

    // Main regions
    private JPanel centerPanel;
    private JTable taskTable;
    private JLabel workloadTextLabel;
    private JLabel workloadValueLabel;
    private JProgressBar progressBar;
    private JLabel detailTitle;
    private JLabel detailCourse;
    private JLabel detailDue;
    private JLabel detailType;
    private JTextArea detailNotes;
    private Task selectedTask;
    private JLabel pageTitleLabel;
    private JLabel viewTitleLabel;
    private JLabel percentLabel;
    private static final int SIDEBAR_WIDTH = 210;   // tweak until it matches your mock
    private int hoveredRow = -1;

    // Colors for sidebar; local variables for when you hover over the sidebar buttons
    private static final Color NAV_SELECTED = new Color(92, 130, 175);
    private static final Color NAV_HOVER = new Color (215, 228, 242);
    private static final Color NAV_NORMAL = new Color(245, 245, 245);
    private static final Color NAV_TEXT_DEFAULT = new Color(60, 60, 60);
    private static final Color NAV_TEXT_ACTIVE  = Color.WHITE;

    private final List<JButton> sidebarButtons = new ArrayList<>();

    private JButton selectedNavButton;

    private TaskTableModel tableModel;
    private JComponent detailsPanel;

    private final TaskStorage storage;

    private enum View {
        HOME,
        ASSIGNMENTS,
        CALENDAR,
        STATS
    }

    private String currentSort = "Sort: Urgency";

    private View currentView = View.ASSIGNMENTS;

    private JComponent buildHomeView() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(com.ryangordon.collegetracker.ui.theme.Theme.BG);
        panel.setOpaque(true);

        return panel;
    }

    private JComponent buildCalendarView() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(com.ryangordon.collegetracker.ui.theme.Theme.BG);
        panel.setOpaque(true);

        return panel;
    }

    private JComponent buildStatsView() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(com.ryangordon.collegetracker.ui.theme.Theme.BG);
        panel.setOpaque(true);

        return panel;
    }

    public MainFrame(TaskManager taskManager, TaskStorage storage) {
        this.taskManager = taskManager;
        this.storage = storage;

        setTitle("College Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        // Root layout: sidebar / top / center / bottom
        setLayout(new BorderLayout());

        getContentPane().setBackground(com.ryangordon.collegetracker.ui.theme.Theme.BG);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildCenter(), BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                storage.save(taskManager.getAllTasks());
            }
        });
    }

    private JComponent buildSidebar() {
        JPanel panel = new PremiumPanel(Theme.SIDEBAR_BG_TOP, Theme.SIDEBAR_BG_BOT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create components
        panel.setBackground(Theme.SIDEBAR_BG_TOP);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        panel.setOpaque(true);

        JLabel title = new JLabel("Navigation");
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        JButton homeButton = makeSidebarButton("Home", "/icons/home.png", View.HOME);
        panel.add(homeButton);
        panel.add(Box.createVerticalStrut(4));

        JButton assignmentsButton = makeSidebarButton("Assignments", "/icons/assignments.png", View.ASSIGNMENTS);
        panel.add(assignmentsButton);
        panel.add(Box.createVerticalStrut(4));

        JButton calendarButton = makeSidebarButton("Calendar", "/icons/calendar.png", View.CALENDAR);
        panel.add(calendarButton);
        panel.add(Box.createVerticalStrut(4));

        JButton statsButton = makeSidebarButton("Stats", "/icons/stats.png", View.STATS);
        panel.add(statsButton);
        panel.add(Box.createVerticalGlue());

        // Default selected on startup
        setSelectedNav(assignmentsButton);

        return panel;
    }

    private void switchView(View view) {
        CardLayout c1 = (CardLayout) centerPanel.getLayout();
        c1.show(centerPanel, view.name());

        setPageTitle(view);
    }

    private JButton makeSidebarButton(String text, String iconPath, View view) {
        JButton btn = new SidebarButton(text, loadIcon(iconPath, 36));
        sidebarButtons.add(btn);

        btn.setHorizontalAlignment(SwingConstants.LEFT);

        // padding only (NO line borders here)
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);

        // btn.setForeground(Theme.SIDEBAR_TEXT_DIM);
        btn.setIconTextGap(10);

        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            setSelectedSidebarButton(btn);
            switchView(view);
        });

        return btn;
    }

    private JComponent buildTopBar() {
        JPanel panel = new GradientPanel(
                new Color(45, 55, 70),         // deep slate blue (left)
                new Color(45, 55, 70, 120)   // soft fade / glimmer
        );
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));

        pageTitleLabel = new JLabel("Assignments"); // default text
        pageTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        pageTitleLabel.setForeground(Color.WHITE);
        pageTitleLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        pageTitleLabel.setOpaque(false);

        panel.add(pageTitleLabel, BorderLayout.CENTER);

        // LEFT GUTTER = same width as sidebar so the title starts above the table, not above the sidebar
        JPanel leftGutter = new JPanel();
        leftGutter.setOpaque(false);
        leftGutter.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 1));
        panel.add(leftGutter, BorderLayout.WEST);

        // TITLE (left aligned, but inside the "content area")
        viewTitleLabel = new JLabel("Assignments");
        viewTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        viewTitleLabel.setForeground(new Color(60, 60, 60));

        JPanel titleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleWrap.setOpaque(false);
        titleWrap.add(viewTitleLabel);
        panel.add(titleWrap, BorderLayout.CENTER);

        /* JTextField searchField = new JTextField();
        searchField.setToolTipText("Search tasks...");
        panel.add(searchField, BorderLayout.CENTER); */

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JComboBox<String> sortBox = new JComboBox<>(new String[] {
                "Sort: Urgency",
                "Sort: Due Date",
                "Sort: Course",
                "Sort: Type"
        });

        sortBox.addActionListener(e -> {
            String selected = (String) sortBox.getSelectedItem();
            if (selected == null) return;

            currentSort = selected;
            refreshTable();
        });

        PillButton addTaskButton = new PillButton("+ Add Task");

        addTaskButton.setPillColors(
                new Color(108, 168, 92),
                new Color(75, 120, 62)
        );

        addTaskButton.addActionListener(e -> {
            AddTaskDialog dialog = new AddTaskDialog(this);
            dialog.setVisible(true);

            if (dialog.getCreatedTask() != null) {
                taskManager.addTask(dialog.getCreatedTask());
                storage.save(taskManager.getAllTasks());
                refreshTable();
                refreshStatus();
            }
        });

        right.add(sortBox);
        right.add(addTaskButton);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Theme.BG);
        wrapper.setOpaque(true);

        // Top bar only applies to the right content area
        wrapper.add(buildTopBar(), BorderLayout.NORTH);

        // Card layout for views
        centerPanel = new JPanel(new CardLayout());
        centerPanel.setBackground(Theme.BG);
        centerPanel.setOpaque(true);

        wrapper.add(centerPanel, BorderLayout.CENTER);

        centerPanel.add(buildAssignmentsView(), View.ASSIGNMENTS.name());
        centerPanel.add(buildHomeView(), View.HOME.name());
        centerPanel.add(buildCalendarView(), View.CALENDAR.name());
        centerPanel.add(buildStatsView(), View.STATS.name());

        // Show default view
        switchView(currentView);

        return wrapper;
    }

    private JComponent buildAssignmentsView() {

        tableModel = new TaskTableModel(taskManager);
        tableModel.setRows(taskManager.getTasksSortedByDueDate());

        tableModel.addTableModelListener(e -> {
            refreshStatus();
            storage.save(taskManager.getAllTasks());
        });

        taskTable = new JTable(tableModel);

        taskTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        taskTable.setOpaque(true);

        // ===== TABLE BASE STYLE =====
        taskTable.setShowGrid(false);
        taskTable.setIntercellSpacing(new Dimension(0, 0));
        taskTable.setRowHeight(36);
        taskTable.setFillsViewportHeight(true);

        taskTable.setSelectionBackground(Theme.SELECTION);
        taskTable.setSelectionForeground(Theme.TEXT);

        taskTable.setBackground(Theme.PANEL);
        taskTable.setForeground(Theme.TEXT);


        detailsPanel = buildDetailsPanel();
        detailsPanel.setVisible(false);

        taskTable.getColumnModel()
                .getColumn(TaskTableModel.COL_URGENCY)
                .setPreferredWidth(140);

        // Urgency column: show colored status text
        taskTable.getColumnModel()
                .getColumn(TaskTableModel.COL_URGENCY)
                .setCellRenderer(new UrgencyCellRenderer());

        // Delete Column:
        taskTable.getColumnModel().getColumn(TaskTableModel.COL_DELETE).setMaxWidth(48);
        taskTable.getColumnModel().getColumn(TaskTableModel.COL_DELETE).setMinWidth(48);
        taskTable.getColumnModel().getColumn(TaskTableModel.COL_DELETE).setPreferredWidth(48);

        taskTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = taskTable.rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    taskTable.repaint();
                }
            }
        });
        taskTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoveredRow = -1;
                taskTable.repaint();
            }
        });

        taskTable.getColumnModel().getColumn(TaskTableModel.COL_DELETE)
                .setCellRenderer((table, value, isSelected, hasFocus, row, col) -> {
                    JLabel lbl = new JLabel();
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setOpaque(true);

                    // match your row background logic
                    Theme.applyRowStriping(lbl, table, row, isSelected);

                    boolean show = isSelected || row == hoveredRow;
                    lbl.setIcon(show ? new TrashIcon(16, Color.WHITE) : null);

                    return lbl;
                });

        taskTable.getColumnModel().getColumn(TaskTableModel.COL_DELETE)
                .setCellEditor(new javax.swing.DefaultCellEditor(new JCheckBox()) {

                    private final JButton button = new JButton();
                    private int editingRow = -1;

                    {
                        button.setOpaque(false);
                        button.setContentAreaFilled(false);
                        button.setBorderPainted(false);
                        button.setFocusPainted(false);
                        button.setHorizontalAlignment(SwingConstants.CENTER);
                        button.setIcon(new TrashIcon(16, Color.WHITE));

                        button.addActionListener(e -> {
                            if (editingRow < 0) return;

                            Task task = tableModel.getTaskAt(editingRow);

                            int choice = JOptionPane.showConfirmDialog(
                                    MainFrame.this,
                                    "Delete \"" + task.getTitle() + "\"?",
                                    "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (choice == JOptionPane.YES_OPTION) {
                                taskManager.removeTask(task);
                                tableModel.setRows(taskManager.getTasksSortedByDueDate());
                                refreshStatus();
                                storage.save(taskManager.getAllTasks());
                            }

                            fireEditingStopped();
                        });
                    }

                    @Override
                    public Component getTableCellEditorComponent(
                            JTable table, Object value, boolean hasFocus,
                            int row, int column
                    ) {
                        editingRow = row;

                        JPanel panel = new JPanel(new GridBagLayout());
                        panel.setOpaque(true);

                        // Paint the SAME row striping as the renderer
                        Theme.applyRowStriping(panel, table, row, hasFocus);

                        // Keep button icon-only
                        button.setOpaque(false);
                        button.setContentAreaFilled(false);
                        button.setBorderPainted(false);
                        button.setFocusPainted(false);

                        panel.add(button);
                        return panel;
                    }

                    @Override
                    public Object getCellEditorValue() {
                        return "DELETE";
                    }
                });

        JTableHeader header = taskTable.getTableHeader();

        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setOpaque(true);

        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        header.setBackground(Theme.PANEL_2);
        header.setForeground(Theme.TEXT);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER));

        // Default text cells (padding + color)
        taskTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);

                // Padding
                c.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                // Background + foreground
                if (isSelected) {
                    c.setBackground(Theme.SELECTION);
                    c.setForeground(Theme.TEXT);
                } else {
                    c.setBackground(row % 2 == 0 ? Theme.PANEL : Theme.PANEL_ALT);
                    c.setForeground(Theme.TEXT);
                }

                return c;
            }
        });

        // LocalDate formatting (Due column)
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d"); // e.g. "Dec 29"

        taskTable.setDefaultRenderer(LocalDate.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value == null) {
                    setText("");
                } else {
                    setText(((LocalDate) value).format(fmt));
                }
            }
        });

        // Checkbox column
        taskTable.setDefaultRenderer(Boolean.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                JCheckBox box = new JCheckBox();
                box.setHorizontalAlignment(SwingConstants.CENTER);
                box.setSelected(Boolean.TRUE.equals(value));
                box.setOpaque(true);

                if (isSelected) {
                    box.setBackground(Theme.SELECTION);
                } else {
                    box.setBackground(row % 2 == 0 ? Theme.PANEL : Theme.PANEL_ALT);
                }

                return box;
            }
        });

        taskTable.setFillsViewportHeight(true);

        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            int viewRow = taskTable.getSelectedRow();
            if (viewRow < 0) {
                clearDetails();
                return;
            }

            int modelRow = taskTable.convertRowIndexToModel(viewRow);
            selectedTask = tableModel.getTaskAt(modelRow); // we’ll add this if you don’t have it yet
            showDetails(selectedTask);
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Kill the default white/gray backgrounds
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.getViewport().setBackground(Theme.BG);

        detailsPanel = buildDetailsPanel();
        detailsPanel.setVisible(false);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scrollPane,
                detailsPanel
        );

        split.setResizeWeight(0.70);
        split.setDividerSize(8);

        // NEW: wrap split + status bar
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(com.ryangordon.collegetracker.ui.theme.Theme.BG);
        root.setOpaque(true);

        root.add(split, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        return root;
    }

    private void setSelectedSidebarButton(JButton selected) {
        for (JButton b : sidebarButtons) {
            b.putClientProperty("selected", Boolean.FALSE);
            b.setForeground(Theme.SIDEBAR_TEXT_DIM);
            b.repaint();
        }
        selected.putClientProperty("selected", Boolean.TRUE);
        selected.setForeground(Theme.SIDEBAR_TEXT);
        selected.repaint();
    }

    private JComponent buildStatusBar() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));

        workloadTextLabel = new JLabel("Workload:");
        workloadValueLabel = new JLabel(taskManager.getWorkloadLevel());

        workloadTextLabel.setForeground(new Color(90, 90, 90));     // Neutral gray
        workloadValueLabel.setFont(workloadValueLabel.getFont().deriveFont(Font.BOLD));

        JPanel workloadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        workloadPanel.setOpaque(false);
        workloadPanel.add(workloadTextLabel);
        workloadPanel.add(workloadValueLabel);

        updateWorkloadColor(taskManager.getWorkloadLevel());


        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(taskManager.getCompletionPercent());
        progressBar.setStringPainted(false);

        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);

        // Green colors (tweak later if needed)
        Color baseGreen   = new Color(102, 205, 120);
        Color stripeGreen = new Color(150, 230, 165);

        progressBar.setBackground(new Color(235, 235, 235));
        progressBar.setUI(new StripedProgressBarUI(baseGreen, stripeGreen));
        progressBar.setPreferredSize(new Dimension(0, 16));     // 16px tall pill

        percentLabel = new JLabel(progressBar.getValue() + "%");
        percentLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(percentLabel, BorderLayout.EAST);
        panel.add(workloadPanel, BorderLayout.WEST);

        return panel;
    }

    private void refreshStatus() {
        String workload = taskManager.getWorkloadLevel();

        workloadValueLabel.setText(workload);
        updateWorkloadColor(workload);
        progressBar.setValue(taskManager.getCompletionPercent());
        percentLabel.setText(progressBar.getValue() + "%");

        updateWorkloadColor(workload);
    }

    private void refreshTable() {
        if (tableModel == null) return;

        List<Task> rows = switch (currentSort) {
            case "Sort: Urgency" -> taskManager.getTasksSortedByUrgency();
            case "Sort: Due Date" -> taskManager.getTasksSortedByDueDate();
            case "Sort: Course" -> taskManager.getTasksSortedByCourse();
            case "Sort: Type" -> taskManager.getTasksSortedByType();
            default -> taskManager.getTasksSortedByDueDate();
        };

        tableModel.setRows(rows);
        refreshStatus();
    }

    private JComponent buildDetailsPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(Color.WHITE);

        detailTitle = new JLabel("Select a task");
        detailTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(detailTitle);
        panel.add(Box.createVerticalStrut(10));

        detailCourse = new JLabel("Course: —");
        detailDue = new JLabel("Due: —");
        detailType = new JLabel("Type: —");

        panel.add(detailCourse);
        panel.add(Box.createVerticalStrut(6));
        panel.add(detailDue);
        panel.add(Box.createVerticalStrut(6));
        panel.add(detailType);
        panel.add(Box.createVerticalStrut(12));

        panel.add(new JLabel("Notes"));
        panel.add(Box.createVerticalStrut(6));

        detailNotes = new JTextArea(8, 20);
        detailNotes.setLineWrap(true);
        detailNotes.setWrapStyleWord(true);
        detailNotes.setEditable(false);

        JScrollPane notesScroll = new JScrollPane(detailNotes);
        panel.add(notesScroll);

        panel.add(Box.createVerticalGlue());

        clearDetails();

        panel.setVisible(false);

        return panel;
    }

    private void showDetails(Task t) {
        detailTitle.setText(t.getTitle());
        detailCourse.setText("Course: " + t.getCourse());
        detailDue.setText("Due: " + (t.getDueDate() == null ? "" : t.getDueDate().toString()));
        detailType.setText("Type: " + t.getType());
        detailNotes.setText(t.getNotes() == null ? "" : t.getNotes());

    }

    private void clearDetails() {
        detailTitle.setText("Select a task");
        detailCourse.setText("Course: ");
        detailDue.setText("Due: ");
        detailType.setText("Type: ");
        detailNotes.setText("");
        selectedTask = null;
    }

    private void setSelectedNav(JButton btn) {
        // Unselect old
        if (selectedNavButton != null) {
            selectedNavButton.setBackground(NAV_NORMAL);
            selectedNavButton.setForeground(NAV_TEXT_DEFAULT);
            selectedNavButton.repaint();
        }

        //Select new
        selectedNavButton = btn;
        btn.setBackground(NAV_SELECTED);
        btn.setForeground(NAV_TEXT_ACTIVE);

        selectedNavButton.repaint();
    }

    private static class ViewButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ViewButtonRenderer() {
            setText("View");
            setFocusPainted(false);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            setText(value == null ? "View" : value.toString());
            return this;
        }
    }

    private static class ViewButtonEditor extends javax.swing.DefaultCellEditor {
        private final JButton button = new JButton("View");
        private final JTable table;
        private final java.util.function.IntConsumer onClick;

        public ViewButtonEditor(JTable table, java.util.function.IntConsumer onClick) {
            super(new JCheckBox()); // DefaultCellEditor needs a component, checkbox is fine
            this.table = table;
            this.onClick = onClick;

            button.setFocusPainted(false);

            button.addActionListener(e -> {
                // Convert the clicked row from VIEW index -> MODEL index (important if sorting)
                int viewRow = table.getEditingRow();
                int modelRow = table.convertRowIndexToModel(viewRow);

                onClick.accept(modelRow);

                fireEditingStopped(); // closes editor cleanly
            });
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value == null ? "View" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "View";
        }
    }

    private void setPageTitle(View view) {
        if (pageTitleLabel == null) return;

        switch (view) {
            case HOME -> pageTitleLabel.setText("Home");
            case ASSIGNMENTS -> pageTitleLabel.setText("Assignments");
            case CALENDAR -> pageTitleLabel.setText("Calendar");
            case STATS -> pageTitleLabel.setText("Stats");
        }
    }

    private void updateWorkloadColor(String workload) {
        switch (workload.toUpperCase()) {
            case "HIGH" -> workloadValueLabel.setForeground(new Color(200, 60, 60));
            case "MEDIUM" -> workloadValueLabel.setForeground(new Color(200, 160, 40));
            case "LOW" -> workloadValueLabel.setForeground(new Color(90, 170, 110));
            default -> workloadValueLabel.setForeground(new Color(80, 80, 80));
        }
    }

    // Load sidebar icons
    private ImageIcon loadIcon(String path, int size) {
        var url = getClass().getResource(path);
        if (url == null) return null;

        Image img = new ImageIcon(url).getImage()
                .getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}