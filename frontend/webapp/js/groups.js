const API_URL = "http://localhost:8080/api";

document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("jwt");
  if (!token) {
    alert("❌ Please log in first!");
    window.location.href = "login.html";
    return;
  }

  const groupsDiv = document.getElementById("groups");
  const sendForm = document.getElementById("sendGroupMessageForm");

  // Load all groups the user is in
  async function loadGroups() {
    groupsDiv.innerHTML = "Loading groups...";
    try {
      const res = await fetch(`${API_URL}/groups`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error(await res.text());
      const groups = await res.json();

      if (!Array.isArray(groups) || groups.length === 0) {
        groupsDiv.innerHTML = "<p>❌ No groups found.</p>";
        return;
      }

      groupsDiv.innerHTML = groups
        .map(
          (g) => `
          <div class="group">
            <h3>${g.name} (ID: ${g.id})</h3>
            <h4>📬 Messages:</h4>
            <ul>
              ${g.messages
                ?.map(
                  (m) =>
                    `<li><strong>${m.message.sender.username}:</strong> ${m.message.content} <small>${new Date(m.message.sentAt).toLocaleString()}</small></li>`
                )
                .join("") || "<li>No messages yet</li>"}
            </ul>
          </div>`
        )
        .join("");
    } catch (err) {
      console.error("Error loading groups:", err);
      groupsDiv.innerHTML = "❌ Failed to load groups.";
    }
  }

  // Send group message
  sendForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const groupId = document.getElementById("groupId").value;
    const content = document.getElementById("groupContent").value;

    try {
      const res = await fetch(`${API_URL}/groups/${groupId}/send`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ content }),
      });

      if (res.ok) {
        alert("✅ Message sent!");
        document.getElementById("groupContent").value = "";
        loadGroups();
      } else {
        const txt = await res.text();
        alert("❌ Failed to send: " + txt);
      }
    } catch (err) {
      console.error("Network error:", err);
      alert("❌ Network error sending message.");
    }
  });

  loadGroups();
});
