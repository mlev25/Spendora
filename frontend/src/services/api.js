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

// Category service
export const categoryService = {
  // Összes kategória lekérése
  async getAll() {
    const response = await apiClient.get('/categories');
    return response.data;
  },
};

// Expense service
export const expenseService = {
  // Összes expense lekérése (saját)
  async getAll() {
    const response = await apiClient.get('/expenses');
    return response.data;
  },

  // Expense létrehozása
  async create(expenseData) {
    const response = await apiClient.post('/expenses', expenseData);
    return response.data;
  },

  // Expense frissítése
  async update(id, expenseData) {
    const response = await apiClient.put(`/expenses/${id}`, expenseData);
    return response.data;
  },

  // Expense törlése
  async delete(id) {
    const response = await apiClient.delete(`/expenses/${id}`);
    return response.data;
  },

  // AI kategória predikció
  async predictCategory(expenseData) {
    const response = await apiClient.post('/expenses/predict-category', {
      name: expenseData.name,
      price: expenseData.price,
      description: expenseData.description
    });
    return response.data;
  },
};

// User service (profile management)
export const userService = {
  // Jelszó változtatás
  async changePassword(passwordData) {
    const response = await apiClient.put('/users/change-password', passwordData);
    return response.data;
  },

  // Profil frissítése
  async updateProfile(profileData) {
    const response = await apiClient.put('/users/update-profile', profileData);
    return response.data;
  },

  // Fiók törlése
  async deleteAccount(password) {
    const response = await apiClient.delete('/users/delete-account', {
      data: { password }, // DELETE request body
    });
    return response.data;
  },
};

// Statistics service
export const statisticsService = {
  // Havi statisztikák (kategória bontással)
  async getMonthly(year, month) {
    const response = await apiClient.get('/statistics/monthly', {
      params: { year, month }
    });
    return response.data;
  },

  // Éves statisztikák (12 hónapos bontással)
  async getYearly(year) {
    const response = await apiClient.get('/statistics/yearly', {
      params: { year }
    });
    return response.data;
  },

  // Kategória részletezés (időszakra)
  async getCategorySpending(startDate, endDate) {
    const response = await apiClient.get('/statistics/categories', {
      params: { 
        startDate: startDate.toISOString().split('T')[0], // YYYY-MM-DD
        endDate: endDate.toISOString().split('T')[0] 
      }
    });
    return response.data;
  },

  // Gyors KPI összegzés
  async getSummary() {
    const response = await apiClient.get('/statistics/summary');
    return response.data;
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
