# Spendora Frontend - Autentikáció Implementáció

## Elkészült funkciók ✅

### 1. **Pinia Store (State Management)**
- **Fájl**: `src/stores/auth.js`
- Felhasználói állapot kezelése
- Token tárolás és kezelés
- localStorage integráció
- Automatikus token visszaállítás oldal újratöltéskor

### 2. **API Service**
- **Fájl**: `src/services/api.js`
- Axios konfiguráció
- Backend endpoints:
  - `POST /api/auth/login` - Bejelentkezés
  - `POST /api/auth/register` - Regisztráció
  - `GET /api/auth/captcha` - CAPTCHA kép lekérése
- Cookie support (CAPTCHA session)
- Authorization header automatikus beállítása

### 3. **Login Page** 🔐
- **Fájl**: `src/views/LoginView.vue`
- Funkciók:
  - Username/Password mezők
  - **CAPTCHA megjelenítés 3 sikertelen próbálkozás után**
  - CAPTCHA frissítés gomb
  - Hibakezelés (LOGIN_FAILED, CAPTCHA_REQUIRED)
  - Sikeres bejelentkezés után átirányítás
  - Responsive design
  - Többnyelvű (HU/EN)
  - Sötét/világos téma támogatás

### 4. **Register Page** 📝
- **Fájl**: `src/views/RegisterView.vue`
- Mezők:
  - Teljes név
  - Felhasználónév
  - Email cím
  - Jelszó (min. 6 karakter)
  - Jelszó megerősítés
  - Szerepkör választás (USER/ADMIN)
- Validációk:
  - Email formátum ellenőrzés
  - Jelszó egyezés ellenőrzés
  - Backend hibák kezelése (felhasználónév foglalt)
- Sikeres regisztráció után átirányítás login-ra

### 5. **Router Konfigurálás** 🛣️
- **Fájl**: `src/router/index.js`
- Új route-ok:
  - `/login` - LoginView
  - `/register` - RegisterView
- Navigation guard: bejelentkezett felhasználók nem érhetik el a login/register oldalakat

### 6. **Header Frissítés** 🎨
- **Fájl**: `src/components/LandingHeader.vue`
- Dinamikus tartalom bejelentkezési állapot alapján:
  - **Kijelentkezve**: Login + Register linkek
  - **Bejelentkezve**: Felhasználónév + Kijelentkezés gomb
- Reaktív store integráció

### 7. **Fordítások** 🌍
- **Fájlok**: `src/locales/hu.json`, `src/locales/en.json`
- Teljes fordítás:
  - Login oldal összes szövege
  - Register oldal összes szövege
  - Header új elemek
  - Hibaüzenetek
  - Sikeres műveletek üzenetei

### 8. **Main.js Frissítés**
- **Fájl**: `src/main.js`
- Pinia inicializálása
- Auth store automatikus betöltése (token helyreállítás)

## Technikai részletek 🔧

### Backend Integráció
- **Base URL**: `http://localhost:8080/api`
- **CORS**: Engedélyezve `localhost:5173` és `127.0.0.1:5173`
- **Credentials**: `withCredentials: true` (CAPTCHA session)

### CAPTCHA Logika
1. Első 2 sikertelen próbálkozás: normál hibakezelés
2. 3. sikertelen próbálkozástól: backend `CAPTCHA_REQUIRED` hibát dob
3. Frontend automatikusan betölti és megjeleníti a CAPTCHA-t
4. Felhasználó megadja a CAPTCHA választ
5. Login újrapróbálás CAPTCHA-val

### Biztonság
- JWT token tárolás localStorage-ban
- Automatikus Authorization header beállítás minden API kéréshez
- Token lejárat kezelés (backend: 1 óra)
- CSRF védelem (credentials support)

## Használat 📖

### Fejlesztői környezet indítása

```bash
cd frontend
npm install  # ha még nem tetted
npm run dev
```

### Tesztelés

1. **Regisztráció**: `http://localhost:5173/register`
   - Adj meg minden adatot
   - Válassz USER vagy ADMIN szerepkört

2. **Bejelentkezés**: `http://localhost:5173/login`
   - Add meg a felhasználónevet és jelszót
   - 3 rossz próbálkozás után megjelenik a CAPTCHA

3. **CAPTCHA tesztelés**:
   - Próbálj meg 3x rossz jelszóval bejelentkezni
   - A 3. próbálkozás után automatikusan megjelenik a CAPTCHA

## Projekt Struktúra 📁

```
frontend/
├── src/
│   ├── components/
│   │   └── LandingHeader.vue    ✅ Frissítve
│   ├── locales/
│   │   ├── hu.json              ✅ Frissítve
│   │   └── en.json              ✅ Frissítve
│   ├── router/
│   │   └── index.js             ✅ Frissítve
│   ├── services/
│   │   └── api.js               ✨ ÚJ
│   ├── stores/
│   │   └── auth.js              ✨ ÚJ
│   ├── views/
│   │   ├── LandingView.vue
│   │   ├── LoginView.vue        ✨ ÚJ
│   │   └── RegisterView.vue     ✨ ÚJ
│   ├── App.vue
│   ├── main.js                  ✅ Frissítve
│   └── style.css
└── package.json                 ✅ Pinia már telepítve
```

## Következő lépések (opcionális) 🚀

1. Dashboard oldal létrehozása bejelentkezett felhasználóknak
2. Route guard védett oldalakhoz
3. Token refresh mechanizmus
4. "Emlékezz rám" funkció
5. Elfelejtett jelszó funkció
6. Email verifikáció
7. Profil szerkesztés oldal
8. Loading states finomhangolása

## Megjegyzések 📝

- A backend 8080-as porton kell hogy fusson
- A CAPTCHA session-based, ezért fontos a `withCredentials: true`
- A Pinia store reaktív, így a Header automatikusan frissül bejelentkezéskor
- A CSS változók támogatják a sötét/világos témát
- Bootstrap 5 használva a form elemekhez és alert üzenetekhez

---
**Készítette**: GitHub Copilot  
**Dátum**: 2026. január 22.
