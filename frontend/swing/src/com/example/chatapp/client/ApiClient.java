package com.example.chatapp.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiClient {
    private final String baseUrl;
    private String token; // JWT

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public void logout() { token = null; }

    public void login(String email, String password) throws IOException {
        String endpoint = baseUrl + "/auth/login";
        String payload = "{\"email\":\"" + esc(email) + "\",\"password\":\"" + esc(password) + "\"}";
        String res = postJson(endpoint, payload, null);

        // naive token extraction: looks for "token":"...".
        String t = extractJsonString(res, "token");
        if (t == null || t.isBlank()) throw new IOException("No token in response.");
        this.token = t;
    }

    public List<Message> getMessages() throws IOException {
        String endpoint = baseUrl + "/messages";  // adjust path if needed
        String res = getJson(endpoint, token);

        List<Message> out = new ArrayList<>();
        String[] objects = res.replace("[", "").replace("]", "").split("\\},\\s*\\{");
        for (String raw : objects) {
            String obj = raw.trim();
            if (obj.isBlank()) continue;
            if (!obj.startsWith("{")) obj = "{" + obj;
            if (!obj.endsWith("}")) obj = obj + "}";

            Long id = extractJsonLong(obj, "id");

            // content + sentAt are inside "message"
            String content = extractJsonString(obj, "content");
            String sentAt = extractJsonString(obj, "sentAt");

            // sender.username is nested
            String sender = extractJsonString(obj, "username"); // will pick up first "username" -> the sender
            // to be safer, you could search for "sender" block then extract
            if (sender == null) sender = "???";

            // recipient.username
            String recipient = null;
            // hack: if we find the "recipient" block, extract username after it
            int idx = obj.indexOf("\"recipient\"");
            if (idx != -1) {
                String sub = obj.substring(idx);
                recipient = extractJsonString(sub, "username");
            }

            Boolean read = extractJsonBoolean(obj, "isRead");

            if (id != null) {
                out.add(new Message(id, sender, recipient, content, sentAt, read != null && read));
            }
        }
        out.sort(Comparator.comparing(Message::getSentAt, Comparator.nullsLast(String::compareTo)).reversed());
        return out;
    }


    public void sendMessage(String recipientUsername, String content) throws IOException {
        String endpoint = baseUrl + "/messages/send";
        String payload = "{\"recipient\":\"" + esc(recipientUsername) + "\",\"content\":\"" + esc(content) + "\"}";
        postJson(endpoint, payload, token);
    }

    public void markAsRead(long messageId) throws IOException {
        String endpoint = baseUrl + "/messages/" + messageId + "/read";
        patch(endpoint, token);
    }

    // ------------------ HTTP helpers ------------------

    private String getJson(String url, String bearer) throws IOException {
        HttpURLConnection conn = open(url, "GET", bearer);
        return read(conn);
    }

    private String postJson(String url, String json, String bearer) throws IOException {
        HttpURLConnection conn = open(url, "POST", bearer);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return read(conn);
    }

    private String patch(String url, String bearer) throws IOException {
        HttpURLConnection conn = open(url, "PATCH", bearer);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        // empty body ok for our endpoint
        try (OutputStream os = conn.getOutputStream()) {
            os.write(new byte[0]);
        }
        return read(conn);
    }

    private HttpURLConnection open(String url, String method, String bearer) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(15000);
        if (bearer != null && !bearer.isBlank()) {
            conn.setRequestProperty("Authorization", "Bearer " + bearer);
        }
        return conn;
    }

    private String read(HttpURLConnection conn) throws IOException {
        int code = conn.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        String text;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line; while ((line = br.readLine()) != null) sb.append(line);
            text = sb.toString();
        }
        if (code < 200 || code >= 300) throw new IOException("HTTP " + code + " -> " + text);
        return text;
    }

    private static String esc(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");
    }

    // ------------------ tiny JSON helpers (very naive) ------------------
    private static String extractJsonString(String json, String key) {
        // matches "key":"value"
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }
    private static Long extractJsonLong(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        return m.find() ? Long.parseLong(m.group(1)) : null;
    }
    private static Boolean extractJsonBoolean(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(true|false)");
        Matcher m = p.matcher(json);
        return m.find() ? Boolean.parseBoolean(m.group(1)) : null;
    }
}

