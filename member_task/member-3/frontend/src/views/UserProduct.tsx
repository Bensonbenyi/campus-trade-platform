import { useState, useEffect, useCallback } from 'react'
import { Tabs, Table, Button, message, Popconfirm, Image } from 'antd'
import { useNavigate } from 'react-router-dom'
import { getMyProductList, changeProductStatus, delProduct } from '../api/product'

interface MyProductItem {
  id: number
  title: string
  price: number
  status: string
  imgList: string[]
  createTime: string
}

const UserProduct = () => {
  const navigate = useNavigate()
  const [activeTab, setActiveTab] = useState('ON_SALE')
  const [tableData, setTableData] = useState<MyProductItem[]>([])
  const tabList = [
    { key: 'ON_SALE', label: '在售' },
    { key: 'OFF_SALE', label: '已下架' },
    { key: 'AUDITING', label: '审核中' }
  ]

  const loadList = useCallback(async (status: string) => {
    const res = await getMyProductList({ page: 1, size: 20, status })
    setTableData(res.data?.records || [])
  }, [])

  useEffect(() => {
    const run = async () => {
      await loadList(activeTab)
    }
    run()
  }, [activeTab, loadList])

  const tabChange = (key: string) => {
    setActiveTab(key)
  }

  const toggleStatus = async (row: MyProductItem) => {
    const targetStatus = row.status === 'ON_SALE' ? 'OFF_SALE' : 'ON_SALE'
    await changeProductStatus(row.id, targetStatus)
    message.success('状态修改成功')
    loadList(activeTab)
  }

  const deleteItem = async (id: number) => {
    await delProduct(id)
    message.success('删除成功')
    loadList(activeTab)
  }

  const goEdit = (id: number) => navigate(`/product/edit/${id}`)

  const columns = [
    {
      title: '商品图',
      render: (record: MyProductItem) => <Image width={80} src={record.imgList[0]} preview={false} />
    },
    { title: '标题', dataIndex: 'title' },
    { title: '售价', dataIndex: 'price', render: (v: number) => `¥${v}` },
    { title: '发布时间', dataIndex: 'createTime' },
    {
      title: '操作',
      render: (record: MyProductItem) => (
        <>
          {record.status !== 'AUDITING' && (
            <Button size="small" onClick={() => toggleStatus(record)} style={{ marginRight: 6 }}>
              {record.status === 'ON_SALE' ? '下架' : '上架'}
            </Button>
          )}
          <Button size="small" onClick={() => goEdit(record.id)} style={{ marginRight: 6 }}>编辑</Button>
          <Popconfirm title="确认删除？" onConfirm={() => deleteItem(record.id)}>
            <Button danger size="small">删除</Button>
          </Popconfirm>
        </>
      )
    }
  ]

  return (
    <div style={{ width: '90%', margin: '30px auto' }}>
      <h2>我的发布商品</h2>
      <Tabs activeKey={activeTab} onChange={tabChange} items={tabList} />
      <Table dataSource={tableData} rowKey="id" columns={columns} />
    </div>
  )
}

export default UserProduct