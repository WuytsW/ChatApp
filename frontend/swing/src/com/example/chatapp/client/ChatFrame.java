package com.example.chatapp.client;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChatFrame extends JFrame {
    private final ApiClient api;
    private final DefaultListModel<Message> model = new DefaultListModel<>();
    private final JList<Message> list = new JList<>(model);

    private final JTextField recipient = new JTextField();
    private final JTextField content = new JTextField();
    private final JButton sendBtn = new JButton("Send");
    private final JButton refreshBtn = new JButton("Refresh");
    private final JButton markReadBtn = new JButton("Mark as read");
    private final JButton logoutBtn = new JButton("Logout");

    public ChatFrame(ApiClient api) {
        super("ChatApp - Messages");
        this.api = api;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list), BorderLayout.CENTER);

        // bottom panel (send form)
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx=0; gc.gridy=0; form.add(new JLabel("Recipient:"), gc);
        gc.gridx=1; gc.gridy=0; gc.weightx=1; form.add(recipient, gc);
        gc.gridx=0; gc.gridy=1; gc.weightx=0; form.add(new JLabel("Content:"), gc);
        gc.gridx=1; gc.gridy=1; gc.weightx=1; form.add(content, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(markReadBtn);
        actions.add(refreshBtn);
        actions.add(sendBtn);
        actions.add(logoutBtn);

        gc.gridx=0; gc.gridy=2; gc.gridwidth=2; form.add(actions, gc);

        add(form, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadMessages());
        sendBtn.addActionListener(e -> sendMessage());
        markReadBtn.addActionListener(e -> markSelected());
        logoutBtn.addActionListener(e -> {
            api.logout();
            new LoginFrame().setVisible(true);
            dispose();
        });

        loadMessages();
    }

    private void loadMessages() {
        setBusy(true);
        new SwingWorker<List<Message>, Void>() {
            String err;
            @Override protected List<Message> doInBackground() {
                try {
                    return api.getMessages();
                } catch (Exception ex) {
                    err = ex.getMessage();
                    return null;
                }
            }
            @Override protected void done() {
                setBusy(false);
                model.clear();
                if (err != null) {
                    JOptionPane.showMessageDialog(ChatFrame.this, "Load failed:\n" + err, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    List<Message> msgs = get();
                    if (msgs != null) msgs.forEach(model::addElement);
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void sendMessage() {
        String to = recipient.getText().trim();
        String text = content.getText().trim();
        if (to.isEmpty() || text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter recipient and content.");
            return;
        }
        sendBtn.setEnabled(false);
        new SwingWorker<Void, Void>() {
            String err;
            @Override protected Void doInBackground() {
                try { api.sendMessage(to, text); } catch (Exception ex) { err = ex.getMessage(); }
                return null;
            }
            @Override protected void done() {
                sendBtn.setEnabled(true);
                if (err != null) {
                    JOptionPane.showMessageDialog(ChatFrame.this, "Send failed:\n" + err, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    content.setText("");
                    loadMessages();
                }
            }
        }.execute();
    }

    private void markSelected() {
        Message sel = list.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a message first.");
            return;
        }
        markReadBtn.setEnabled(false);
        new SwingWorker<Void, Void>() {
            String err;
            @Override protected Void doInBackground() {
                try { api.markAsRead(sel.getId()); } catch (Exception ex) { err = ex.getMessage(); }
                return null;
            }
            @Override protected void done() {
                markReadBtn.setEnabled(true);
                if (err != null) {
                    JOptionPane.showMessageDialog(ChatFrame.this, "Mark read failed:\n" + err, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    loadMessages();
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        refreshBtn.setEnabled(!busy);
        sendBtn.setEnabled(!busy);
        markReadBtn.setEnabled(!busy);
    }
}
