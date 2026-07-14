import { useState } from 'react'
import { Form, Input, Button, Steps, Card, message, Typography } from 'antd'
import { PhoneOutlined, LockOutlined, IdcardOutlined, UserOutlined, SafetyOutlined } from '@ant-design/icons'
import { Link, useNavigate } from 'react-router-dom'
import { userApi } from '../api/user'

const { Title, Text } = Typography

const stepItems = [
  { title: 'Verify' },
  { title: 'Info' },
  { title: 'Done' },
]

export default function RegisterPage() {
  const [current, setCurrent] = useState(0)
  const [loading, setLoading] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const [step1Data, setStep1Data] = useState({ phone: '', code: '' })
  const [form] = Form.useForm()
  const navigate = useNavigate()

  const sendCode = async () => {
    try {
      await form.validateFields(['phone'])
      const phone = form.getFieldValue('phone')
      await userApi.sendCode(phone)
      message.success('Code sent (MVP: 888888)')
      setCountdown(60)
      const timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) { clearInterval(timer); return 0 }
          return prev - 1
        })
      }, 1000)
    } catch { /* ignore */ }
  }

  const handleNext = async () => {
    try {
      if (current === 0) {
        await form.validateFields(['phone', 'code'])
        setStep1Data({
          phone: form.getFieldValue('phone'),
          code: form.getFieldValue('code'),
        })
        setCurrent(1)
      } else if (current === 1) {
        await form.validateFields(['studentId', 'realName', 'password', 'confirmPassword'])
        setLoading(true)
        const values = form.getFieldsValue()
        await userApi.register({
          phone: step1Data.phone,
          code: step1Data.code,
          password: values.password,
          studentId: values.studentId,
          realName: values.realName,
        })
        setCurrent(2)
      }
    } catch {
      // validation error
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      display: 'flex', justifyContent: 'center', alignItems: 'flex-start',
      minHeight: '100vh', background: '#f5f5f5',
      padding: 'clamp(16px, 5vw, 40px) 16px', boxSizing: 'border-box',
    }}>
      <Card style={{ width: '100%', maxWidth: 480, boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
        <Title level={3} style={{ textAlign: 'center', marginBottom: 24, fontSize: 'clamp(20px, 5vw, 28px)' }}>
          Register
        </Title>
        <Steps current={current} items={stepItems} style={{ marginBottom: 32 }} />
        <Form form={form} size="large">
          {current === 0 && (
            <>
              <Form.Item name="phone" rules={[
                { required: true, message: 'Please enter phone' },
                { pattern: /^1[3-9]\d{9}$/, message: 'Invalid phone' },
              ]}>
                <Input prefix={<PhoneOutlined />} placeholder="Phone" />
              </Form.Item>
              <Form.Item name="code" rules={[{ required: true, message: 'Please enter code' }]}>
                <Input
                  prefix={<SafetyOutlined />}
                  placeholder="Verification code"
                  suffix={
                    <Button type="link" onClick={sendCode} disabled={countdown > 0}
                      style={{ padding: 0, whiteSpace: 'nowrap' }}>
                      {countdown > 0 ? `${countdown}s` : 'Get code'}
                    </Button>
                  }
                />
              </Form.Item>
            </>
          )}
          {current === 1 && (
            <>
              <Form.Item name="studentId" rules={[
                { required: true, message: 'Please enter student ID' },
                { pattern: /^20\d{2}10\d{4}$/, message: 'Invalid format' },
              ]}>
                <Input prefix={<IdcardOutlined />} placeholder="Student ID" />
              </Form.Item>
              <Form.Item name="realName" rules={[{ required: true, message: 'Please enter name' }]}>
                <Input prefix={<UserOutlined />} placeholder="Real name" />
              </Form.Item>
              <Form.Item name="password" rules={[
                { required: true, message: 'Please enter password' },
                { min: 6, message: 'At least 6 characters' },
              ]}>
                <Input.Password prefix={<LockOutlined />} placeholder="Password" />
              </Form.Item>
              <Form.Item name="confirmPassword" dependencies={['password']}
                rules={[
                  { required: true, message: 'Please confirm password' },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('password') === value) return Promise.resolve()
                      return Promise.reject(new Error('Passwords do not match'))
                    },
                  }),
                ]}>
                <Input.Password prefix={<LockOutlined />} placeholder="Confirm password" />
              </Form.Item>
            </>
          )}
          {current === 2 && (
            <div style={{ textAlign: 'center', padding: 'clamp(20px, 5vw, 40px)' }}>
              <Title level={4} style={{ color: '#52c41a', fontSize: 'clamp(18px, 4vw, 24px)' }}>
                Registration success!
              </Title>
              <Text>Welcome to Campus Trade</Text>
              <br /><br />
              <Button type="primary" size="large" onClick={() => navigate('/login')} block>
                Go to Login
              </Button>
            </div>
          )}
        </Form>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 16, gap: 8 }}>
          {current > 0 && current < 2 && (
            <Button onClick={() => setCurrent(current - 1)}>Previous</Button>
          )}
          <div style={{ flex: 1 }} />
          {current < 2 && (
            <Button type="primary" onClick={handleNext} loading={loading}>
              {current === 1 ? 'Submit' : 'Next'}
            </Button>
          )}
        </div>
        {current === 0 && (
          <div style={{ textAlign: 'center', marginTop: 16 }}>
            <Text>Already have an account? </Text>
            <Link to="/login">Login</Link>
          </div>
        )}
      </Card>
    </div>
  )
}