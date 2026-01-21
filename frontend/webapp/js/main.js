const API_URL = "http://localhost:8080/api";

document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("jwt");
  if (!token) {
    alert("❌ Please log in first!");
    window.location.href = "login.html";
    return;
  }

  const messagesDiv = document.getElementById("messages");
  const sendForm = document.getElementById("sendMessageForm");
  const logoutBtn = document.getElementById("logoutBtn");
  const groupsBtn = document.getElementById("groupsBtn");

  async function loadMessages() {
    messagesDiv.innerHTML = "Loading messages...";
    try {
      const res = await fetch(`${API_URL}/messages/unread`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) {
        const txt = await res.text();
        console.error("Load messages failed:", res.status, txt);
        messagesDiv.innerHTML = "❌ Failed to load messages.";
        return;
      }
      const messages = await res.json();
      if (!Array.isArray(messages) || messages.length === 0) {
        messagesDiv.innerHTML = "<p>No messages yet.</p>";
        return;
      }
      messagesDiv.innerHTML = messages
        .map(
          (m) => `
          <div class="message">
            <p><strong>From:</strong> ${m.message.sender?.username ?? "unknown"}</p>
            <p>${m.message.content}</p>
            <small>📅 ${new Date(m.message.sentAt).toLocaleString()}</small>
            <strong>Read:</strong> ${m.isRead ? "✅ Yes" : "❌ No"}
            <br>
            ${!m.isRead ? `<button onclick="markAsRead(${m.id})">Mark as read</button>` : ""}   
          </div>`
        )
        .join("");
    } catch (err) {
      console.error("Error loading messages:", err);
      console.log("JWT present?", !!localStorage.getItem("jwt"));
      console.log("Auth header preview:", `Bearer ${localStorage.getItem("jwt")?.slice(0,20)}...`);

      messagesDiv.innerHTML = "❌ Failed to load messages (network).";
    }
  }

  sendForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const recipient = document.getElementById("recipient").value.trim();
    const content = document.getElementById("content").value.trim();
    if (!recipient || !content) {
      alert("Please fill in recipient and content.");
      return;
    }

    const btn = sendForm.querySelector("button[type=submit]");
    btn.disabled = true;

    try {
      const res = await fetch(`${API_URL}/messages/send`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ recipient, content }),
      });

      const text = await res.text();
      let data;
      try { data = JSON.parse(text); } catch { data = text; }

      if (res.ok) {
        console.log("Message sent:", data);
        document.getElementById("content").value = "";
        await loadMessages();
      } else {
        console.error("Send failed:", res.status, data);
        alert(`❌ Failed to send message (${res.status}).`);
      }
    } catch (err) {
      console.error("Network error sending:", err);
      alert("❌ Network error while sending.");
    } finally {
      btn.disabled = false;
    }
  });

  logoutBtn.addEventListener("click", () => {
    localStorage.removeItem("jwt");
    window.location.href = "login.html";
  });

  if (groupsBtn) {
    groupsBtn.addEventListener("click", () => {
      window.location.href = "groups.html";
    });
  }

  window.markAsRead = async (id) => {
    const authToken = localStorage.getItem("jwt");
    if (!authToken) {
      alert("Not authenticated!");
      return;
    }

    const res = await fetch(`http://localhost:8080/api/messages/${id}/read`, {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (res.ok) {
      alert("Message marked as read ✅");
      await loadMessages();
    } else {
      alert("Failed to mark as read ❌");
    }
  };

  loadMessages();
});
