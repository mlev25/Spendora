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
          <span class="chat-icon">
            <img src="/ai.png" alt="AI" class="icon-img" />
          </span>
          <span>{{ $t('chat.title') }}</span>
        </div>
        <div class="chat-header-buttons">
          <button @click="resetChat" class="chat-reset-btn" :title="$t('chat.restart')">↻</button>
          <button @click="closeChat" class="chat-close-btn" :title="$t('chat.close')">×</button>
        </div>
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
import './styles/ChatWidget.css';

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
  mounted() {
    // Üzenetek betöltése sessionStorage-ból
    const savedMessages = sessionStorage.getItem('chatMessages');
    if (savedMessages) {
      try {
        this.messages = JSON.parse(savedMessages);
      } catch (e) {
        console.error('Failed to load chat messages:', e);
      }
    }
  },
  methods: {
    openChat() {
      this.isOpen = true;
    },
    closeChat() {
      this.isOpen = false;
      // Opcionális: chat törlése bezáráskor
      // this.messages = [];
      // sessionStorage.removeItem('chatMessages');
    },
    resetChat() {
      // Üzenetek törlése
      this.messages = [];
      // SessionStorage törlése
      sessionStorage.removeItem('chatMessages');
      // Scroll to top
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer;
        if (container) {
          container.scrollTop = 0;
        }
      });
    },
    saveMessages() {
      sessionStorage.setItem('chatMessages', JSON.stringify(this.messages));
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

      // Mentés sessionStorage-ba
      this.saveMessages();

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

        // Mentés sessionStorage-ba
        this.saveMessages();

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
        // Mentés sessionStorage-ba (hibaüzenettel együtt)
        this.saveMessages();
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

