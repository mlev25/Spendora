<template>
  <div class="chat-widget">
    <!-- Chat gomb (ha be van zárva) -->
    <button v-if="!isOpen" @click="openChat" class="chat-toggle-btn" :title="$t('chat.openChat')">
      💬
    </button>

    <!-- Chat ablak (ha nyitva van) -->
    <div v-if="isOpen" class="chat-window">
      <!-- Header -->
      <div class="chat-header">
        <div class="chat-title">
          <span class="chat-icon">🤖</span>
          <span>{{ $t('chat.title') }}</span>
        </div>
        <button @click="closeChat" class="chat-close-btn" :title="$t('chat.close')">✕</button>
      </div>

      <!-- Messages -->
      <div class="chat-messages" ref="messagesContainer">
        <!-- Welcome message -->
        <div v-if="messages.length === 0" class="chat-message assistant">
          <div class="message-content">
            {{ $t('chat.welcomeMessage') }}
          </div>
        </div>

        <!-- Conversation messages -->
        <div 
          v-for="(msg, index) in messages" 
          :key="index" 
          :class="['chat-message', msg.role]"
        >
          <div class="message-content">
            {{ msg.content }}
          </div>
        </div>

        <!-- Loading indicator -->
        <div v-if="isLoading" class="chat-message assistant">
          <div class="message-content loading">
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
          </div>
        </div>
      </div>

      <!-- Input -->
      <div class="chat-input-area">
        <input 
          v-model="userInput"
          @keyup.enter="sendMessage"
          :placeholder="$t('chat.inputPlaceholder')"
          :disabled="isLoading"
          class="chat-input"
        />
        <button 
          @click="sendMessage" 
          :disabled="isLoading || !userInput.trim()"
          class="chat-send-btn"
          :title="$t('chat.send')"
        >
          ➤
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { chatService } from '@/services/api';

export default {
  name: 'ChatWidget',
  data() {
    return {
      isOpen: false,
      messages: [],
      userInput: '',
      isLoading: false,
    };
  },
  methods: {
    openChat() {
      this.isOpen = true;
    },
    closeChat() {
      this.isOpen = false;
      // Opcionális: chat törlése bezáráskor
      // this.messages = [];
    },
    async sendMessage() {
      if (!this.userInput.trim() || this.isLoading) return;

      const userMessage = this.userInput.trim();
      this.userInput = '';

      // User üzenet hozzáadása
      this.messages.push({
        role: 'user',
        content: userMessage,
      });

      // Scroll to bottom
      this.$nextTick(() => {
        this.scrollToBottom();
      });

      // Loading state
      this.isLoading = true;

      try {
        // API hívás
        const response = await chatService.ask(this.messages);

        // Assistant válasz hozzáadása
        this.messages.push({
          role: 'assistant',
          content: response,
        });

        // Scroll to bottom
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } catch (error) {
        console.error('Chat error:', error);
        this.messages.push({
          role: 'assistant',
          content: this.$t('chat.errorMessage'),
        });
      } finally {
        this.isLoading = false;
      }
    },
    scrollToBottom() {
      const container = this.$refs.messagesContainer;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    },
  },
};
</script>

<style scoped>
.chat-widget {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

.chat-toggle-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  border: none;
  font-size: 28px;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.4);
  transition: all 0.3s ease;
}

.chat-toggle-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 123, 255, 0.6);
}

.chat-window {
  width: 380px;
  height: 550px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 1.1rem;
}

.chat-icon {
  font-size: 1.5rem;
}

.chat-close-btn {
  background: transparent;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.2s;
}

.chat-close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8f9fa;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-message {
  display: flex;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.chat-message.user {
  justify-content: flex-end;
}

.chat-message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  line-height: 1.5;
}

.chat-message.user .message-content {
  background: #007bff;
  color: white;
  border-bottom-right-radius: 4px;
}

.chat-message.assistant .message-content {
  background: white;
  color: #2c3e50;
  border: 1px solid #e0e0e0;
  border-bottom-left-radius: 4px;
}

.message-content.loading {
  display: flex;
  gap: 6px;
  padding: 12px 20px;
}

.loading-dot {
  width: 8px;
  height: 8px;
  background: #007bff;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% { 
    transform: scale(0);
  } 
  40% { 
    transform: scale(1);
  }
}

.chat-input-area {
  display: flex;
  padding: 16px;
  background: white;
  border-top: 1px solid #e0e0e0;
  gap: 10px;
}

.chat-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #dee2e6;
  border-radius: 24px;
  font-size: 0.95rem;
  outline: none;
  transition: border-color 0.2s;
}

.chat-input:focus {
  border-color: #007bff;
}

.chat-input:disabled {
  background: #f8f9fa;
  cursor: not-allowed;
}

.chat-send-btn {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: #007bff;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.chat-send-btn:hover:not(:disabled) {
  background: #0056b3;
  transform: scale(1.05);
}

.chat-send-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
  opacity: 0.5;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .chat-window {
    width: calc(100vw - 20px);
    height: calc(100vh - 100px);
    max-width: 400px;
  }

  .chat-widget {
    bottom: 10px;
    right: 10px;
  }
}

/* Scrollbar styling */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #007bff;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #0056b3;
}
</style>
