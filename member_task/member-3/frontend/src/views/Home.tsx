import { useState, useEffect, useCallback } from 'react'
import { Row, Col, Tabs } from 'antd'

import { getCategoryTree, getProductList } from '../api/product'

interface CategoryItem {
  id?: number
  categoryName: string
}
interface ProductItem {
  id: number
  title: string
  price: number
  imgList: string[]
}


import ProductCard from '../components/ProductCard'

const Home = () => {
  const [categoryList, setCategoryList] = useState<CategoryItem[]>([])
  const [productList, setProductList] = useState<ProductItem[]>([])
  const [currentCateId, setCurrentCateId] = useState<number | undefined>()

  const loadCategory = useCallback(async () => {
    const res = await getCategoryTree()
    setCategoryList([{ id: undefined, categoryName: '全部' }, ...res.data])
  }, [])

  const loadProduct = useCallback(async (cateId?: number) => {
    const res = await getProductList({ page: 1, size: 20, categoryId: cateId })
    setProductList(res.data?.records || [])
  }, [])

  useEffect(() => {
    const run = async () => {
      await loadCategory()
    }
    run()
  }, [loadCategory])

  useEffect(() => {
    const run = async () => {
      await loadProduct(currentCateId)
    }
    run()
  }, [currentCateId, loadProduct])

  const onTabChange = (key: string) => {
    const id = key ? Number(key) : undefined
    setCurrentCateId(id)
  }

  return (
    <div style={{ padding: '20px' }}>
      <Tabs
        activeKey={String(currentCateId ?? '')}
        onChange={onTabChange}
        items={categoryList.map(item => ({
          key: String(item.id ?? ''),
          label: item.categoryName
        }))}
      />
      <Row gutter={[16, 16]} style={{ marginTop: 20 }}>
        {productList.map(item => (
          <Col span={6} key={item.id}>
            <ProductCard data={item} />
          </Col>
        ))}
      </Row>
    </div>
  )
}

export default Home