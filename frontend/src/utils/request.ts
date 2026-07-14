import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = token
  return config
})

request.interceptors.response.use(res => {
  return res.data
}, err => {
  const msg = err.response?.data?.msg || '接口请求失败'
  alert(msg)
  return Promise.reject(err)
})

export default request
