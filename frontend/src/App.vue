<template>
  <div class="main-app-container">
    <LandingHeader />
    <main class="flex-grow-1">
      <router-view />
    </main>
    <LandingFooter />
    
    <!-- Chat Widget (csak ha be van jelentkezve) -->
    <ChatWidget v-if="isAuthenticated" ref="chatWidget" />
  </div>
</template>

<script>
import LandingHeader from './components/LandingHeader.vue';
import LandingFooter from './components/LandingFooter.vue';
import ChatWidget from './components/ChatWidget.vue';
import { useAuthStore } from './stores/auth';
import { computed, ref, provide } from 'vue';

export default {
  components: {
    LandingHeader,
    LandingFooter,
    ChatWidget
  },
  setup() {
    const authStore = useAuthStore();
    const isAuthenticated = computed(() => authStore.isAuthenticated);
    const chatWidget = ref(null);
    
    // Provide openChat function to all child components
    const openChat = () => {
      if (chatWidget.value) {
        chatWidget.value.openChat();
      }
    };
    
    provide('openChat', openChat);
    
    return {
      isAuthenticated,
      chatWidget
    };
  }
}
</script>

<style>
/* * Globális stílusok (az egész appra vonatkoznak)
 * Ez a rész a teljes képernyős elrendezést biztosítja, amit a Header és Footer igényel.
 */

/* Beállítja a html és body elemeket 100%-os magasságra */
html, body {
    height: 100%;
    margin: 0;
}

/* Az #app (amit a main.js mountol) és a fő konténer */
#app, .main-app-container {
    display: flex;
    flex-direction: column; /* Elrendezés oszlopokban (fentről lefelé) */
    min-height: 100vh;      /* Betölti a teljes látható magasságot */
    background-color: var(--color-background);
}

/* A main elem kitölti a rendelkezésre álló helyet a header és footer között */
main {
    flex-grow: 1; /* Ez a lényeg! A main részt nyújtja meg */
}
</style>