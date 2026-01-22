import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Axios instance létrehozása
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // CAPTCHA session cookie-hoz
});

// Auth service
export const authService = {
  // Login
  async login(credentials) {
    const response = await apiClient.post('/auth/login', credentials);
    return response.data;
  },

  // Register
  async register(userData) {
    const response = await apiClient.post('/auth/register', userData);
    return response.data;
  },

  // CAPTCHA kép lekérése
  async getCaptcha() {
    const response = await apiClient.get('/auth/captcha');
    return response.data; // Base64 enkódolt kép
  },
};

// Token beállítása minden requesthez
export const setAuthToken = (token) => {
  if (token) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

export default apiClient;
