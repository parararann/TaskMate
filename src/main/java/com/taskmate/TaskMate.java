package com.taskmate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TaskMate extends JFrame {

    private final Color navy = new Color(30, 41, 59);
    private final Color blue = new Color(59, 130, 246);
    private final Color background = new Color(245, 247, 251);

    // Kolom ID disimpan di model untuk operasi database, tetapi disembunyikan dari tabel.
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JLabel totalValue;
    private JLabel todoValue;
    private JLabel progressValue;
    private JLabel doneValue;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    public TaskMate() {
        setTitle("TaskMate - Manajemen Tugas");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createMainContent(), BorderLayout.CENTER);

        // Ambil data dari MySQL saat aplikasi dibuka.
        loadTasks();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBackground(navy);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 20, 15));

        JLabel logo = new JLabel("  TaskMate");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("  PERSONAL TASK MANAGER");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 9));
        subtitle.setForeground(new Color(203, 213, 225));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));

        JButton dashboardButton = createMenuButton("▦   Dashboard", true);
        JButton tasksButton = createMenuButton("☷   Daftar Tugas", false);
        tasksButton.addActionListener(e -> {
            if (taskTable != null) {
                taskTable.requestFocusInWindow();
            }
        });

        sidebar.add(dashboardButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(tasksButton);
        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("  TaskMate v1.0");
        footer.setForeground(new Color(203, 213, 225));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);

        return sidebar;
    }

    private JButton createMenuButton(String title, boolean active) {
        JButton button = new JButton(title);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(active ? blue : navy);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(190, 45));
        button.setPreferredSize(new Dimension(180, 45));
        button.setBorder(new EmptyBorder(0, 12, 0, 8));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(background);
        main.setBorder(new EmptyBorder(28, 30, 25, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(navy);

        JLabel description = new JLabel(
                "Kelola dan pantau semua tugasmu di satu tempat."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));
        description.setForeground(new Color(100, 116, 139));

        heading.add(title);
        heading.add(Box.createVerticalStrut(7));
        heading.add(description);

        JButton addButton = new JButton("+ Tambah Tugas");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(blue);
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(12, 18, 12, 18));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddTaskDialog());

        header.add(heading, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        JPanel stats = new JPanel(new GridLayout(1, 4, 15, 0));
        stats.setOpaque(false);

        totalValue = createStatValue();
        todoValue = createStatValue();
        progressValue = createStatValue();
        doneValue = createStatValue();

        stats.add(createStatCard("Total Tugas", totalValue,
                new Color(59, 130, 246)));
        stats.add(createStatCard("Belum Dikerjakan", todoValue,
                new Color(245, 158, 11)));
        stats.add(createStatCard("Sedang Dikerjakan", progressValue,
                new Color(139, 92, 246)));
        stats.add(createStatCard("Selesai", doneValue,
                new Color(16, 185, 129)));

        JPanel top = new JPanel(new BorderLayout(0, 25));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(stats, BorderLayout.CENTER);

        JPanel taskPanel = new JPanel(new BorderLayout(0, 15));
        taskPanel.setBackground(Color.WHITE);
        taskPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel taskTitle = new JLabel("Daftar Tugas");
        taskTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        taskTitle.setForeground(navy);

        searchField = new JTextField(15);
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
        searchButton.addActionListener(e -> refreshTable());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setOpaque(false);
        filters.add(searchField);
        filters.add(statusFilter);
        filters.add(searchButton);

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(taskTitle, BorderLayout.WEST);
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
        taskTable.setRowHeight(34);
        taskTable.setSelectionBackground(new Color(219, 234, 254));
        taskTable.setSelectionForeground(navy);
        taskTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        taskTable.getTableHeader().setBackground(new Color(241, 245, 249));
        taskTable.getTableHeader().setForeground(navy);

        // Sembunyikan kolom ID dari tampilan, tetapi tetap simpan di model.
        taskTable.removeColumn(taskTable.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JButton editButton = new JButton("Edit Tugas");
        JButton deleteButton = new JButton("Hapus Tugas");
        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(editButton);
        actions.add(deleteButton);

        taskPanel.add(tableHeader, BorderLayout.NORTH);
        taskPanel.add(scrollPane, BorderLayout.CENTER);
        taskPanel.add(actions, BorderLayout.SOUTH);

        main.add(top, BorderLayout.NORTH);
        main.add(taskPanel, BorderLayout.CENTER);

        refreshStats();
        return main;
    }

    private JLabel createStatValue() {
        JLabel value = new JLabel("0");
        value.setFont(new Font("SansSerif", Font.BOLD, 28));
        value.setForeground(navy);
        return value;
    }

    private JPanel createStatCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(15, 14, 15, 12)));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(100, 116, 139));

        card.add(label);
        card.add(Box.createVerticalStrut(12));
        card.add(value);
        return card;
    }

    // Membaca semua tugas dari MySQL dan menampilkannya di tabel.
    private void loadTasks() {
        String sql = "SELECT id, nama_tugas, deskripsi, tenggat, status "
                + "FROM tasks ORDER BY id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            tableModel.setRowCount(0);
            int number = 1;

            while (result.next()) {
                tableModel.addRow(new Object[]{
                        result.getInt("id"),
                        number++,
                        result.getString("nama_tugas"),
                        result.getString("deskripsi"),
                        result.getString("tenggat"),
                        result.getString("status")
                });
            }

            refreshStats();
            refreshTable();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tidak dapat mengambil data tugas dari database.\n"
                    + e.getMessage(),
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

            String sql = "INSERT INTO tasks (nama_tugas, deskripsi, tenggat, status) "
                    + "VALUES (?, ?, ?, ?)";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         sql, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, name);
                statement.setString(2, descriptionField.getText().trim());
                statement.setString(3, deadlineField.getText().trim());
                statement.setString(4, (String) statusBox.getSelectedItem());
                statement.executeUpdate();

                loadTasks();
                JOptionPane.showMessageDialog(this, "Tugas berhasil disimpan.");

            } catch (SQLException e) {
                showDatabaseError("menyimpan tugas", e);
            }
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

        JTextField name = new JTextField(tableModel.getValueAt(modelRow, 2).toString());
        JTextField description = new JTextField(tableModel.getValueAt(modelRow, 3).toString());
        JTextField deadline = new JTextField(tableModel.getValueAt(modelRow, 4).toString());

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

            String sql = "UPDATE tasks SET nama_tugas = ?, deskripsi = ?, "
                    + "tenggat = ?, status = ? WHERE id = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, name.getText().trim());
                statement.setString(2, description.getText().trim());
                statement.setString(3, deadline.getText().trim());
                statement.setString(4, (String) status.getSelectedItem());
                statement.setInt(5, taskId);
                statement.executeUpdate();

                loadTasks();
                JOptionPane.showMessageDialog(this, "Tugas berhasil diperbarui.");

            } catch (SQLException e) {
                showDatabaseError("memperbarui tugas", e);
            }
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
        String taskName = tableModel.getValueAt(modelRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah kamu yakin ingin menghapus tugas \"" + taskName + "\"?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM tasks WHERE id = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, taskId);
                statement.executeUpdate();
                loadTasks();
                JOptionPane.showMessageDialog(this, "Tugas berhasil dihapus.");

            } catch (SQLException e) {
                showDatabaseError("menghapus tugas", e);
            }
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

        // Indeks kolom model: ID=0, No.=1, Nama=2, Deskripsi=3, Tenggat=4, Status=5.
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

    private void showDatabaseError(String action, SQLException e) {
        JOptionPane.showMessageDialog(
                this,
                "Terjadi kesalahan saat " + action + ".\n" + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
        );
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
