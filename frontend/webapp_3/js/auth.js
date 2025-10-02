const loginForm = qs('#login-form');
const registerForm = qs('#register-form');
const tabLogin = qs('#tab-login');
const tabRegister = qs('#tab-register');

function showLogin() {
  tabLogin.classList.add('active'); tabRegister.classList.remove('active');
  loginForm.classList.remove('hidden'); registerForm.classList.add('hidden');
}
function showRegister() {
  tabRegister.classList.add('active'); tabLogin.classList.remove('active');
  registerForm.classList.remove('hidden'); loginForm.classList.add('hidden');
}

tabLogin.addEventListener('click', showLogin);
tabRegister.addEventListener('click', showRegister);

// If already logged in, go to main
if (getToken()) window.location.href = 'main.html';

loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  qs('#login-error').textContent = '';
  const form = new FormData(loginForm);
  const email_or_password = form.get('email_or_password');
  const password = form.get('password');
  try {
    const data = await login(email_or_password, password);
    // Expect backend returns { token, user } or similar
    const token = data.token || data.jwt || data.accessToken;
    const user = data.user || data.me || await getCurrentUser().catch(() => ({}));
    if (!token) throw new Error('No token returned by server.');
    saveToken(token, user);
    window.location.href = 'main.html';
  } catch (err) {
    qs('#login-error').textContent = err.message || 'Login failed';
  }
});

registerForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  qs('#register-error').textContent = '';
  const form = new FormData(registerForm);
  const payload = {
    username: form.get('username'),
    email: form.get('email'),
    password: form.get('password')
  };
  try {
    await registerUser(payload);
    // After register, auto-login or switch to login tab
    showLogin();
    qs('#login-error').textContent = 'Registration successful. Please log in.';
  } catch (err) {
    qs('#register-error').textContent = err.message || 'Registration failed';
  }
});
