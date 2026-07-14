import { useState } from 'react'
import { Form, Input, Button, Steps, Card, message, Typography } from 'antd'
import { PhoneOutlined, LockOutlined, IdcardOutlined, UserOutlined, SafetyOutlined } from '@ant-design/icons'
import { Link, useNavigate } from 'react-router-dom'
import { userApi } from '../api/user'

const { Title, Text } = Typography

const stepItems = [
  { title: '验证手机' },
  { title: '填写信息' },
  { title: '注册成功' },
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
      message.success('验证码已发送（固定888888）')
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
        const phone = form.getFieldValue('phone')
        const res: any = await userApi.checkPhone(phone)
        if (res.data === true) {
          message.error('该手机号已注册')
          return
        }
        setStep1Data({ phone, code: form.getFieldValue('code') })
        setCurrent(1)
      } else if (current === 1) {
        await form.validateFields(['studentId', 'realName', 'password', 'confirmPassword'])
        setLoading(true)
        const values = form.getFieldsValue()
        const checkRes: any = await userApi.checkStudentId(values.studentId)
        if (checkRes.data === true) {
          message.error('该学号已注册')
          return
        }
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
          注册账号
        </Title>
        <Steps current={current} items={stepItems} style={{ marginBottom: 32 }} />
        <Form form={form} size="large">
          {current === 0 && (
            <>
              <Form.Item name="phone" rules={[
                { required: true, message: '请输入手机号' },
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
              ]}>
                <Input prefix={<PhoneOutlined />} placeholder="手机号" />
              </Form.Item>
              <Form.Item name="code" rules={[{ required: true, message: '请输入验证码' }]}>
                <Input
                  prefix={<SafetyOutlined />}
                  placeholder="验证码"
                  suffix={
                    <Button type="link" onClick={sendCode} disabled={countdown > 0}
                      style={{ padding: 0, whiteSpace: 'nowrap' }}>
                      {countdown > 0 ? `${countdown}s` : '获取验证码'}
                    </Button>
                  }
                />
              </Form.Item>
            </>
          )}
          {current === 1 && (
            <>
              <Form.Item name="studentId" rules={[
                { required: true, message: '请输入学号' },
                { pattern: /^\d{14}$/, message: '学号格式不正确' },
              ]}>
                <Input prefix={<IdcardOutlined />} placeholder="学号" />
              </Form.Item>
              <Form.Item name="realName" rules={[{ required: true, message: '请输入真实姓名' }]}>
                <Input prefix={<UserOutlined />} placeholder="真实姓名" />
              </Form.Item>
              <Form.Item name="password" rules={[
                { required: true, message: '请输入密码' },
                { min: 6, message: '密码至少6位' },
              ]}>
                <Input.Password prefix={<LockOutlined />} placeholder="密码" />
              </Form.Item>
              <Form.Item name="confirmPassword" dependencies={['password']}
                rules={[
                  { required: true, message: '请确认密码' },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('password') === value) return Promise.resolve()
                      return Promise.reject(new Error('两次密码不一致'))
                    },
                  }),
                ]}>
                <Input.Password prefix={<LockOutlined />} placeholder="确认密码" />
              </Form.Item>
            </>
          )}
          {current === 2 && (
            <div style={{ textAlign: 'center', padding: 'clamp(20px, 5vw, 40px)' }}>
              <Title level={4} style={{ color: '#52c41a', fontSize: 'clamp(18px, 4vw, 24px)' }}>
                注册成功！
              </Title>
              <Text>欢迎加入校园闲置交易平台</Text>
              <br /><br />
              <Button type="primary" size="large" onClick={() => navigate('/login')} block>
                去登录
              </Button>
            </div>
          )}
        </Form>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 16, gap: 8 }}>
          {current > 0 && current < 2 && (
            <Button onClick={() => setCurrent(current - 1)}>上一步</Button>
          )}
          <div style={{ flex: 1 }} />
          {current < 2 && (
            <Button type="primary" onClick={handleNext} loading={loading}>
              {current === 1 ? '提交注册' : '下一步'}
            </Button>
          )}
        </div>
        {current === 0 && (
          <div style={{ textAlign: 'center', marginTop: 16 }}>
            <Text>已有账号？</Text>
            <Link to="/login"> 去登录</Link>
          </div>
        )}
      </Card>
    </div>
  )
}