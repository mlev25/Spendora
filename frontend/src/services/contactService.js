import apiClient from './apiClient';

export const contactService = {
  async sendMessage(data) {
    const response = await apiClient.post('/contact', data);
    return response.data;
  },
};
