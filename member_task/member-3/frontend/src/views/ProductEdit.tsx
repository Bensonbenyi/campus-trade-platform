import { useState, useEffect, useCallback } from 'react'
import { Form, Upload, Button, Input, Select, Radio, message } from 'antd'
import { UploadOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import { getCategoryTree, getProductDetail, editProduct } from '../api/product'
import { uploadFile } from '../api/file'
import type { UploadRequestOption } from 'rc-upload/lib/interface'

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

const ProductEdit = () => {
  const [form] = Form.useForm<FormValues>()
  const navigate = useNavigate()
  const { id } = useParams()
  const [cateList, setCateList] = useState<CategoryItem[]>([])
  const [imgUrls, setImgUrls] = useState<string[]>([])

  const loadCategory = useCallback(async () => {
    const res = await getCategoryTree()
    setCateList(res.data)
  }, [])

  const loadDetail = useCallback(async () => {
    if (!id) return
    const res = await getProductDetail(id)
    setImgUrls(res.data.imgList || [])
    form.setFieldsValue(res.data)
  }, [id, form])

  useEffect(() => {
    loadCategory()
  }, [loadCategory])

  useEffect(() => {
    loadDetail()
  }, [loadDetail])

  const handleUpload = async (options: UploadRequestOption) => {
    const { file, onSuccess } = options
    const formData = new FormData()
    formData.append('file', file as File)
    const res = await uploadFile(formData)
    setImgUrls([...imgUrls, res.data.url])
    onSuccess?.(res.data)
  }

  const submit = async (values: FormValues) => {
    if (!id) return
    await editProduct(id, { ...values, imgList: imgUrls })
    message.success('修改成功')
    navigate('/user/products')
  }

  return (
    <div style={{ width: '60%', margin: '40px auto' }}>
      <h2>编辑闲置商品</h2>
      <Form form={form} onFinish={submit} layout="vertical">
        <Form.Item label="商品标题" name="title" rules={[{ min: 2, max: 50, required: true }]}>
          <Input placeholder="2-50字" />
        </Form.Item>
        <Form.Item label="商品描述" name="description" rules={[{ min: 10, max: 1000, required: true }]}>
          <Input.TextArea rows={4} placeholder="10-1000字描述详情" />
        </Form.Item>
        <Form.Item label="商品分类" name="categoryId" rules={[{ required: true }]}>
          <Select options={cateList.map(i => ({ label: i.categoryName, value: i.id }))} />
        </Form.Item>
        <Form.Item label="售价" name="price" rules={[{ required: true }]}>
          <Input type="number" prefix="¥" />
        </Form.Item>
        <Form.Item label="原价" name="originalPrice">
          <Input type="number" prefix="¥" />
        </Form.Item>
        <Form.Item label="成色" name="condition" rules={[{ required: true }]}>
          <Radio.Group>
            <Radio value="NEW">全新</Radio>
            <Radio value="LIGHT">轻微使用</Radio>
            <Radio value="NORMAL">正常使用</Radio>
            <Radio value="HEAVY">明显磨损</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item label="交易方式" name="tradeType" rules={[{ required: true }]}>
          <Radio.Group>
            <Radio value="FACE">线下面交</Radio>
            <Radio value="EXPRESS">快递邮寄</Radio>
            <Radio value="BOTH">均可</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item label="商品图片(1-9张)">
          <Upload
            customRequest={handleUpload}
            listType="picture-card"
            maxCount={9}
            fileList={imgUrls.map((url, idx) => ({ url, uid: String(idx), name: `img${idx}` }))}
            onRemove={(file) => {
              setImgUrls(imgUrls.filter(u => u !== file.url))
            }}
          >
            <div><UploadOutlined /><div>上传图片</div></div>
          </Upload>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" block size="large">保存修改</Button>
        </Form.Item>
      </Form>
    </div>
  )
}

export default ProductEdit