import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

let isRedirecting = false;

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      const token = localStorage.getItem('token');
      const currentPath = window.location.pathname;
      if (token && !isRedirecting && currentPath !== '/login') {
        isRedirecting = true;

        console.log('Token lejárt - tisztítás és átirányítás...');

        localStorage.removeItem('token');
        localStorage.removeItem('user');
        delete apiClient.defaults.headers.common['Authorization'];

        setTimeout(() => {
          alert('A munkamenet lejárt!\n\nKérjük, jelentkezz be újra.');
          window.location.href = '/login';
          isRedirecting = false;
        }, 100);
      }
    }

    return Promise.reject(error);
  }
);

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

  // CAPTCHA
  async getCaptcha() {
    const response = await apiClient.get('/auth/captcha');
    return response.data; // Base64
  },
};

// Category service
export const categoryService = {
  async getAll() {
    const response = await apiClient.get('/categories');
    return response.data;
  },
};

// Expense service
export const expenseService = {
  async getAll() {
    const response = await apiClient.get('/expenses');
    return response.data;
  },

  async create(expenseData) {
    const response = await apiClient.post('/expenses', expenseData);
    return response.data;
  },

  async update(id, expenseData) {
    const response = await apiClient.put(`/expenses/${id}`, expenseData);
    return response.data;
  },

  async delete(id) {
    const response = await apiClient.delete(`/expenses/${id}`);
    return response.data;
  },

  // AI predikcio
  async predictCategory(expenseData) {
    const response = await apiClient.post('/expenses/predict-category', {
      name: expenseData.name,
      price: expenseData.price,
      description: expenseData.description
    });
    return response.data;
  },
};

// User service
export const userService = {
  async changePassword(passwordData) {
    const response = await apiClient.put('/users/change-password', passwordData);
    return response.data;
  },

  async updateProfile(profileData) {
    const response = await apiClient.put('/users/update-profile', profileData);
    return response.data;
  },

  async deleteAccount(password) {
    const response = await apiClient.delete('/users/delete-account', {
      data: { password },
    });
    return response.data;
  },
};

// Statistics service
export const statisticsService = {

  async getMonthly(year, month) {
    const response = await apiClient.get('/statistics/monthly', {
      params: { year, month }
    });
    return response.data;
  },

  async getYearly(year) {
    const response = await apiClient.get('/statistics/yearly', {
      params: { year }
    });
    return response.data;
  },

  async getCategorySpending(startDate, endDate) {
    const response = await apiClient.get('/statistics/categories', {
      params: { 
        startDate: startDate.toISOString().split('T')[0], // YYYY-MM-DD
        endDate: endDate.toISOString().split('T')[0] 
      }
    });
    return response.data;
  },

  async getSummary() {
    const response = await apiClient.get('/statistics/summary');
    return response.data;
  },
};

// Token minden requesthez
export const setAuthToken = (token) => {
  if (token) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

export default apiClient;
