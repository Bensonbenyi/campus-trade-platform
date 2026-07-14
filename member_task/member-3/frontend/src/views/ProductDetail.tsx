import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Image, Button, Space, Card, Divider, Avatar, message } from 'antd'
import { getProductDetail } from '../api/product'
import { addCollect, cancelCollect, getCollectStatus } from '../api/collect'


interface ProductDetailItem {
  id: number
  title: string
  price: number
  originalPrice: number
  conditionName: string
  tradeTypeName: string
  createTime: string
  description: string
  imgList: string[]
  sellerId: number
  sellerAvatar: string
  sellerNickname: string
}

const ProductDetail = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [product, setProduct] = useState<ProductDetailItem | null>(null)
  const [isCollected, setIsCollected] = useState(false)

  const loadData = useCallback(async () => {
    if (!id) return
    const res = await getProductDetail(id)
    setProduct(res.data)
    const collectRes = await getCollectStatus(res.data.id)
    setIsCollected(collectRes.data)
  }, [id])

  useEffect(() => {
    const run = async () => {
      await loadData()
    }
    run()
  }, [loadData])

  const toggleCollect = async () => {
    if (!product) return
    if (isCollected) await cancelCollect(product.id)
    else await addCollect(product.id)
    setIsCollected(!isCollected)
    message.success(isCollected ? '取消收藏' : '收藏成功')
  }

  const contactSeller = async () => {
    if (!product) return

    navigate('/message')
  }

  const goOrder = () => {
    if (!product) return
    navigate(`/order/create/${product.id}`)
  }

  if (!product) return <div style={{ textAlign: 'center', padding: 40 }}>加载中...</div>

  return (
    <div style={{ width: '70%', margin: '30px auto' }}>
      <Space size={30} style={{ alignItems: 'flex-start' }}>
        <Image.PreviewGroup>
          {product.imgList?.map((url: string, idx: number) => (
            <Image width={400} src={url} key={idx} preview={false} />
          ))}
        </Image.PreviewGroup>

        <div style={{ flex: 1 }}>
          <h1>{product.title}</h1>
          <h2 style={{ color: 'red' }}>¥{product.price}</h2>
          <p>原价：¥{product.originalPrice}</p>
          <Divider />
          <p>成色：{product.conditionName}</p>
          <p>交易方式：{product.tradeTypeName}</p>
          <p>发布时间：{product.createTime}</p>
          <Divider />
          <Card size="small" title="卖家信息">
            <Space style={{ alignItems: 'center' }}>
              <Avatar src={product.sellerAvatar} size={48} />
              <span>昵称：{product.sellerNickname}</span>
            </Space>
          </Card>
          <Space style={{ marginTop: 20 }}>
            <Button onClick={contactSeller}>联系卖家</Button>
            <Button type="primary" onClick={goOrder}>立即下单</Button>
            <Button onClick={toggleCollect}>{isCollected ? '已收藏' : '收藏'}</Button>
          </Space>
        </div>
      </Space>
      <Divider />
      <div>
        <h3>商品详情描述</h3>
        <p>{product.description}</p>
      </div>
    </div>
  )
}

export default ProductDetail