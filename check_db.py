import sqlite3

conn = sqlite3.connect('budget.db')
cursor = conn.cursor()

print("=== Users Table Schema ===")
cursor.execute("PRAGMA table_info(users)")
for row in cursor.fetchall():
    print(row)

print("\n=== Users Table Contents ===")
cursor.execute("SELECT * FROM users")
for row in cursor.fetchall():
    print(row)

conn.close()
