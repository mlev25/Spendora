import { defineStore } from 'pinia';
import { setAuthToken } from '../services/api.js';

// JWT token dekódolása és ellenőrzése
function isTokenExpired(token) {
  if (!token) return true;
  
  try {
    // JWT payload dekódolása (format: header.payload.signature)
    const payload = JSON.parse(atob(token.split('.')[1]));
    
    // exp mező Unix timestamp másodpercben
    if (payload.exp) {
      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp < currentTime;
    }
    
    // Ha nincs exp mező, biztonsági okokból lejártnak tekintjük
    console.warn('Token has no expiration field - treating as expired');
    return true;
  } catch (error) {
    console.error('Token parsing error:', error);
    return true; // Ha nem lehet dekódolni, tekintjük lejártnak
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    isAuthenticated: false,
  }),

  getters: {
    getUser: (state) => state.user,
    getToken: (state) => state.token,
    isLoggedIn: (state) => state.isAuthenticated,
  },

  actions: {
    setUser(userData) {
      this.user = userData;
      this.isAuthenticated = true;
      localStorage.setItem('user', JSON.stringify(userData));
    },

    setToken(token) {
      this.token = token;
      localStorage.setItem('token', token);
      setAuthToken(token);
    },

    logout() {
      // Chat history törlése a kilépő felhasználóhoz
      const userId = this.user?.id || this.user?.username || 'guest';
      sessionStorage.removeItem(`chatMessages_${userId}`);

      this.user = null;
      this.token = null;
      this.isAuthenticated = false;
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      setAuthToken(null);
    },

    initializeAuth() {
      const token = localStorage.getItem('token');
      const user = localStorage.getItem('user');

      if (token && user) {
        // Ellenőrizzük, hogy a token még érvényes-e
        if (isTokenExpired(token)) {
          console.log('Token expired, logging out...');
          this.logout();
          return;
        }

        this.token = token;
        this.user = JSON.parse(user);
        this.isAuthenticated = true;
        setAuthToken(token);
      }
    },

    // Token érvényesség ellenőrzése (pl. navigation guard-hoz)
    checkTokenValidity() {
      if (this.isAuthenticated && this.token) {
        if (isTokenExpired(this.token)) {
          console.log('Token expired during session, logging out...');
          this.logout();
          return false;
        }
        return true;
      }
      return false;
    },
  },
});
