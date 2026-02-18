import apiClient from './apiClient';

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
