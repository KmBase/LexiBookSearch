import { PageContainer } from '@ant-design/pro-components';
import { Card, theme } from 'antd';
import React from 'react';
import AIAssistant from '@/components/AIAssistant';

/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returns
 */
const InfoCard: React.FC<{
  title: string;
  index: number;
  desc: string;
  href: string;
}> = ({ title, href, index, desc }) => {
  const { useToken } = theme;
  const { token } = useToken();

  return (
    <div
      style={{
        backgroundColor: token.colorBgContainer,
        boxShadow: token.boxShadow,
        borderRadius: '8px',
        fontSize: '14px',
        color: token.colorTextSecondary,
        lineHeight: '22px',
        padding: '16px 19px',
        minWidth: '220px',
        flex: 1,
      }}
    >
      <div
        style={{
          display: 'flex',
          gap: '4px',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            width: 48,
            height: 48,
            lineHeight: '22px',
            backgroundSize: '100%',
            textAlign: 'center',
            padding: '8px 16px 16px 12px',
            color: '#FFF',
            fontWeight: 'bold',
            backgroundImage:
              "url('https://gw.alipayobjects.com/zos/bmw-prod/daaf8d50-8e6d-4251-905d-676a24ddfa12.svg')",
          }}
        >
          {index}
        </div>
        <div
          style={{
            fontSize: '16px',
            color: token.colorText,
            paddingBottom: 8,
          }}
        >
          {title}
        </div>
      </div>
      <div
        style={{
          fontSize: '14px',
          color: token.colorTextSecondary,
          textAlign: 'justify',
          lineHeight: '22px',
          marginBottom: 8,
        }}
      >
        {desc}
      </div>
      <a href={href} target="_blank" rel="noreferrer">
        了解更多 {'>'}
      </a>
    </div>
  );
};

const Welcome: React.FC = () => {
  const { token } = theme.useToken();

  return (
    <PageContainer>
      <Card
        style={{
          borderRadius: 8,
        }}
        bodyStyle={{
          backgroundImage: 'linear-gradient(75deg, #FBFDFF 0%, #F5F7FF 100%)',
        }}
      >
        <div
          style={{
            backgroundPosition: '100% -30%',
            backgroundRepeat: 'no-repeat',
            backgroundSize: '274px auto',
            backgroundImage:
              "url('https://gw.alipayobjects.com/mdn/rms_a9745b/afts/img/A*BuFmQqsB2iAAAAAAAAAAAAAAARQnAQ')",
          }}
        >
          <div
            style={{
              fontSize: '20px',
              color: token.colorTextHeading,
            }}
          >
            欢迎使用律π知识管理系统
          </div>
          <p
            style={{
              fontSize: '14px',
              color: token.colorTextSecondary,
              lineHeight: '22px',
              marginTop: 16,
              marginBottom: 32,
              width: '65%',
            }}
          >
            这是一个智能化的知识管理平台，为了充分利用系统功能，请按照以下步骤操作：导入图书信息 --&gt; 获取OPAC数据 --&gt; 添加标签分类 --&gt; 抽取内容并建立索引 --&gt; 全文检索。如需帮助，可随时点击右下角的AI助手图标获取支持。
          </p>
          <div
            style={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 16,
            }}
          >
            <InfoCard
              index={1}
              href="/book/list"
              title="图书管理"
              desc="第一步：在图书管理页面中导入您的图书信息。支持单本添加或批量导入，系统会自动获取OPAC数据，丰富图书元数据。"
            />
            <InfoCard
              index={2}
              title="标签管理"
              href="/book/tags"
              desc="第二步：为已导入的图书添加标签，进行分类整理。这将帮助您更好地组织图书，也能提升后续检索的精确度。"
            />
            <InfoCard
              index={3}
              title="全文检索"
              href="/book/fullTextSearch"
              desc="第三步：完成以上步骤后，系统会自动建立索引。您可以使用全文检索功能，支持多字段组合查询、同义词扩展等高级特性。"
            />
          </div>
        </div>
      </Card>
      <AIAssistant />
    </PageContainer>
  );
};

export default Welcome;
