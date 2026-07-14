import { Card, Image } from 'antd'
import { useNavigate } from 'react-router-dom'

// ✅ 精确定义商品类型，去掉 any
interface ProductItem {
  id: number
  title: string
  price: number
  imgList: string[]
}
interface Props {
  data: ProductItem
}

const ProductCard = ({ data }: Props) => {
  const navigate = useNavigate()
  const goDetail = () => navigate(`/product/${data.id}`)

  if (!data?.imgList?.length) return null

  return (
    <Card hoverable onClick={goDetail} cover={<Image height={180} src={data.imgList[0]} preview={false} />}>
      <Card.Meta title={data.title} description={`¥${data.price}`} />
    </Card>
  )
}

export default ProductCard