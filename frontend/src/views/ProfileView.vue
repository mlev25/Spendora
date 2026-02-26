<template>
  <div class="profile-container">
    <h1 class="page-title">{{ $t('profile.title') }}</h1>

    <!-- Profil információk -->
    <div class="profile-section">
      <h2>{{ $t('profile.accountInfo') }}</h2>
      <form @submit.prevent="handleUpdateProfile" class="profile-form">
        <div class="form-group">
          <label for="name">{{ $t('profile.name') }}</label>
          <input
            type="text"
            id="name"
            v-model="profileData.name"
            class="form-control"
            required
          />
        </div>

        <div class="form-group">
          <label for="username">{{ $t('profile.username') }}</label>
          <input
            type="text"
            id="username"
            v-model="profileData.username"
            class="form-control"
            required
          />
        </div>

        <div v-if="profileError" class="alert alert-error">
          {{ profileError }}
        </div>

        <div v-if="profileSuccess" class="alert alert-success">
          {{ profileSuccess }}
        </div>

        <button type="submit" class="btn-primary" :disabled="isUpdatingProfile">
          {{ isUpdatingProfile ? $t('common.saving') : $t('profile.updateButton') }}
        </button>
      </form>
    </div>

    <!-- Jelszó változtatás -->
    <div class="profile-section">
      <h2>{{ $t('profile.changePassword') }}</h2>
      <form @submit.prevent="handleChangePassword" class="profile-form">
        <div class="form-group">
          <label for="currentPassword">{{ $t('profile.currentPassword') }}</label>
          <input
            type="password"
            id="currentPassword"
            v-model="passwordData.currentPassword"
            class="form-control"
            required
          />
        </div>

        <div class="form-group">
          <label for="newPassword">{{ $t('profile.newPassword') }}</label>
          <input
            type="password"
            id="newPassword"
            v-model="passwordData.newPassword"
            class="form-control"
            required
            minlength="6"
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">{{ $t('profile.confirmPassword') }}</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="passwordData.confirmPassword"
            class="form-control"
            required
            minlength="6"
          />
        </div>

        <div v-if="passwordError" class="alert alert-error">
          {{ passwordError }}
        </div>

        <div v-if="passwordSuccess" class="alert alert-success">
          {{ passwordSuccess }}
        </div>

        <button type="submit" class="btn-primary" :disabled="isChangingPassword">
          {{ isChangingPassword ? $t('common.saving') : $t('profile.changePasswordButton') }}
        </button>
      </form>
    </div>

    <!-- Fiók törlése -->
    <div class="profile-section danger-section">
      <h2>{{ $t('profile.dangerZone') }}</h2>
      <p class="danger-text">{{ $t('profile.deleteWarning') }}</p>
      
      <button class="btn-danger" @click="showDeleteModal = true">
        {{ $t('profile.deleteAccountButton') }}
      </button>
    </div>

    <!-- Delete confirmation modal -->
    <div v-if="showDeleteModal" class="modal-overlay" @click.self="showDeleteModal = false">
      <div class="modal-content">
        <h2>{{ $t('profile.confirmDelete') }}</h2>
        <p>{{ $t('profile.deleteMessage') }}</p>
        
        <div class="form-group">
          <label for="deletePassword">{{ $t('profile.enterPassword') }}</label>
          <input
            type="password"
            id="deletePassword"
            v-model="deletePassword"
            class="form-control"
            :placeholder="$t('profile.passwordPlaceholder')"
          />
        </div>

        <div v-if="deleteError" class="alert alert-error">
          {{ deleteError }}
        </div>

        <div class="modal-actions">
          <button class="btn-secondary" @click="showDeleteModal = false">
            {{ $t('common.cancel') }}
          </button>
          <button class="btn-danger" @click="handleDeleteAccount" :disabled="isDeletingAccount">
            {{ isDeletingAccount ? $t('profile.deleting') : $t('profile.confirmDeleteButton') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import './styles/ProfileView.css';
import { useAuthStore } from '../stores/auth.js';
import { userService } from '../services/api.js';

export default {
  name: 'ProfileView',
  data() {
    return {
      profileData: {
        name: '',
        username: '',
      },
      userEmail: '',
      passwordData: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      },
      deletePassword: '',
      showDeleteModal: false,
      isUpdatingProfile: false,
      isChangingPassword: false,
      isDeletingAccount: false,
      profileError: '',
      profileSuccess: '',
      passwordError: '',
      passwordSuccess: '',
      deleteError: '',
    };
  },
  mounted() {
    this.loadUserData();
  },
  methods: {
    loadUserData() {
      const authStore = useAuthStore();
      this.profileData.name = authStore.user?.name || '';
      this.profileData.username = authStore.user?.username || '';
      this.userEmail = authStore.user?.email || '';
    },
    async handleUpdateProfile() {
      this.isUpdatingProfile = true;
      this.profileError = '';
      this.profileSuccess = '';

      const authStore = useAuthStore();
      const usernameChanged = this.profileData.username !== authStore.user?.username;

      try {
        const updatedUser = await userService.updateProfile(this.profileData);
        
        // If username changed, logout and redirect to login
        if (usernameChanged) {
          alert(this.$t('profile.usernameChangedLogout'));
          authStore.logout();
          this.$router.push('/login');
          return;
        }

        // Update auth store if only name changed
        authStore.user.name = updatedUser.name;

        this.profileSuccess = this.$t('profile.updateSuccess');
        setTimeout(() => {
          this.profileSuccess = '';
        }, 3000);
      } catch (error) {
        console.error('Failed to update profile:', error);
        this.profileError = error.response?.data || this.$t('profile.updateError');
      } finally {
        this.isUpdatingProfile = false;
      }
    },
    async handleChangePassword() {
      this.isChangingPassword = true;
      this.passwordError = '';
      this.passwordSuccess = '';

      if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
        this.passwordError = this.$t('profile.passwordMismatch');
        this.isChangingPassword = false;
        return;
      }

      try {
        await userService.changePassword({
          currentPassword: this.passwordData.currentPassword,
          newPassword: this.passwordData.newPassword,
        });

        this.passwordSuccess = this.$t('profile.passwordChangeSuccess');
        this.passwordData = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: '',
        };

        setTimeout(() => {
          this.passwordSuccess = '';
        }, 3000);
      } catch (error) {
        console.error('Failed to change password:', error);
        this.passwordError = error.response?.data || this.$t('profile.passwordChangeError');
      } finally {
        this.isChangingPassword = false;
      }
    },
    async handleDeleteAccount() {
      if (!this.deletePassword) {
        this.deleteError = this.$t('profile.passwordRequired');
        return;
      }

      this.isDeletingAccount = true;
      this.deleteError = '';

      try {
        await userService.deleteAccount(this.deletePassword);
        
        // Logout and redirect
        const authStore = useAuthStore();
        authStore.logout();
        this.$router.push('/');
      } catch (error) {
        console.error('Failed to delete account:', error);
        this.deleteError = error.response?.data || this.$t('profile.deleteError');
        this.isDeletingAccount = false;
      }
    },
  },
};
</script>
