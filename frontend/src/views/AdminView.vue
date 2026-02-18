<template>
  <div class="admin-container">
    <h1 class="mb-4">{{ $t('admin.title') }}</h1>

    <!-- Globális statisztikák -->
    <div class="row g-3 mb-4">
      <div class="col-md-3">
        <div class="card stat-card">
          <div class="card-body">
            <h6 class="text-muted">{{ $t('admin.totalUsers') }}</h6>
            <h2 class="mb-0">{{ stats.totalUsers || 0 }}</h2>
          </div>
        </div>
      </div>

      <div class="col-md-3">
        <div class="card stat-card">
          <div class="card-body">
            <h6 class="text-muted">{{ $t('admin.totalExpenses') }}</h6>
            <h2 class="mb-0">{{ stats.totalExpenses || 0 }}</h2>
          </div>
        </div>
      </div>

      <div class="col-md-3">
        <div class="card stat-card">
          <div class="card-body">
            <h6 class="text-muted">{{ $t('admin.totalAmount') }}</h6>
            <h2 class="mb-0">{{ formatCurrency(stats.totalAmount || 0) }}</h2>
          </div>
        </div>
      </div>

      <div class="col-md-3">
        <div class="card stat-card">
          <div class="card-body">
            <h6 class="text-muted">{{ $t('admin.avgPerUser') }}</h6>
            <h2 class="mb-0">{{ formatCurrency(stats.avgExpensePerUser || 0) }}</h2>
          </div>
        </div>
      </div>
    </div>

    <!-- Felhasználók listája -->
    <div class="card">
      <div class="card-body">
        <h5 class="card-title mb-4">{{ $t('admin.userList') }}</h5>

        <div v-if="loading" class="text-center py-5">
          <span class="spinner-border spinner-border-lg"></span>
          <p class="mt-3">{{ $t('admin.loading') }}</p>
        </div>

        <div v-else-if="users.length === 0" class="text-center text-muted py-5">
          {{ $t('admin.noUsers') }}
        </div>

        <div v-else class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th>ID</th>
                <th>{{ $t('admin.username') }}</th>
                <th>{{ $t('admin.name') }}</th>
                <th>{{ $t('admin.email') }}</th>
                <th>{{ $t('admin.roles') }}</th>
                <th>{{ $t('admin.lastLogin') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>{{ user.id }}</td>
                <td>
                  <strong>{{ user.username }}</strong>
                </td>
                <td>{{ user.name }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <span 
                    v-for="role in user.roles" 
                    :key="role"
                    :class="['badge', role === 'ADMIN' ? 'bg-danger' : 'bg-primary']"
                    class="me-1"
                  >
                    {{ role }}
                  </span>
                </td>
                <td>{{ formatDate(user.lastLoginDate) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { adminService } from '@/services/api';

export default {
  name: 'AdminView',
  data() {
    return {
      users: [],
      stats: {},
      loading: true,
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const [usersData, statsData] = await Promise.all([
          adminService.getAllUsers(),
          adminService.getGlobalStats(),
        ]);
        
        this.users = usersData;
        this.stats = statsData;
      } catch (error) {
        console.error('Admin data load error:', error);
        if (error.response?.status === 403) {
          alert(this.$t('admin.accessDenied'));
          this.$router.push('/');
        } else {
          alert(this.$t('admin.loadError'));
        }
      } finally {
        this.loading = false;
      }
    },
    formatCurrency(amount) {
      return new Intl.NumberFormat('hu-HU', {
        style: 'currency',
        currency: 'HUF',
        minimumFractionDigits: 0,
      }).format(amount);
    },
    formatDate(dateString) {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('hu-HU', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
      }).format(date);
    },
  },
};
</script>

<style scoped>
.admin-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

.stat-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-card h2 {
  color: #007bff;
  font-weight: bold;
}

.table {
  margin-bottom: 0;
}

.table thead {
  background: #f8f9fa;
}

.table tbody tr:hover {
  background: #f8f9fa;
}

.badge {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
}

@media (max-width: 768px) {
  .admin-container {
    padding: 1rem;
  }
  
  .table-responsive {
    font-size: 0.875rem;
  }
}
</style>
