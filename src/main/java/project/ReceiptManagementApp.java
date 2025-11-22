package project;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ReceiptManagementApp extends JFrame {
    private PermissionHandler permissionHandler;
    private ReceiptHandler receiptHandler;
    private UserHandler userHandler;
    private User currentUser;
    
    public ReceiptManagementApp() {
        setTitle("Receipt Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize handlers
        this.permissionHandler = new PermissionHandler();
        this.receiptHandler = new ReceiptHandler(permissionHandler);
        this.userHandler = new UserHandler();
        
        // Initialize default users
        initializeUsers();
        
        // Show login screen
        showLoginScreen();
        setVisible(true);
    }
    
    private void initializeUsers() {
        userHandler.createUser("admin", Role.ADMIN, "admin123");
        userHandler.createUser("alice", Role.MANAGER, "manager123");
        userHandler.createUser("bob", Role.ACCOUNTANT, "account123");
        userHandler.createUser("charlie", Role.SALESPERSON, "sales123");
        userHandler.createUser("diana", Role.SALESPERSON, "sales456");
    }
    
    // ================== LOGIN SYSTEM ==================
    
    private void showLoginScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Receipt Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        // Login form
        JPanel formPanel = createLoginForm();
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        revalidate();
    }
    
    private JPanel createLoginForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            new EmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField userField = new JTextField(15);
        panel.add(userField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(100, 30));
        panel.add(loginBtn, gbc);
        
        // Demo users info
        gbc.gridy = 3;
        JLabel infoLabel = new JLabel("<html><center><small>Demo Users:<br/>admin/admin123, alice/manager123, bob/account123, charlie/sales123</small></center></html>");
        panel.add(infoLabel, gbc);
        
        // Login action
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                showError("Please enter both username and password!");
                return;
            }
            
            if (userHandler.authenticateUser(username, password)) {
                currentUser = userHandler.findUser(username);
                showSuccess("Welcome, " + currentUser.getUsername() + "!");
                showDashboard();
            } else {
                showError("Invalid username or password!");
                passwordField.setText("");
            }
        });
        
        return panel;
    }
    
    // ================== DASHBOARD ROUTING ==================
    
    private void showDashboard() {
        switch (currentUser.getRole()) {
            case SALESPERSON -> showSalespersonDashboard();
            case ACCOUNTANT -> showAccountantDashboard();
            case MANAGER -> showManagerDashboard();
            case ADMIN -> showAdminDashboard();
        }
    }
    
    // ================== SALESPERSON DASHBOARD ==================
    
    private void showSalespersonDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header with navigation
        mainPanel.add(createHeader("Salesperson Dashboard"), BorderLayout.NORTH);
        
        // Split panel: form on left, receipts on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createReceiptSubmissionForm());
        splitPane.setRightComponent(createPersonalReceiptsPanel());
        splitPane.setDividerLocation(450); // Increased from 350 to give more space for form
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        revalidate();
    }
    
    private JPanel createReceiptSubmissionForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Submit New Receipt"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Form fields
        JTextField amountField = new JTextField(15);
        JTextField descField = new JTextField(15);
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField imageField = new JTextField("No file selected");
        imageField.setEditable(false);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Amount (DKK):"), gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(descField, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);
        
        // Image
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Receipt Image:"), gbc);
        gbc.gridx = 1;
        panel.add(imageField, gbc);
        
        // Browse button
        gbc.gridx = 2;
        JButton browseBtn = new JButton("Browse");
        panel.add(browseBtn, gbc);
        
        // Submit button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitBtn = new JButton("Submit Receipt");
        submitBtn.setPreferredSize(new Dimension(200, 35));
        panel.add(submitBtn, gbc);
        
        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif", "bmp"));
        browseBtn.addActionListener(e -> {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                imageField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        // Submit action
        submitBtn.addActionListener(e -> {
            try {
                if (amountField.getText().trim().isEmpty() || descField.getText().trim().isEmpty()) {
                    showError("Please fill in amount and description!");
                    return;
                }
                
                if (imageField.getText().equals("No file selected")) {
                    showError("Please select a receipt image!");
                    return;
                }
                
                double amount = Double.parseDouble(amountField.getText());
                String description = descField.getText().trim();
                LocalDate date = LocalDate.parse(dateField.getText());
                String imagePath = imageField.getText();
                
                receiptHandler.createReceipt(currentUser, amount, date, description, imagePath);
                showSuccess("Receipt submitted successfully!");
                
                // Clear form
                amountField.setText("");
                descField.setText("");
                dateField.setText(LocalDate.now().toString());
                imageField.setText("No file selected");
                
                // Refresh dashboard
                showSalespersonDashboard();
                
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });
        
        return panel;
    }
    
    private JPanel createPersonalReceiptsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("My Receipts"));
        
        // Table with more detailed columns
        String[] columns = {"ID", "Amount", "Description", "Date", "Status", "Handler", "Decision By"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Receipt receipt : receiptHandler.listReceipt(currentUser)) {
            String handler = receipt.getAccountant() != null ? receipt.getAccountant().getUsername() : "Not handled";
            String decisionBy = receipt.getStatusChangedBy() != null ? receipt.getStatusChangedBy().getUsername() : "Pending";
            
            model.addRow(new Object[]{
                receipt.getReceiptId(),
                String.format("DKK %.2f", receipt.getAmount()),
                receipt.getDescription(),
                receipt.getDate().toString(),
                receipt.getStatus().toString(),
                handler,
                decisionBy
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Button panel with View Details and Refresh
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int receiptId = (int) model.getValueAt(row, 0);
                Receipt receipt = receiptHandler.findReceiptById(receiptId);
                if (receipt != null) {
                    showReceiptDetailsDialog(receipt);
                }
            } else {
                showError("Please select a receipt first!");
            }
        });
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> showSalespersonDashboard());
        
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ================== ACCOUNTANT DASHBOARD ==================
    
    private void showAccountantDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        mainPanel.add(createHeader("Accountant Dashboard"), BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pending Receipts", createAccountantReceiptsPanel(true));
        tabbedPane.addTab("Handled Receipts", createAccountantReceiptsPanel(false));
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        revalidate();
    }
    
    private JPanel createAccountantReceiptsPanel(boolean pending) {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Amount", "Description", "Submitter", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Receipt receipt : receiptHandler.listReceipt(currentUser)) {
            if (pending) {
                // Show receipts that are PENDING and not yet handled by anyone
                if (receipt.getStatus() == Status.PENDING && receipt.getAccountant() == null) {
                    model.addRow(new Object[]{
                        receipt.getReceiptId(),
                        String.format("DKK %.2f", receipt.getAmount()),
                        receipt.getDescription(),
                        receipt.getSubmitter().getUsername(),
                        receipt.getDate().toString(),
                        receipt.getStatus().toString()
                    });
                }
            } else {
                // Show receipts that this accountant has handled (any status except PENDING)
                if (receipt.getAccountant() != null && 
                    receipt.getAccountant().getUsername().equals(currentUser.getUsername()) &&
                    receipt.getStatus() != Status.PENDING) {
                    model.addRow(new Object[]{
                        receipt.getReceiptId(),
                        String.format("DKK %.2f", receipt.getAmount()),
                        receipt.getDescription(),
                        receipt.getSubmitter().getUsername(),
                        receipt.getDate().toString(),
                        receipt.getStatus().toString()
                    });
                }
            }
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int receiptId = (int) model.getValueAt(row, 0);
                Receipt receipt = receiptHandler.findReceiptById(receiptId);
                if (receipt != null) {
                    showReceiptDetailsDialog(receipt);
                }
            } else {
                showError("Please select a receipt first!");
            }
        });
        buttonPanel.add(viewDetailsBtn);
        
        if (pending) {
            JButton handleBtn = new JButton("Mark as Handled");
            handleBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int receiptId = (int) model.getValueAt(row, 0);
                    try {
                        receiptHandler.handleReceipt(currentUser, receiptId);
                        showSuccess("Receipt marked as handled!");
                        showAccountantDashboard();
                    } catch (Exception ex) {
                        showError("Error: " + ex.getMessage());
                    }
                } else {
                    showError("Please select a receipt first!");
                }
            });
            buttonPanel.add(handleBtn);
        }
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> showAccountantDashboard());
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    // ================== MANAGER DASHBOARD ==================
    
    private void showManagerDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        mainPanel.add(createHeader("Manager Dashboard"), BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pending Review", createManagerReceiptsPanel("pending"));
        tabbedPane.addTab("Approved", createManagerReceiptsPanel("approved"));
        tabbedPane.addTab("Rejected", createManagerReceiptsPanel("rejected"));
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        revalidate();
    }
    
    private JPanel createManagerReceiptsPanel(String type) {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Amount", "Description", "Submitter", "Handler", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Receipt receipt : receiptHandler.listReceipt(currentUser)) {
            boolean shouldAdd = false;
            
            if (type.equals("pending") && receipt.getStatus() == Status.HANDLED) {
                shouldAdd = true;
            } else if (type.equals("approved") && receipt.getStatus() == Status.APPROVED) {
                shouldAdd = true;
            } else if (type.equals("rejected") && receipt.getStatus() == Status.REJECTED) {
                shouldAdd = true;
            }
            
            if (shouldAdd) {
                model.addRow(new Object[]{
                    receipt.getReceiptId(),
                    String.format("DKK %.2f", receipt.getAmount()),
                    receipt.getDescription(),
                    receipt.getSubmitter().getUsername(),
                    receipt.getAccountant() != null ? receipt.getAccountant().getUsername() : "N/A",
                    receipt.getDate().toString()
                });
            }
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int receiptId = (int) model.getValueAt(row, 0);
                Receipt receipt = receiptHandler.findReceiptById(receiptId);
                if (receipt != null) {
                    showReceiptDetailsDialog(receipt);
                }
            } else {
                showError("Please select a receipt first!");
            }
        });
        buttonPanel.add(viewDetailsBtn);
        
        if (type.equals("pending")) {
            JButton approveBtn = new JButton("Approve");
            approveBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int receiptId = (int) model.getValueAt(row, 0);
                    try {
                        receiptHandler.approveReceipt(currentUser, receiptId);
                        showSuccess("Receipt approved!");
                        showManagerDashboard();
                    } catch (Exception ex) {
                        showError("Error: " + ex.getMessage());
                    }
                } else {
                    showError("Please select a receipt first!");
                }
            });
            
            JButton rejectBtn = new JButton("Reject");
            rejectBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String reason = JOptionPane.showInputDialog(this, "Rejection reason:");
                    if (reason != null && !reason.trim().isEmpty()) {
                        int receiptId = (int) model.getValueAt(row, 0);
                        try {
                            receiptHandler.rejectReceipt(currentUser, receiptId, reason);
                            showSuccess("Receipt rejected!");
                            showManagerDashboard();
                        } catch (Exception ex) {
                            showError("Error: " + ex.getMessage());
                        }
                    }
                } else {
                    showError("Please select a receipt first!");
                }
            });
            
            buttonPanel.add(approveBtn);
            buttonPanel.add(rejectBtn);
        }
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> showManagerDashboard());
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    // ================== ADMIN DASHBOARD ==================
    
    private void showAdminDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        mainPanel.add(createHeader("Administrator Dashboard"), BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("System Overview", createSystemOverviewPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        revalidate();
    }
    
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // User list
        String[] columns = {"Username", "Role", "Password"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (User user : userHandler.listUsers(currentUser)) {
            model.addRow(new Object[]{
                user.getUsername(),
                user.getRole().toString(),
                user.getPassword()
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Password management panel
        JPanel passwordPanel = createPasswordManagementPanel();
        panel.add(passwordPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPasswordManagementPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Password Management"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // User selection dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select User:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> userCombo = new JComboBox<>();
        for (String username : userHandler.getAllUsernames()) {
            userCombo.addItem(username);
        }
        panel.add(userCombo, gbc);
        
        // New password field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField newPasswordField = new JPasswordField(15);
        panel.add(newPasswordField, gbc);
        
        // Change password button
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 2;
        JButton changePasswordBtn = new JButton("Change Password");
        panel.add(changePasswordBtn, gbc);
        
        // Change password action
        changePasswordBtn.addActionListener(e -> {
            String selectedUsername = (String) userCombo.getSelectedItem();
            String newPassword = new String(newPasswordField.getPassword());
            
            if (selectedUsername == null || newPassword.trim().isEmpty()) {
                showError("Please select a user and enter a new password!");
                return;
            }
            
            try {
                if (userHandler.changePassword(currentUser, selectedUsername, newPassword)) {
                    showSuccess("Password changed successfully for " + selectedUsername + "!");
                    newPasswordField.setText("");
                } else {
                    showError("Failed to change password!");
                }
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });
        
        return panel;
    }
    
    private JPanel createSystemOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // System statistics
        List<Receipt> allReceipts = receiptHandler.listReceipts();
        int totalReceipts = allReceipts.size();
        long pendingReceipts = allReceipts.stream().filter(r -> r.getStatus() == Status.PENDING).count();
        long approvedReceipts = allReceipts.stream().filter(r -> r.getStatus() == Status.APPROVED).count();
        long rejectedReceipts = allReceipts.stream().filter(r -> r.getStatus() == Status.REJECTED).count();
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("System Overview", SwingConstants.CENTER), gbc);
        
        gbc.gridy = 1;
        panel.add(new JLabel("Total Receipts: " + totalReceipts), gbc);
        
        gbc.gridy = 2;
        panel.add(new JLabel("Pending: " + pendingReceipts), gbc);
        
        gbc.gridy = 3;
        panel.add(new JLabel("Approved: " + approvedReceipts), gbc);
        
        gbc.gridy = 4;
        panel.add(new JLabel("Rejected: " + rejectedReceipts), gbc);
        
        gbc.gridy = 5;
        panel.add(new JLabel("Total Users: " + userHandler.getAllUsernames().size()), gbc);
        
        return panel;
    }
    
    // ================== UTILITY METHODS ==================
    
    private JPanel createHeader(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(new JLabel("User: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")"));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> showLoginScreen());
        rightPanel.add(logoutBtn);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showReceiptDetailsDialog(Receipt receipt) {
        JDialog dialog = new JDialog(this, "Receipt Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Receipt details
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        detailsPanel.add(new JLabel("Receipt ID:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(receipt.getReceiptId())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.format("DKK %.2f", receipt.getAmount())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(receipt.getDescription(), 3, 20);
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        detailsPanel.add(new JScrollPane(descArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(receipt.getDate().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        detailsPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(receipt.getStatus().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        detailsPanel.add(new JLabel("Submitted by:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(receipt.getSubmitter().getUsername()), gbc);
        
        if (receipt.getAccountant() != null) {
            gbc.gridx = 0; gbc.gridy = 6;
            detailsPanel.add(new JLabel("Handled by:"), gbc);
            gbc.gridx = 1;
            detailsPanel.add(new JLabel(receipt.getAccountant().getUsername()), gbc);
        }
        
        if (receipt.getStatusChangedBy() != null) {
            gbc.gridx = 0; gbc.gridy = 7;
            detailsPanel.add(new JLabel("Decision by:"), gbc);
            gbc.gridx = 1;
            detailsPanel.add(new JLabel(receipt.getStatusChangedBy().getUsername()), gbc);
        }
        
        if (receipt.getReason() != null && !receipt.getReason().isEmpty()) {
            gbc.gridx = 0; gbc.gridy = 8;
            detailsPanel.add(new JLabel("Rejection reason:"), gbc);
            gbc.gridx = 1;
            JTextArea reasonArea = new JTextArea(receipt.getReason(), 2, 20);
            reasonArea.setEditable(false);
            reasonArea.setWrapStyleWord(true);
            reasonArea.setLineWrap(true);
            detailsPanel.add(new JScrollPane(reasonArea), gbc);
        }
        
        // Receipt image
        if (receipt.getPhotoPath() != null && !receipt.getPhotoPath().isEmpty()) {
            gbc.gridx = 0; gbc.gridy = 9;
            detailsPanel.add(new JLabel("Receipt Image:"), gbc);
            gbc.gridx = 1; gbc.gridy = 10; gbc.gridwidth = 2;
            
            JPanel imagePanel = new JPanel(new BorderLayout());
            
            // Try to load and display the image
            try {
                java.io.File imageFile = new java.io.File(receipt.getPhotoPath());
                if (imageFile.exists()) {
                    javax.swing.ImageIcon imageIcon = new javax.swing.ImageIcon(receipt.getPhotoPath());
                    // Scale the image to fit nicely in the dialog
                    java.awt.Image image = imageIcon.getImage().getScaledInstance(200, 150, java.awt.Image.SCALE_SMOOTH);
                    javax.swing.ImageIcon scaledIcon = new javax.swing.ImageIcon(image);
                    
                    JLabel imageLabel = new JLabel(scaledIcon);
                    imageLabel.setBorder(BorderFactory.createEtchedBorder());
                    imagePanel.add(imageLabel, BorderLayout.CENTER);
                } else {
                    // If file doesn't exist, show path only
                    JLabel imageLabel = new JLabel("Image file not found: " + receipt.getPhotoPath());
                    imageLabel.setBorder(BorderFactory.createEtchedBorder());
                    imageLabel.setPreferredSize(new Dimension(200, 100));
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imagePanel.add(imageLabel, BorderLayout.CENTER);
                }
            } catch (Exception e) {
                // Fallback to showing just the path if image loading fails
                JLabel imageLabel = new JLabel("Cannot display image: " + receipt.getPhotoPath());
                imageLabel.setBorder(BorderFactory.createEtchedBorder());
                imageLabel.setPreferredSize(new Dimension(200, 100));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            }
            
            detailsPanel.add(imagePanel, gbc);
        }
        
        dialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReceiptManagementApp());
    }
}