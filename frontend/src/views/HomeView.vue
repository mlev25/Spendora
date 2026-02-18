<template>
  <div class="home-container">
    <div class="welcome-section">
      <h1 class="welcome-title">{{ $t('home.welcome') }}, {{ userName }}!</h1>
      <p class="welcome-subtitle">{{ $t('home.subtitle') }}</p>
    </div>

    <div class="dashboard-grid">
      <!-- Statisztikák kártya -->
      <div class="dashboard-card stats-card" @click="goToStatistics">
        <h3 class="card-title">{{ $t('home.stats.title') }}</h3>
        <p class="card-description">{{ $t('home.stats.description') }}</p>
        <div class="card-stats">
          <div class="stat-item">
            <span class="stat-value">{{ expenses.length }}</span>
            <span class="stat-label">{{ $t('home.stats.totalExpenses') }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ totalThisMonth }} Ft</span>
            <span class="stat-label">{{ $t('home.stats.thisMonth') }}</span>
          </div>
        </div>
      </div>

      <!-- Új kiadás kártya -->
      <div class="dashboard-card action-card">
        <h3 class="card-title">{{ $t('home.addExpense.title') }}</h3>
        <p class="card-description">{{ $t('home.addExpense.description') }}</p>
        <button class="action-btn" @click="openAddModal">{{ $t('home.addExpense.button') }}</button>
      </div>

      <!-- Kategóriák kártya -->
      <div class="dashboard-card categories-card">
        <h3 class="card-title">{{ $t('home.categories.title') }}</h3>
        <p class="card-description">{{ $t('home.categories.description') }}</p>
        <div class="category-list">
          <span
            v-for="category in categories"
            :key="category.id"
            class="category-badge"
            :style="{ backgroundColor: category.color }"
          >
            {{ category.name }}
          </span>
        </div>
      </div>

      <!-- Chatbot kártya -->
      <div class="dashboard-card chatbot-card">
        <h3 class="card-title">{{ $t('home.chatbot.title') }}</h3>
        <p class="card-description">{{ $t('home.chatbot.description') }}</p>
        <button @click="openChat" class="action-btn secondary">{{ $t('home.chatbot.button') }}</button>
      </div>
    </div>

    <!-- Gyors infó szekció -->
    <div class="quick-info">
      <div class="info-box">
        <h4>{{ $t('home.info.lastLogin') }}</h4>
        <p>{{ formattedLastLogin }}</p>
      </div>
      <div class="info-box">
        <h4>{{ $t('home.info.accountType') }}</h4>
        <p>{{ userRole }}</p>
      </div>
    </div>

    <!-- Expense lista -->
    <ExpenseList
      :expenses="expenses"
      :categories="categories"
      :loading="loading"
      @add="openAddModal"
      @edit="openEditModal"
      @delete="deleteExpense"
    />

    <!-- Add/Edit Modal -->
    <AddExpenseModal
      :isOpen="isModalOpen"
      :expense="selectedExpense"
      @close="closeModal"
      @success="handleExpenseSuccess"
    />
  </div>
</template>

<script>
import './styles/HomeView.css';
import { useAuthStore } from '../stores/auth.js';
import { expenseService, categoryService } from '../services/api.js';
import AddExpenseModal from '../components/AddExpenseModal.vue';
import ExpenseList from '../components/ExpenseList.vue';
import { inject } from 'vue';

export default {
  name: 'HomeView',
  components: {
    AddExpenseModal,
    ExpenseList,
  },
  setup() {
    const openChatFunc = inject('openChat');
    return {
      openChatFunc
    };
  },
  data() {
    return {
      expenses: [],
      categories: [],
      loading: false,
      isModalOpen: false,
      selectedExpense: null,
    };
  },
  computed: {
    userName() {
      const authStore = useAuthStore();
      return authStore.user?.name || authStore.user?.username || 'Felhasználó';
    },
    userRole() {
      const authStore = useAuthStore();
      const role = authStore.user?.roles?.[0] || 'USER';
      return this.$t(`roles.${role}`);
    },
    formattedLastLogin() {
      const authStore = useAuthStore();
      if (!authStore.user?.lastLoginDate) return this.$t('home.info.noData');
      
      const date = new Date(authStore.user.lastLoginDate);
      return date.toLocaleString(this.$i18n.locale);
    },
    totalThisMonth() {
      const now = new Date();
      const thisMonth = now.getMonth();
      const thisYear = now.getFullYear();

      const total = this.expenses
        .filter((expense) => {
          const expenseDate = new Date(expense.date);
          return (
            expenseDate.getMonth() === thisMonth &&
            expenseDate.getFullYear() === thisYear
          );
        })
        .reduce((sum, expense) => sum + parseFloat(expense.price), 0);

      return new Intl.NumberFormat('hu-HU').format(total);
    },
  },
  async mounted() {
    await this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const [expensesData, categoriesData] = await Promise.all([
          expenseService.getAll(),
          categoryService.getAll(),
        ]);
        this.expenses = expensesData;
        this.categories = categoriesData;
      } catch (error) {
        console.error('Failed to load data:', error);
      } finally {
        this.loading = false;
      }
    },
    openAddModal() {
      this.selectedExpense = null;
      this.isModalOpen = true;
    },
    openEditModal(expense) {
      this.selectedExpense = expense;
      this.isModalOpen = true;
    },
    closeModal() {
      this.isModalOpen = false;
      this.selectedExpense = null;
    },
    async handleExpenseSuccess() {
      await this.loadData();
    },
    async deleteExpense(expenseId) {
      try {
        await expenseService.delete(expenseId);
        await this.loadData();
      } catch (error) {
        console.error('Failed to delete expense:', error);
        alert(this.$t('expense.deleteError'));
      }
    },
    goToStatistics() {
      this.$router.push('/statistics');
    },
    openChat() {
      // Chat widget megnyitása az inject-elt függvénnyel
      if (this.openChatFunc) {
        this.openChatFunc();
      }
    },
  },
};
</script>

.welcome-title {
  font-size: clamp(1.75rem, 4vw, 2.5rem);

