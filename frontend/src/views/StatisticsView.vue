<template>
  <div class="statistics-container">
    <h1 class="mb-4">{{ $t('statistics.title') }}</h1>

    <!-- Time Period Filters -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3 align-items-end">
          <div class="col-md-3">
            <label class="form-label">{{ $t('statistics.period') }}</label>
            <select v-model="selectedPeriod" class="form-select" @change="onPeriodChange">
              <option value="month">{{ $t('statistics.thisMonth') }}</option>
              <option value="year">{{ $t('statistics.thisYear') }}</option>
            </select>
          </div>
          
          <div v-if="selectedPeriod === 'month'" class="col-md-3">
            <label class="form-label">{{ $t('statistics.selectMonth') }}</label>
            <input type="month" v-model="selectedMonth" class="form-control" @change="loadData" />
          </div>

          <div v-if="selectedPeriod === 'year'" class="col-md-3">
            <label class="form-label">{{ $t('statistics.selectYear') }}</label>
            <input type="number" v-model="selectedYear" class="form-control" 
                   :min="2020" :max="new Date().getFullYear() + 1" @change="loadData" />
          </div>

          <div class="col-md-3">
            <button @click="loadData" class="btn btn-primary w-100" :disabled="loading">
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              {{ $t('statistics.refresh') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- KPI Summary Cards -->
    <div v-if="summary" class="kpi-section mb-4">
      <!-- Mobile Carousel -->
      <div class="kpi-carousel-mobile">
        <button @click="prevKpi" class="carousel-nav-btn prev-btn" :disabled="currentKpiIndex === 0">
          <span>‹</span>
        </button>
        
        <div class="kpi-carousel-wrapper">
          <div class="kpi-carousel-track" :style="{ transform: `translateX(-${currentKpiIndex * 100}%)` }">
            <div class="kpi-carousel-item">
              <div class="card kpi-card clickable" @click="showExpenses">
                <div class="card-body">
                  <h6 class="text-muted">{{ periodLabel }}</h6>
                  <h3 class="mb-0">{{ formatCurrency(summary.totalExpenses) }}</h3>
                  <small class="text-muted">{{ summary.expenseCount }} {{ $t('statistics.items') }}</small>
                </div>
              </div>
            </div>

            <div class="kpi-carousel-item">
              <div class="card kpi-card">
                <div class="card-body">
                  <h6 class="text-muted">{{ $t('statistics.averageExpense') }}</h6>
                  <h3 class="mb-0">{{ formatCurrency(summary.averageExpense) }}</h3>
                </div>
              </div>
            </div>

            <div class="kpi-carousel-item">
              <div class="card kpi-card">
                <div class="card-body">
                  <h6 class="text-muted">{{ $t('statistics.highest') }}</h6>
                  <h3 class="mb-0">{{ formatCurrency(summary.highestExpense) }}</h3>
                </div>
              </div>
            </div>

            <div class="kpi-carousel-item">
              <div class="card kpi-card">
                <div class="card-body">
                  <h6 class="text-muted">{{ $t('statistics.lowest') }}</h6>
                  <h3 class="mb-0">{{ formatCurrency(summary.lowestExpense) }}</h3>
                </div>
              </div>
            </div>
          </div>
        </div>

        <button @click="nextKpi" class="carousel-nav-btn next-btn" :disabled="currentKpiIndex === 3">
          <span>›</span>
        </button>
      </div>

      <!-- Desktop Grid -->
      <div class="kpi-grid-desktop row g-3">
        <div class="col-md-3">
          <div class="card kpi-card clickable" @click="showExpenses">
            <div class="card-body">
              <h6 class="text-muted">{{ periodLabel }}</h6>
              <h3 class="mb-0">{{ formatCurrency(summary.totalExpenses) }}</h3>
              <small class="text-muted">{{ summary.expenseCount }} {{ $t('statistics.items') }}</small>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="card kpi-card">
            <div class="card-body">
              <h6 class="text-muted">{{ $t('statistics.averageExpense') }}</h6>
              <h3 class="mb-0">{{ formatCurrency(summary.averageExpense) }}</h3>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="card kpi-card">
            <div class="card-body">
              <h6 class="text-muted">{{ $t('statistics.highest') }}</h6>
              <h3 class="mb-0">{{ formatCurrency(summary.highestExpense) }}</h3>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="card kpi-card">
            <div class="card-body">
              <h6 class="text-muted">{{ $t('statistics.lowest') }}</h6>
              <h3 class="mb-0">{{ formatCurrency(summary.lowestExpense) }}</h3>
            </div>
          </div>
        </div>
      </div>

      <!-- Carousel Indicators -->
      <div class="carousel-indicators-mobile">
        <span 
          v-for="i in 4" 
          :key="i" 
          class="indicator-dot"
          :class="{ active: currentKpiIndex === i - 1 }"
          @click="currentKpiIndex = i - 1"
        ></span>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="row g-4">
      <!-- Category Pie Chart -->
      <div class="col-lg-6">
        <div class="card chart-card">
          <div class="card-body">
            <h5 class="card-title mb-3">{{ $t('statistics.categoryBreakdown') }}</h5>
            <div v-if="categoryData && categoryData.labels.length > 0" class="chart-wrapper">
              <Pie :data="categoryData" :options="pieOptions" />
            </div>
            <div v-else class="text-center text-muted py-5">
              {{ $t('statistics.noData') }}
            </div>
          </div>
        </div>
      </div>

      <!-- Monthly/Weekly Bar Chart -->
      <div class="col-lg-6">
        <div class="card chart-card">
          <div class="card-body">
            <h5 class="card-title mb-3">{{ barChartTitle }}</h5>
            <div v-if="monthlyData && monthlyData.labels.length > 0" class="chart-wrapper">
              <Bar :data="monthlyData" :options="barOptions" />
            </div>
            <div v-else class="text-center text-muted py-5">
              {{ $t('statistics.noData') }}
            </div>
          </div>
        </div>
      </div>

      <!-- Trend/Daily Line Chart -->
      <div class="col-lg-6">
        <div class="card chart-card">
          <div class="card-body">
            <h5 class="card-title mb-3">{{ lineChartTitle }}</h5>
            <div v-if="trendData && trendData.labels.length > 0" class="chart-wrapper">
              <Line :data="trendData" :options="lineOptions" />
            </div>
            <div v-else class="text-center text-muted py-5">
              {{ $t('statistics.noData') }}
            </div>
          </div>
        </div>
      </div>

      <!-- Top Categories List -->
      <div class="col-lg-6">
        <div class="card chart-card">
          <div class="card-body">
            <h5 class="card-title mb-3">{{ $t('statistics.topCategories') }}</h5>
            <div v-if="topCategories && topCategories.length > 0" class="top-categories-list">
              <div v-for="(cat, index) in topCategories" :key="cat.categoryId" class="category-item">
                <div class="category-rank">{{ index + 1 }}</div>
                <div class="category-color" :style="{ backgroundColor: cat.categoryColor }"></div>
                <div class="category-info">
                  <div class="category-name">{{ getCategoryTranslation(cat.categoryName) }}</div>
                  <div class="category-details">
                    <span>{{ cat.expenseCount }} {{ $t('statistics.items') }}</span>
                  </div>
                </div>
                <div class="category-amount">
                  <div class="amount-value">{{ formatCurrency(cat.totalAmount) }}</div>
                  <div class="amount-percentage">{{ cat.percentage.toFixed(1) }}%</div>
                </div>
              </div>
            </div>
            <div v-else class="text-center text-muted py-5">
              {{ $t('statistics.noData') }}
            </div>
          </div>
        </div>
      </div>

      <!-- Combined Stats Card -->
      <div v-if="summary" class="col-lg-6">
        <div class="card chart-card">
          <div class="card-body">
            <!-- Extremes Section -->
            <h5 class="card-title mb-3">{{ $t('statistics.extremes') }}</h5>
            <div class="extreme-stats-compact">
              <div class="extreme-item-compact">
                <div class="extreme-label-compact">{{ $t('statistics.highest') }}</div>
                <div class="extreme-value-compact highest">{{ formatCurrency(summary.highestExpense) }}</div>
              </div>
              <div class="extreme-item-compact">
                <div class="extreme-label-compact">{{ $t('statistics.lowest') }}</div>
                <div class="extreme-value-compact lowest">{{ formatCurrency(summary.lowestExpense) }}</div>
              </div>
            </div>

            <hr class="my-3">

            <!-- Monthly Comparison Section -->
            <h5 class="card-title mb-3">{{ comparisonTitle }}</h5>
            <div class="comparison-stats-compact">
              <div class="comparison-row">
                <div class="comparison-item-compact">
                  <div class="comparison-period-compact">{{ currentPeriodLabel }}</div>
                  <div class="comparison-value-compact current">{{ formatCurrency(summary.thisMonthTotal) }}</div>
                </div>
                <div class="comparison-arrow-compact">→</div>
                <div class="comparison-item-compact">
                  <div class="comparison-period-compact">{{ previousPeriodLabel }}</div>
                  <div class="comparison-value-compact">{{ formatCurrency(summary.lastMonthTotal) }}</div>
                </div>
              </div>
              <div class="comparison-diff-compact" :class="monthDiffClass">
                {{ monthDiffText }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Placeholder for Future Visualization -->
      <div class="col-lg-6">
        <div class="card chart-card">
          <div class="card-body">
            <h5 class="card-title mb-3">{{ $t('statistics.spendingPatterns') }}</h5>
            <div v-if="spendingPatterns" class="patterns-content">
              <div class="pattern-item">
                <div class="pattern-header">
                  
                  <span class="pattern-label">{{ spendingPatterns.avgLabel }}</span>
                </div>
                <div class="pattern-value">{{ formatCurrency(spendingPatterns.avgValue) }}</div>
                <div class="pattern-progress">
                  <div class="progress">
                    <div class="progress-bar bg-primary" :style="{ width: '100%' }"></div>
                  </div>
                </div>
              </div>

              <div class="pattern-item">
                <div class="pattern-header">
                  
                  <span class="pattern-label">{{ $t('statistics.transactionFreq') }}</span>
                </div>
                <div class="pattern-value">{{ spendingPatterns.frequency }}</div>
                <div class="pattern-subtext">{{ spendingPatterns.freqLabel }}</div>
              </div>

              <div class="pattern-item">
                <div class="pattern-header">
                  
                  <span class="pattern-label">{{ $t('statistics.highestVsAvg') }}</span>
                </div>
                <div class="pattern-value">{{ spendingPatterns.ratio }}x</div>
                <div class="pattern-subtext">{{ spendingPatterns.ratioLabel }}</div>
                <div class="pattern-progress">
                  <div class="progress">
                    <div class="progress-bar bg-danger" :style="{ width: Math.min(spendingPatterns.ratio * 10, 100) + '%' }"></div>
                  </div>
                </div>
              </div>

              <div class="pattern-item">
                <div class="pattern-header">
                  
                  <span class="pattern-label">{{ $t('statistics.totalExpenses') }}</span>
                </div>
                <div class="pattern-value">{{ formatCurrency(spendingPatterns.total) }}</div>
                <div class="pattern-subtext">{{ spendingPatterns.countLabel }}</div>
              </div>
            </div>
            <div v-else class="text-center text-muted py-5">
              {{ $t('statistics.noData') }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Expenses Modal -->
    <div v-if="showExpensesModal" class="modal-overlay" @click.self="closeExpensesModal">
      <div class="modal-content-large">
        <div class="modal-header">
          <h3>{{ periodLabel }} - {{ $t('statistics.expensesList') }}</h3>
          <button class="close-btn" @click="closeExpensesModal">×</button>
        </div>
        <div class="modal-body">
          <div class="expenses-wrapper">
            <ExpenseList
              :expenses="periodExpenses"
              :categories="categories"
              :loading="loadingExpenses"
              :activeFilter="'all'"
              :hide-filters="true"
              :hide-title="true"
              :read-only="true"
              @edit="handleEdit"
              @delete="handleDelete"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import './styles/StatisticsView.css';
import { Pie, Bar, Line } from 'vue-chartjs';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Filler } from 'chart.js';
import { statisticsService, expenseService, categoryService } from '@/services/api';
import ExpenseList from '@/components/ExpenseList.vue';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Filler);

export default {
  name: 'StatisticsView',
  components: {
    Pie,
    Bar,
    Line,
    ExpenseList,
  },
  data() {
    return {
      currentKpiIndex: 0,
      loading: false,
      selectedPeriod: 'month',
      selectedMonth: '',
      selectedYear: new Date().getFullYear(),
      summary: null,
      categoryData: null,
      monthlyData: null,
      trendData: null,
      rawCategories: null,
      showExpensesModal: false,
      periodExpenses: [],
      categories: [],
      loadingExpenses: false,
      pieOptions: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            labels: {
              padding: 12,
              font: {
                size: 11,
                family: "'Inter', 'Segoe UI', sans-serif",
              },
              usePointStyle: true,
              pointStyle: 'circle',
              boxWidth: 8,
              boxHeight: 8,
            },
          },
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            padding: 12,
            titleFont: {
              size: 14,
              weight: 'bold',
            },
            bodyFont: {
              size: 13,
            },
            borderColor: 'rgba(255, 255, 255, 0.1)',
            borderWidth: 1,
            callbacks: {
              label: (context) => {
                const label = context.label || '';
                const value = context.parsed || 0;
                const percentage = context.dataset.percentages[context.dataIndex];
                return `${label}: ${this.formatCurrency(value)} (${percentage}%)`;
              },
            },
          },
        },
        layout: {
          padding: 10,
        },
      },
      barOptions: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false,
          },
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            padding: 12,
            titleFont: {
              size: 14,
              weight: 'bold',
            },
            bodyFont: {
              size: 13,
            },
            borderColor: 'rgba(255, 255, 255, 0.1)',
            borderWidth: 1,
            callbacks: {
              label: (context) => this.formatCurrency(context.parsed.y),
            },
          },
        },
        scales: {
          x: {
            grid: {
              display: false,
            },
            ticks: {
              font: {
                size: window.innerWidth <= 768 ? 10 : 11,
                family: "'Inter', 'Segoe UI', sans-serif",
              },
              maxRotation: 0,
              minRotation: 0,
              autoSkip: true,
              maxTicksLimit: window.innerWidth <= 768 ? 6 : 12,
            },
          },
          y: {
            beginAtZero: true,
            grid: {
              color: 'rgba(0, 0, 0, 0.05)',
              drawBorder: false,
            },
            ticks: {
              font: {
                size: window.innerWidth <= 768 ? 10 : 11,
                family: "'Inter', 'Segoe UI', sans-serif",
              },
              callback: (value) => {
                if (window.innerWidth <= 768) {
                  return value >= 1000 ? (value / 1000).toFixed(0) + 'K' : value;
                }
                return this.formatCurrency(value);
              },
            },
          },
        },
        layout: {
          padding: {
            top: 10,
            bottom: 5,
            left: 5,
            right: 5,
          },
        },
        borderRadius: 8,
        borderSkipped: false,
      },
      lineOptions: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false,
          },
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            padding: 12,
            titleFont: {
              size: 14,
              weight: 'bold',
            },
            bodyFont: {
              size: 13,
            },
            borderColor: 'rgba(255, 255, 255, 0.1)',
            borderWidth: 1,
            callbacks: {
              label: (context) => this.formatCurrency(context.parsed.y),
            },
          },
        },
        scales: {
          x: {
            grid: {
              display: false,
            },
            ticks: {
              font: {
                size: window.innerWidth <= 768 ? 10 : 11,
                family: "'Inter', 'Segoe UI', sans-serif",
              },
              maxRotation: 0,
              minRotation: 0,
              autoSkip: true,
              maxTicksLimit: window.innerWidth <= 768 ? 8 : 15,
            },
          },
          y: {
            beginAtZero: true,
            grid: {
              color: 'rgba(0, 0, 0, 0.05)',
              drawBorder: false,
            },
            ticks: {
              font: {
                size: window.innerWidth <= 768 ? 10 : 11,
                family: "'Inter', 'Segoe UI', sans-serif",
              },
              callback: (value) => {
                if (window.innerWidth <= 768) {
                  return value >= 1000 ? (value / 1000).toFixed(0) + 'K' : value;
                }
                return this.formatCurrency(value);
              },
            },
          },
        },
        layout: {
          padding: {
            top: 10,
            bottom: 5,
            left: 5,
            right: 5,
          },
        },
        elements: {
          point: {
            radius: window.innerWidth <= 768 ? 2 : 4,
            hoverRadius: window.innerWidth <= 768 ? 4 : 6,
            backgroundColor: 'white',
            borderWidth: 2,
          },
          line: {
            borderWidth: window.innerWidth <= 768 ? 2 : 3,
          },
        },
      },
    };
  },
  computed: {
    periodLabel() {
      return this.selectedPeriod === 'month' 
        ? this.$t('statistics.thisMonth') 
        : this.$t('statistics.thisYear');
    },
    comparisonTitle() {
      return this.selectedPeriod === 'month'
        ? this.$t('statistics.monthComparison')
        : this.$t('statistics.yearComparison');
    },
    currentPeriodLabel() {
      if (this.selectedPeriod === 'month') {
        const [year, month] = this.selectedMonth.split('-');
        return `${year}.${month}.`;
      }
      return this.selectedYear.toString();
    },
    previousPeriodLabel() {
      if (this.selectedPeriod === 'month') {
        const [year, month] = this.selectedMonth.split('-');
        const prevMonth = parseInt(month) - 1;
        const prevYear = prevMonth === 0 ? parseInt(year) - 1 : parseInt(year);
        const displayMonth = prevMonth === 0 ? 12 : prevMonth;
        return `${prevYear}.${String(displayMonth).padStart(2, '0')}.`;
      }
      return (this.selectedYear - 1).toString();
    },
    topCategories() {
      if (!this.rawCategories || this.rawCategories.length === 0) return [];
      return this.rawCategories
        .sort((a, b) => b.totalAmount - a.totalAmount)
        .slice(0, 3);
    },
    barChartTitle() {
      if (this.selectedPeriod === 'month') {
        return this.$t('statistics.weeklySpending');
      }
      return this.$t('statistics.monthlySpending');
    },
    lineChartTitle() {
      if (this.selectedPeriod === 'month') {
        return this.$t('statistics.dailySpending');
      }
      return this.$t('statistics.spendingTrend');
    },
    monthDiffClass() {
      if (!this.summary) return '';
      const diff = this.summary.thisMonthTotal - this.summary.lastMonthTotal;
      return diff > 0 ? 'increase' : diff < 0 ? 'decrease' : 'neutral';
    },
    monthDiffText() {
      if (!this.summary) return '';
      const diff = this.summary.thisMonthTotal - this.summary.lastMonthTotal;
      const percentage = this.summary.lastMonthTotal ? ((diff / this.summary.lastMonthTotal) * 100).toFixed(1) : 0;
      if (diff > 0) {
        return `+${this.formatCurrency(diff)} (${percentage}%)`;
      } else if (diff < 0) {
        return `${this.formatCurrency(diff)} (${percentage}%)`;
      }
      return this.$t('statistics.noChange');
    },
    spendingPatterns() {
      if (!this.summary) return null;

      const { startDate, endDate } = this.getDateRange();
      const daysDiff = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24)) + 1;

      let avgLabel, avgValue, frequency, freqLabel, ratioLabel;

      if (this.selectedPeriod === 'year') {
        // Éves nézet: havi átlag (teljes évre vetítve)
        avgValue = this.summary.totalExpenses / 12;
        avgLabel = this.$t('statistics.avgPerMonth');
        frequency = (this.summary.expenseCount / 12).toFixed(1);
        freqLabel = this.$t('statistics.transactionsPerMonth');
        ratioLabel = this.$t('statistics.timesMonthlyAvg');
      } else {
        // Havi nézet: napi átlag (csak a kiválasztott hónapra)
        avgValue = this.summary.totalExpenses / daysDiff;
        avgLabel = this.$t('statistics.avgPerDay');
        frequency = (this.summary.expenseCount / daysDiff).toFixed(1);
        freqLabel = this.$t('statistics.transactionsPerDay');
        ratioLabel = this.$t('statistics.timesDailyAvg');
      }

      const ratioVsAvg = avgValue > 0 
        ? (this.summary.highestExpense / avgValue).toFixed(1)
        : 0;

      return {
        avgLabel,
        avgValue,
        frequency,
        freqLabel,
        ratio: ratioVsAvg,
        ratioLabel,
        total: this.summary.totalExpenses, // ez most a kiválasztott időszak összege
        countLabel: `${this.summary.expenseCount} ${this.$t('statistics.items')}`,
      };
    },
  },
  watch: {
    '$i18n.locale'() {
      // Amikor változik a nyelv, újratöltjük az adatokat hogy a labelek frissüljenek
      if (this.categoryData || this.monthlyData || this.trendData) {
        this.loadData();
      }
    },
  },
  mounted() {
    this.initializeFilters();
    this.loadData();
  },
  methods: {
    getCategoryTranslation(categoryName) {
      const translationKey = `categories.${categoryName}`;
      const translated = this.$t(translationKey);
      return translated !== translationKey ? translated : categoryName;
    },
    prevKpi() {
      if (this.currentKpiIndex > 0) {
        this.currentKpiIndex--;
      }
    },
    nextKpi() {
      if (this.currentKpiIndex < 3) {
        this.currentKpiIndex++;
      }
    },
    initializeFilters() {
      const now = new Date();
      this.selectedMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
      this.selectedYear = now.getFullYear();
    },
    
    onPeriodChange() {
      this.loadData();
    },

    async loadData() {
      this.loading = true;
      try {
        await Promise.all([
          this.loadSummary(),
          this.loadCategoryData(),
          this.loadMonthlyData(),
          this.loadTrendData(),
        ]);
      } catch (error) {
        console.error('Error loading statistics:', error);
        this.$toast?.error(this.$t('statistics.loadError'));
      } finally {
        this.loading = false;
      }
    },

    async loadSummary() {
      try {
        const { startDate, endDate } = this.getDateRange();
        const expenses = await this.getExpensesInRange(startDate, endDate);
        
        // Számoljuk az előző időszakot (hónap vagy év)
        let previousPeriodExpenses = [];
        if (this.selectedPeriod === 'month') {
          const [year, month] = this.selectedMonth.split('-');
          const prevMonthDate = new Date(year, month - 2, 1); // előző hónap első napja
          const prevMonthEnd = new Date(year, month - 1, 0); // előző hónap utolsó napja
          previousPeriodExpenses = await this.getExpensesInRange(prevMonthDate, prevMonthEnd);
        } else if (this.selectedPeriod === 'year') {
          // Előző év
          const prevYearStart = new Date(this.selectedYear - 1, 0, 1);
          const prevYearEnd = new Date(this.selectedYear - 1, 11, 31);
          previousPeriodExpenses = await this.getExpensesInRange(prevYearStart, prevYearEnd);
        }
        
        // Számítások a kiválasztott időszakra
        const totalExpenses = expenses.reduce((sum, exp) => sum + parseFloat(exp.price), 0);
        const expenseCount = expenses.length;
        const averageExpense = expenseCount > 0 ? totalExpenses / expenseCount : 0;
        
        const prices = expenses.map(exp => parseFloat(exp.price));
        const highestExpense = prices.length > 0 ? Math.max(...prices) : 0;
        const lowestExpense = prices.length > 0 ? Math.min(...prices) : 0;
        
        // Előző időszak összege
        const lastPeriodTotal = previousPeriodExpenses.reduce((sum, exp) => sum + parseFloat(exp.price), 0);
        
        this.summary = {
          totalExpenses: totalExpenses,
          expenseCount: expenseCount,
          averageExpense: averageExpense,
          highestExpense: highestExpense,
          lowestExpense: lowestExpense,
          thisMonthTotal: totalExpenses, // ez most a kiválasztott időszak összege
          lastMonthTotal: lastPeriodTotal,
          thisYearTotal: this.selectedPeriod === 'year' ? totalExpenses : 0,
        };
      } catch (error) {
        console.error('Error loading summary:', error);
      }
    },

    async loadCategoryData() {
      try {
        const { startDate, endDate } = this.getDateRange();
        const categories = await statisticsService.getCategorySpending(startDate, endDate);

        if (categories && categories.length > 0) {
          this.rawCategories = categories;
          this.categoryData = {
            labels: categories.map(c => this.getCategoryTranslation(c.categoryName)),
            datasets: [{
              data: categories.map(c => c.totalAmount),
              percentages: categories.map(c => c.percentage.toFixed(1)),
              backgroundColor: categories.map(c => c.categoryColor || this.getRandomColor()),
            }],
          };
        } else {
          this.rawCategories = null;
          this.categoryData = null;
        }
      } catch (error) {
        console.error('Error loading category data:', error);
        this.rawCategories = null;
        this.categoryData = null;
      }
    },

    async loadMonthlyData() {
      try {
        if (this.selectedPeriod === 'year') {
          // Éves nézet: 12 hónap
          const data = await statisticsService.getYearly(this.selectedYear);
          
          const monthNames = [
            this.$t('months.jan'), this.$t('months.feb'), this.$t('months.mar'),
            this.$t('months.apr'), this.$t('months.may'), this.$t('months.jun'),
            this.$t('months.jul'), this.$t('months.aug'), this.$t('months.sep'),
            this.$t('months.oct'), this.$t('months.nov'), this.$t('months.dec'),
          ];

          const monthlyValues = [];
          for (let i = 1; i <= 12; i++) {
            monthlyValues.push(data.monthlyBreakdown[i] || 0);
          }

          this.monthlyData = {
            labels: monthNames,
            datasets: [{
              label: this.$t('statistics.spending'),
              data: monthlyValues,
              backgroundColor: this.createBarGradients(monthlyValues),
              borderColor: 'rgba(54, 162, 235, 1)',
              borderWidth: 0,
              hoverBackgroundColor: 'rgba(54, 162, 235, 0.9)',
            }],
          };
        } else if (this.selectedPeriod === 'month') {
          // Havi nézet: heti bontás
          const { startDate, endDate } = this.getDateRange();
          const expenses = await this.getExpensesInRange(startDate, endDate);
          
          // Hétek számítása
          const weeklyData = this.aggregateByWeek(expenses, startDate, endDate);
          
          this.monthlyData = {
            labels: weeklyData.labels,
            datasets: [{
              label: this.$t('statistics.spending'),
              data: weeklyData.values,
              backgroundColor: this.createBarGradients(weeklyData.values),
              borderColor: 'rgba(75, 192, 192, 1)',
              borderWidth: 0,
              hoverBackgroundColor: 'rgba(75, 192, 192, 0.9)',
            }],
          };
        } else {
          this.monthlyData = null;
        }
      } catch (error) {
        console.error('Error loading monthly data:', error);
        this.monthlyData = null;
      }
    },

    async loadTrendData() {
      try {
        if (this.selectedPeriod === 'year') {
          // Éves nézet: havi trend
          const data = await statisticsService.getYearly(this.selectedYear);
          
          const monthNames = [
            this.$t('months.jan'), this.$t('months.feb'), this.$t('months.mar'),
            this.$t('months.apr'), this.$t('months.may'), this.$t('months.jun'),
            this.$t('months.jul'), this.$t('months.aug'), this.$t('months.sep'),
            this.$t('months.oct'), this.$t('months.nov'), this.$t('months.dec'),
          ];

          const monthlyValues = [];
          for (let i = 1; i <= 12; i++) {
            monthlyValues.push(data.monthlyBreakdown[i] || 0);
          }

          this.trendData = {
            labels: monthNames,
            datasets: [{
              label: this.$t('statistics.trend'),
              data: monthlyValues,
              borderColor: 'rgba(75, 192, 192, 1)',
              backgroundColor: 'rgba(75, 192, 192, 0.15)',
              tension: 0.4,
              fill: true,
              pointBackgroundColor: 'white',
              pointBorderColor: 'rgba(75, 192, 192, 1)',
              pointBorderWidth: 2,
              pointRadius: 4,
              pointHoverRadius: 6,
            }],
          };
        } else if (this.selectedPeriod === 'month') {
          // Havi nézet: napi bontás
          const { startDate, endDate } = this.getDateRange();
          const expenses = await this.getExpensesInRange(startDate, endDate);
          
          const dailyData = this.aggregateByDay(expenses, startDate, endDate);
          
          this.trendData = {
            labels: dailyData.labels,
            datasets: [{
              label: this.$t('statistics.spending'),
              data: dailyData.values,
              borderColor: 'rgba(255, 99, 132, 1)',
              backgroundColor: 'rgba(255, 99, 132, 0.15)',
              tension: 0.4,
              fill: true,
              pointBackgroundColor: 'white',
              pointBorderColor: 'rgba(255, 99, 132, 1)',
              pointBorderWidth: 2,
              pointRadius: 3,
              pointHoverRadius: 5,
            }],
          };
        } else {
          this.trendData = null;
        }
      } catch (error) {
        console.error('Error loading trend data:', error);
        this.trendData = null;
      }
    },

    getDateRange() {
      if (this.selectedPeriod === 'month') {
        const [year, month] = this.selectedMonth.split('-');
        const startDate = new Date(year, month - 1, 1);
        const endDate = new Date(year, month, 0);
        return { startDate, endDate };
      } else {
        const startDate = new Date(this.selectedYear, 0, 1);
        const endDate = new Date(this.selectedYear, 11, 31);
        return { startDate, endDate };
      }
    },

    async getExpensesInRange(startDate, endDate) {
      try {
        const allExpenses = await expenseService.getAll();
        
        return allExpenses.filter(expense => {
          const expenseDate = new Date(expense.date);
          return expenseDate >= startDate && expenseDate <= endDate;
        });
      } catch (error) {
        console.error('Error fetching expenses:', error);
        return [];
      }
    },

    aggregateByWeek(expenses, startDate, endDate) {
      const weekLabels = [];
      const weekValues = [];
      
      const start = new Date(startDate);
      const end = new Date(endDate);
      
      let weekNumber = 1;
      let currentWeekStart = new Date(start);
      
      while (currentWeekStart <= end) {
        const currentWeekEnd = new Date(currentWeekStart);
        currentWeekEnd.setDate(currentWeekEnd.getDate() + 6);
        
        if (currentWeekEnd > end) {
          currentWeekEnd.setTime(end.getTime());
        }
        
        const weekTotal = expenses
          .filter(exp => {
            const expDate = new Date(exp.date);
            return expDate >= currentWeekStart && expDate <= currentWeekEnd;
          })
          .reduce((sum, exp) => sum + parseFloat(exp.price), 0);
        
        weekLabels.push(`${weekNumber}. ${this.$t('statistics.week')}`);
        weekValues.push(weekTotal);
        
        weekNumber++;
        currentWeekStart.setDate(currentWeekStart.getDate() + 7);
      }
      
      return { labels: weekLabels, values: weekValues };
    },

    aggregateByDay(expenses, startDate, endDate) {
      const dayLabels = [];
      const dayValues = [];
      
      const start = new Date(startDate);
      const end = new Date(endDate);
      
      const currentDay = new Date(start);
      
      while (currentDay <= end) {
        const dayTotal = expenses
          .filter(exp => {
            const expDate = new Date(exp.date);
            return expDate.toDateString() === currentDay.toDateString();
          })
          .reduce((sum, exp) => sum + parseFloat(exp.price), 0);
        
        dayLabels.push(`${currentDay.getDate()}.`);
        dayValues.push(dayTotal);
        
        currentDay.setDate(currentDay.getDate() + 1);
      }
      
      return { labels: dayLabels, values: dayValues };
    },

    formatCurrency(value) {
      if (value === null || value === undefined) return '0 Ft';
      return new Intl.NumberFormat('hu-HU', {
        style: 'currency',
        currency: 'HUF',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(value);
    },

    async showExpenses() {
      this.showExpensesModal = true;
      this.loadingExpenses = true;
      
      try {
        const { startDate, endDate } = this.getDateRange();
        const [expenses, categories] = await Promise.all([
          expenseService.getAll(),
          categoryService.getAll(),
        ]);
        
        // Szűrjük az adott időszak kiadásait
        this.periodExpenses = expenses.filter(exp => {
          const expDate = new Date(exp.date);
          return expDate >= startDate && expDate <= endDate;
        }).sort((a, b) => new Date(b.date) - new Date(a.date)); // Dátum szerint csökkenő
        
        this.categories = categories;
      } catch (error) {
        console.error('Failed to load expenses:', error);
      } finally {
        this.loadingExpenses = false;
      }
    },

    closeExpensesModal() {
      this.showExpensesModal = false;
      this.periodExpenses = [];
    },

    handleEdit(expense) {
      this.$router.push(`/expenses/${expense.id}`);
    },

    async handleDelete(expenseId) {
      try {
        await expenseService.delete(expenseId);
        await this.showExpenses();
        await this.loadData();
      } catch (error) {
        console.error('Failed to delete expense:', error);
        alert(this.$t('expense.deleteError'));
      }
    },

    createBarGradients(values) {
      const max = Math.max(...values);
      return values.map(value => {
        const intensity = max > 0 ? value / max : 0;
        const baseColor = this.selectedPeriod === 'year' 
          ? { r: 54, g: 162, b: 235 }   // Blue for yearly
          : { r: 75, g: 192, b: 192 };  // Teal for weekly
        
        const alpha = 0.4 + (intensity * 0.5); // 0.4 to 0.9
        return `rgba(${baseColor.r}, ${baseColor.g}, ${baseColor.b}, ${alpha})`;
      });
    },

    getRandomColor() {
      const letters = '0123456789ABCDEF';
      let color = '#';
      for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
      }
      return color;
    },
  },
};
</script>

<style scoped>
.statistics-container {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.kpi-card {
  transition: transform 0.2s, box-shadow 0.2s;
  height: 100%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0, 123, 255, 0.15);
  background-color: #daf3f5;
}

.kpi-card h3 {
  color: #007bff;
  font-weight: 600;
  font-size: 1.5rem;
}

.kpi-card h6 {
  font-size: 0.9rem;
  margin-bottom: 0.75rem;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
}

.chart-card {
  height: 100%;
  min-height: 350px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.chart-card .card-title {
  font-weight: 600;
  color: #2c3e50;
}

.chart-wrapper {
  position: relative;
  height: 300px;
  padding: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-wrapper canvas {
  max-height: 100%;
}

/* Top Categories List */
.top-categories-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.category-item:hover {
  transform: translateX(5px);
  background: linear-gradient(135deg, #e9ecef 0%, #f8f9fa 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-rank {
  font-size: 1.25rem;
  font-weight: bold;
  background: linear-gradient(135deg, #007bff, #0056b3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  min-width: 35px;
  text-align: center;
}

.category-color {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.category-info {
  flex-grow: 1;
}

.category-name {
  font-weight: 600;
  font-size: 1rem;
  margin-bottom: 0.25rem;
}

.category-details {
  font-size: 0.8rem;
  color: #6c757d;
}

.category-amount {
  text-align: right;
}

.amount-value {
  font-size: 1.1rem;
  font-weight: bold;
  color: #007bff;
}

.amount-percentage {
  font-size: 0.8rem;
  color: #6c757d;
}

/* Compact Extreme Stats */
.extreme-stats-compact {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  padding: 1rem 0;
}

.extreme-item-compact {
  text-align: center;
  padding: 1rem;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease;
}

.extreme-item-compact:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.extreme-label-compact {
  font-size: 0.85rem;
  color: #6c757d;
  margin-bottom: 0.5rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.extreme-value-compact {
  font-size: 1.25rem;
  font-weight: bold;
}

.extreme-value-compact.highest {
  color: #dc3545;
  text-shadow: 0 1px 2px rgba(220, 53, 69, 0.2);
}

.extreme-value-compact.lowest {
  color: #28a745;
  text-shadow: 0 1px 2px rgba(40, 167, 69, 0.2);
}

/* Compact Comparison Stats */
.comparison-stats-compact {
  padding: 1rem 0;
}

.comparison-row {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-bottom: 1rem;
  gap: 0.5rem;
}

.comparison-item-compact {
  text-align: center;
  flex: 1;
}

.comparison-period-compact {
  font-size: 0.8rem;
  color: #6c757d;
  margin-bottom: 0.25rem;
}

.comparison-value-compact {
  font-size: 1.25rem;
  font-weight: bold;
  color: #495057;
}

.comparison-value-compact.current {
  color: #007bff;
}

.comparison-arrow-compact {
  font-size: 1.5rem;
  color: #adb5bd;
}

.comparison-diff-compact {
  text-align: center;
  font-size: 1rem;
  font-weight: bold;
  padding: 0.75rem;
  border-radius: 12px;
  margin-top: 0.5rem;
  border: 2px solid transparent;
  transition: all 0.2s ease;
}

.comparison-diff-compact.increase {
  background: linear-gradient(135deg, rgba(220, 53, 69, 0.15), rgba(220, 53, 69, 0.05));
  color: #dc3545;
  border-color: rgba(220, 53, 69, 0.2);
}

.comparison-diff-compact.decrease {
  background: linear-gradient(135deg, rgba(40, 167, 69, 0.15), rgba(40, 167, 69, 0.05));
  color: #28a745;
  border-color: rgba(40, 167, 69, 0.2);
}

.comparison-diff-compact.neutral {
  background: linear-gradient(135deg, rgba(108, 117, 125, 0.15), rgba(108, 117, 125, 0.05));
  color: #6c757d;
  border-color: rgba(108, 117, 125, 0.2);
}

/* Placeholder Card */
.placeholder-card {
  border: 2px dashed #dee2e6;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 50%, #f8f9fa 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.placeholder-card:hover {
  border-color: #007bff;
  background: linear-gradient(135deg, #e7f3ff 0%, #f0f8ff 50%, #e7f3ff 100%);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.1);
}

.placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 280px;
  text-align: center;
}

.placeholder-icon {
  font-size: 3.5rem;
  margin-bottom: 1rem;
  opacity: 0.4;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.placeholder-text {
  font-size: 1rem;
  color: #6c757d;
  margin-bottom: 1.5rem;
  font-weight: 500;
}

.placeholder-suggestions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
  width: 100%;
  max-width: 400px;
}

.suggestion-item {
  padding: 0.6rem 1rem;
  background: white;
  border-radius: 10px;
  font-size: 0.85rem;
  color: #495057;
  border: 1px solid #dee2e6;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.suggestion-item:hover {
  border-color: #007bff;
  color: #007bff;
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(0, 123, 255, 0.15);
}

/* Spending Patterns */
.patterns-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  padding: 0.5rem 0;
}

.pattern-item {
  padding: 1rem;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.pattern-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.pattern-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.pattern-icon {
  font-size: 1.25rem;
}

.pattern-label {
  font-size: 0.8rem;
  color: #6c757d;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.pattern-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #007bff;
  margin-bottom: 0.5rem;
}

.pattern-subtext {
  font-size: 0.85rem;
  color: #6c757d;
  margin-top: 0.25rem;
}

.pattern-progress {
  margin-top: 0.5rem;
}

.pattern-progress .progress {
  height: 6px;
  border-radius: 3px;
  background-color: #e9ecef;
}

.pattern-progress .progress-bar {
  border-radius: 3px;
  transition: width 0.6s ease;
}

/* KPI Carousel Styles */
.kpi-section {
  position: relative;
}

.kpi-carousel-mobile {
  display: none;
}

.kpi-grid-desktop {
  display: flex;
}

.kpi-carousel-wrapper {
  overflow: hidden;
  width: 100%;
  padding: 0 10px;
}

.kpi-carousel-track {
  display: flex;
  transition: transform 0.3s ease-in-out;
}

.kpi-carousel-item {
  min-width: 100%;
  flex-shrink: 0;
}

.carousel-nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: white;
  border: 2px solid #007bff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.carousel-nav-btn:hover:not(:disabled) {
  background: #007bff;
  color: white;
  transform: translateY(-50%) scale(1.1);
}

.carousel-nav-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
  border-color: #ccc;
}

.carousel-nav-btn span {
  font-size: 24px;
  font-weight: bold;
  line-height: 1;
}

.prev-btn {
  left: -15px;
}

.next-btn {
  right: -15px;
}

.carousel-indicators-mobile {
  display: none;
  justify-content: center;
  gap: 8px;
  margin-top: 15px;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ccc;
  cursor: pointer;
  transition: all 0.2s ease;
}

.indicator-dot.active {
  background: #007bff;
  width: 24px;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .statistics-container {
    padding: 1rem;
  }
  
  .kpi-carousel-mobile {
    display: flex;
    align-items: center;
    position: relative;
    padding: 0 30px;
  }

  .kpi-grid-desktop {
    display: none;
  }

  .carousel-indicators-mobile {
    display: flex;
  }
  
  .chart-card {
    min-height: 350px;
  }
  
  .chart-wrapper {
    height: 300px;
    padding: 1rem 0.5rem;
  }

  .chart-card .card-title {
    font-size: 1rem;
    margin-bottom: 0.5rem;
  }

  .extreme-stats-compact {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }

  .comparison-row {
    flex-direction: column;
  }

  .comparison-arrow-compact {
    transform: rotate(90deg);
  }

  .placeholder-suggestions {
    grid-template-columns: 1fr;
  }

  .patterns-content {
    grid-template-columns: 1fr;
  }
}
</style>
