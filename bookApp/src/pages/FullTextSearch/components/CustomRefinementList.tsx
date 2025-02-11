import { useRefinementList } from 'react-instantsearch';
import { Input, Space, Tag, Checkbox } from 'antd';
import { SearchOutlined } from '@ant-design/icons';

function CustomRefinementList(props: any) {
  const {
    items,
    refine,
    searchForItems,
  } = useRefinementList(props);


  return (
    <div style={{minHeight:210}}>
      <div style={{marginBottom:10, fontSize:16,fontWeight:600}}>
        <Input
          placeholder="在结果中检索书籍名称"
          prefix={<SearchOutlined style={{fontSize:16}} />}
          type="search"
          autoComplete="off"
          autoCorrect="off"
          autoCapitalize="off"
          spellCheck={false}
          maxLength={512}
          onChange={(event) => searchForItems(event.currentTarget.value)}
        />
      </div>
      <div style={{ fontSize:16, overflowY: 'auto', overflowX: 'hidden'}}>
        <ul style={{padding:0}}>
          <Space direction="vertical" style={{ width: '100%' }}>
            {items.map((item) => (
              <li key={item.label} style={{display:'flex',alignItems:'center'}}>
                <Checkbox
                  checked={item.isRefined}
                  onChange={() => refine(item.value)}
                  style={{ borderRadius: '10%' }}
                >
                  <span style={{ fontSize: 14, lineHeight: 0.5 }}>{item.label}</span>
                  <Tag color="default" style={{ marginLeft: 8 }}>
                    {item.count}
                  </Tag>
                </Checkbox>
              </li>
            ))}
          </Space>
        </ul>
      </div>
    </div>
  );
}

export default CustomRefinementList;