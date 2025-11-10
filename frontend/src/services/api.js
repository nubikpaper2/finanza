import axios from 'axios';

const API_URL = '/auth';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar token a las peticiones
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const authService = {
  login: async (email, password) => {
    const response = await api.post(`${API_URL}/login`, { email, password });
    return response.data;
  },

  register: async (userData) => {
    const response = await api.post(`${API_URL}/register`, userData);
    return response.data;
  },
};

// Servicio de Categorías
export const categoryService = {
  getAll: async () => {
    const response = await api.get('/categories');
    return response.data;
  },

  getByType: async (type) => {
    const response = await api.get(`/categories/type/${type}`);
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`/categories/${id}`);
    return response.data;
  },

  create: async (categoryData) => {
    const response = await api.post('/categories', categoryData);
    return response.data;
  },

  update: async (id, categoryData) => {
    const response = await api.put(`/categories/${id}`, categoryData);
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/categories/${id}`);
    return response.data;
  },
};

// Servicio de Transacciones
export const transactionService = {
  getAll: async (params = {}) => {
    const response = await api.get('/transactions', { params });
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`/transactions/${id}`);
    return response.data;
  },

  create: async (transactionData) => {
    const response = await api.post('/transactions', transactionData);
    return response.data;
  },

  update: async (id, transactionData) => {
    const response = await api.put(`/transactions/${id}`, transactionData);
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/transactions/${id}`);
    return response.data;
  },

  createTransfer: async (transferData) => {
    const response = await api.post('/transactions/transfer', transferData);
    return response.data;
  },
};

export default api;
