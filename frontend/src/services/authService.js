import apiClient from './apiClient';

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

  // CAPTCHA
  async getCaptcha() {
    const response = await apiClient.get('/auth/captcha');
    return response.data; // Base64
  },
};
