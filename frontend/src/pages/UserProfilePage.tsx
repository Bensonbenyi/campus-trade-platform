import { useEffect, useState } from 'react'
import { Card, Form, Input, Button, Avatar, message, Spin, Typography } from 'antd'
import { UserOutlined, LogoutOutlined, PhoneOutlined, IdcardOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { userApi } from '../api/user'
import type { UserVO } from '../api/user'
import { useUserStore } from '../store/userStore'

const { Title, Text } = Typography

export default function UserProfilePage() {
  const [user, setUser] = useState<UserVO | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [form] = Form.useForm()
  const navigate = useNavigate()
  const { logout } = useUserStore()

  useEffect(() => {
    loadUserInfo()
  }, [])

  const loadUserInfo = async () => {
    try {
      const res: any = await userApi.getUserInfo()
      const data = res.data
      setUser(data)
      form.setFieldsValue({ nickname: data.nickname, contactPhone: data.phone })
    } catch {
      navigate('/login')
    } finally {
      setLoading(false)
    }
  }

  const handleSave = async (values: { nickname: string; contactPhone: string }) => {
    setSaving(true)
    try {
      await userApi.updateUserInfo(values)
      message.success('保存成功')
      setUser((prev) => prev ? { ...prev, nickname: values.nickname } : prev)
    } finally {
      setSaving(false)
    }
  }

  const handleLogout = () => {
    logout()
    message.success('已退出登录')
    navigate('/login')
  }

  if (loading) return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}><Spin size="large" /></div>

  return (
    <div style={{ maxWidth: 600, width: '100%', margin: 'clamp(16px, 4vw, 40px) auto', padding: '0 16px', boxSizing: 'border-box' }}>
      <Card>
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <Avatar size={80} icon={<UserOutlined />} src={user?.avatar} />
          <Title level={4} style={{ marginTop: 12, fontSize: 'clamp(18px, 4vw, 24px)', wordBreak: 'break-all' }}>
            {user?.nickname}
          </Title>
          <Text type="secondary">{user?.role === 'ADMIN' ? '管理员' : '普通用户'}</Text>
        </div>

        <div style={{ marginBottom: 16, padding: '12px', background: '#fafafa', borderRadius: 8, fontSize: 'clamp(13px, 3.5vw, 14px)' }}>
          <p style={{ margin: '4px 0', wordBreak: 'break-all' }}>
            <PhoneOutlined /> 手机号：{user?.phone}
          </p>
          <p style={{ margin: '4px 0', wordBreak: 'break-all' }}>
            <IdcardOutlined /> 学号：{user?.studentId}
          </p>
          <p style={{ margin: '4px 0', wordBreak: 'break-all' }}>
            <UserOutlined /> 姓名：{user?.realName}
          </p>
        </div>

        <Form form={form} layout="vertical" onFinish={handleSave}>
          <Form.Item name="nickname" label="昵称" rules={[{ required: true, message: '请输入昵称' }]}>
            <Input placeholder="昵称" />
          </Form.Item>
          <Form.Item name="contactPhone" label="联系方式">
            <Input placeholder="联系方式" disabled />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={saving} block>
              保存修改
            </Button>
          </Form.Item>
        </Form>

        <Button danger icon={<LogoutOutlined />} onClick={handleLogout} block>
          退出登录
        </Button>
      </Card>
    </div>
  )
}