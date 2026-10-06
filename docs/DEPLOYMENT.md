# 🚀 Complete Deployment Guide — Smart Placement System (SPMS)

Ye guide aapko project ko cloud par step-by-step deploy karne ke liye banayi gayi hai bina kisi confusion ke. Har step simple **Hinglish** me clearly explained hai.

---

## 📊 Platform Comparison & Free-Tier Limits (Pehle Ye Padho!)

Deployment service choose karne se pehle official limits aur restrictions samajhna zaroori hai:

| Service | Category | Free Tier Limits | Sleeping / Inactivity | Expiry / Credits | Verdict |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Vercel** | Frontend (React SPA) | • 100 GB Bandwidth / month<br>• Unlimited Deployments<br>• Global Fast Edge CDN | **Kabhi sleep nahi hota** (Static Edge Hosting) | **Hamesha Free** (Hobby plan) | 🟢 **Best for Frontend** |
| **Netlify** | Frontend (React SPA) | • 100 GB Bandwidth / month<br>• 300 Build minutes / month | **Kabhi sleep nahi hota** | **Hamesha Free** | 🟢 Good Alternative |
| **Render** | Backend (Spring Boot Docker) | • 512 MB RAM, 0.1 CPU<br>• 750 free instance hours / month (1 service 24/7 chal sakti hai) | ⚠️ **15 minutes inactivity ke baad sleep ho jata hai**.<br>Agli request par **30–60s cold start** lagta hai. | **Hamesha Free** (750 hours har month renew hote hain) | 🟡 **Best Free Option for Backend** |
| **Railway** | Backend / Fullstack | • 1 GB RAM, 2 vCPU | Sleep toggle kar sakte ho | ⚠️ **$5 One-time Trial credit (30 days)** ya $1/month free credit. Usage khatam hone par service pause ho jati hai. | 🟠 Trial ke liye accha, par permanent free nahi hai |
| **Aiven** | Managed MySQL Database | • **1 GB SSD Storage**<br>• 1 CPU, 1 GB RAM<br>• Dedicated MySQL 8.0 instance<br>• SSL strictly enforced | Inactivity par power off ho sakta hai (Dashboard se restart ho jata hai) | **Hamesha Free** (Koi 30-day trial expiry nahi hai) | 🟢 **Best for Free MySQL Database** |
| **Railway MySQL**| Managed MySQL Database | • Usage-based credits consume karta hai | Database 24/7 run hota hai | ⚠️ $5 trial credit jaldi khatam ho jata hai kyunki DB continuous run hota hai | 🟠 Sirf quick testing ke liye |

> 💡 **Hamari Recommended Free Setup**:
> - **Frontend**: **Vercel**
> - **Backend**: **Render** (using root Dockerfile)
> - **Database**: **Aiven for MySQL** (Free Tier with 1 GB Storage & SSL)

---

## ⚠️ Resume Uploads & Ephemeral Disk (Known Limitation)

### Hamara `LocalFileStorageServiceImpl` Kaise Kaam Karta Hai?
- Jab student resume upload karta hai, backend file ko local server disk par store karta hai: `/app/uploads/resumes/student_{id}_{uuid}.pdf`.
- Database me sirf iska path aur filename save hota hai.

### Free Cloud Hosts (Render / Container) Ka Issue:
- Render free tier aur standard containers ka filesystem **Ephemeral (Temporary)** hota hai.
- Jab bhi Render container restart hota hai, code redeploy hota hai, ya service 15-minute inactivity ke baad spin-down hoke wapas jagti hai, to **local disk reset ho jati hai**.
- Database me student ka resume link rahega, lekin disk se actual PDF delete ho jayegi (Download karne par `404 Not Found` aayega).

### Future Production Solutions (Roadmap):
1. **Cloud Object Storage (Recommended)**:
   - **Cloudflare R2** ya **AWS S3**: Resumes direct cloud bucket me upload honge.
   - Cloudflare R2 me **10 GB storage free** milti hai aur **zero egress/download fees** hoti hai!
   - Spring Boot me `S3FileStorageServiceImpl` implement karke AWS S3 SDK connect kiya ja sakta hai.
2. **Persistent Disk**:
   - Render / Railway par paid persistent volume mount karna (`/app/uploads`).

*(Abhi ke demo/college presentation ke liye local filesystem perfectly functional hai, bas restart par files clear hoti hain).*

---

## 🛠️ Step-by-Step Deployment Instructions

### STEP 1: MySQL Database Banayein (Aiven Setup)

1. [Aiven Console](https://console.aiven.io/) par jao aur free account banao.
2. **Create Service** par click karo:
   - Service Type: **MySQL** select karo.
   - Cloud Provider: **AWS** ya **Google Cloud** (apni nearest region chuno, e.g. `ap-south-1 Mumbai` ya `eu-central-1 Frankfurt`).
   - Service Plan: **Free Plan** (1 GB storage, 1 CPU, 1 GB RAM) select karo.
   - Service Name: `spms-mysql` rakho.
   - **Create Service** button par click karo.
3. Service create hone ke baad 2–3 minute me status **Running** ho jayega.
4. Dashboard me **Connection Information** tab se ye values copy kar lo:
   - **Host** (e.g., `mysql-xxxx-project.aivencloud.com`)
   - **Port** (e.g., `12345` ya standard port)
   - **User** (default `avnadmin`)
   - **Password** (eye icon click karke copy karo)
   - **Database Name** (default `defaultdb`)
   - **Service URI / JDBC URL**
5. *(Important)*: Aiven cloud database SSL enforce karta hai, isliye backend me `DB_SSL=true` set karna padega.

---

### STEP 2: Strong JWT_SECRET Generate Karein

JWT token HMAC-SHA256 se sign hota hai, jiske liye kam se kam **256-bit (32 bytes)** ka strong secret chahiye.

Apne local terminal ya PowerShell me ye command run karke 64-character random hex string generate karein:
```powershell
# Windows PowerShell:
-join ((1..64) | ForEach-Object { '{0:x}' -f (Get-Random -Max 16) })
```
Ya Git Bash / Linux / Mac terminal me:
```bash
openssl rand -hex 32
```
Output kuch aisa aayega: `9f8e7d6c5b4a3...` (isko safe jagah copy kar lo).

---

### STEP 3: Backend Deploy Karein (Render Setup)

1. [Render Dashboard](https://dashboard.render.com/) par jao aur GitHub se login karo.
2. **New +** button par click karo aur **Web Service** choose karo.
3. Apni GitHub repository select karo (`Abhinaw321/smart_placement`).
4. Basic Settings fill karo:
   - **Name**: `smart-placement-backend`
   - **Region**: Same region chuno jo database ki hai (e.g. Frankfurt / Singapore).
   - **Branch**: `main` (feature/deployment merge karne ke baad).
   - **Root Directory**: Khali chhod do (root `.`).
   - **Runtime**: **Docker** select karo.
   - **Dockerfile Path**: `./Dockerfile` (ya `./backend/Dockerfile`).
   - **Instance Type**: **Free** (0.1 CPU, 512 MB RAM).
5. **Environment Variables** section me jaakar **Add Environment Variable** click karke ye sab add karo:

| Variable Key | Sample Placeholder Value | Explanation |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `prod` | Production profile activate karta hai |
| `PORT` | `8080` | Render port (Render internally match karega) |
| `DB_HOST` | `your-db-host.aivencloud.com` | Aiven / Railway Host |
| `DB_PORT` | `3306` (ya Aiven assigned port) | Database Port |
| `DB_NAME` | `defaultdb` | Database Name |
| `DB_USERNAME` | `avnadmin` | Database User |
| `DB_PASSWORD` | `your_actual_db_password` | Database Password |
| `DB_SSL` | `true` | Aiven ke liye SSL enable karta hai |
| `JWT_SECRET` | *(Step 2 me generate kiya hua 64-char key)* | Cryptographic Signing Key (fail-fast check laga hai) |
| `JWT_EXPIRATION` | `86400000` | Token validity (24 hours in ms) |
| `CORS_ALLOWED_ORIGINS`| `http://localhost:3000` | *(Step 5 me yahan Vercel frontend URL replace karenge)* |
| `SEED_DEMO_DATA` | `true` | Pehli baar demo data seed karne ke liye `true` rakhein |
| `SWAGGER_ENABLED` | `true` | Production me API docs access karne ke liye |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Database me tables create karne ke liye |

6. **Create Web Service** par click karo.
7. Render Docker image build karega (Maven packages download karega aur jar compile karega). Isme pehli baar 3–5 minutes lagte hain.
8. Status **Live** hone par upar se backend URL copy kar lo:
   - Example: `https://smart-placement-backend.onrender.com`
9. Test Health: Browser me open karo:
   - `https://smart-placement-backend.onrender.com/actuator/health`
   - Status: `{"status":"UP"}` aana chahiye!

---

### STEP 4: Frontend Deploy Karein (Vercel Setup)

1. [Vercel Dashboard](https://vercel.com/) par jao aur GitHub se login karo.
2. **Add New...** -> **Project** par click karo.
3. Apni repo `smart_placement` import karo.
4. Project Configuration:
   - **Framework Preset**: **Vite** (auto-detected).
   - **Root Directory**: **Edit** click karke `frontend` select karo! (Most important!).
   - **Build Command**: `npm run build` (default).
   - **Output Directory**: `dist` (default).
   - **Install Command**: `npm install` (default).
5. **Environment Variables**:
   - Key: `VITE_API_BASE_URL`
   - Value: `https://smart-placement-backend.onrender.com` *(Apna Render backend URL paste karein bina trailing slash `/` ke)*.
6. **Deploy** button click karein.
7. Vercel build 30–60 seconds me finish ho jayega aur aapko Live URL mil jayega:
   - Example: `https://smart-placement.vercel.app`

---

### STEP 5: Backend CORS Update Karein

Ab aapke paas actual Vercel URL hai! Backend ko is domain se requests allow karne ke liye:
1. Render Dashboard me apne Web Service -> **Environment** tab par jao.
2. `CORS_ALLOWED_ORIGINS` variable ko update karo:
   ```text
   https://smart-placement.vercel.app,http://localhost:3000
   ```
   *(Apna actual Vercel domain daalein, comma separated)*.
3. **Save Changes** par click karein. Render service ko automatically restart kar dega.

---

## 🧪 Step 6: Smoke Test Checklist

Deployment verify karne ke liye ye steps follow karein:

- [ ] **1. Actuator Health Probe**:
  - Visit: `https://your-backend.onrender.com/actuator/health`
  - Response: `{"status":"UP"}`.
- [ ] **2. Swagger UI (Optional)**:
  - Visit: `https://your-backend.onrender.com/swagger-ui.html`
  - All 10 controller tags aur endpoints visible hone chahiye.
- [ ] **3. Student Login**:
  - Open Vercel URL (`https://your-frontend.vercel.app`).
  - Email: `student@smartplacement.com` | Password: `Student@123`
  - Student Dashboard open hona chahiye with Alex Rivera profile and metrics.
- [ ] **4. Job Application & Eligibility**:
  - Jobs tab me jao, Google LLC ya Microsoft job par "Apply" click karo.
  - Eligibility engine check pass hona chahiye aur application submit honi chahiye.
- [ ] **5. Recruiter Login**:
  - Email: `recruiter@google.com` | Password: `Recruiter@123`
  - Applicants review karo aur interview schedule test karo.
- [ ] **6. TPO Admin Login**:
  - Email: `admin@smartplacement.com` | Password: `Admin@123`
  - Analytics cards load hone chahiye aur CSV export download hona chahiye.
- [ ] **7. Browser Refresh Test (SPA Routing)**:
  - Student ya recruiter portal par rahte hue browser refresh (F5) karo.
  - Page reload hona chahiye bina 404 error ke (`vercel.json` rewrite verification).

---

## 🩺 Step 7: Troubleshooting Guide

| Problem / Error | Cause | Exact Fix |
| :--- | :--- | :--- |
| **CORS error in Browser Console** (`No 'Access-Control-Allow-Origin' header`) | Backend ke `CORS_ALLOWED_ORIGINS` me Vercel ka exact URL match nahi kar raha. | Render dashboard me `CORS_ALLOWED_ORIGINS` check karein. Dhyaan rahe: protocol (`https://`) sahi ho aur end me trailing slash `/` na ho. |
| **502 Bad Gateway on Render** | Backend container crash ho gaya ya port bind nahi ho paya. | Render ke **Logs** tab check karein. Check karein ki `JWT_SECRET` empty to nahi hai (fail-fast check) aur database credentials sahi hain. |
| **Database Connection Refused / Timeout** | Database server unreachable hai ya firewall block kar raha hai. | Aiven dashboard me check karein ki service state "Running" hai. Host aur Port cross-verify karein. Aiven me IP filter allow all (`0.0.0.0/0`) hona chahiye. |
| **SSL Handshake Exception (`useSSL`)** | Managed MySQL (Aiven) SSL connection demand karta hai par config me `DB_SSL=false` hai. | Render environment me `DB_SSL=true` set karein aur redeploy karein. |
| **404 Not Found on Page Refresh (F5)** | Vercel static server ko client-side route (`/student`, `/recruiter`) ki physical HTML file nahi mili. | `frontend/vercel.json` me rewrite rule verify karein (`{"source": "/(.*)", "destination": "/index.html"}`). |
| **First Request Takes 50+ Seconds (Cold Start)** | Render free tier 15 min inactivity par service ko spin-down (sleep) kar deta hai. | Normal behavior hai! Render container ko boot up hone me 30-60s lagte hain. Page par loading spinner dikhega, fir smoothly chalega. |
| **JWT_SECRET Missing Error on Startup** | Prod profile me security guard ne app crash kar diya. | Render me `JWT_SECRET` environment variable add karein with minimum 32 bytes (64 hex characters). |
