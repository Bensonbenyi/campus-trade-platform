// src/api/files.ts
import request from '../utils/request'

export const uploadFile = (data: FormData) =>
  request.post('/api/file/upload', data)