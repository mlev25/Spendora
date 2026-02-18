import apiClient from './apiClient';

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
