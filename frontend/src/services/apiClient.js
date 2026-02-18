import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

let isRedirecting = false;

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      const token = localStorage.getItem('token');
      const currentPath = window.location.pathname;
      if (token && !isRedirecting && currentPath !== '/login') {
        isRedirecting = true;

        console.log('Token lejárt - tisztítás és átirányítás...');

        localStorage.removeItem('token');
        localStorage.removeItem('user');
        delete apiClient.defaults.headers.common['Authorization'];

        setTimeout(() => {
          alert('A munkamenet lejárt!\n\nKérjük, jelentkezz be újra.');
          window.location.href = '/login';
          isRedirecting = false;
        }, 100);
      }
    }

    return Promise.reject(error);
  }
);

// Token minden requesthez
export const setAuthToken = (token) => {
  if (token) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

export default apiClient;
