import request from '../utils/request'

export const getMessageList = (params: { page: number; size: number }) =>
  request.get('/api/message', { params })
