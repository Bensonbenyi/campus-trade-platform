import { create } from 'zustand'

interface UserInfo {
  id: number
  phone: string
  nickname: string
  avatar: string
  studentId: string
  realName: string
  role: string
}

interface UserStore {
  token: string
  user: UserInfo | null
  setToken: (val: string) => void
  setUser: (user: UserInfo) => void
  logout: () => void
}

export const useUserStore = create<UserStore>((set) => ({
  token: localStorage.getItem('token') || '',
  user: null,
  setToken: (val) => {
    localStorage.setItem('token', val)
    set({ token: val })
  },
  setUser: (user) => set({ user }),
  logout: () => {
    localStorage.removeItem('token')
    set({ token: '', user: null })
  },
}))
