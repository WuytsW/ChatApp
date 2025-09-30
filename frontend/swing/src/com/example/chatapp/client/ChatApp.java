package com.example.chatapp.client;
import javax.swing.*;

public class ChatApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}

