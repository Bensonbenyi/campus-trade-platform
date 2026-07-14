import { useState, useEffect, useCallback } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Row, Col, Drawer, Input, Button, Select, Slider, Radio, Space } from 'antd'
import { SearchOutlined, FilterOutlined } from '@ant-design/icons'
import { searchProduct, getHotSearch } from '../api/search'
import { getCategoryTree } from '../api/product'
import ProductCard from '../components/ProductCard'

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
interface FilterType {
  categoryId?: number
  priceMin: number
  priceMax: number
  condition: string
  tradeType: string
  sort: string
}

const Search = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const keyword = searchParams.get('keyword') || ''
  const [inputKey, setInputKey] = useState(keyword)
  const [filterOpen, setFilterOpen] = useState(false)
  const [productList, setProductList] = useState<ProductItem[]>([])
  const [cateList, setCateList] = useState<CategoryItem[]>([])
  const [hotWords, setHotWords] = useState<string[]>([])
  const [filter, setFilter] = useState<FilterType>({
    categoryId: undefined,
    priceMin: 0,
    priceMax: 9999,
    condition: '',
    tradeType: '',
    sort: 'NEW'
  })

  const searchGoods = useCallback(async () => {
    const params = { keyword, page: 1, size: 20, ...filter }
    const res = await searchProduct(keyword,params)
    setProductList(res.data?.records || [])
  }, [keyword, filter])

  const loadCategory = useCallback(async () => {
    const res = await getCategoryTree()
    setCateList(res.data)
  }, [])

  const loadHot = useCallback(async () => {
    const res = await getHotSearch()
    setHotWords(res.data)
  }, [])

  useEffect(() => {
    const run = async () => {
      await loadCategory()
      await loadHot()
    }
    run()
  }, [loadCategory, loadHot])

  useEffect(() => {
    const run = async () => {
      await searchGoods()
    }
    run()
  }, [searchGoods])

  const submitSearch = () => {
    setSearchParams({ keyword: inputKey })
  }

  const changeSort = (val: string) => {
    setFilter({ ...filter, sort: val })
  }

  const confirmFilter = () => {
    setFilterOpen(false)
  }

  return (
    <div style={{ padding: '20px' }}>
      <Space style={{ marginBottom: 20 }}>
        <Input
          value={inputKey}
          onChange={e => setInputKey(e.target.value)}
          placeholder="搜索闲置商品"
          style={{ width: 400 }}
          onPressEnter={submitSearch}
        />
        <Button type="primary" icon={<SearchOutlined />} onClick={submitSearch}>搜索</Button>
        <Button icon={<FilterOutlined />} onClick={() => setFilterOpen(true)}>筛选</Button>
      </Space>
      <Space style={{ marginBottom: 20 }}>
        <span>热门搜索：</span>
        {hotWords.map(word => (
          <Button size="small" key={word} onClick={() => { setInputKey(word); setSearchParams({ keyword: word }) }}>{word}</Button>
        ))}
      </Space>
      <Radio.Group value={filter.sort} onChange={e => changeSort(e.target.value)} style={{ marginBottom: 20 }}>
        <Radio value="NEW">最新发布</Radio>
        <Radio value="PRICE_ASC">价格升序</Radio>
        <Radio value="PRICE_DESC">价格降序</Radio>
      </Radio.Group>
      <Drawer title="筛选条件" open={filterOpen} onClose={() => setFilterOpen(false)} width={320}>
        <div style={{ marginBottom: 20 }}>
          <p>商品分类</p>
          <Select
            style={{ width: '100%' }}
            allowClear
            placeholder="全部分类"
            options={cateList.map(i => ({ label: i.categoryName, value: i.id }))}
            value={filter.categoryId}
            onChange={v => setFilter({ ...filter, categoryId: v })}
          />
        </div>
        <div style={{ marginBottom: 20 }}>
          <p>价格区间</p>
          <Slider
            range
            min={0}
            max={10000}
            value={[filter.priceMin, filter.priceMax]}
            onChange={(v: number[]) => setFilter({ ...filter, priceMin: v[0], priceMax: v[1] })}
          />
          <p>{filter.priceMin} ~ {filter.priceMax} 元</p>
        </div>
        <div style={{ marginBottom: 20 }}>
          <p>成色</p>
          <Radio.Group value={filter.condition} onChange={e => setFilter({ ...filter, condition: e.target.value })}>
            <Radio value="">全部</Radio>
            <Radio value="NEW">全新</Radio>
            <Radio value="LIGHT">轻微使用</Radio>
            <Radio value="NORMAL">正常使用</Radio>
            <Radio value="HEAVY">明显磨损</Radio>
          </Radio.Group>
        </div>
        <div style={{ marginBottom: 20 }}>
          <p>交易方式</p>
          <Radio.Group value={filter.tradeType} onChange={e => setFilter({ ...filter, tradeType: e.target.value })}>
            <Radio value="">全部</Radio>
            <Radio value="FACE">面交</Radio>
            <Radio value="EXPRESS">快递</Radio>
            <Radio value="BOTH">均可</Radio>
          </Radio.Group>
        </div>
        <Button type="primary" block onClick={confirmFilter}>确认筛选</Button>
      </Drawer>
      <Row gutter={[16, 16]}>
        {productList.map(item => (
          <Col span={6} key={item.id}>
            <ProductCard data={item} />
          </Col>
        ))}
      </Row>
    </div>
  )
}

export default Search