// Small helpers
function qs(sel, el = document) { return el.querySelector(sel); }
function qsa(sel, el = document) { return [...el.querySelectorAll(sel)]; }

function getParam(name) {
  const p = new URLSearchParams(window.location.search);
  return p.get(name);
}

function formatTime(ts) {
  // Accept ISO string or epoch millis
  try {
    const d = typeof ts === 'number' ? new Date(ts) : new Date(String(ts));
    return d.toLocaleString();
  } catch { return String(ts); }
}

function saveToken(token, me) {
  localStorage.setItem('token', token);
  if (me) localStorage.setItem('me', JSON.stringify(me));
}

function getToken() { return localStorage.getItem('token'); }
function getMe() { try { return JSON.parse(localStorage.getItem('me') || '{}'); } catch { return {}; } }

function requireAuthOrRedirect() {
  if (!getToken()) { window.location.href = 'index.html'; }
}

function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('me');
  window.location.href = 'index.html';
}

function handleLogoutButton() {
  const btn = qs('#logout-btn');
  if (btn) btn.addEventListener('click', logout);
}
