import { defineStore } from 'pinia';
import axios from 'axios';

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
      // Axios default header beállítása
      if (token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      }
    },

    logout() {
      this.user = null;
      this.token = null;
      this.isAuthenticated = false;
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      delete axios.defaults.headers.common['Authorization'];
    },

    // Állapot helyreállítása localStorage-ból
    initializeAuth() {
      const token = localStorage.getItem('token');
      const user = localStorage.getItem('user');

      if (token && user) {
        this.token = token;
        this.user = JSON.parse(user);
        this.isAuthenticated = true;
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      }
    },
  },
});
