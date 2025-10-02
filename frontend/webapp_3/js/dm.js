requireAuthOrRedirect();
handleLogoutButton();

const other = getParam('with');
if (!other) window.location.href = 'main.html';

qs('#dm-title').textContent = `Chat with ${other}`;
const messagesEl = qs('#messages');
const form = qs('#send-dm-form');
const sendErr = qs('#send-error');

function renderMessages(list) {
  messagesEl.innerHTML = '';
  const me = getMe()?.username || getMe()?.email_or_username;
  (list || []).forEach(m => {
    const from = m.sender?.username || m.sender || m.from;
    const content = m.content || m.text || '';
    const time = m.createdAt || m.timestamp || m.sentAt;
    const div = document.createElement('div');
    div.className = 'msg' + (from === me ? ' me' : '');
    div.innerHTML = `
      <div class="msg-meta">${from} · ${time ? formatTime(time) : ''}</div>
      <div>${content}</div>
    `;
    messagesEl.appendChild(div);
  });
  messagesEl.scrollTop = messagesEl.scrollHeight;
}

async function loadDM() {
  try {
    const msgs = await getDMWith(other);
    renderMessages(msgs);
  } catch (e) {
    messagesEl.innerHTML = `<div class="error">${e.message}</div>`;
  }
}

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  sendErr.textContent = '';
  const content = new FormData(form).get('content');
  try {
    await sendDM(other, content);
    await loadDM();
    form.reset();
  } catch (err) {
    sendErr.textContent = err.message || 'Failed to send message';
  }
});

loadDM();
setInterval(loadDM, 6000);
