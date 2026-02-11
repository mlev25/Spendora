import { defineStore } from 'pinia';
import { setAuthToken } from '../services/api.js';

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
      // Mentés localStorage-ba
      localStorage.setItem('user', JSON.stringify(userData));
    },

    setToken(token) {
      this.token = token;
      localStorage.setItem('token', token);
      // Axios header beállítása az apiClient instance-re
      setAuthToken(token);
    },

    logout() {
      this.user = null;
      this.token = null;
      this.isAuthenticated = false;
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      setAuthToken(null);
    },

    // Állapot helyreállítása localStorage-ból
    initializeAuth() {
      const token = localStorage.getItem('token');
      const user = localStorage.getItem('user');

      if (token && user) {
        this.token = token;
        this.user = JSON.parse(user);
        this.isAuthenticated = true;
        setAuthToken(token);
      }
    },
  },
});
