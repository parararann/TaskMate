package com.taskmate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TaskMate extends JFrame {

    private final Color navy = new Color(30, 41, 59);
    private final Color blue = new Color(59, 130, 246);
    private final Color background = new Color(245, 247, 251);
    private final Color sidebarInactive = new Color(51, 65, 85);

    private final TaskDAO taskDAO = new TaskDAO();

    private CardLayout cardLayout;
    private JPanel pagePanel;

    private JButton dashboardButton;
    private JButton tasksButton;

    private JTable taskTable;
    private DefaultTableModel tableModel;

    private JLabel totalValue;
    private JLabel todoValue;
    private JLabel progressValue;
    private JLabel doneValue;

    private JTextField searchField;
    private JComboBox<String> statusFilter;

    private static final String PAGE_DASHBOARD = "dashboard";
    private static final String PAGE_TASKS = "tasks";

    public TaskMate() {
        setTitle("TaskMate - Manajemen Tugas");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        pagePanel = new JPanel();
        cardLayout = new CardLayout();
        pagePanel.setLayout(cardLayout);
        pagePanel.setBackground(background);

        pagePanel.add(createDashboardPage(), PAGE_DASHBOARD);
        pagePanel.add(createTasksPage(), PAGE_TASKS);

        add(createSidebar(), BorderLayout.WEST);
        add(pagePanel, BorderLayout.CENTER);

        loadTasks();
        showPage(PAGE_DASHBOARD);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(navy);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 18, 20, 18));

        JLabel logo = new JLabel("TaskMate");
        logo.setFont(new Font("SansSerif", Font.BOLD, 25));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("PERSONAL TASK MANAGER");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitle.setForeground(new Color(203, 213, 225));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(7));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(55));

        dashboardButton = createMenuButton("▦   Dashboard");
        tasksButton = createMenuButton("☷   Daftar Tugas");

        dashboardButton.addActionListener(e -> showPage(PAGE_DASHBOARD));
        tasksButton.addActionListener(e -> showPage(PAGE_TASKS));

        sidebar.add(dashboardButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(tasksButton);
        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("TaskMate v1.0");
        footer.setForeground(new Color(203, 213, 225));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);

        return sidebar;
    }

    private JButton createMenuButton(String title) {
        JButton button = new JButton(title);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(sidebarInactive);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(200, 48));
        button.setPreferredSize(new Dimension(195, 48));
        button.setBorder(new EmptyBorder(0, 14, 0, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private void showPage(String pageName) {
        cardLayout.show(pagePanel, pageName);

        boolean dashboardActive = PAGE_DASHBOARD.equals(pageName);
        dashboardButton.setBackground(dashboardActive ? blue : sidebarInactive);
        tasksButton.setBackground(dashboardActive ? sidebarInactive : blue);

        dashboardButton.setForeground(Color.WHITE);
        tasksButton.setForeground(Color.WHITE);

        if (PAGE_DASHBOARD.equals(pageName)) {
            refreshStats();
        } else {
            refreshTable();
        }
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
    }

    private JPanel createDashboardPage() {
        JPanel page = new JPanel(new BorderLayout(0, 25));
        page.setBackground(background);
        page.setBorder(new EmptyBorder(30, 32, 28, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 29));
        title.setForeground(navy);

        JLabel description = new JLabel(
                "Ringkasan dan pantau perkembangan seluruh tugasmu."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(new Color(100, 116, 139));

        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(description);

        JButton addButton = new JButton("+ Tambah Tugas");
        styleButton(addButton, blue, Color.WHITE);
        addButton.addActionListener(e -> showAddTaskDialog());

        JButton viewTasksButton = new JButton("Lihat Daftar Tugas");
        styleButton(viewTasksButton, navy, Color.WHITE);
        viewTasksButton.addActionListener(e -> showPage(PAGE_TASKS));

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerActions.setOpaque(false);
        headerActions.add(viewTasksButton);
        headerActions.add(addButton);

        header.add(heading, BorderLayout.WEST);
        header.add(headerActions, BorderLayout.EAST);

        JPanel stats = new JPanel(new GridLayout(2, 2, 16, 16));
        stats.setOpaque(false);

        totalValue = createStatValue();
        todoValue = createStatValue();
        progressValue = createStatValue();
        doneValue = createStatValue();

        stats.add(createStatCard("Total Tugas", totalValue, blue));
        stats.add(createStatCard("Belum Dikerjakan", todoValue,
                new Color(245, 158, 11)));
        stats.add(createStatCard("Sedang Dikerjakan", progressValue,
                new Color(139, 92, 246)));
        stats.add(createStatCard("Selesai", doneValue,
                new Color(16, 185, 129)));

        JPanel information = new JPanel();
        information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
        information.setBackground(Color.WHITE);
        information.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(22, 24, 22, 24)));

        JLabel infoTitle = new JLabel("Selamat datang di TaskMate");
        infoTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        infoTitle.setForeground(navy);

        JLabel infoText = new JLabel(
                "<html>Gunakan menu <b>Daftar Tugas</b> untuk melihat, mencari, " +
                "memfilter, mengedit, dan menghapus tugas. " +
                "Semua perubahan tersimpan ke database melalui Hibernate.</html>"
        );
        infoText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoText.setForeground(new Color(71, 85, 105));
        infoText.setAlignmentX(Component.LEFT_ALIGNMENT);

        information.add(infoTitle);
        information.add(Box.createVerticalStrut(12));
        information.add(infoText);

        JPanel top = new JPanel(new BorderLayout(0, 25));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(stats, BorderLayout.CENTER);

        page.add(top, BorderLayout.NORTH);
        page.add(information, BorderLayout.CENTER);

        return page;
    }

    private JPanel createTasksPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(background);
        page.setBorder(new EmptyBorder(30, 32, 28, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Daftar Tugas");
        title.setFont(new Font("SansSerif", Font.BOLD, 29));
        title.setForeground(navy);

        JLabel description = new JLabel(
                "Kelola seluruh tugas yang tersimpan di database."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(new Color(100, 116, 139));

        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(description);

        JButton addButton = new JButton("+ Tambah Tugas");
        styleButton(addButton, blue, Color.WHITE);
        addButton.addActionListener(e -> showAddTaskDialog());

        header.add(heading, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        JPanel taskPanel = new JPanel(new BorderLayout(0, 15));
        taskPanel.setBackground(Color.WHITE);
        taskPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(18, 18, 18, 18)));

        searchField = new JTextField(16);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setToolTipText("Cari nama tugas");
        searchField.addActionListener(e -> refreshTable());

        statusFilter = new JComboBox<>(new String[]{
                "Semua Status",
                "Belum Dikerjakan",
                "Sedang Dikerjakan",
                "Selesai"
        });
        statusFilter.addActionListener(e -> refreshTable());

        JButton searchButton = new JButton("Cari");
        styleButton(searchButton, blue, Color.WHITE);
        searchButton.addActionListener(e -> refreshTable());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setOpaque(false);
        filters.add(searchField);
        filters.add(statusFilter);
        filters.add(searchButton);

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);

        JLabel tableTitle = new JLabel("Seluruh Data Tugas");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        tableTitle.setForeground(navy);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(filters, BorderLayout.EAST);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "No.", "Nama Tugas", "Deskripsi", "Tenggat", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        taskTable.setRowHeight(36);
        taskTable.setSelectionBackground(new Color(219, 234, 254));
        taskTable.setSelectionForeground(navy);
        taskTable.setGridColor(new Color(226, 232, 240));
        taskTable.setShowGrid(true);
        taskTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        taskTable.getTableHeader().setBackground(new Color(241, 245, 249));
        taskTable.getTableHeader().setForeground(navy);
        taskTable.getTableHeader().setReorderingAllowed(false);

        // ID tetap ada di model untuk operasi DAO, tetapi tidak ditampilkan.
        taskTable.removeColumn(taskTable.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JButton editButton = new JButton("Edit Tugas");
        JButton deleteButton = new JButton("Hapus Tugas");
        styleButton(editButton, new Color(37, 99, 235), Color.WHITE);
        styleButton(deleteButton, new Color(220, 38, 38), Color.WHITE);

        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(editButton);
        actions.add(deleteButton);

        taskPanel.add(tableHeader, BorderLayout.NORTH);
        taskPanel.add(scrollPane, BorderLayout.CENTER);
        taskPanel.add(actions, BorderLayout.SOUTH);

        page.add(header, BorderLayout.NORTH);
        page.add(taskPanel, BorderLayout.CENTER);

        return page;
    }

    private JLabel createStatValue() {
        JLabel value = new JLabel("0");
        value.setFont(new Font("SansSerif", Font.BOLD, 30));
        value.setForeground(navy);
        return value;
    }

    private JPanel createStatCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                new EmptyBorder(20, 18, 20, 18)));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(100, 116, 139));

        card.add(label);
        card.add(Box.createVerticalStrut(14));
        card.add(value);
        return card;
    }

    private void loadTasks() {
        try {
            List<Task> tasks = taskDAO.tampilkanSemuaTask();

            tableModel.setRowCount(0);
            int number = 1;

            for (Task task : tasks) {
                tableModel.addRow(new Object[]{
                    task.getId(),
                    number++,
                    task.getNamaTugas(),
                    task.getDeskripsi(),
                    task.getTenggat(),
                    task.getStatus()
                });
            }

            refreshStats();
            refreshTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tidak dapat mengambil data tugas dari database.\n" + e.getMessage(),
                    "Kesalahan Database",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showAddTaskDialog() {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField deadlineField = new JTextField();

        JComboBox<String> statusBox = new JComboBox<>(new String[]{
                "Belum Dikerjakan",
                "Sedang Dikerjakan",
                "Selesai"
        });

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Nama tugas:"));
        form.add(nameField);
        form.add(new JLabel("Deskripsi:"));
        form.add(descriptionField);
        form.add(new JLabel("Tenggat (contoh: 30-10-2026):"));
        form.add(deadlineField);
        form.add(new JLabel("Status:"));
        form.add(statusBox);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Tambah Tugas",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Nama tugas wajib diisi.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Task task = new Task();
            task.setNamaTugas(name);
            task.setDeskripsi(descriptionField.getText().trim());
            task.setTenggat(deadlineField.getText().trim());
            task.setStatus((String) statusBox.getSelectedItem());

            taskDAO.tambahTask(task);
            loadTasks();

            JOptionPane.showMessageDialog(this, "Tugas berhasil disimpan.");
        }
    }

    private void editTask() {
        int viewRow = taskTable.getSelectedRow();

        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this, "Pilih tugas yang ingin diedit terlebih dahulu.");
            return;
        }

        int modelRow = taskTable.convertRowIndexToModel(viewRow);
        int taskId = ((Number) tableModel.getValueAt(modelRow, 0)).intValue();

        JTextField name = new JTextField(
                String.valueOf(tableModel.getValueAt(modelRow, 2)));
        JTextField description = new JTextField(
                String.valueOf(tableModel.getValueAt(modelRow, 3)));
        JTextField deadline = new JTextField(
                String.valueOf(tableModel.getValueAt(modelRow, 4)));

        JComboBox<String> status = new JComboBox<>(new String[]{
                "Belum Dikerjakan",
                "Sedang Dikerjakan",
                "Selesai"
        });
        status.setSelectedItem(tableModel.getValueAt(modelRow, 5).toString());

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Nama tugas:"));
        form.add(name);
        form.add(new JLabel("Deskripsi:"));
        form.add(description);
        form.add(new JLabel("Tenggat:"));
        form.add(deadline);
        form.add(new JLabel("Status:"));
        form.add(status);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Edit Tugas", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            if (name.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama tugas wajib diisi.");
                return;
            }

            Task updatedTask = new Task();
            updatedTask.setId(taskId);
            updatedTask.setNamaTugas(name.getText().trim());
            updatedTask.setDeskripsi(description.getText().trim());
            updatedTask.setTenggat(deadline.getText().trim());
            updatedTask.setStatus((String) status.getSelectedItem());

            taskDAO.updateTask(updatedTask);
            loadTasks();

            JOptionPane.showMessageDialog(this, "Tugas berhasil diperbarui.");
        }
    }

    private void deleteTask() {
        int viewRow = taskTable.getSelectedRow();

        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this, "Pilih tugas yang ingin dihapus terlebih dahulu.");
            return;
        }

        int modelRow = taskTable.convertRowIndexToModel(viewRow);
        int taskId = ((Number) tableModel.getValueAt(modelRow, 0)).intValue();
        String taskName = String.valueOf(tableModel.getValueAt(modelRow, 2));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah kamu yakin ingin menghapus tugas \"" + taskName + "\"?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            taskDAO.hapusTask(taskId);
            loadTasks();
            JOptionPane.showMessageDialog(this, "Tugas berhasil dihapus.");
        }
    }

    private void refreshTable() {
        if (taskTable == null || tableModel == null
                || searchField == null || statusFilter == null) {
            return;
        }

        String query = searchField.getText().trim();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(tableModel);
        taskTable.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Indeks model: ID=0, No.=1, Nama=2, Deskripsi=3, Tenggat=4, Status=5.
        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter(
                    "(?i)" + Pattern.quote(query), 2));
        }

        if (!"Semua Status".equals(selectedStatus)) {
            filters.add(RowFilter.regexFilter(
                    "^" + Pattern.quote(selectedStatus) + "$", 5));
        }

        sorter.setRowFilter(filters.isEmpty()
                ? null : RowFilter.andFilter(filters));
    }

    private void refreshStats() {
        if (tableModel == null || totalValue == null || todoValue == null
                || progressValue == null || doneValue == null) {
            return;
        }

        int total = tableModel.getRowCount();
        int todo = 0;
        int progress = 0;
        int done = 0;

        for (int i = 0; i < total; i++) {
            String status = String.valueOf(tableModel.getValueAt(i, 5));

            switch (status) {
                case "Belum Dikerjakan" -> todo++;
                case "Sedang Dikerjakan" -> progress++;
                case "Selesai" -> done++;
                default -> { }
            }
        }

        totalValue.setText(String.valueOf(total));
        todoValue.setText(String.valueOf(todo));
        progressValue.setText(String.valueOf(progress));
        doneValue.setText(String.valueOf(done));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Gunakan tampilan default jika Look and Feel sistem gagal.
            }

            new TaskMate().setVisible(true);
        });
    }
}
