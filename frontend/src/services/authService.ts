import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export class AuthService {
  async login(username: string, password: string) {
    const response = await axios.post(`${API_URL}/auth/login`, {
      username,
      password,
    });
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
    }
    return response.data;
  }

  async register(username: string, password: string) {
    return axios.post(`${API_URL}/register`, {
      username,
      password,
    });
  }

  logout() {
    localStorage.removeItem('token');
  }
}
