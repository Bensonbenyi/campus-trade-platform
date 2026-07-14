import { useState, useEffect, useCallback } from 'react'
import { Form, Upload, Button, Input, Select, Radio, message } from 'antd'
import { UploadOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { getCategoryTree, publishProduct } from '../api/product'
import { uploadFile } from '../api/file'

type CustomRequestParam = Parameters<
    NonNullable<React.ComponentProps<typeof Upload>['customRequest']>
>[0]

interface CategoryItem {
  id: number
  categoryName: string
}

interface FormValues {
  title: string
  description: string
  categoryId: number
  price: number
  originalPrice?: number
  condition: string
  tradeType: string
}

const Publish = () => {
  const [form] = Form.useForm<FormValues>()
  const navigate = useNavigate()
  const [cateList, setCateList] = useState<CategoryItem[]>([])
  const [imgUrls, setImgUrls] = useState<string[]>([])

  const loadCategory = useCallback(async () => {
    const res = await getCategoryTree()
    setCateList(res.data)
  }, [])

  useEffect(() => {
    const run = async () => {
      await loadCategory()
    }
    run()
  }, [loadCategory])

  const handleUpload = async (options: CustomRequestParam) => {
    const { file, onSuccess } = options
    const formData = new FormData()
    formData.append('file', file as File)
    const res = await uploadFile(formData)
    setImgUrls(prev => [...prev, res.data.url])
    onSuccess?.(res.data)
  }

  const submit = async (values: FormValues) => {
    if (imgUrls.length < 1) {
      message.warning('至少上传1张商品图片')
      return
    }
    await publishProduct({ ...values, imgList: imgUrls })
    message.success('发布成功')
    navigate('/')
  }

  return (
      <div style={{ width: '60%', margin: '40px auto' }}>
        <h2>发布闲置商品</h2>
        <Form form={form} onFinish={submit} layout="vertical">
          <Form.Item label="商品标题" name="title" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item label="商品描述" name="description" rules={[{ required: true }]}>
            <Input.TextArea />
          </Form.Item>
          <Form.Item label="商品分类" name="categoryId" rules={[{ required: true }]}>
            <Select options={cateList.map(i => ({ label: i.categoryName, value: i.id }))} />
          </Form.Item>
          <Form.Item label="售价" name="price" rules={[{ required: true }]}>
            <Input type="number" />
          </Form.Item>
          <Form.Item label="成色" name="condition" rules={[{ required: true }]}>
            <Radio.Group>
              <Radio value="NEW">全新</Radio>
              <Radio value="USED">二手</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label="交易方式" name="tradeType" rules={[{ required: true }]}>
            <Radio.Group>
              <Radio value="FACE">面交</Radio>
              <Radio value="EXPRESS">快递</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label="上传图片">
            <Upload
                customRequest={handleUpload}
                listType="picture-card"
                fileList={imgUrls.map((url, idx) => ({
                  url,
                  uid: String(idx),
                  name: `${idx}.jpg`,
                  status: 'done' as const
                }))}
            >
              <UploadOutlined />
            </Upload>
          </Form.Item>
          <Button type="primary" htmlType="submit">发布</Button>
        </Form>
      </div>
  )
}

export default Publish