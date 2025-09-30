package com.example.chatapp.client;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField email = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JButton loginBtn = new JButton("Log in");
    private final JButton openChatAnyway = new JButton("Open Chat (dev)");

    // change if your backend runs elsewhere
    private final ApiClient api = new ApiClient("http://localhost:8080/api");

    public LoginFrame() {
        super("ChatApp - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 220);
        setLocationRelativeTo(null);

        var panel = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0; panel.add(new JLabel("Email:"), gc);
        gc.gridx = 1; gc.weightx = 1; panel.add(email, gc);

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0; panel.add(new JLabel("Password:"), gc);
        gc.gridx = 1; gc.weightx = 1; panel.add(password, gc);

        var btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(openChatAnyway);
        btns.add(loginBtn);

        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2; gc.weightx = 1; panel.add(btns, gc);
        add(panel);

        loginBtn.addActionListener(e -> doLogin());
        openChatAnyway.addActionListener(e -> openChat());
    }

    private void doLogin() {
        loginBtn.setEnabled(false);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String err = null;
            @Override protected Void doInBackground() {
                try {
                    api.login(email.getText().trim(), new String(password.getPassword()));
                } catch (Exception ex) {
                    err = ex.getMessage();
                }
                return null;
            }
            @Override protected void done() {
                loginBtn.setEnabled(true);
                if (err != null) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Login failed:\n" + err, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    openChat();
                }
            }
        };
        worker.execute();
    }

    private void openChat() {
        ChatFrame chat = new ChatFrame(api);
        chat.setVisible(true);
        dispose();
    }
}
