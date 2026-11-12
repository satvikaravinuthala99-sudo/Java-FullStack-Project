# EchoVault — Java Maven JSP/Servlet + MySQL

A working starter web application for a digital time capsule. It follows the supplied assignment's stack and restrictions: **Java 17, JSP, Servlets, JDBC, MySQL, Eclipse, Maven and Tomcat 10.1**. JSP pages use scriptlets; JSTL and JSP Expression Language are not used.

## Included features
- Registration and sign-in with salted PBKDF2 password hashing.
- Session-based user authentication and logout.
- Create private or email-addressed shared capsules.
- Attach one image/audio/video/PDF file (10 MB max).
- Server-side future-date lock: capsule contents and attachments are not served until unlock time.
- Dashboard with live countdowns.
- Separate admin login and read-only capsule overview.
- Premium dark nebula UI with a locally bundled SVG background.

## Requirements
- JDK 17
- Eclipse IDE for Enterprise Java and Web Developers (or Eclipse with Web Tools)
- Apache Tomcat 10.1.x
- MySQL 8.x
- Maven 3.8+

## 1. Create the database
1. Start MySQL.
2. Open MySQL Workbench or a terminal and execute `database/schema.sql`.
3. Default JDBC settings are `jdbc:mysql://localhost:3306/echovault_db`, user `root`, password `root`.
4. If your MySQL password differs, set environment variables before starting Eclipse/Tomcat:
   - `ECHOVAULT_DB_URL` (optional)
   - `ECHOVAULT_DB_USER`
   - `ECHOVAULT_DB_PASSWORD`

**Windows PowerShell example** (change the password):
```powershell
$env:ECHOVAULT_DB_USER="root"
$env:ECHOVAULT_DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```
**macOS/Linux Terminal example:**
```bash
export ECHOVAULT_DB_USER=root
export ECHOVAULT_DB_PASSWORD='YOUR_MYSQL_PASSWORD'
```
If Eclipse is already open, restart Eclipse after setting environment variables so Tomcat inherits them.

## 2. Import into Eclipse
1. Extract `EchoVault-Eclipse-Maven-Project.zip`.
2. Eclipse → **File → Import → Maven → Existing Maven Projects**.
3. Select the extracted `EchoVault` folder; ensure `pom.xml` is selected; Finish.
4. Right-click project → **Maven → Update Project** → check Force Update → OK.
5. Add Tomcat 10.1: **Window → Show View → Servers** → create a new server → Apache Tomcat v10.1 → select your Tomcat installation directory.
6. Add `EchoVault` to the server and start it.
7. Open `http://localhost:8080/echovault/`.

## 3. Build and run from Terminal
From the folder containing `pom.xml`:
```bash
mvn clean package
```
Copy `target/echovault.war` into Tomcat's `webapps` folder, start Tomcat, then visit `http://localhost:8080/echovault/`.

## 4. Demo admin
On first successful application startup, if the `admins` table is empty, the app creates:
- Username: `admin`
- Password: `EchoAdmin@123`

Admin page: `http://localhost:8080/echovault/admin-login.jsp`
Change the demo password implementation before production use. The sample admin seeding is intended for classroom demonstration only.

## 5. Upload storage
Uploaded files are saved outside the public web application directory at:
- Windows: `%USERPROFILE%\echovault-uploads`
- macOS/Linux: `~/echovault-uploads`

Only the authenticated owner or the invited recipient can request a capsule. Files are delivered through a servlet only after the capsule unlock time. The database stores file metadata, not the file bytes.

## 6. Important implementation notes
- Tomcat 10.1 uses `jakarta.servlet.*`; do not change imports to `javax.servlet.*`.
- The default JDBC password is `root` only as a local development convenience. Set environment variables for your own machine.
- The project uses prepared statements and foreign keys.
- Shared access is by recipient email after that person has registered and signed in with the same email.
- This is a college-project baseline, not a production digital-legacy service. Production deployment needs HTTPS, CSRF protection, email verification, password reset, malware scanning, rate limiting, encrypted file storage, backup/retention policy, and a carefully reviewed account recovery process.
- A date lock controls access through the application; it cannot stop a server/database administrator from reading stored data. Do not describe it as end-to-end encryption.

## Project structure
```text
EchoVault/
├── pom.xml
├── database/schema.sql
├── src/main/java/com/echovault/
│   ├── dao/             # JDBC data access
│   ├── listener/        # startup admin seeding
│   ├── model/           # Capsule model
│   ├── servlet/         # registration, login, capsules, admin
│   └── util/            # DB connection, password hashing, flash messages
└── src/main/webapp/
    ├── assets/css/style.css
    ├── assets/js/app.js
    ├── assets/images/nebula.svg
    ├── partials/
    └── JSP pages
```
