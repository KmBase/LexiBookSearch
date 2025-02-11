import React from 'react';
import { Prompts } from '@ant-design/x';
import type { PromptProps } from '@ant-design/x';
import styles from './PromptPanel.module.less';

interface PromptPanelProps {
  onSelect: (content: string) => void;
}

const defaultPrompts: PromptProps[] = [
  {
    key: 'legal-consult',
    label: '法律咨询',
    description: '我需要咨询一个法律问题',
    children: [
      {
        key: 'contract',
        label: '合同问题',
        description: '帮我解答一个合同相关的法律问题',
      },
      {
        key: 'rights',
        label: '权益保护',
        description: '我想了解如何保护自己的合法权益',
      },
    ],
  },
  {
    key: 'case-analysis',
    label: '案例分析',
    description: '帮我分析一个法律案例',
    children: [
      {
        key: 'civil',
        label: '民事案例',
        description: '分析一个民事纠纷案例',
      },
      {
        key: 'commercial',
        label: '商业案例',
        description: '分析一个商业纠纷案例',
      },
    ],
  },
  {
    key: 'doc-draft',
    label: '文书起草',
    description: '帮我起草一份法律文书',
    children: [
      {
        key: 'contract-draft',
        label: '合同起草',
        description: '帮我起草一份合同文本',
      },
      {
        key: 'complaint',
        label: '诉讼文书',
        description: '帮我起草一份诉讼文书',
      },
    ],
  },
  {
    key: 'law-explain',
    label: '法条解释',
    description: '解释某个法律条文的含义',
    children: [
      {
        key: 'civil-law',
        label: '民法典',
        description: '解释民法典中的相关条款',
      },
      {
        key: 'company-law',
        label: '公司法',
        description: '解释公司法中的相关条款',
      },
    ],
  },
];

const PromptPanel: React.FC<PromptPanelProps> = ({ onSelect }) => {
  const handleSelect = (info: { data: PromptProps }) => {
    const content = info.data.description;
    if (typeof content === 'string') {
      onSelect(content);
    }
  };

  return (
    <div className={styles.promptPanel}>
      <h3 className={styles.title}>常用提示语</h3>
      <Prompts
        items={defaultPrompts}
        onItemClick={handleSelect}
        vertical
        wrap
        styles={{
          list: { width: '100%' },
          item: { marginBottom: 8 },
        }}
        classNames={{
          item: styles.promptItem,
        }}
      />
    </div>
  );
};

export default PromptPanel; 