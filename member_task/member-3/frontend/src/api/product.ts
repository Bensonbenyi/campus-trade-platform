import request from '../utils/request'

interface Product {
  id: number
  title: string
  price: number
  originalPrice?: number
  description: string
  categoryId: number
  condition: string
  tradeType: string
  imgList: string[]
  status: string
  createTime: string
}

export const publishProduct = (data: Omit<Product, 'id' | 'status' | 'createTime'>) =>
  request.post('/api/product', data)

export const getProductDetail = (id: string | number) =>
  request.get(`/api/product/${id}`)

export const getProductList = (params: { 
  page: number; 
  size: number; 
  keyword?: string;
  categoryId?: number 
}) => 
  request.get('/api/product', { params })

export const getMyProductList = (params: { page: number; size: number; status: string }) => 
  request.get('/api/product/my', { params })

export const editProduct = (id: string | number, data: Partial<Product>) => 
  request.put(`/api/product/${id}`, data)

export const delProduct = (id: string | number) => 
  request.delete(`/api/product/${id}`)

export const changeProductStatus = (id: string | number, status: string) => 
  request.put(`/api/product/${id}/status`, { status })

export const getCategoryTree = () => 
  request.get('/api/product/category')