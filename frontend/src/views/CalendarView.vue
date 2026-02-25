<template>
  <div class="calendar-container">
    <!-- Month Navigation -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="month-selector">
          <button @click="prevMonth" class="nav-btn">
            ↺
          </button>
          <h2 class="current-month">
            {{ $t(`months.${currentMonth}`) }} {{ currentYear }}
          </h2>
          <button @click="nextMonth" class="nav-btn">
            ↻
          </button>
        </div>
      </div>
    </div>

    <!-- Calendar Grid -->
    <div v-if="!loading" class="calendar-card card">
      <div class="card-body">
        <!-- Weekday Headers -->
        <div class="calendar-grid">
          <div v-for="day in weekDays" :key="day" class="weekday-header">
            {{ $t(`calendar.days.${day}`) }}
          </div>

          <!-- Calendar Days -->
          <div
            v-for="day in calendarDays"
            :key="day.dateString"
            :class="[
              'calendar-day',
              {
                'other-month': !day.isCurrentMonth,
                'today': day.isToday,
                'has-expenses': day.hasExpenses,
                'clickable': day.hasExpenses
              }
            ]"
            @click="day.hasExpenses && showDayExpenses(day)"
          >
            <div class="day-number">{{ day.dayNumber }}</div>
            
            <div v-if="day.hasExpenses" class="day-content">
              <div class="day-total">{{ formatCurrency(day.totalAmount) }}</div>
              <div class="expense-dots">
                <span
                  v-for="(expense, idx) in day.expenses.slice(0, 3)"
                  :key="idx"
                  class="expense-dot"
                  :style="{ backgroundColor: expense.categoryColor }"
                  :title="getCategoryTranslation(expense.categoryName)"
                ></span>
                <span v-if="day.expenses.length > 3" class="more-indicator">
                  +{{ day.expenses.length - 3 }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-else class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">{{ $t('common.loading') }}</span>
      </div>
    </div>

    <!-- Day Expenses Modal -->
    <div v-if="selectedDay" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ selectedDay.dateString }} - {{ $t('calendar.expenses') }}</h3>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="day-summary mb-3">
            <strong>{{ $t('calendar.totalForDay') }}:</strong>
            {{ formatCurrency(selectedDay.totalAmount) }}
            <span class="text-muted">({{ selectedDay.expenses.length }} {{ $t('statistics.items') }})</span>
          </div>
          
          <div class="expense-list">
            <div
              v-for="expense in selectedDay.expenses"
              :key="expense.id"
              class="expense-item"
            >
              <div
                class="category-indicator"
                :style="{ backgroundColor: expense.categoryColor }"
              ></div>
              <div class="expense-info">
                <div class="expense-name">{{ expense.name }}</div>
                <div class="expense-category text-muted">
                  {{ getCategoryTranslation(expense.categoryName) }}
                </div>
                <div v-if="expense.description" class="expense-description text-muted">
                  {{ expense.description }}
                </div>
              </div>
              <div class="expense-amount">
                {{ formatCurrency(expense.price) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import './styles/CalendarView.css';
import axios from 'axios';

export default {
  name: 'CalendarView',
  data() {
    const now = new Date();
    return {
      currentMonth: now.getMonth() + 1,
      currentYear: now.getFullYear(),
      calendarData: [],
      loading: false,
      selectedDay: null,
      weekDays: ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'],
    };
  },
  computed: {
    calendarDays() {
      const year = this.currentYear;
      const month = this.currentMonth;
      
      // First day of month
      const firstDay = new Date(year, month - 1, 1);
      // Last day of month
      const lastDay = new Date(year, month, 0);
      
      // Get day of week (0 = Sunday, 1 = Monday, etc.)
      let firstDayOfWeek = firstDay.getDay();
      // Convert Sunday (0) to 7 for Monday-start week
      firstDayOfWeek = firstDayOfWeek === 0 ? 7 : firstDayOfWeek;
      
      const daysInMonth = lastDay.getDate();
      const days = [];
      
      // Previous month days to fill first week
      const prevMonthDays = firstDayOfWeek - 1;
      const prevMonthLastDay = new Date(year, month - 1, 0).getDate();
      for (let i = prevMonthDays; i > 0; i--) {
        days.push(this.createDayObject(
          prevMonthLastDay - i + 1,
          month === 1 ? 12 : month - 1,
          month === 1 ? year - 1 : year,
          false
        ));
      }
      
      // Current month days
      for (let i = 1; i <= daysInMonth; i++) {
        days.push(this.createDayObject(i, month, year, true));
      }
      
      // Next month days to fill last week
      const remainingDays = 42 - days.length; // 6 weeks * 7 days
      for (let i = 1; i <= remainingDays; i++) {
        days.push(this.createDayObject(
          i,
          month === 12 ? 1 : month + 1,
          month === 12 ? year + 1 : year,
          false
        ));
      }
      
      return days;
    },
  },
  mounted() {
    this.loadCalendarData();
  },
  watch: {
    '$i18n.locale'() {
      // Refresh when language changes
      this.loadCalendarData();
    },
  },
  methods: {
    createDayObject(day, month, year, isCurrentMonth) {
      const date = new Date(year, month - 1, day);
      const dateString = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      
      // Find expenses for this day
      const dayData = this.calendarData.find(d => d.date === dateString);
      
      const today = new Date();
      const isToday = 
        date.getDate() === today.getDate() &&
        date.getMonth() === today.getMonth() &&
        date.getFullYear() === today.getFullYear();
      
      return {
        dayNumber: day,
        month,
        year,
        dateString,
        isCurrentMonth,
        isToday,
        hasExpenses: !!dayData,
        totalAmount: dayData?.totalAmount || 0,
        expenses: dayData?.expenses || [],
      };
    },
    async loadCalendarData() {
      this.loading = true;
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(
          `http://localhost:8080/api/expenses/calendar`,
          {
            params: {
              year: this.currentYear,
              month: this.currentMonth,
            },
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        this.calendarData = response.data;
      } catch (error) {
        console.error('Failed to load calendar data:', error);
      } finally {
        this.loading = false;
      }
    },
    prevMonth() {
      if (this.currentMonth === 1) {
        this.currentMonth = 12;
        this.currentYear--;
      } else {
        this.currentMonth--;
      }
      this.loadCalendarData();
    },
    nextMonth() {
      if (this.currentMonth === 12) {
        this.currentMonth = 1;
        this.currentYear++;
      } else {
        this.currentMonth++;
      }
      this.loadCalendarData();
    },
    showDayExpenses(day) {
      this.selectedDay = day;
    },
    closeModal() {
      this.selectedDay = null;
    },
    formatCurrency(value) {
      if (!value) return '0 Ft';
      return new Intl.NumberFormat('hu-HU', {
        style: 'decimal',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(value) + ' Ft';
    },
    getCategoryTranslation(categoryName) {
      if (!categoryName) return '';
      const translationKey = `categories.${categoryName}`;
      const translated = this.$t(translationKey);
      return translated !== translationKey ? translated : categoryName;
    },
  },
};
</script>
