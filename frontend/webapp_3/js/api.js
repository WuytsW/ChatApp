// Adjust this to match your backend origin
const BASE_URL = "http://localhost:8080/api"; // keep /api since controllers are under /api/*

// ---- Endpoints mapped 1:1 to your Spring controllers ----
const endpoints = {
  auth: {
    // POST /api/auth/login
    login: "/auth/login", // AuthController.login  :contentReference[oaicite:0]{index=0}
  },
  user: {
    // POST /api/user/register
    register: "/user/register", // UserController.register  :contentReference[oaicite:1]{index=1}
    // GET /api/user/get
    me: "/user/get",            // UserController.getUser (current principal)  :contentReference[oaicite:2]{index=2}
    // GET /api/user/get/user?username=...
    byUsername: "/user/get/user", // UserController.getUserByUsername  :contentReference[oaicite:3]{index=3}
    // POST /api/user/friends/add?friend_name=...
    addFriend: "/user/friends/add", // UserController.addFriend  :contentReference[oaicite:4]{index=4}
  },
  dms: {
    // GET /api/messages/direct/get
    list: "/messages/direct/get", // DirectMessageController.getUserMessages  :contentReference[oaicite:5]{index=5}
    // GET /api/messages/direct/get/unread
    unread: "/messages/direct/get/unread", // getUserUnreadMessages  :contentReference[oaicite:6]{index=6}
    // GET /api/messages/direct/get/sender?sender=...
    fromSender: "/messages/direct/get/sender", // getDirectMessagesFromSender  :contentReference[oaicite:7]{index=7}
    // GET /api/messages/direct/get/conversation?username2=...
    conversation: "/messages/direct/get/conversation", // getDirectConversation  :contentReference[oaicite:8]{index=8}
    // GET /api/messages/direct/get/recipient?recipient=...
    forRecipient: "/messages/direct/get/recipient", // getDirectMessagesForRecipient  :contentReference[oaicite:9]{index=9}
    // POST /api/messages/direct/send
    send: "/messages/direct/send", // send  :contentReference[oaicite:10]{index=10}
    // PATCH /api/messages/direct/read?id=...
    markRead: "/messages/direct/read", // markDirectMessageAsRead  :contentReference[oaicite:11]{index=11}
  },
  groupMessages: {
    // GET /api/messages/group/get
    list: "/messages/group/get", // GroupMessageController.getGroupMessages  :contentReference[oaicite:12]{index=12}
    // GET /api/messages/group/get/group?id=...
    byGroup: "/messages/group/get/group", // getGroupMessagesByGroup  :contentReference[oaicite:13]{index=13}
    // POST /api/messages/group/send
    send: "/messages/group/send", // sendGroupMessage  :contentReference[oaicite:14]{index=14}
    // PATCH /api/messages/group/read?group_message_id=...
    markRead: "/messages/group/read", // markAsRead  :contentReference[oaicite:15]{index=15}
  },
  groups: {
    // GET /api/groups/get
    list: "/groups/get", // GroupController.getGroups  :contentReference[oaicite:16]{index=16}
    // POST /api/groups/new?name=...
    create: "/groups/new", // GroupController.createGroup  :contentReference[oaicite:17]{index=17}
    // POST /api/groups/add  (JSON body { group_name, member_name })
    addMember: "/groups/add", // GroupController.addUserToGroup  :contentReference[oaicite:18]{index=18}
  },
};

// ---- Core fetch with auth + 401 handling ----
async function apiFetch(path, opts = {}) {
  const token = getToken && getToken();
  const headers = Object.assign({ "Content-Type": "application/json" }, opts.headers || {});
  if (token) headers["Authorization"] = `Bearer ${token}`;
  const res = await fetch(BASE_URL + path, { ...opts, headers });
  if (res.status === 401) {
    if (typeof logout === "function") logout();
    throw new Error("Unauthorized");
  }
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || res.statusText || "Request failed");
  }
  const ct = res.headers.get("content-type") || "";
  return ct.includes("application/json") ? res.json() : res.text();
}

// Small helper for building ?query=strings
const q = (params) => "?" + new URLSearchParams(params).toString();

// ---------------- AUTH ----------------
async function login(email_or_username, password) {
  // matches LoginRequest { email_or_username, password }
  return apiFetch(endpoints.auth.login, {
    method: "POST",
    body: JSON.stringify({ email_or_username, password }),
  });
}

// ---------------- USER ----------------
async function registerUser({ username, email, password }) {
  return apiFetch(endpoints.user.register, {
    method: "POST",
    body: JSON.stringify({ username, email, password }),
  });
}
async function getCurrentUser() {
  return apiFetch(endpoints.user.me, { method: "GET" });
}
async function getUserByUsername(username) {
  return apiFetch(`${endpoints.user.byUsername}${q({ username })}`, { method: "GET" });
}
async function addFriend(friend_name) {
  return apiFetch(`${endpoints.user.addFriend}${q({ friend_name })}`, { method: "POST" });
}

// ---------------- DIRECT MESSAGES ----------------
async function listDirectMessages() {
  return apiFetch(endpoints.dms.list, { method: "GET" });
}
async function listUnreadDirectMessages() {
  return apiFetch(endpoints.dms.unread, { method: "GET" });
}
async function listDirectMessagesFromSender(sender) {
  return apiFetch(`${endpoints.dms.fromSender}${q({ sender })}`, { method: "GET" });
}
async function getDirectConversation(username2) {
  return apiFetch(`${endpoints.dms.conversation}${q({ username2 })}`, { method: "GET" });
}
async function listDirectMessagesForRecipient(recipient) {
  return apiFetch(`${endpoints.dms.forRecipient}${q({ recipient })}`, { method: "GET" });
}
async function sendDM(recipient, content) {
  // SendDirectMessageRequest { recipient, content }
  return apiFetch(endpoints.dms.send, {
    method: "POST",
    body: JSON.stringify({ recipient, content }),
  });
}
async function markDMAsRead(id) {
  return apiFetch(`${endpoints.dms.markRead}${q({ id })}`, { method: "PATCH" });
}

// ---------------- GROUP MESSAGES ----------------
async function listGroupMessages() {
  return apiFetch(endpoints.groupMessages.list, { method: "GET" });
}
async function listGroupMessagesByGroup(group_id) {
  return apiFetch(`${endpoints.groupMessages.byGroup}${q({ id: group_id })}`, { method: "GET" });
}
async function sendGroupMessage(group_id, content) {
  // SendGroupMessageRequest { group_id, content }
  return apiFetch(endpoints.groupMessages.send, {
    method: "POST",
    body: JSON.stringify({ group_id, content }),
  });
}
async function markGroupMessageAsRead(group_message_id) {
  return apiFetch(`${endpoints.groupMessages.markRead}${q({ group_message_id })}`, {
    method: "PATCH",
  });
}

// ---------------- GROUPS ----------------
async function listGroups() {
  return apiFetch(endpoints.groups.list, { method: "GET" });
}
async function createGroup(name) {
  return apiFetch(`${endpoints.groups.create}${q({ name })}`, { method: "POST" });
}
async function addGroupMember(group_name, member_name) {
  // AddGroupMemberRequest { group_name, member_name }
  return apiFetch(endpoints.groups.addMember, {
    method: "POST",
    body: JSON.stringify({ group_name, member_name }),
  });
}

// ---------------- Optional helper: DM threads (client-side) ----------------
async function getDMThreads() {
  // Build threads by grouping all DMs locally (no server endpoint provided)
  const all = await listDirectMessages();
  const me = (getMe && (getMe()?.username || getMe()?.email_or_username)) || null;
  const map = new Map();
  (all || []).forEach((m) => {
    const other = m.sender === me ? m.recipient : m.sender;
    if (!map.has(other)) map.set(other, []);
    map.get(other).push(m);
  });
  return [...map.entries()].map(([other, messages]) => {
    const last = messages[messages.length - 1];
    return { other, lastMessage: last, count: messages.length };
  });
}
