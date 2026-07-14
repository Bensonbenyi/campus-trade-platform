import { useEffect, useState } from 'react'
import { Card, Button, Modal, Form, Input, Switch, Popconfirm, message, Spin, Empty, Tag, Typography } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, EnvironmentOutlined, PhoneOutlined, UserOutlined, HomeOutlined } from '@ant-design/icons'
import { userApi } from '../api/user'
import type { AddressDTO } from '../api/user'

const { Title } = Typography

export default function UserAddressPage() {
  const [addresses, setAddresses] = useState<AddressDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingAddress, setEditingAddress] = useState<AddressDTO | null>(null)
  const [saving, setSaving] = useState(false)
  const [form] = Form.useForm()

  useEffect(() => { loadAddresses() }, [])

  const loadAddresses = async () => {
    setLoading(true)
    try {
      const res: any = await userApi.getAddressList()
      setAddresses(res.data || [])
    } finally { setLoading(false) }
  }

  const openAdd = () => {
    setEditingAddress(null)
    form.resetFields()
    form.setFieldsValue({ isDefault: false })
    setModalOpen(true)
  }

  const openEdit = (addr: AddressDTO) => {
    setEditingAddress(addr)
    form.setFieldsValue(addr)
    setModalOpen(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setSaving(true)
      if (editingAddress?.id) {
        await userApi.updateAddress(editingAddress.id, values)
        message.success('修改成功')
      } else {
        await userApi.addAddress(values)
        message.success('添加成功')
      }
      setModalOpen(false)
      loadAddresses()
    } finally { setSaving(false) }
  }

  const handleDelete = async (id: number) => {
    await userApi.deleteAddress(id)
    message.success('删除成功')
    loadAddresses()
  }

  if (loading) return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}><Spin size="large" /></div>

  return (
    <div style={{ maxWidth: 700, width: '100%', margin: 'clamp(16px, 4vw, 40px) auto', padding: '0 16px', boxSizing: 'border-box' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap', gap: 8 }}>
        <Title level={4} style={{ margin: 0, fontSize: 'clamp(18px, 4vw, 24px)' }}>收货地址</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openAdd}>新增地址</Button>
      </div>

      {addresses.length === 0 ? (
        <Empty description="暂无收货地址" />
      ) : (
        addresses.map((addr) => (
          <Card
            key={addr.id}
            style={{ marginBottom: 12 }}
            extra={addr.isDefault && <Tag color="blue">默认</Tag>}
            actions={[
              <EditOutlined key="edit" onClick={() => openEdit(addr)} />,
              <Popconfirm
                key="delete"
                title="确定删除该地址吗？"
                onConfirm={() => handleDelete(addr.id!)}
                okText="确定"
                cancelText="取消"
              >
                <DeleteOutlined style={{ color: '#ff4d4f' }} />
              </Popconfirm>,
            ]}
          >
            <p style={{ fontSize: 'clamp(13px, 3.5vw, 14px)', margin: '4px 0', wordBreak: 'break-all' }}>
              <UserOutlined /> {addr.contactName} &nbsp;&nbsp; <PhoneOutlined /> {addr.contactPhone}
            </p>
            <p style={{ fontSize: 'clamp(13px, 3.5vw, 14px)', margin: '4px 0', wordBreak: 'break-all' }}>
              <EnvironmentOutlined /> {addr.campus} &nbsp; <HomeOutlined /> {addr.building} {addr.room}
            </p>
          </Card>
        ))
      )}

      <Modal
        title={editingAddress ? '编辑地址' : '新增地址'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        confirmLoading={saving}
        destroyOnClose
        width="min(520px, 95vw)"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="contactName" label="联系人" rules={[{ required: true, message: '请输入联系人' }]}>
            <Input placeholder="联系人姓名" />
          </Form.Item>
          <Form.Item name="contactPhone" label="联系电话" rules={[
            { required: true, message: '请输入联系电话' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
          ]}>
            <Input placeholder="联系电话" />
          </Form.Item>
          <Form.Item name="campus" label="校区" rules={[{ required: true, message: '请输入校区' }]}>
            <Input placeholder="如：仙林校区" />
          </Form.Item>
          <Form.Item name="building" label="楼栋" rules={[{ required: true, message: '请输入楼栋' }]}>
            <Input placeholder="如：1号楼" />
          </Form.Item>
          <Form.Item name="room" label="宿舍号" rules={[{ required: true, message: '请输入宿舍号' }]}>
            <Input placeholder="如：301" />
          </Form.Item>
          <Form.Item name="isDefault" label="设为默认地址" valuePropName="checked">
            <Switch />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}