package com.rpg.gui;

import com.rpg.UserDatabase;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private boolean authenticated = false;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;
    private final UserDatabase userDatabase;

    private static final Color BG = new Color(30, 30, 40);
    private static final Color FIELD_BG = new Color(45, 45, 58);
    private static final Color TEXT = new Color(210, 210, 210);
    private static final Color ACCENT = new Color(80, 160, 220);

    public LoginDialog(JFrame parent, UserDatabase userDatabase) {
        super(parent, "Ashen Gate: Warden Access", true);
        this.userDatabase = userDatabase;

        setSize(360, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Ashen Gate", SwingConstants.CENTER);
        title.setForeground(ACCENT);
        title.setFont(new Font("Serif", Font.BOLD, 22));
        add(title, gbc);

        gbc.gridy = 1;
        JLabel subtitle = new JLabel("The gate listens. Speak your oath.", SwingConstants.CENTER);
        subtitle.setForeground(new Color(150, 150, 150));
        subtitle.setFont(new Font("Serif", Font.ITALIC, 12));
        add(subtitle, gbc);

        // Username
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridx = 0;
        JLabel userLabel = new JLabel("Warden name:");
        userLabel.setForeground(TEXT);
        add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(14);
        styleField(usernameField);
        add(usernameField, gbc);

        // Password
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel passLabel = new JLabel("Oath phrase:");
        passLabel.setForeground(TEXT);
        add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(14);
        styleField(passwordField);
        add(passwordField, gbc);

        // Login button
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Enter the Gate");
        loginButton.setBackground(new Color(60, 120, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> attemptLogin());
        add(loginButton, gbc);

        // Status
        gbc.gridy = 5;
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(200, 60, 60));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        add(statusLabel, gbc);

        // Enter key triggers login
        getRootPane().setDefaultButton(loginButton);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userDatabase.verifyCredentials(username, password)) {
            var record = userDatabase.findUser(username);
            if (record.isPresent() && "ADMIN".equalsIgnoreCase(record.get().getRole())) {
                authenticated = true;
                dispose();
                return;
            }
        }
        statusLabel.setText("The ember dims. Invalid credentials.");
        passwordField.setText("");
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setFont(new Font("Monospaced", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 75)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
    }
}
