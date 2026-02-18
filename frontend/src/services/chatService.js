import apiClient from './apiClient';

export const chatService = {
  // AI chatbot kérdés
  async ask(messages) {
    const response = await apiClient.post('/chat/ask', {
      messages: messages
    });
    return response.data.response;
  },
};
