import request from '../utils/request'

export interface UserVO {
  id: number
  phone: string
  nickname: string
  avatar: string
  studentId: string
  realName: string
  role: string
  token?: string
}

export interface AddressDTO {
  id?: number
  contactName: string
  contactPhone: string
  campus: string
  building: string
  room: string
  isDefault: boolean
}

export const userApi = {
  sendCode: (phone: string) =>
    request.post('/api/user/send-code', null, { params: { phone } }),

  register: (data: { phone: string; code: string; password: string; studentId: string; realName: string }) =>
    request.post('/api/user/register', data),

  login: (data: { phone: string; password: string }) =>
    request.post('/api/user/login', data),

  getUserInfo: () =>
    request.get('/api/user/info'),

  updateUserInfo: (data: { nickname?: string; avatar?: string; contactPhone?: string }) =>
    request.put('/api/user/info', data),

  getAddressList: () =>
    request.get('/api/user/address'),

  addAddress: (data: AddressDTO) =>
    request.post('/api/user/address', data),

  updateAddress: (id: number, data: AddressDTO) =>
    request.put('/api/user/address/' + id, data),

  deleteAddress: (id: number) =>
    request.delete('/api/user/address/' + id),

  checkPhone: (phone: string) =>
    request.get('/api/user/check-phone', { params: { phone } }),

  checkStudentId: (studentId: string) =>
    request.get('/api/user/check-student-id', { params: { studentId } }),
}
