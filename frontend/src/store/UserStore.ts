import { create } from "zustand";

interface UserInfo {
  uuid: string;
  nickname: string;
  email: string;
  avatar: string;
  gender: number;
  signature: string;
  birthday: string;
  isAdmin: number;
  status: number;
  createdAt: string;
}

interface UserState {
  user: UserInfo | null;
  token: string | null;
  isAuthenticated: boolean;

  logout: () => void;
  setUser: (user: UserInfo) => void;
  setToken: (token: string) => void;
  setIsAuthenticated: (isAuthenticated: boolean) => void;
}

export const useUserStore = create<UserState>()((set) => ({
  user: null,
  token: localStorage.getItem("token"),
  isAuthenticated: false,

  setUser: (user: UserInfo) => {
    set({ user });
  },

  setToken: (token: string) => {
    set({ token });
    localStorage.setItem("token", token);
  },

  setIsAuthenticated: (isAuthenticated: boolean) => {
    set({ isAuthenticated });
  },

  // 登出方法
  logout: () => {
    set({
      user: null,
      token: null,
      isAuthenticated: false,
    });
    localStorage.removeItem("token");
  },
}));
