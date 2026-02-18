import apiClient from './apiClient';

export const adminService = {
  // Összes felhasználó lekérése
  async getAllUsers() {
    const response = await apiClient.get('/admin/users');
    return response.data;
  },
  
  // Globális statisztikák
  async getGlobalStats() {
    const response = await apiClient.get('/admin/stats');
    return response.data;
  },
};
