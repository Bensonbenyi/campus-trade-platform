import { useState } from 'react'
import { Form, Input, Button, Card, message, Typography } from 'antd'
import { PhoneOutlined, LockOutlined } from '@ant-design/icons'
import { Link, useNavigate } from 'react-router-dom'
import { userApi } from '../api/user'
import { useUserStore } from '../store/userStore'

const { Title, Text } = Typography

export default function LoginPage() {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { setToken, setUser } = useUserStore()

  const onFinish = async (values: { phone: string; password: string }) => {
    setLoading(true)
    try {
      const res: any = await userApi.login(values)
      if (res.code !== 200) {
        message.error(res.msg || 'Login failed')
        return
      }
      const data = res.data
      if (!data || !data.token) {
        message.error('Login failed: invalid response')
        return
      }
      setToken(data.token)
      setUser({
        id: data.id,
        phone: data.phone,
        nickname: data.nickname,
        avatar: data.avatar || '',
        studentId: data.studentId,
        realName: data.realName,
        role: data.role,
      })
      message.success('Login success')
      navigate('/')
    } catch (err: any) {
      message.error(err?.response?.data?.msg || err?.message || 'Network error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      display: 'flex', justifyContent: 'center', alignItems: 'center',
      minHeight: '100vh', background: '#f5f5f5', padding: '16px', boxSizing: 'border-box',
    }}>
      <Card style={{ width: '100%', maxWidth: 400, boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
        <Title level={3} style={{ textAlign: 'center', marginBottom: 24, fontSize: 'clamp(20px, 5vw, 28px)' }}>
          Campus Trade
        </Title>
        <Form onFinish={onFinish} size="large">
          <Form.Item name="phone" rules={[
            { required: true, message: 'Please enter phone' },
            { pattern: /^1[3-9]\d{9}$/, message: 'Invalid phone' },
          ]}>
            <Input prefix={<PhoneOutlined />} placeholder="Phone" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: 'Please enter password' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="Password" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>Login</Button>
          </Form.Item>
        </Form>
        <div style={{ textAlign: 'center' }}>
          <Text>No account? </Text>
          <Link to="/register">Register</Link>
        </div>
      </Card>
    </div>
  )
}