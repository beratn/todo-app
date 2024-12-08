import axios, { AxiosInstance, InternalAxiosRequestConfig } from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api/todos',
});

// Add request interceptor
instance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: any) => {
    return Promise.reject(error);
  }
);

export class TodoService {
  private axiosInstance: AxiosInstance;

  constructor() {
    this.axiosInstance = instance;
  }

  getTodos() {
    return this.axiosInstance.get('');
  }

  createTodo(title: string, description: string) {
    return this.axiosInstance.post('', { title, description });
  }

  toggleTodo(id: string) {
    return this.axiosInstance.put(`/${id}/toggle`);
  }
}
