import requests

BASE = "http://localhost:8080"

def safe_json(resp):
    try:
        return resp.json()
    except Exception:
        return {"raw": resp.text}

def register(username, email, password):
    print(f"📌 Registering {username}...")
    r = requests.post(f"{BASE}/api/users/register", json={
        "username": username,
        "email": email,
        "password": password
    })
    print("  Status:", r.status_code, safe_json(r))

def login(email, password):
    print(f"🔐 Logging in as {email}...")
    r = requests.post(f"{BASE}/api/auth/login", json={
        "email": email,
        "password": password
    })
    print("  Status:", r.status_code, safe_json(r))
    return safe_json(r).get("token")

def send_message(token, recipient, content):
    headers = {"Authorization": f"Bearer {token}"} if token else {}
    r = requests.post(f"{BASE}/api/messages/send", headers=headers, json={
        "recipient": recipient,
        "content": content
    })
    print(f"📨 Sending to {recipient}:")
    print("  Status:", r.status_code, safe_json(r))

if __name__ == "__main__":
    # 1. Register both users (ignore if already exists)
    register("user1", "user1@example.com", "pass123")
    register("user2", "user2@example.com", "pass123")

    # 2. Login and get tokens
    token_user1 = login("user1@example.com", "pass123")
    token_user2 = login("user2@example.com", "pass123")

    # 3. Messaging Scenarios

    print("\n✅ CASE 1: user1 → user2 (valid)")
    send_message(token_user1, "user2", "Hello from user1 ✅")

    print("\n✅ CASE 2: user2 → user1 (valid)")
    send_message(token_user2, "user1", "Hello from user2 ✅")

    print("\n❌ CASE 3: user2 tries to impersonate user1 (impossible now)")
    # No "sender" field anymore — JWT decides sender.
    # This will send as user2 no matter what.
    send_message(token_user2, "user2", "Trying to spoof user1 🚫")

    print("\n❌ CASE 4: No token (should be 401)")
    send_message(None, "user1", "No token 🚫")
