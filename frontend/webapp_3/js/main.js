requireAuthOrRedirect();
handleLogoutButton();

const dmList = qs('#dm-threads');
const groupsList = qs('#groups');

async function renderDMThreads() {
  dmList.innerHTML = '<div class="muted">Loading…</div>';
  try {
    const me = getMe();
    const threads = await getDMThreads();
    if (!threads?.length) {
      dmList.innerHTML = '<div class="muted">No direct messages yet.</div>';
      return;
    }
    dmList.innerHTML = '';
    threads.forEach(t => {
      // Support both shapes: backend-thread {other, lastMessage} or {username, lastMessage}
      const other = t.other || t.username || t.with || t.partner;
      const last = t.lastMessage || t.last || {};
      const preview = (last.content || '').slice(0, 80);
      const time = last.createdAt || last.timestamp || last.sentAt;
      const item = document.createElement('div');
      item.className = 'list-item';
      item.innerHTML = `
        <a href="dm.html?with=${encodeURIComponent(other)}">${other}</a>
        <div class="muted small">${preview}${preview.length === 80 ? '…' : ''} · ${time ? formatTime(time) : ''}</div>
      `;
      dmList.appendChild(item);
    });
  } catch (e) {
    dmList.innerHTML = `<div class="error small">${e.message}</div>`;
  }
}

async function renderGroups() {
  groupsList.innerHTML = '<div class="muted">Loading…</div>';
  try {
    const groups = await listGroups();
    if (!groups?.length) {
      groupsList.innerHTML = '<div class="muted">No groups yet.</div>';
      return;
    }
    groupsList.innerHTML = '';
    groups.forEach(g => {
      const id = g.id || g.group_id;
      const name = g.name || g.group_name || `Group ${id}`;
      const item = document.createElement('div');
      item.className = 'list-item';
      item.innerHTML = `
        <a href="group.html?id=${encodeURIComponent(id)}">${name}</a>
        <div class="muted small">${(g.members?.length ?? 0)} members</div>
      `;
      groupsList.appendChild(item);
    });
  } catch (e) {
    groupsList.innerHTML = `<div class="error small">${e.message}</div>`;
  }
}

renderDMThreads();
renderGroups();

// Simple polling (optional)
setInterval(() => { renderDMThreads(); renderGroups(); }, 15000);
