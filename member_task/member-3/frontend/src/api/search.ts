import request from '../utils/request'

export const searchProduct = (keyword: string, params: { page: number; size: number }) =>
  request.get('/api/product/search', { params: { keyword, ...params } })
export const getHotSearch = async () => {
    return request.get('/search/hot')
}