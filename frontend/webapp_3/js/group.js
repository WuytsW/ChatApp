requireAuthOrRedirect();
handleLogoutButton();

const groupId = getParam('id');
if (!groupId) window.location.href = 'main.html';

const titleEl = qs('#group-title');
const membersEl = qs('#members');
const messagesEl = qs('#messages');
const addMemberForm = qs('#add-member-form');
const addMemberErr = qs('#add-member-error');
const sendForm = qs('#send-group-msg-form');
const sendErr = qs('#send-error');

let groupData = null;

function renderMembers(list) {
  membersEl.innerHTML = '';
  (list || []).forEach(u => {
    const li = document.createElement('li');
    li.className = 'list-item';
    const uname = u.username || u.name || u;
    li.innerHTML = `<a href="dm.html?with=${encodeURIComponent(uname)}">${uname}</a>`;
    membersEl.appendChild(li);
  });
}

function renderMessages(list) {
  messagesEl.innerHTML = '';
  const me = getMe()?.username || getMe()?.email_or_username;
  (list || []).forEach(m => {
    const div = document.createElement('div');
    const from = m.sender?.username || m.sender || m.author || m.from;
    const content = m.content || m.text || '';
    const time = m.createdAt || m.timestamp || m.sentAt;
    div.className = 'msg' + (from === me ? ' me' : '');
    div.innerHTML = `
      <div class="msg-meta">${from} · ${time ? formatTime(time) : ''}</div>
      <div>${content}</div>
    `;
    messagesEl.appendChild(div);
  });
  messagesEl.scrollTop = messagesEl.scrollHeight;
}

async function loadGroup() {
  try {
    const g = await getGroup(groupId);
    groupData = g;
    const name = g.name || g.group_name || `Group ${groupId}`;
    titleEl.textContent = name;
    renderMembers(g.members || g.memberList || []);
    renderMessages(g.messages || g.group_messages || []);
  } catch (e) {
    titleEl.textContent = `Group ${groupId}`;
    messagesEl.innerHTML = `<div class="error">${e.message}</div>`;
  }
}

addMemberForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  addMemberErr.textContent = '';
  const form = new FormData(addMemberForm);
  const member_name = form.get('member_name');
  const group_name = (groupData?.name || groupData?.group_name);
  if (!group_name) {
    addMemberErr.textContent = 'Group name unknown. Ensure the group details API returns a name.';
    return;
  }
  try {
    await addGroupMember(group_name, member_name);
    await loadGroup();
    addMemberForm.reset();
  } catch (err) {
    addMemberErr.textContent = err.message || 'Failed to add member';
  }
});

sendForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  sendErr.textContent = '';
  const form = new FormData(sendForm);
  const content = form.get('content');
  try {
    await sendGroupMessage(groupId, content);
    await loadGroup();
    sendForm.reset();
  } catch (err) {
    sendErr.textContent = err.message || 'Failed to send message';
  }
});

loadGroup();
// Polling for new messages
setInterval(loadGroup, 8000);
