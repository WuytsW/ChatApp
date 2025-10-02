// Adjust this to match your backend origin
const BASE_URL = "http://localhost:8080/api"; // e.g. "/api" if served behind same origin; otherwise "http://localhost:8080"

const endpoints = {
  auth: {
    login: "/auth/login",            // POST
    register: "/auth/register",      // POST (if implemented)
    me: "/users/me"                  // GET (if implemented)
  },
  dms: {
    list: "/messages/direct/get",    // GET
    threads: "/direct_messages/threads", // optional
    withUser: (u) => `/direct_messages/with/${encodeURIComponent(u)}`, // GET
    send: "/direct_messages/send"    // POST
  },
  groups: {
    list: "/groups/get",             // GET
    byId: (id) => `/groups/${id}`,   // GET
    addMember: "/groups/add-member", // POST (AddGroupMemberRequest)
    sendMessage: (id) => `/groups/${id}/messages` // POST (SendGroupMessageRequest {content})
  }
};

// Core fetch with auth + 401 redirect
async function apiFetch(path, opts = {}) {
  const token = getToken();
  const headers = Object.assign({ "Content-Type": "application/json" }, opts.headers || {});
  if (token) headers["Authorization"] = `Bearer ${token}`;
  const res = await fetch(BASE_URL + path, { ...opts, headers });
  if (res.status === 401) {
    logout();
    return Promise.reject(new Error("Unauthorized"));
  }
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || res.statusText || "Request failed");
  }
  const ct = res.headers.get("content-type") || "";
  return ct.includes("application/json") ? res.json() : res.text();
}

// AUTH
async function login(email_or_password, password) {
  // matches LoginRequest
  const body = { email_or_password, password };
  return apiFetch(endpoints.auth.login, { method: "POST", body: JSON.stringify(body) });
}
async function registerUser({ username, email, password }) {
  // Adjust to your DTO if needed
  const body = { username, email, password };
  return apiFetch(endpoints.auth.register, { method: "POST", body: JSON.stringify(body) });
}
async function getCurrentUser() {
  return apiFetch(endpoints.auth.me, { method: "GET" });
}

// DMs
async function getDMThreads() {
  // Try native threads endpoint, else fallback to grouping client-side
  try {
    return await apiFetch(endpoints.dms.threads, { method: "GET" });
  } catch {
    const all = await apiFetch(endpoints.dms.list, { method: "GET" });
    // Expecting array of { id, sender, recipient, content, createdAt }
    const me = getMe()?.username || getMe()?.email_or_username;
    const map = new Map();
    (all || []).forEach(m => {
      const other = (m.sender === me) ? m.recipient : m.sender;
      if (!map.has(other)) map.set(other, []);
      map.get(other).push(m);
    });
    return [...map.entries()].map(([other, messages]) => {
      const last = messages[messages.length - 1];
      return { other, lastMessage: last, count: messages.length };
    });
  }
}
async function getDMWith(username) {
  return apiFetch(endpoints.dms.withUser(username), { method: "GET" });
}
async function sendDM(recipient, content) {
  // matches SendDirectMessageRequest
  return apiFetch(endpoints.dms.send, { method: "POST", body: JSON.stringify({ recipient, content }) });
}

// Groups
async function listGroups() {
  return apiFetch(endpoints.groups.list, { method: "GET" });
}
async function getGroup(id) {
  return apiFetch(endpoints.groups.byId(id), { method: "GET" });
}
async function addGroupMember(group_name, member_name) {
  // matches AddGroupMemberRequest
  return apiFetch(endpoints.groups.addMember, {
    method: "POST",
    body: JSON.stringify({ group_name, member_name })
  });
}
async function sendGroupMessage(group_id, content) {
  // matches SendGroupMessageRequest path style
  return apiFetch(endpoints.groups.sendMessage(group_id), {
    method: "POST",
    body: JSON.stringify({ content })
  });
}
