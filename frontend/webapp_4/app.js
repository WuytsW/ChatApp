// ===== CONFIG: adjust to your backend =====
const BASE_URL = "http://localhost:8080/api";

// Likely controller mappings (please adjust to your actual methods/paths):
const ENDPOINTS = {
  // AuthController:
  //   @PostMapping("/login") -> returns { token: "..." }
  login: "/auth/login",

  // UserController (optional):
  //   e.g., @GetMapping("/users/me") -> current user details
  me: "/users/me",

  // DirectMessageController:
  //   e.g., @RequestMapping("/messages/direct"), @GetMapping("/get")
  directList: "/messages/direct/get",
  //   e.g., @PostMapping("/send") with JSON { recipientId, content }
  directSend: "/messages/direct/send",

  // Friends endpoints (from your earlier posts):
  //   @PostMapping("/friends/add") with JSON { friend_name } or form param
  //   @GetMapping("/friends")       -> list of friends (guess; adjust to your real path/controller)
  friendsList: "/user/friends/get",
  friendAdd: "/user/friends/add",

  // GroupController / GroupMessageController:
  //   e.g., @GetMapping("/groups") -> list of groups for user
  groupsList: "/groups/get",
  //   e.g., @GetMapping("/messages/group/{groupId}/get")
  groupMessages: (groupId) => `/messages/group/get/group/${encodeURIComponent(groupId)}`,
  //   e.g., @PostMapping("/messages/group/{groupId}/send") with JSON { content }
  groupSend: "/messages/group/send",
  //   e.g., @PostMapping("/groups/{groupId}/addMember") with JSON { memberId }
  groupAddMember: "/groups/add",
};
// =========================================

const els = {
  // pages
  pageLogin: document.getElementById("page-login"),
  pageNav: document.getElementById("page-nav"),
  pageDirect: document.getElementById("page-direct"),
  pageFriends: document.getElementById("page-friends"),
  pageGroups: document.getElementById("page-groups"),

  // login
  loginForm: document.getElementById("login-form"),
  username: document.getElementById("username"),
  password: document.getElementById("password"),
  loginError: document.getElementById("login-error"),

  // nav
  navButtons: document.querySelectorAll("#page-nav [data-goto]"),
  logoutBtn: document.getElementById("btn-logout"),

  // direct
  refreshDirectBtn: document.getElementById("btn-refresh-direct"),
  directError: document.getElementById("direct-error"),
  directList: document.getElementById("direct-list"),
  directSendForm: document.getElementById("direct-send-form"),
  dmTo: document.getElementById("dm-to"),
  dmText: document.getElementById("dm-text"),
  directSendInfo: document.getElementById("direct-send-info"),
  directSendError: document.getElementById("direct-send-error"),

  // friends
  refreshFriendsBtn: document.getElementById("btn-refresh-friends"),
  friendsError: document.getElementById("friends-error"),
  friendsList: document.getElementById("friends-list"),
  friendAddForm: document.getElementById("friend-add-form"),
  friendName: document.getElementById("friend-name"),
  friendAddInfo: document.getElementById("friend-add-info"),
  friendAddError: document.getElementById("friend-add-error"),

  // groups
  refreshGroupsBtn: document.getElementById("btn-refresh-groups"),
  groupsError: document.getElementById("groups-error"),
  groupsList: document.getElementById("groups-list"),
  groupLoadForm: document.getElementById("group-load-form"),
  groupId: document.getElementById("group-id"),
  groupMsgError: document.getElementById("group-msg-error"),
  groupMsgList: document.getElementById("group-msg-list"),
  groupSendForm: document.getElementById("group-send-form"),
  groupSendId: document.getElementById("group-send-id"),
  groupSendText: document.getElementById("group-send-text"),
  groupSendInfo: document.getElementById("group-send-info"),
  groupSendError: document.getElementById("group-send-error"),

  // new elements for adding group members
  groupAddMemberForm: document.getElementById("group-add-member-form"),
  groupAddId: document.getElementById("group-add-id"),
  groupAddMemberId: document.getElementById("group-add-member-id"),
  groupAddMemberInfo: document.getElementById("group-add-member-info"),
  groupAddMemberError: document.getElementById("group-add-member-error"),
};

// --- token helpers ---
function saveToken(t) { localStorage.setItem("jwt", t); }
function getToken() { return localStorage.getItem("jwt"); }
function clearToken() { localStorage.removeItem("jwt"); }

// --- UI helpers ---
function show(el) { el.style.display = ""; }
function hide(el) { el.style.display = "none"; }
function goto(id) {
  const pages = [els.pageLogin, els.pageNav, els.pageDirect, els.pageFriends, els.pageGroups];
  pages.forEach(hide);
  show(els.pageNav);
  show(document.getElementById(id));
}
function setLoggedInUI(isLoggedIn) {
  if (isLoggedIn) {
    hide(els.pageLogin);
    show(els.pageNav);
    goto("page-direct");
  } else {
    show(els.pageLogin);
    hide(els.pageNav);
    hide(els.pageDirect);
    hide(els.pageFriends);
    hide(els.pageGroups);
  }
}

// --- fetch helpers ---
async function apiGet(path) {
  const res = await fetch(BASE_URL + path, {
    method: "GET",
    headers: { Authorization: "Bearer " + getToken() }
  });
  if (!res.ok) throw new Error(await res.text() || ("HTTP " + res.status));
  return res.json();
}
async function apiPost(path, bodyObj) {
  const res = await fetch(BASE_URL + path, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + getToken()
    },
    body: JSON.stringify(bodyObj || {})
  });
  if (!res.ok) throw new Error(await res.text() || ("HTTP " + res.status));
  return res.json().catch(() => ({})); // in case endpoint returns no body
}

// --- generic renderer for arrays/objects ---
function renderJsonList(el, data) {
  el.textContent = "";
  if (!data || (Array.isArray(data) && data.length === 0)) {
    el.textContent = "No data.";
    return;
  }
  if (Array.isArray(data)) {
    data.forEach((item, i) => {
      const box = document.createElement("div");
      const h = document.createElement("h4");
      h.textContent = "Item " + (i + 1);
      box.appendChild(h);
      const pre = document.createElement("pre");
      pre.textContent = JSON.stringify(item, null, 2);
      box.appendChild(pre);
      el.appendChild(box);
    });
  } else {
    const pre = document.createElement("pre");
    pre.textContent = JSON.stringify(data, null, 2);
    el.appendChild(pre);
  }
}

// ====== Login ======
els.loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  els.loginError.textContent = "";

  const body = {
    // earlier you used: email_or_username + password
    // your AuthController earlier showed @PostMapping("/login")
    // Adjust keys to match LoginRequest (commonly { username, password } or { email_or_username, password })
    email_or_username: els.username.value.trim(),
    password: els.password.value
  };

  try {
    const data = await (async () => {
      const res = await fetch(BASE_URL + ENDPOINTS.login, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });
      if (!res.ok) throw new Error(await res.text() || ("HTTP " + res.status));
      return res.json();
    })();

    const token = data.token || data.jwt || data.access_token;
    if (!token) throw new Error("No token in login response.");
    saveToken(token);
    setLoggedInUI(true);
    await refreshDirect();
  } catch (err) {
    els.loginError.textContent = err.message || String(err);
  }
});

// ====== Nav ======
els.navButtons.forEach(btn => {
  btn.addEventListener("click", async () => {
    goto(btn.getAttribute("data-goto"));
    if (btn.getAttribute("data-goto") === "page-direct") await refreshDirect();
    if (btn.getAttribute("data-goto") === "page-friends") await refreshFriends();
    if (btn.getAttribute("data-goto") === "page-groups") await refreshGroups();
  });
});

els.logoutBtn.addEventListener("click", () => {
  clearToken();
  setLoggedInUI(false);
});

// ====== Direct Messages ======
async function refreshDirect() {
  els.directError.textContent = "";
  els.directList.textContent = "Loading...";
  try {
    const data = await apiGet(ENDPOINTS.directList);
    renderJsonList(els.directList, data);
  } catch (err) {
    els.directList.textContent = "";
    els.directError.textContent = err.message || String(err);
  }
}
els.refreshDirectBtn.addEventListener("click", refreshDirect);

els.directSendForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  els.directSendError.textContent = "";
  els.directSendInfo.textContent = "";
  try {
    // Guessing request shape based on common patterns:
    // If your SendDirectMessageRequest is different, adjust these keys.
    const payload = {
      recipientId: Number(els.dmTo.value),
      content: els.dmText.value
    };
    await apiPost(ENDPOINTS.directSend, payload);
    els.directSendInfo.textContent = "Sent!";
    els.dmText.value = "";
    await refreshDirect();
  } catch (err) {
    els.directSendError.textContent = err.message || String(err);
  }
});

// ====== Friends ======
async function refreshFriends() {
  els.friendsError.textContent = "";
  els.friendsList.textContent = "Loading...";
  try {
    const data = await apiGet(ENDPOINTS.friendsList); // Adjust if your UserController exposes another path
    renderJsonList(els.friendsList, data);
  } catch (err) {
    els.friendsList.textContent = "";
    els.friendsError.textContent = err.message || String(err);
  }
}
els.refreshFriendsBtn.addEventListener("click", refreshFriends);

els.friendAddForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  els.friendAddInfo.textContent = "";
  els.friendAddError.textContent = "";
  try {
    // You previously used @PostMapping("/friends/add") accepting JSON { friend_name }
    await apiPost(ENDPOINTS.friendAdd, { friend_name: els.friendName.value.trim() });
    els.friendAddInfo.textContent = "Friend added.";
    els.friendName.value = "";
    await refreshFriends();
  } catch (err) {
    els.friendAddError.textContent = err.message || String(err);
  }
});

// ====== Groups ======
async function refreshGroups() {
  els.groupsError.textContent = "";
  els.groupsList.textContent = "Loading...";
  try {
    const data = await apiGet(ENDPOINTS.groupsList); // Adjust to your GroupController list path
    renderJsonList(els.groupsList, data);
  } catch (err) {
    els.groupsList.textContent = "";
    els.groupsError.textContent = err.message || String(err);
  }
}
els.refreshGroupsBtn.addEventListener("click", refreshGroups);

els.groupLoadForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  els.groupMsgError.textContent = "";
  els.groupMsgList.textContent = "Loading...";
  const groupId = Number(els.groupId.value);
  try {
    const data = await apiGet(ENDPOINTS.groupMessages(groupId));
    renderJsonList(els.groupMsgList, data);
  } catch (err) {
    els.groupMsgList.textContent = "";
    els.groupMsgError.textContent = err.message || String(err);
  }
});

els.groupSendForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  els.groupSendInfo.textContent = "";
  els.groupSendError.textContent = "";

  const groupId = Number(els.groupSendId.value);
  const content = els.groupSendText.value;

  try {
    // ✅ Include groupId and content in the body
    await apiPost(ENDPOINTS.groupSend, { groupId, content });

    els.groupSendInfo.textContent = "Sent!";
    els.groupSendText.value = "";

    // ✅ Optionally refresh the messages if viewing the same group
    if (Number(els.groupId.value) === groupId) {
      els.groupMsgList.textContent = "Refreshing...";
      const data = await apiGet(ENDPOINTS.groupMessages(groupId));
      renderJsonList(els.groupMsgList, data);
    }
  } catch (err) {
    els.groupSendError.textContent = err.message || String(err);
  }
});
els.groupAddMemberForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  els.groupAddMemberInfo.textContent = "";
  els.groupAddMemberError.textContent = "";
  try {
    const groupId = Number(els.groupAddId.value);
    const memberId = Number(els.groupAddMemberId.value);
    await apiPost(ENDPOINTS.groupAddMember, { groupId, memberId });
    els.groupAddMemberInfo.textContent = "Member added!";
    els.groupAddId.value = "";
    els.groupAddMemberId.value = "";
    await refreshGroups();
  } catch (err) {
    els.groupAddMemberError.textContent = err.message || String(err);
  }
});

// ===== Init =====
(async function init() {
  if (getToken()) {
    setLoggedInUI(true);
    await refreshDirect();
  } else {
    setLoggedInUI(false);
  }
})();
