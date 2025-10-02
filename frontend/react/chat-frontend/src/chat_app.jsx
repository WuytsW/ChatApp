import React, { useEffect, useMemo, useState } from "react";

// Single-file React frontend for the provided Spring Boot chat backend
// Styling: Tailwind CSS (assumes Tailwind present in host project). 
// Components: lightweight, no external UI lib to keep it drop-in.
// ---------------------------------------------------------------
// Quick start:
// - Put this file in a React/Vite app as src/ChatApp.jsx
// - Ensure Tailwind is configured (or replace classNames with your CSS)
// - Render <ChatApp /> in your App
// - Set the API Base URL in the top-right Settings after first load
// ---------------------------------------------------------------

// ----------------------- Utilities -----------------------
function classNames(...xs) { return xs.filter(Boolean).join(" "); }

function useLocalStorage(key, initialValue) {
  const [value, setValue] = useState(() => {
    try {
      const raw = localStorage.getItem(key);
      return raw ? JSON.parse(raw) : initialValue;
    } catch {
      return initialValue;
    }
  });
  useEffect(() => {
    try { localStorage.setItem(key, JSON.stringify(value)); } catch {}
  }, [key, value]);
  return [value, setValue];
}

async function apiRequest({ baseUrl, path, method = "GET", token, body, params, headers }) {
  const url = new URL(path, baseUrl.endsWith("/") ? baseUrl : baseUrl + "/");
  if (params) Object.entries(params).forEach(([k, v]) => url.searchParams.set(k, v));
  const res = await fetch(url.toString(), {
    method,
    headers: {
      "Content-Type": body instanceof FormData ? undefined : "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(headers || {}),
    },
    body: body ? (body instanceof FormData ? body : JSON.stringify(body)) : undefined,
  });
  const contentType = res.headers.get("content-type") || "";
  const data = contentType.includes("application/json") ? await res.json().catch(() => ({})) : await res.text();
  if (!res.ok) {
    const message = typeof data === "string" ? data : data?.error || res.statusText;
    throw new Error(message || `HTTP ${res.status}`);
  }
  return data;
}

// ----------------------- Endpoint map (best-effort from code) -----------------------
// If your backend routes differ slightly, tweak here.
const endpoints = {
  login: (baseUrl) => ({ path: "/api/auth/login", method: "POST" }),
  meByUsername: (username) => ({ path: `/api/user/${encodeURIComponent(username)}`, method: "GET" }),
  addFriend: (friendName) => ({ path: `/api/user/friends/add/${encodeURIComponent(friendName)}`, method: "POST" }),
  groupsList: () => ({ path: "/api/groups", method: "GET" }),
  groupsCreate: () => ({ path: "/api/groups/new", method: "POST" }), // name as form field or query
  directMessagesSend: () => ({ path: "/api/messages/direct/send", method: "POST" }),
  directMessageMarkRead: (id) => ({ path: `/api/messages/direct/${id}/read`, method: "PATCH" }),
  groupMessagesList: (groupId) => ({ path: `/api/messages/group/${groupId}` , method: "GET" }),
  groupMessagesSend: (groupId) => ({ path: `/api/messages/group/send/${groupId}`, method: "POST" }),
  groupMessageMarkRead: (groupMessageId) => ({ path: `/api/messages/group/${groupMessageId}/read`, method: "POST" }),
  // Optional (if you have one): list all users / my profile
  usersList: () => ({ path: "/api/user", method: "GET" }),
};

// ----------------------- UI -----------------------
const TabButton = ({ active, onClick, children }) => (
  <button onClick={onClick} className={classNames(
    "px-4 py-2 text-sm rounded-full border",
    active ? "bg-black text-white border-black" : "bg-white hover:bg-gray-50 border-gray-300"
  )}>{children}</button>
);

const Pill = ({ children }) => (
  <span className="px-2 py-0.5 text-xs rounded-full bg-gray-100 border border-gray-200">{children}</span>
);

function Section({ title, actions, children }) {
  return (
    <section className="bg-white rounded-2xl shadow-sm border border-gray-200 p-5">
      <div className="flex items-center justify-between gap-3 mb-4">
        <h2 className="text-lg font-semibold">{title}</h2>
        <div className="flex items-center gap-2">{actions}</div>
      </div>
      {children}
    </section>
  );
}

function Field({ label, children }) {
  return (
    <label className="block text-sm">
      <div className="mb-1 text-gray-700">{label}</div>
      {children}
    </label>
  );
}

function TextInput(props) {
  return <input {...props} className={classNames("w-full px-3 py-2 rounded-xl border focus:outline-none focus:ring", props.className)} />
}

function PrimaryButton({ children, ...rest }) {
  return <button {...rest} className="px-4 py-2 rounded-xl bg-black text-white hover:opacity-90 disabled:opacity-50" >{children}</button>
}

function SecondaryButton({ children, ...rest }) {
  return <button {...rest} className="px-3 py-2 rounded-xl border border-gray-300 hover:bg-gray-50" >{children}</button>
}

function DangerButton({ children, ...rest }) {
  return <button {...rest} className="px-3 py-2 rounded-xl bg-red-600 text-white hover:opacity-90" >{children}</button>
}

// ----------------------- Main App -----------------------
export default function ChatApp() {
  const [apiBase, setApiBase] = useLocalStorage("chat.apiBase", "http://localhost:8080");
  const [token, setToken] = useLocalStorage("chat.token", "");
  const [username, setUsername] = useLocalStorage("chat.username", "");

  const [activeTab, setActiveTab] = useLocalStorage("chat.tab", "direct");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const isAuthed = Boolean(token);

  // ----------------------- Auth -----------------------
  const [loginUser, setLoginUser] = useState("");
  const [loginPass, setLoginPass] = useState("");

  async function handleLogin(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const { path, method } = endpoints.login(apiBase);
      const res = await apiRequest({ baseUrl: apiBase, path, method, body: { username: loginUser, password: loginPass } });
      if (res?.token) {
        setToken(res.token);
        setUsername(loginUser);
      } else {
        throw new Error(res?.error || "Login failed");
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  function handleLogout() {
    setToken("");
    setUsername("");
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top bar */}
      <header className="sticky top-0 z-10 bg-white/80 backdrop-blur border-b border-gray-200">
        <div className="max-w-5xl mx-auto px-4 py-3 flex items-center gap-3 justify-between">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-2xl bg-black text-white grid place-items-center font-bold">C</div>
            <div>
              <div className="font-semibold">ChatApp</div>
              <div className="text-xs text-gray-500">Frontend for Spring Boot backend</div>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Settings apiBase={apiBase} setApiBase={setApiBase} token={token} setToken={setToken} username={username} />
          </div>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 py-6 space-y-6">
        {!isAuthed ? (
          <AuthCard loginUser={loginUser} setLoginUser={setLoginUser} loginPass={loginPass} setLoginPass={setLoginPass} onLogin={handleLogin} loading={loading} error={error} />
        ) : (
          <>
            <NavTabs activeTab={activeTab} setActiveTab={setActiveTab} onLogout={handleLogout} username={username} />

            {activeTab === "direct" && (
              <DirectMessages apiBase={apiBase} token={token} username={username} />
            )}
            {activeTab === "groups" && (
              <Groups apiBase={apiBase} token={token} username={username} />
            )}
            {activeTab === "friends" && (
              <Friends apiBase={apiBase} token={token} username={username} />
            )}
            {activeTab === "profile" && (
              <Profile apiBase={apiBase} token={token} username={username} />
            )}
          </>
        )}
      </main>
    </div>
  );
}

// ----------------------- Components -----------------------
function AuthCard({ loginUser, setLoginUser, loginPass, setLoginPass, onLogin, loading, error }) {
  return (
    <Section title="Sign in">
      <form onSubmit={onLogin} className="max-w-sm space-y-4">
        <Field label="Username">
          <TextInput autoFocus placeholder="alice" value={loginUser} onChange={(e) => setLoginUser(e.target.value)} />
        </Field>
        <Field label="Password">
          <TextInput type="password" placeholder="••••••••" value={loginPass} onChange={(e) => setLoginPass(e.target.value)} />
        </Field>
        {error && <div className="text-red-600 text-sm">{error}</div>}
        <PrimaryButton disabled={loading}>{loading ? "Signing in…" : "Sign in"}</PrimaryButton>
      </form>
    </Section>
  );
}

function Settings({ apiBase, setApiBase, token, setToken, username }) {
  const [open, setOpen] = useState(false);
  return (
    <div className="relative">
      <SecondaryButton onClick={() => setOpen((x) => !x)}>
        <span className="mr-2">Settings</span>
        <Pill>{username ? username : "guest"}</Pill>
      </SecondaryButton>
      {open && (
        <div className="absolute right-0 mt-2 w-80 bg-white border border-gray-200 rounded-2xl shadow-lg p-4 space-y-3">
          <Field label="API Base URL">
            <TextInput value={apiBase} onChange={(e) => setApiBase(e.target.value)} placeholder="http://localhost:8080" />
          </Field>
          <div className="text-xs text-gray-500">All requests are sent with <code>Authorization: Bearer &lt;token&gt;</code> if you are signed in.</div>
        </div>
      )}
    </div>
  );
}

function NavTabs({ activeTab, setActiveTab, onLogout, username }) {
  const tabs = [
    { id: "direct", label: "Direct" },
    { id: "groups", label: "Groups" },
    { id: "friends", label: "Friends" },
    { id: "profile", label: "Profile" },
  ];
  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-2 flex-wrap">
        {tabs.map(t => (
          <TabButton key={t.id} active={activeTab === t.id} onClick={() => setActiveTab(t.id)}>{t.label}</TabButton>
        ))}
      </div>
      <div className="flex items-center gap-2">
        <DangerButton onClick={onLogout}>Log out</DangerButton>
      </div>
    </div>
  );
}

// ----- Direct Messages -----
function DirectMessages({ apiBase, token, username }) {
  const [recipient, setRecipient] = useState("");
  const [content, setContent] = useState("");
  const [sending, setSending] = useState(false);
  const [info, setInfo] = useState("");
  const [err, setErr] = useState("");

  async function sendDM() {
    setErr(""); setInfo(""); setSending(true);
    try {
      const { path, method } = endpoints.directMessagesSend();
      const res = await apiRequest({ baseUrl: apiBase, path, method, token, body: { recipient, content } });
      setInfo(`Message sent to ${recipient} (id: ${res?.id ?? "?"})`);
      setContent("");
    } catch (e) {
      setErr(e.message);
    } finally { setSending(false); }
  }

  return (
    <div className="grid md:grid-cols-2 gap-6">
      <Section title="Send Direct Message" actions={<Pill>From: {username}</Pill>}>
        <div className="space-y-3 max-w-xl">
          <Field label="Recipient username">
            <TextInput placeholder="bob" value={recipient} onChange={(e) => setRecipient(e.target.value)} />
          </Field>
          <Field label="Message">
            <textarea className="w-full px-3 py-2 rounded-xl border focus:outline-none focus:ring min-h-[120px]" placeholder="Type your message…" value={content} onChange={(e) => setContent(e.target.value)} />
          </Field>
          <div className="flex items-center gap-2">
            <PrimaryButton onClick={sendDM} disabled={!recipient || !content || sending}>{sending ? "Sending…" : "Send"}</PrimaryButton>
            {info && <div className="text-green-700 text-sm">{info}</div>}
            {err && <div className="text-red-600 text-sm">{err}</div>}
          </div>
        </div>
      </Section>

      <Section title="Mark Direct Message as Read">
        <MarkDirectRead apiBase={apiBase} token={token} />
      </Section>
    </div>
  );
}

function MarkDirectRead({ apiBase, token }) {
  const [id, setId] = useState("");
  const [resp, setResp] = useState("");
  const [err, setErr] = useState("");

  async function mark() {
    setResp(""); setErr("");
    try {
      const { path, method } = endpoints.directMessageMarkRead(id);
      const res = await apiRequest({ baseUrl: apiBase, path, method, token });
      setResp(typeof res === "string" ? res : JSON.stringify(res));
    } catch (e) { setErr(e.message); }
  }

  return (
    <div className="space-y-3 max-w-md">
      <Field label="Direct Message ID">
        <TextInput placeholder="123" value={id} onChange={(e) => setId(e.target.value)} />
      </Field>
      <PrimaryButton onClick={mark} disabled={!id}>Mark as read</PrimaryButton>
      {resp && <div className="text-green-700 text-sm break-all">{resp}</div>}
      {err && <div className="text-red-600 text-sm">{err}</div>}
    </div>
  );
}

// ----- Groups -----
function Groups({ apiBase, token, username }) {
  const [groups, setGroups] = useState([]);
  const [newGroupName, setNewGroupName] = useState("");
  const [createBusy, setCreateBusy] = useState(false);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [groupMessages, setGroupMessages] = useState([]);
  const [msgText, setMsgText] = useState("");

  async function refreshGroups() {
    try {
      const { path, method } = endpoints.groupsList();
      const res = await apiRequest({ baseUrl: apiBase, path, method, token });
      setGroups(res || []);
    } catch (e) {
      console.error(e);
    }
  }

  useEffect(() => { refreshGroups(); }, []);

  async function createGroup() {
    setCreateBusy(true);
    try {
      const { path, method } = endpoints.groupsCreate();
      // Some backends expect form-encoded or query param; try JSON first
      const res = await apiRequest({ baseUrl: apiBase, path, method, token, body: { name: newGroupName } });
      setNewGroupName("");
      await refreshGroups();
      setSelectedGroup(res);
    } catch (e) {
      // Fallback: attempt URL param style (?name=...)
      try {
        const { path, method } = endpoints.groupsCreate();
        const res2 = await apiRequest({ baseUrl: apiBase, path, method, token, params: { name: newGroupName } });
        setNewGroupName("");
        await refreshGroups();
        setSelectedGroup(res2);
      } catch (e2) {
        alert(e2.message);
      }
    } finally {
      setCreateBusy(false);
    }
  }

  async function loadGroupMessages(g) {
    try {
      const { path, method } = endpoints.groupMessagesList(g.id || g.groupId || g.group_id || g);
      const res = await apiRequest({ baseUrl: apiBase, path, method, token });
      setGroupMessages(Array.isArray(res) ? res : (res?.messages || []));
      setSelectedGroup(g);
    } catch (e) { alert(e.message); }
  }

  async function sendGroupMessage() {
    try {
      const gid = selectedGroup?.id || selectedGroup?.groupId || selectedGroup;
      const { path, method } = endpoints.groupMessagesSend(gid);
      const res = await apiRequest({ baseUrl: apiBase, path, method, token, body: { content: msgText } });
      setMsgText("");
      await loadGroupMessages(selectedGroup);
    } catch (e) { alert(e.message); }
  }

  return (
    <div className="grid lg:grid-cols-3 gap-6">
      <Section title="Your Groups" actions={<SecondaryButton onClick={refreshGroups}>Refresh</SecondaryButton>}>
        <div className="space-y-3">
          <div className="flex items-end gap-2">
            <Field label="Create new group">
              <TextInput placeholder="Team Alpha" value={newGroupName} onChange={(e) => setNewGroupName(e.target.value)} />
            </Field>
            <PrimaryButton disabled={!newGroupName || createBusy} onClick={createGroup}>{createBusy ? "Creating…" : "Create"}</PrimaryButton>
          </div>
          <ul className="divide-y divide-gray-200">
            {groups?.length ? groups.map((g) => (
              <li key={g.id || g.name} className="py-2 flex items-center justify-between">
                <div>
                  <div className="font-medium">{g.name}</div>
                  <div className="text-xs text-gray-500">id: {g.id ?? "?"}</div>
                </div>
                <SecondaryButton onClick={() => loadGroupMessages(g)}>Open</SecondaryButton>
              </li>
            )) : (
              <div className="text-sm text-gray-500">No groups yet.</div>
            )}
          </ul>
        </div>
      </Section>

      <Section title="Group Messages" actions={selectedGroup && <Pill>{selectedGroup?.name || `Group ${selectedGroup?.id ?? "?"}`}</Pill>}>
        {selectedGroup ? (
          <div className="space-y-3">
            <div className="max-h-80 overflow-auto rounded-xl border border-gray-200 p-3 bg-gray-50">
              {groupMessages?.length ? groupMessages.map((m) => (
                <div key={m.id} className="py-1">
                  <div className="text-sm"><span className="font-medium">{m?.message?.sender?.username || m?.sender?.username || "?"}:</span> {m?.message?.content || m?.content}</div>
                  <div className="text-xs text-gray-500">id: {m.id ?? m.messageId ?? "?"}</div>
                </div>
              )) : <div className="text-sm text-gray-500">No messages.</div>}
            </div>
            <div className="flex items-end gap-2">
              <Field label="Message">
                <TextInput placeholder="Say hi…" value={msgText} onChange={(e) => setMsgText(e.target.value)} />
              </Field>
              <PrimaryButton disabled={!msgText} onClick={sendGroupMessage}>Send</PrimaryButton>
            </div>
          </div>
        ) : (
          <div className="text-sm text-gray-500">Select a group to view messages.</div>
        )}
      </Section>

      <Section title="Mark Group Message as Read">
        <MarkGroupRead apiBase={apiBase} token={token} />
      </Section>
    </div>
  );
}

function MarkGroupRead({ apiBase, token }) {
  const [id, setId] = useState("");
  const [resp, setResp] = useState("");
  const [err, setErr] = useState("");

  async function mark() {
    setResp(""); setErr("");
    try {
      const { path, method } = endpoints.groupMessageMarkRead(id);
      const res = await apiRequest({ baseUrl: apiBase, path, method, token });
      setResp(typeof res === "string" ? res : JSON.stringify(res));
    } catch (e) { setErr(e.message); }
  }

  return (
    <div className="space-y-3 max-w-md">
      <Field label="Group Message ID">
        <TextInput placeholder="456" value={id} onChange={(e) => setId(e.target.value)} />
      </Field>
      <PrimaryButton onClick={mark} disabled={!id}>Mark as read</PrimaryButton>
      {resp && <div className="text-green-700 text-sm break-all">{resp}</div>}
      {err && <div className="text-red-600 text-sm">{err}</div>}
    </div>
  );
}

// ----- Friends -----
function Friends({ apiBase, token, username }) {
  const [friend, setFriend] = useState("");
  const [info, setInfo] = useState("");
  const [err, setErr] = useState("");

  async function add() {
    setInfo(""); setErr("");
    try {
      const { path, method } = endpoints.addFriend(friend);
      const res = await apiRequest({ baseUrl: apiBase, path, method, token });
      setInfo(`Added ${friend}`);
      setFriend("");
    } catch (e) { setErr(e.message); }
  }

  return (
    <div className="grid md:grid-cols-2 gap-6">
      <Section title="Add Friend">
        <div className="space-y-3 max-w-md">
          <Field label="Friend username">
            <TextInput placeholder="bob" value={friend} onChange={(e) => setFriend(e.target.value)} />
          </Field>
          <PrimaryButton onClick={add} disabled={!friend}>Add</PrimaryButton>
          {info && <div className="text-green-700 text-sm">{info}</div>}
          {err && <div className="text-red-600 text-sm">{err}</div>}
        </div>
      </Section>
      <Section title="Tip">
        <div className="text-sm text-gray-700">
          Use the Profile tab to fetch your details (including friends) if your backend includes them in the response.
        </div>
      </Section>
    </div>
  );
}

// ----- Profile -----
function Profile({ apiBase, token, username }) {
  const [profileUsername, setProfileUsername] = useState(username || "");
  const [data, setData] = useState(null);
  const [err, setErr] = useState("");

  async function fetchProfile() {
    setErr(""); setData(null);
    try {
      const { path, method } = endpoints.meByUsername(profileUsername);
      const res = await apiRequest({ baseUrl: apiBase, path, method, token });
      setData(res);
    } catch (e) { setErr(e.message); }
  }

  return (
    <Section title="Profile">
      <div className="grid md:grid-cols-2 gap-6">
        <div className="space-y-3 max-w-md">
          <Field label="Username">
            <TextInput value={profileUsername} onChange={(e) => setProfileUsername(e.target.value)} />
          </Field>
          <div className="flex items-center gap-2">
            <SecondaryButton onClick={fetchProfile}>Fetch</SecondaryButton>
            {err && <div className="text-red-600 text-sm">{err}</div>}
          </div>
          {data && (
            <pre className="text-xs bg-gray-900 text-gray-100 rounded-xl p-3 overflow-auto max-h-96">{JSON.stringify(data, null, 2)}</pre>
          )}
        </div>
        <div className="text-sm text-gray-600">
          <p className="mb-2">This makes a GET request to <code>/api/user/{'{username}'}</code> using your bearer token.</p>
          <ul className="list-disc pl-5 space-y-1">
            <li>If your <code>User</code> JSON includes <code>friends</code>, you will see them here.</li>
            <li>Adjust the endpoint map at the top if your route differs.</li>
            <li>All calls use <code>fetch</code> with <code>Authorization: Bearer</code>.</li>
          </ul>
        </div>
      </div>
    </Section>
  );
}
