import axios from "axios";

export const axiosInstance = axios.create({
  baseURL: "http://127.0.0.1:10020/api",
  withCredentials: true,
});

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
