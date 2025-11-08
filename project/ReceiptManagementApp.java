package project;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


public class ReceiptManagementApp extends JFrame {
    private PermissionHandler permissionHandler;
    private ReceiptHandler receiptHandler;
    private User currentUser;
    
    private void showReceiptDetails(Receipt receipt) {
        JDialog dialog = new JDialog(this, "Receipt Details", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        // Create the main content panel with GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add receipt details
        addDetailField(contentPanel, gbc, "Receipt ID:", String.valueOf(receipt.getReceiptId()));
        addDetailField(contentPanel, gbc, "Amount:", String.format("$%.2f", receipt.getAmount()));
        addDetailField(contentPanel, gbc, "Description:", receipt.getDescription());
        addDetailField(contentPanel, gbc, "Submitted By:", receipt.getSubmitter().getUsername());
        addDetailField(contentPanel, gbc, "Submission Date:", receipt.getDate().toString());
        addDetailField(contentPanel, gbc, "Status:", receipt.getStatus().toString());

        if (receipt.getAccountant() != null) {
            addDetailField(contentPanel, gbc, "Handled By:", receipt.getAccountant().getUsername());
            addDetailField(contentPanel, gbc, "Handled Date:", receipt.getAccountantDate().toString());
        }

        if (receipt.getStatus() == Status.APPROVED || receipt.getStatus() == Status.REJECTED) {
            addDetailField(contentPanel, gbc, "Decision By:", receipt.getStatusChangedBy().getUsername());
            addDetailField(contentPanel, gbc, "Decision Date:", receipt.statusChangedAt().toString());
            if (receipt.getStatus() == Status.REJECTED) {
                addDetailField(contentPanel, gbc, "Rejection Reason:", receipt.getReason());
            }
        }

        // Add image panel
        try {
            ImageIcon imageIcon = new ImageIcon(receipt.getPhotoPath());
            // Scale the image if it's too large
            if (imageIcon.getIconWidth() > 300) {
                imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(300, -1, java.awt.Image.SCALE_SMOOTH));
            }
            JLabel imageLabel = new JLabel(imageIcon);
            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.setBorder(BorderFactory.createTitledBorder("Receipt Image"));
            imagePanel.add(imageLabel, BorderLayout.CENTER);
            
            // Add a scroll pane for the content
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            JScrollPane detailsScroll = new JScrollPane(contentPanel);
            detailsScroll.setBorder(null);
            
            mainPanel.add(detailsScroll, BorderLayout.CENTER);
            mainPanel.add(imagePanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Image not available");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dialog.add(errorLabel, BorderLayout.SOUTH);
        }

        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    private void addDetailField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField field = new JTextField(value);
        field.setEditable(false);
        field.setBorder(null);
        field.setBackground(null);
        panel.add(field, gbc);
        
        gbc.gridy++;
    }


    public ReceiptManagementApp() {
        setTitle("Receipt Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize handlers
        this.permissionHandler = new PermissionHandler();
        this.receiptHandler = new ReceiptHandler(permissionHandler);
        
        // Show login screen first
        showLoginScreen();
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReceiptManagementApp());
    }


    private void showLoginScreen() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        // Title panel at the top
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Receipt Management System");
        titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f));
        titlePanel.add(titleLabel);
        
        // Form panel in the center
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            new EmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;
        
        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:", SwingConstants.RIGHT);
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField userField = new JTextField(15);
        formPanel.add(userField, gbc);
        
        // Role label and combo box
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel roleLabel = new JLabel("Role:", SwingConstants.RIGHT);
        formPanel.add(roleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        JComboBox<Role> roleBox = new JComboBox<>(Role.values());
        roleBox.setPreferredSize(userField.getPreferredSize());
        formPanel.add(roleBox, gbc);
        
        // Login button in its own panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(120, 30));
        formPanel.add(loginBtn, gbc);
        
        // Add components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        revalidate();
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Role role = (Role) roleBox.getSelectedItem();
            currentUser = new User(username, role);
            showMainDashboard();
        });
        
        // panel variable does not exist here; ensure the main panel is set and revalidated
        setContentPane(mainPanel);
        revalidate();
    }


    private void showMainDashboard() {
        if (currentUser.getRole() == Role.SALESPERSON) {
            showSalespersonDashboard();
        } else if (currentUser.getRole() == Role.ACCOUNTANT) {
            showAccountantDashboard();
        } else if (currentUser.getRole() == Role.MANAGER) {
            showManagerDashboard();
        }
    }


private void showSalespersonDashboard() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    
    // Navigation bar
    JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JLabel userInfo = new JLabel("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
    JButton logoutBtn = new JButton("Logout");
    logoutBtn.addActionListener(e -> showLoginScreen());
    navPanel.add(userInfo);
    navPanel.add(logoutBtn);
    mainPanel.add(navPanel, BorderLayout.NORTH);
    
    // Form panel - Now using GridBagLayout for better control
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);
    
    // Amount field
    JLabel amountLabel = new JLabel("Amount:");
    JTextField amountField = new JTextField(20);
    
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.0;
    panel.add(amountLabel, gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    panel.add(amountField, gbc);
    
    // Description field
    JLabel descLabel = new JLabel("Description:");
    JTextField descField = new JTextField(20);
    
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0.0;
    panel.add(descLabel, gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    panel.add(descField, gbc);
    
    // Date field
    JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
    JTextField dateField = new JTextField(LocalDate.now().toString());
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0.0;
    panel.add(dateLabel, gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    panel.add(dateField, gbc);
    
    // Image upload field
    JLabel imageLabel = new JLabel("Receipt Image:");
    JTextField imagePathField = new JTextField(20);
    imagePathField.setEditable(false); // Make it read-only
    imagePathField.setText("No file selected");
    
    JButton browseBtn = new JButton("Browse...");
    JFileChooser fileChooser = new JFileChooser();
    
    // Add file filter for images only
    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String name = f.getName().toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                   name.endsWith(".png") || name.endsWith(".gif");
        }
        
        @Override
        public String getDescription() {
            return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
        }
    });
    
    browseBtn.addActionListener(e -> {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    });
    
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 0.0;
    panel.add(imageLabel, gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 0.7;
    panel.add(imagePathField, gbc);
    
    gbc.gridx = 2;
    gbc.weightx = 0.3;
    panel.add(browseBtn, gbc);
    
    // Buttons
    JButton submitBtn = new JButton("Submit Receipt");
    JButton clearBtn = new JButton("Clear");
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    buttonPanel.add(submitBtn);
    buttonPanel.add(clearBtn);
    
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 3;
    gbc.insets = new Insets(15, 5, 5, 5);
    panel.add(buttonPanel, gbc);
    
    submitBtn.addActionListener(e -> {
        try {
            if (amountField.getText().trim().isEmpty() || descField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if image is selected
            if (imagePathField.getText().equals("No file selected")) {
                int choice = JOptionPane.showConfirmDialog(this, 
                    "No receipt image selected. Do you want to continue without an image?", 
                    "No Image", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            double amount = Double.parseDouble(amountField.getText());
            String description = descField.getText().trim();
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String imagePath = imagePathField.getText().equals("No file selected") ? "no_image" : imagePathField.getText();
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            receiptHandler.createReceipt(currentUser, amount, date, description, imagePath);
            JOptionPane.showMessageDialog(this, "Receipt submitted successfully! \n PLease keep the original receipt!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            amountField.setText("");
            descField.setText("");
            dateField.setText(LocalDate.now().toString());
            imagePathField.setText("No file selected");
            
            // Refresh the receipts table
            showSalespersonDashboard();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    
    clearBtn.addActionListener(e -> {
        amountField.setText("");
        descField.setText("");
        dateField.setText(LocalDate.now().toString());
        imagePathField.setText("No file selected");
    });
    
    // Add a panel to contain both the form and table
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(panel, BorderLayout.NORTH);
    
    // Add table showing user's receipts
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Receipt ID");
    model.addColumn("Amount");
    model.addColumn("Description");
    model.addColumn("Date");
    model.addColumn("Status");
    model.addColumn("Comments");
    
    // Populate table with current user's receipts
    for (Receipt receipt : receiptHandler.listReceipts()) {
        if (receipt.getSubmitter().getUsername().equals(currentUser.getUsername())) {
            String status = receipt.getStatus().toString();
            if (receipt.getStatus() == Status.PENDING && receipt.getAccountant() != null) {
                status = "Under Review";
            }
            
            String comments = "";
            if (receipt.getStatus() == Status.REJECTED && receipt.getReason() != null) {
                comments = "Rejected: " + receipt.getReason();
            } else if (receipt.getStatus() == Status.APPROVED) {
                comments = "Approved";
            }
            
            model.addRow(new Object[]{
                receipt.getReceiptId(),
                String.format("$%.2f", receipt.getAmount()),
                receipt.getDescription(),
                receipt.getDate().toString(),
                status,
                comments
            });
        }
    }
    
        JTable table = new JTable(model);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Receipts"));
        
        // Add refresh and view details buttons for the table
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = table.convertRowIndexToModel(selectedRow);
                int receiptId = (int) model.getValueAt(selectedRow, 0);
                Receipt receipt = receiptHandler.findReceiptById(receiptId);
                if (receipt != null) {
                    showReceiptDetails(receipt);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a receipt to view details.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> showSalespersonDashboard());
        
        tableButtonPanel.add(viewDetailsBtn);
        tableButtonPanel.add(refreshBtn);    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(scrollPane, BorderLayout.CENTER);
    tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);
    tablePanel.setBorder(new EmptyBorder(10, 20, 20, 20));
    
    centerPanel.add(tablePanel, BorderLayout.CENTER);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    
    setContentPane(mainPanel);
    revalidate();
}



    private void showAccountantDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Navigation bar
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userInfo = new JLabel("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> showLoginScreen());
        navPanel.add(userInfo);
        navPanel.add(logoutBtn);
        mainPanel.add(navPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for different receipt categories
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pending Receipts Tab
        JPanel pendingPanel = createReceiptPanel(true);
        tabbedPane.addTab("Pending Receipts", pendingPanel);
        
        // Handled Receipts Tab
        JPanel handledPanel = createReceiptPanel(false);
        tabbedPane.addTab("Handled Receipts", handledPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
        revalidate();
    }
    
    private JPanel createReceiptPanel(boolean isPending) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Table showing receipts
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Receipt ID");
        model.addColumn("Amount");
        model.addColumn("Description");
        model.addColumn("Submitted By");
        model.addColumn("Date");
        model.addColumn("Status");
        if (!isPending) {
            model.addColumn("Handled Date");
            model.addColumn("Manager Decision");
        }
        
        for (Receipt receipt : receiptHandler.listReceipts()) {
            if (isPending && receipt.getStatus() == Status.PENDING && receipt.getAccountant() == null) {
                addReceiptToTable(model, receipt, false);
            } else if (!isPending && receipt.getAccountant() != null && 
                      receipt.getAccountant().getUsername().equals(currentUser.getUsername())) {
                addReceiptToTable(model, receipt, true);
            }
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        
        // Add sorting capability
        table.setAutoCreateRowSorter(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Add View Details button
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = table.convertRowIndexToModel(selectedRow);
                int receiptId = (int) model.getValueAt(selectedRow, 0);
                Receipt receipt = receiptHandler.findReceiptById(receiptId);
                if (receipt != null) {
                    showReceiptDetails(receipt);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a receipt to view details.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(viewDetailsBtn);
        
        if (isPending) {
            JButton handleBtn = new JButton("Mark as Handled");
            handleBtn.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedRow = table.convertRowIndexToModel(selectedRow); // Convert view index to model index
                    int receiptId = (int) model.getValueAt(selectedRow, 0);
                    receiptHandler.handleReceipt(currentUser, receiptId);
                    JOptionPane.showMessageDialog(this, "Receipt marked as handled!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showAccountantDashboard();
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a receipt first!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(handleBtn);
        }
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> showAccountantDashboard());
        buttonPanel.add(refreshBtn);
        
        // Add summary labels
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        int totalReceipts = table.getRowCount();
        double totalAmount = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            String amountStr = ((String) table.getValueAt(i, 1)).replace("$", "");
            totalAmount += Double.parseDouble(amountStr);
        }
        
        JLabel summaryLabel = new JLabel(String.format("Total Receipts: %d | Total Amount: $%.2f", 
                                       totalReceipts, totalAmount));
        summaryPanel.add(summaryLabel);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(summaryPanel, BorderLayout.WEST);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        
        panel.add(southPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private void addReceiptToTable(DefaultTableModel model, Receipt receipt, boolean includeHandledInfo) {
        Object[] rowData;
        if (includeHandledInfo) {
            String managerDecision = "Pending";
            if (receipt.getStatus() == Status.APPROVED) {
                managerDecision = "Approved";
            } else if (receipt.getStatus() == Status.REJECTED) {
                managerDecision = "Rejected: " + receipt.getReason();
            }
            
            rowData = new Object[]{
                receipt.getReceiptId(),
                String.format("$%.2f", receipt.getAmount()),
                receipt.getDescription(),
                receipt.getSubmitter().getUsername(),
                receipt.getDate().toString(),
                receipt.getStatus(),
                receipt.getAccountantDate().toString(),
                managerDecision
            };
        } else {
            rowData = new Object[]{
                receipt.getReceiptId(),
                String.format("$%.2f", receipt.getAmount()),
                receipt.getDescription(),
                receipt.getSubmitter().getUsername(),
                receipt.getDate().toString(),
                receipt.getStatus()
            };
        }
        model.addRow(rowData);
    }


    private void showManagerDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Navigation bar
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userInfo = new JLabel("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> showLoginScreen());
        navPanel.add(userInfo);
        navPanel.add(logoutBtn);
        mainPanel.add(navPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for different receipt categories
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pending Review Tab
        JPanel pendingReviewPanel = createManagerReceiptPanel("pending");
        tabbedPane.addTab("Pending Review", pendingReviewPanel);
        
        // Approved Tab
        JPanel approvedPanel = createManagerReceiptPanel("approved");
        tabbedPane.addTab("Approved", approvedPanel);
        
        // Rejected Tab
        JPanel rejectedPanel = createManagerReceiptPanel("rejected");
        tabbedPane.addTab("Rejected", rejectedPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
        revalidate();
    }
    
    private JPanel createManagerReceiptPanel(String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Table showing receipts
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Receipt ID");
        model.addColumn("Amount");
        model.addColumn("Description");
        model.addColumn("Submitted By");
        model.addColumn("Submitted Date");
        model.addColumn("Handled By");
        model.addColumn("Handled Date");
        if (!type.equals("pending")) {
            model.addColumn("Decision Date");
            if (type.equals("rejected")) {
                model.addColumn("Rejection Reason");
            }
        }
        
        double totalAmount = 0;
        for (Receipt receipt : receiptHandler.listReceipts()) {
            boolean shouldAdd = false;
            if (type.equals("pending") && receipt.getAccountant() != null && receipt.getStatus() == Status.PENDING) {
                shouldAdd = true;
            } else if (type.equals("approved") && receipt.getStatus() == Status.APPROVED) {
                shouldAdd = true;
            } else if (type.equals("rejected") && receipt.getStatus() == Status.REJECTED) {
                shouldAdd = true;
            }
            
            if (shouldAdd) {
                Object[] rowData;
                if (type.equals("pending")) {
                    rowData = new Object[]{
                        receipt.getReceiptId(),
                        String.format("$%.2f", receipt.getAmount()),
                        receipt.getDescription(),
                        receipt.getSubmitter().getUsername(),
                        receipt.getDate().toString(),
                        receipt.getAccountant().getUsername(),
                        receipt.getAccountantDate().toString()
                    };
                } else if (type.equals("approved")) {
                    rowData = new Object[]{
                        receipt.getReceiptId(),
                        String.format("$%.2f", receipt.getAmount()),
                        receipt.getDescription(),
                        receipt.getSubmitter().getUsername(),
                        receipt.getDate().toString(),
                        receipt.getAccountant().getUsername(),
                        receipt.getAccountantDate().toString(),
                        receipt.getStatusByManagerAt().toString()
                    };
                } else { // rejected
                    rowData = new Object[]{
                        receipt.getReceiptId(),
                        String.format("$%.2f", receipt.getAmount()),
                        receipt.getDescription(),
                        receipt.getSubmitter().getUsername(),
                        receipt.getDate().toString(),
                        receipt.getAccountant().getUsername(),
                        receipt.getAccountantDate().toString(),
                        receipt.getStatusByManagerAt().toString(),
                        receipt.getReason()
                    };
                }
                model.addRow(rowData);
                totalAmount += receipt.getAmount();
            }
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        
        // Set preferred column widths
        if (type.equals("rejected")) {
            table.getColumnModel().getColumn(8).setPreferredWidth(200); // Reason column
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Add View Details button
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = table.convertRowIndexToModel(selectedRow);
                int receiptId = (int) model.getValueAt(selectedRow, 0);
                Receipt receipt = receiptHandler.findReceiptById(receiptId);
                if (receipt != null) {
                    showReceiptDetails(receipt);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a receipt to view details.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(viewDetailsBtn);
        
        if (type.equals("pending")) {
            JButton approveBtn = new JButton("Approve Selected");
            JButton rejectBtn = new JButton("Reject Selected");
            
            approveBtn.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedRow = table.convertRowIndexToModel(selectedRow);
                    int receiptId = (int) model.getValueAt(selectedRow, 0);
                    receiptHandler.approveReceipt(currentUser, receiptId);
                    JOptionPane.showMessageDialog(this, "Receipt approved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showManagerDashboard();
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a receipt first!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            rejectBtn.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedRow = table.convertRowIndexToModel(selectedRow);
                    String reason = JOptionPane.showInputDialog(this, "Reason for rejection:", "");
                    if (reason != null && !reason.trim().isEmpty()) {
                        int receiptId = (int) model.getValueAt(selectedRow, 0);
                        receiptHandler.rejectReceipt(currentUser, receiptId, reason);
                        JOptionPane.showMessageDialog(this, "Receipt rejected!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        showManagerDashboard();
                    } else if (reason != null) {
                        JOptionPane.showMessageDialog(this, "Please provide a rejection reason!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a receipt first!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            buttonPanel.add(approveBtn);
            buttonPanel.add(rejectBtn);
        }
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> showManagerDashboard());
        buttonPanel.add(refreshBtn);
        
        // Add summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String statusText = type.substring(0, 1).toUpperCase() + type.substring(1);
        JLabel summaryLabel = new JLabel(String.format("%s Receipts: %d | Total Amount: $%.2f", 
                                       statusText, table.getRowCount(), totalAmount));
        summaryPanel.add(summaryLabel);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(summaryPanel, BorderLayout.WEST);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        
        panel.add(southPanel, BorderLayout.SOUTH);
        return panel;
    }
}