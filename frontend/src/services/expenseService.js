import apiClient from './apiClient';

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
