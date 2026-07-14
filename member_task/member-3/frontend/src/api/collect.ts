// src/api/collects.ts
import request from '../utils/request'


export const addCollect = (productId: number) => 
  request.post('/api/collect', { productId })

export const cancelCollect = (productId: number) => 
  request.delete(`/api/collect/${productId}`)

export const getCollectStatus = (productId: number) => 
  request.get(`/api/collect/status/${productId}`)