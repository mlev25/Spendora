import apiClient from './apiClient';

export const categoryService = {
  async getAll() {
    const response = await apiClient.get('/categories');
    return response.data;
  },
};
