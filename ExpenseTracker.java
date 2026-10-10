package expensetracker;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpenseTracker extends JFrame {

    private final Color NAVY = new Color(24, 30, 56);
    private final Color PURPLE = new Color(112, 82, 235);
    private final Color BG = new Color(245, 246, 252);
    private final Color TEXT = new Color(43, 49, 72);
    private final Color MUTED = new Color(130, 137, 158);
    private final Color GREEN = new Color(26, 165, 120);
    private final Color RED = new Color(220, 75, 94);
    private final Color ORANGE = new Color(239, 153, 58);

    private JTextField descriptionField;
    private JTextField amountField;
    private JTextField dateField;
    private JComboBox<String> categoryBox;

    private DefaultTableModel tableModel;
    private JTable expenseTable;

    private JLabel totalLabel;
    private JLabel countLabel;
    private JLabel averageLabel;

    private JPanel summaryPanel;

    private final String[] categories = {
        "Food", "Transport", "Education",
        "Shopping", "Bills", "Health", "Other"
    };

    public ExpenseTracker() {
        setTitle("Expense Tracker | Personal Finance");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createWorkspace(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(205, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 20, 15));

        JLabel logo = new JLabel("  $  MONEY FLOW");
        logo.setFont(new Font("SansSerif", Font.BOLD, 19));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel small = new JLabel("      PERSONAL FINANCE");
        small.setFont(new Font("SansSerif", Font.PLAIN, 10));
        small.setForeground(new Color(167, 174, 205));
        small.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(small);
        sidebar.add(Box.createVerticalStrut(45));

        sidebar.add(createNavItem("▦   Dashboard", true));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavItem("＋   Add Expense", false));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavItem("▤   Transactions", false));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavItem("◉   Categories", false));

        sidebar.add(Box.createVerticalGlue());

        JPanel tip = new JPanel();
        tip.setLayout(new BoxLayout(tip, BoxLayout.Y_AXIS));
        tip.setBackground(new Color(38, 45, 76));
        tip.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));
        tip.setAlignmentX(Component.LEFT_ALIGNMENT);
        tip.setMaximumSize(new Dimension(180, 115));

        JLabel tipTitle = new JLabel("SMART MONEY TIP");
        tipTitle.setForeground(new Color(191, 178, 255));
        tipTitle.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel tipText = new JLabel(
            "<html>Track every expense<br>to understand your<br>spending habits.</html>");
        tipText.setForeground(Color.WHITE);
        tipText.setFont(new Font("SansSerif", Font.PLAIN, 12));

        tip.add(tipTitle);
        tip.add(Box.createVerticalStrut(10));
        tip.add(tipText);

        sidebar.add(tip);
        sidebar.add(Box.createVerticalStrut(15));

        JLabel footer = new JLabel("  EXPENSE TRACKER v2.0");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footer.setForeground(new Color(155, 164, 195));
        sidebar.add(footer);

        return sidebar;
    }

    private JPanel createNavItem(String text, boolean active) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(180, 43));
        item.setPreferredSize(new Dimension(180, 43));
        item.setBackground(active ? PURPLE : NAVY);
        item.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(Color.WHITE);
        item.add(label, BorderLayout.CENTER);

        return item;
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(0, 0));
        workspace.setBackground(BG);
        workspace.add(createTopBar(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        body.add(createDashboard());
        body.add(Box.createVerticalStrut(18));
        body.add(createExpenseForm());
        body.add(Box.createVerticalStrut(18));
        body.add(createBottomSection());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(BG);

        workspace.add(scroll, BorderLayout.CENTER);
        return workspace;
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(17, 25, 17, 25));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Dashboard Overview");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcomeLabel.setForeground(TEXT);

        JLabel subtitle = new JLabel(
            "Welcome back! Here's your financial summary.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        titles.add(welcomeLabel);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);

        JLabel date = new JLabel("TODAY  |  " + LocalDate.now());
        date.setFont(new Font("SansSerif", Font.BOLD, 11));
        date.setForeground(PURPLE);

        top.add(titles, BorderLayout.WEST);
        top.add(date, BorderLayout.EAST);
        return top;
    }

    private JPanel createDashboard() {
        JPanel dashboard = new JPanel(new GridLayout(1, 3, 14, 0));
        dashboard.setOpaque(false);
        dashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        dashboard.setPreferredSize(new Dimension(700, 125));

        totalLabel = new JLabel("PKR 0.00");
        countLabel = new JLabel("0");
        averageLabel = new JLabel("PKR 0.00");

        dashboard.add(createStatCard("TOTAL SPENT", totalLabel, PURPLE, "↗"));
        dashboard.add(createStatCard("TRANSACTIONS", countLabel, GREEN, "#"));
        dashboard.add(createStatCard("AVERAGE EXPENSE", averageLabel, ORANGE, "◷"));

        return dashboard;
    }

    private JPanel createStatCard(
        String heading, JLabel value, Color accent, String symbol) {

        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(232, 234, 243)),
            BorderFactory.createEmptyBorder(17, 17, 17, 17)));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel(heading);
        title.setFont(new Font("SansSerif", Font.BOLD, 11));
        title.setForeground(MUTED);

        JLabel icon = new JLabel(symbol, SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.BOLD, 17));
        icon.setForeground(accent);
        icon.setPreferredSize(new Dimension(34, 34));

        top.add(title, BorderLayout.WEST);
        top.add(icon, BorderLayout.EAST);

        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(accent);

        card.add(top, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private JPanel createExpenseForm() {
        JPanel card = new JPanel(new BorderLayout(0, 17));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(232, 234, 243)),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        JLabel heading = new JLabel("＋  ADD NEW EXPENSE");
        heading.setFont(new Font("SansSerif", Font.BOLD, 15));
        heading.setForeground(TEXT);

        JPanel fields = new JPanel(new GridLayout(1, 4, 12, 0));
        fields.setOpaque(false);

        descriptionField = new JTextField();
        amountField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        categoryBox = new JComboBox<>(categories);

        fields.add(createInputGroup("Description", descriptionField));
        fields.add(createInputGroup("Amount (PKR)", amountField));
        fields.add(createInputGroup("Category", categoryBox));
        fields.add(createInputGroup("Date (YYYY-MM-DD)", dateField));

        JButton addButton = createButton(
            "＋  Add Expense", PURPLE, Color.WHITE);
        addButton.addActionListener(e -> addExpense());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel required = new JLabel("All fields are required");
        required.setForeground(MUTED);
        required.setFont(new Font("SansSerif", Font.PLAIN, 11));

        bottom.add(required, BorderLayout.WEST);
        bottom.add(addButton, BorderLayout.EAST);

        card.add(heading, BorderLayout.NORTH);
        card.add(fields, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createInputGroup(String title, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 7));
        group.setOpaque(false);

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(MUTED);

        input.setFont(new Font("SansSerif", Font.PLAIN, 13));
        input.setPreferredSize(new Dimension(120, 35));

        if (input instanceof JTextField) {
            input.setBackground(new Color(249, 250, 254));
            input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 239)),
                BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        } else {
            input.setBackground(new Color(249, 250, 254));
        }

        group.add(label, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }
    private JPanel createBottomSection() {
        JPanel section = new JPanel(new BorderLayout(15, 0));
        section.setOpaque(false);
        section.setPreferredSize(new Dimension(700, 330));
        section.setMinimumSize(new Dimension(500, 300));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        section.add(createHistoryPanel(), BorderLayout.CENTER);
        section.add(createSummaryPanel(), BorderLayout.EAST);
        return section;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(232, 234, 243)),
            BorderFactory.createEmptyBorder(17, 17, 17, 17)));

        JLabel heading = new JLabel("Recent Transactions");
        heading.setFont(new Font("SansSerif", Font.BOLD, 15));
        heading.setForeground(TEXT);

        String[] columns = {
            "Description", "Amount (PKR)", "Category", "Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setRowHeight(32);
        expenseTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        expenseTable.setForeground(TEXT);
        expenseTable.setGridColor(new Color(239, 241, 248));
        expenseTable.setShowVerticalLines(false);
        expenseTable.setSelectionBackground(new Color(232, 226, 255));
        expenseTable.setSelectionForeground(TEXT);
        expenseTable.setFillsViewportHeight(true);
        expenseTable.setAutoCreateRowSorter(true);

        JTableHeader header = expenseTable.getTableHeader();
        header.setPreferredSize(new Dimension(100, 35));
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setBackground(new Color(246, 247, 252));
        header.setForeground(MUTED);

        DefaultTableCellRenderer renderer =
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean selected,
                    boolean focus, int row, int column) {

                    Component c = super.getTableCellRendererComponent(
                        table, value, selected, focus, row, column);

                    if (!selected) {
                        c.setBackground(row % 2 == 0
                            ? Color.WHITE : new Color(249, 250, 254));
                        c.setForeground(TEXT);
                    }

                    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return c;
                }
            };

        for (int i = 0; i < expenseTable.getColumnCount(); i++) {
            expenseTable.getColumnModel().getColumn(i)
                .setCellRenderer(renderer);
        }

        JScrollPane scroll = new JScrollPane(expenseTable);
        scroll.setBorder(BorderFactory.createLineBorder(
            new Color(236, 238, 246)));

        JButton deleteButton = createButton(
            "Delete Selected", RED, Color.WHITE);
        deleteButton.addActionListener(e -> deleteExpense());

        JButton summaryButton = createButton(
            "Refresh Summary", new Color(239, 240, 249), TEXT);
        summaryButton.addActionListener(e -> refreshDashboard());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(summaryButton);
        actions.add(deleteButton);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(255, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(232, 234, 243)),
            BorderFactory.createEmptyBorder(17, 15, 17, 15)));

        JLabel heading = new JLabel("Spending by Category");
        heading.setFont(new Font("SansSerif", Font.BOLD, 14));
        heading.setForeground(TEXT);

        summaryPanel = new JPanel();
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));

        JLabel empty = new JLabel(
            "<html>Add expenses to see your breakdown.</html>");
        empty.setForeground(MUTED);
        empty.setFont(new Font("SansSerif", Font.PLAIN, 12));
        summaryPanel.add(empty);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(summaryPanel, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(
        String text, Color background, Color foreground) {

        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addExpense() {
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        String dateText = dateField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();

        if (description.isEmpty() || amountText.isEmpty()
                || dateText.isEmpty()) {
            showMessage("Please fill in all fields.",
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
            if (!Double.isFinite(amount) || amount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showMessage("Enter an amount greater than zero.",
                "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            showMessage("Use a valid date, for example 2026-10-10.",
                "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.addRow(new Object[] {
            description, String.format("%.2f", amount), category, dateText
        });

        descriptionField.setText("");
        amountField.setText("");
        dateField.setText(LocalDate.now().toString());
        categoryBox.setSelectedIndex(0);

        refreshDashboard();

        JOptionPane.showMessageDialog(this,
            "Expense added successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();

        if (selectedRow == -1) {
            showMessage("Please select a transaction first.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
            this,
            "Do you want to delete the selected expense?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            int modelRow = expenseTable.convertRowIndexToModel(selectedRow);
            tableModel.removeRow(modelRow);
            refreshDashboard();
        }
    }

    private void refreshDashboard() {
        if (tableModel == null) {
            return;
        }

        double total = 0;
        int count = tableModel.getRowCount();
        Map<String, Double> totals = new LinkedHashMap<>();

        for (String category : categories) {
            totals.put(category, 0.0);
        }

        for (int i = 0; i < count; i++) {
            double amount = Double.parseDouble(
                tableModel.getValueAt(i, 1).toString());
            String category = tableModel.getValueAt(i, 2).toString();

            total += amount;
            totals.put(category,
                totals.getOrDefault(category, 0.0) + amount);
        }

        totalLabel.setText(String.format("PKR %,.2f", total));
        countLabel.setText(String.valueOf(count));

        double average = count == 0 ? 0 : total / count;
        averageLabel.setText(String.format("PKR %,.2f", average));

        updateCategorySummary(totals, total);
    }

    private void updateCategorySummary(
        Map<String, Double> totals, double total) {

        summaryPanel.removeAll();

        Color[] colors = {
            PURPLE, GREEN, ORANGE, new Color(70, 145, 230),
            RED, new Color(30, 165, 180), new Color(150, 110, 180)
        };

        int index = 0;

        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            double amount = entry.getValue();
            if (amount <= 0) {
                continue;
            }

            JPanel item = new JPanel();
            item.setOpaque(false);
            item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 53));

            JPanel line = new JPanel(new BorderLayout());
            line.setOpaque(false);

            JLabel name = new JLabel(entry.getKey());
            name.setFont(new Font("SansSerif", Font.PLAIN, 11));
            name.setForeground(TEXT);

            JLabel value = new JLabel(String.format("%.0f", amount));
            value.setFont(new Font("SansSerif", Font.BOLD, 11));
            value.setForeground(TEXT);

            line.add(name, BorderLayout.WEST);
            line.add(value, BorderLayout.EAST);

            int percent = total == 0 ? 0 :
                (int) Math.round(amount / total * 100);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(percent);
            bar.setStringPainted(false);
            bar.setForeground(colors[index % colors.length]);
            bar.setBackground(new Color(239, 241, 248));
            bar.setBorderPainted(false);
            bar.setPreferredSize(new Dimension(180, 7));
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));

            item.add(line);
            item.add(Box.createVerticalStrut(6));
            item.add(bar);
            item.add(Box.createVerticalStrut(9));

            summaryPanel.add(item);
            index++;
        }

        if (index == 0) {
            JLabel empty = new JLabel(
                "<html>No expenses yet.<br>Add one to see your<br>category breakdown.</html>");
            empty.setForeground(MUTED);
            empty.setFont(new Font("SansSerif", Font.PLAIN, 12));
            summaryPanel.add(empty);
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Use the default Swing appearance.
            }

            ExpenseTracker app = new ExpenseTracker();
            app.setVisible(true);
        });
    }
}