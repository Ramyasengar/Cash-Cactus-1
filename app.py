from flask import Flask, render_template, request, redirect, url_for, session
import sqlite3
import re

app = Flask(__name__, static_folder='static', template_folder='templates')
app.secret_key = 'your_secret_key_change_this'
app.config['SESSION_COOKIE_SAMESITE'] = "Lax"
app.config['SESSION_COOKIE_SECURE'] = False

# ---------------- DB CONNECTION ----------------
def get_db_connection():
    conn = sqlite3.connect("budget.db")
    conn.row_factory = sqlite3.Row
    return conn

# ---------------- INIT DB ----------------
def init_db():
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            mobile TEXT UNIQUE NOT NULL,
            email TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL
        )
    ''')

    cursor.execute('''
        CREATE TABLE IF NOT EXISTS expenses (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            title TEXT NOT NULL,
            amount REAL NOT NULL,
            category TEXT NOT NULL,
            date TEXT NOT NULL,
            FOREIGN KEY(user_id) REFERENCES users(id)
        )
    ''')

    conn.commit()
    conn.close()

init_db()

# ---------------- LANDING PAGE ----------------
@app.route('/')
def index():
    return render_template('index.html')

# ---------------- REGISTER ----------------
@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        name = request.form.get('name', '').strip()
        email = request.form.get('email', '').strip()
        mobile = request.form.get('mobile', '').strip()
        password = request.form.get('password', '').strip()
        confirm_password = request.form.get('confirm-password', '').strip()

        # ---------- BASIC EMPTY CHECK ----------
        if not all([name, email, mobile, password, confirm_password]):
            return render_template('register.html', error="All fields are required")

        # ---------- EMAIL VALIDATION ----------
        email_regex = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
        if not re.match(email_regex, email):
            return render_template('register.html', error="Invalid email format")

        # ---------- MOBILE VALIDATION (STRICT) ----------
        if not mobile.isdigit() or len(mobile) != 10:
            return render_template(
                'register.html',
                error="Mobile number must contain exactly 10 digits"
            )

        # ---------- PASSWORD VALIDATION ----------
        if len(password) < 6:
            return render_template(
                'register.html',
                error="Password must be at least 6 characters long"
            )

        if password != confirm_password:
            return render_template(
                'register.html',
                error="Passwords do not match"
            )

        try:
            conn = get_db_connection()
            cursor = conn.cursor()

            # ---------- INSERT USER ----------
            cursor.execute("""
                INSERT INTO users (name, email, mobile, password)
                VALUES (?, ?, ?, ?)
            """, (name, email, mobile, password))

            conn.commit()
            conn.close()

            # AFTER REGISTER → LOGIN PAGE
            return redirect(url_for('login'))

        except sqlite3.IntegrityError:
            return render_template(
                'register.html',
                error="Email or mobile number already registered"
            )
        except Exception as e:
            return render_template(
                'register.html',
                error=f"Server error: {str(e)}"
            )

    return render_template('register.html')

# ---------------- LOGIN ----------------
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form.get('email', '').strip()
        password = request.form.get('password', '').strip()

        if not email or not password:
            return render_template('login.html', error="Email and password are required")

        try:
            conn = get_db_connection()
            cursor = conn.cursor()

            cursor.execute(
                "SELECT * FROM users WHERE email=? AND password=?",
                (email, password)
            )
            user = cursor.fetchone()
            conn.close()

            if user:
                session['user_id'] = user['id']
                session['user_name'] = user['name']

                return redirect(url_for('home'))
            else:
                return render_template('login.html', error="Invalid email or password")

        except Exception as e:
            return render_template('login.html', error=f"Error: {str(e)}")

    return render_template('login.html')

# ---------------- HOME PAGE ----------------
@app.route('/home')
def home():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    return render_template('home.html', user_name=session.get('user_name'))


@app.route('/edit-profile', methods=['GET', 'POST'])
def edit_profile():
    if 'user_id' not in session:
        return redirect(url_for('login'))

    conn = get_db_connection()
    cursor = conn.cursor()

    if request.method == 'POST':
        name = request.form.get('name').strip()
        mobile = request.form.get('mobile').strip()
        email = request.form.get('email').strip()

        # BACKEND VALIDATIONS
        if not name or not mobile or not email:
            return render_template(
                'edit_profile.html',
                error="All fields are required",
                user=request.form
            )

        if not mobile.isdigit() or len(mobile) != 10:
            return render_template(
                'edit_profile.html',
                error="Mobile number must be 10 digits",
                user=request.form
            )

        if not email.endswith("@gmail.com"):
            return render_template(
                'edit_profile.html',
                error="Email must be username@gmail.com",
                user=request.form
            )

        try:
            cursor.execute("""
                UPDATE users
                SET name = ?, mobile = ?, email = ?
                WHERE id = ?
            """, (name, mobile, email, session['user_id']))

            conn.commit()

            # update session name also
            session['user_name'] = name

            return redirect(url_for('home'))

        except sqlite3.IntegrityError:
            return render_template(
                'edit_profile.html',
                error="Email or mobile already exists",
                user=request.form
            )

    # GET REQUEST → load existing data
    cursor.execute("SELECT name, mobile, email FROM users WHERE id = ?", (session['user_id'],))
    user = cursor.fetchone()
    conn.close()

    return render_template('edit_profile.html', user=user)


# ---------------- DASHBOARD ----------------
@app.route('/dashboard')
def dashboard():
    if 'user_id' not in session:
        return redirect(url_for('login'))

    try:
        conn = get_db_connection()
        cursor = conn.cursor()

        cursor.execute(
            "SELECT * FROM expenses WHERE user_id=? ORDER BY date DESC",
            (session['user_id'],)
        )
        expenses = cursor.fetchall()
        conn.close()

        return render_template(
            'homedashboard.html',
            user_name=session.get('user_name'),
            expenses=expenses
        )

    except Exception as e:
        return f"Error loading dashboard: {str(e)}"

# ---------------- LOGOUT ----------------
@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('index'))

@app.route('/about')
def about():
    return render_template('about.html')

@app.route("/privacy")
def privacy():
    return render_template("privacy.html")


# ---------------- RUN ----------------
if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=False)
