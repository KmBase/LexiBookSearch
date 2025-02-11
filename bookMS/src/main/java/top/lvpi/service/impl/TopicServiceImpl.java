package top.lvpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.lvpi.mapper.TopicMapper;
import top.lvpi.mapper.BookTopicMapper;
import top.lvpi.model.dto.topic.TopicImportDTO;
import top.lvpi.model.dto.topic.TopicPathDTO;
import top.lvpi.model.dto.topic.TopicTreeDTO;
import top.lvpi.model.entity.Topic;
import top.lvpi.model.entity.BookTopic;
import top.lvpi.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

    @Autowired
    private BookTopicMapper bookTopicMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Topic topic) {
        // 如果是不带层级的标签（parentId为null），则验证level值
        if (topic.getParentId() == null) {
            // 如果level为null，设置默认值100
            if (topic.getLevel() == null) {
                topic.setLevel(100);
            }
        }
        
        boolean result = super.save(topic);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Topic topic) {
        // 如果是不带层级的标签（parentId为null），则验证level值
        if (topic.getParentId() == null) {
            // 如果level为null，设置默认值100
            if (topic.getLevel() == null) {
                topic.setLevel(100);
            }
        }
        
        boolean result = super.updateById(topic);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        boolean result = super.removeById(id);

        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long importTopics(TopicImportDTO topicImportDTO) {
        if (topicImportDTO == null || !StringUtils.hasText(topicImportDTO.getTitle())) {
            throw new IllegalArgumentException("主题名称不能为空");
        }
        // 清理主题名称
        topicImportDTO.setTitle(cleanString(topicImportDTO.getTitle()));
        return importTopicWithParent(topicImportDTO, null, 0);
    }

    /**
     * 递归导入主题及其子主题
     * @param importDTO 导入数据
     * @param parentId 父主题ID
     * @param level 当前层级
     * @return 导入的主题ID
     */
    private Long importTopicWithParent(TopicImportDTO importDTO, Long parentId, Integer level) {
        // 1. 创建当前主题
        Topic topic = new Topic();
        topic.setName(importDTO.getTitle());
        topic.setParentId(parentId);
        topic.setLevel(level);
        this.save(topic);

        // 2. 递归处理子主题
        if (importDTO.getChildren() != null && !importDTO.getChildren().isEmpty()) {
            for (TopicImportDTO child : importDTO.getChildren()) {
                // 清理子主题名称
                child.setTitle(cleanString(child.getTitle()));
                importTopicWithParent(child, topic.getId(), level + 1);
            }
        }

        return topic.getId();
    }

    /**
     * 清理字符串中的<EOL>和多余空格
     * @param str 原始字符串
     * @return 清理后的字符串
     */
    private String cleanString(String str) {
        if (str == null) {
            return null;
        }
        // 1. 移除<EOL>
        str = str.replace("<EOL>", "");
        // 2. 移除首尾空格
        str = str.trim();
        // 3. 将连续的空格替换为单个空格
        str = str.replaceAll("\\s+", " ");
        return str;
    }

    @Override
    public List<Topic> getTopicsByBookId(Long bookId) {
        // 1. 获取书籍关联的所有主题ID
        LambdaQueryWrapper<BookTopic> bookTopicWrapper = new LambdaQueryWrapper<>();
        bookTopicWrapper.eq(BookTopic::getBookId, bookId)
                .eq(BookTopic::getIsDeleted, 0);
        List<BookTopic> bookTopics = bookTopicMapper.selectList(bookTopicWrapper);
        
        if (bookTopics.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 获取这些主题的详细信息
        Set<Long> topicIds = bookTopics.stream()
                .map(BookTopic::getTopicId)
                .collect(Collectors.toSet());

        // 3. 查询这些主题及其所有父主题
        Set<Long> allTopicIds = new HashSet<>(topicIds);
        List<Topic> topics = this.listByIds(topicIds);
        
        // 递归获取所有父主题
        for (Topic topic : topics) {
            Long parentId = topic.getParentId();
            while (parentId != null) {
                allTopicIds.add(parentId);
                Topic parentTopic = this.getById(parentId);
                if (parentTopic != null) {
                    parentId = parentTopic.getParentId();
                } else {
                    break;
                }
            }
        }
        List<Topic> topicsResult = this.listByIds(allTopicIds);
        //剔除parent_id、level为null的
        topicsResult.removeIf(topic -> topic.getParentId() == null || topic.getLevel() == null);
        // 4. 返回所有相关主题
        return topicsResult;
    }

    @Override
    public List<TopicTreeDTO> getTopicTreeByBookId(Long bookId) {
        // 1. 获取所有相关主题
        List<Topic> allTopics = getTopicsByBookId(bookId);
        if (allTopics.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 过滤掉parentId为空且level大于等于100的主题
        allTopics = allTopics.stream()
                .filter(topic -> topic.getParentId() != null && topic.getLevel() != null && topic.getLevel() < 100)
                .collect(Collectors.toList());

        // 3. 转换为TopicTreeDTO
        List<TopicTreeDTO> allTopicTrees = allTopics.stream()
                .map(this::convertToTreeDTO)
                .collect(Collectors.toList());

        // 4. 构建主题ID到DTO的映射，方便查找
        Map<Long, TopicTreeDTO> topicMap = allTopicTrees.stream()
                .collect(Collectors.toMap(TopicTreeDTO::getId, dto -> dto));

        // 5. 构建树形结构
        List<TopicTreeDTO> rootTopics = new ArrayList<>();
        for (TopicTreeDTO topic : allTopicTrees) {
            Long parentId = topic.getParentId();
            if (parentId == null || parentId == 0) {
                // 这是一个根节点
                rootTopics.add(topic);
            } else {
                // 将当前节点添加到父节点的children列表中
                TopicTreeDTO parentTopic = topicMap.get(parentId);
                if (parentTopic != null) {
                    if (parentTopic.getChildren() == null) {
                        parentTopic.setChildren(new ArrayList<>());
                    }
                    parentTopic.getChildren().add(topic);
                }
            }
        }

        return rootTopics;
    }

    private TopicTreeDTO convertToTreeDTO(Topic topic) {
        TopicTreeDTO dto = new TopicTreeDTO();
        BeanUtils.copyProperties(topic, dto);
        return dto;
    }

    @Override
    public List<Topic> getTopicWithParents(Long topicId) {
        List<Topic> result = new ArrayList<>();
        
        // 获取当前主题
        Topic currentTopic = this.getById(topicId);
        if (currentTopic == null || currentTopic.getParentId() == null || currentTopic.getLevel() == null) {
            return result;
        }

        // 使用LinkedList在头部插入，这样最终结果会按层级从高到低排序
        LinkedList<Topic> orderedResult = new LinkedList<>();
        orderedResult.add(currentTopic);

        // 递归获取父主题
        Long parentId = currentTopic.getParentId();
        while (parentId != null) {
            Topic parentTopic = this.getById(parentId);
            if (parentTopic != null && parentTopic.getParentId() != null && parentTopic.getLevel() != null) {
                orderedResult.addFirst(parentTopic); // 在头部插入父主题
                parentId = parentTopic.getParentId();
            } else {
                break;
            }
        }

        return new ArrayList<>(orderedResult);
    }

    @Override
    public List<TopicPathDTO> getBookTopicPaths(Long bookId) {
        // 1. 获取书籍关联的所有主题ID
        LambdaQueryWrapper<BookTopic> bookTopicWrapper = new LambdaQueryWrapper<>();
        bookTopicWrapper.eq(BookTopic::getBookId, bookId)
                .eq(BookTopic::getIsDeleted, 0);
        List<BookTopic> bookTopics = bookTopicMapper.selectList(bookTopicWrapper);
        
        if (bookTopics.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 获取每个主题的完整路径, 
        List<TopicPathDTO> result = new ArrayList<>();
        for (BookTopic bookTopic : bookTopics) {
            List<Topic> topicPath = getTopicWithParents(bookTopic.getTopicId());
            // 过滤掉parentId为null、level大于等于100的主题
            topicPath = topicPath.stream()
                    .filter(topic -> topic.getParentId() != null && topic.getLevel() != null && topic.getLevel() < 100)
                    .collect(Collectors.toList());

            if (!topicPath.isEmpty()) {
                TopicPathDTO pathDTO = new TopicPathDTO();
                pathDTO.setTopicId(bookTopic.getTopicId());
                // 使用 > 连接主题名称
                String path = topicPath.stream()
                        .map(Topic::getName)
                        .collect(Collectors.joining(" > "));
                pathDTO.setPath(path);
                // 主题级别数量
                pathDTO.setLevelSize(topicPath.size() - 1);
                result.add(pathDTO);
            }
        }

        return result;
    }

    @Override
    public List<Topic> getBookTopicTags(Long bookId) {
        // 1. 获取书籍关联的所有主题ID
        LambdaQueryWrapper<BookTopic> bookTopicWrapper = new LambdaQueryWrapper<>();
        bookTopicWrapper.eq(BookTopic::getBookId, bookId)
                .eq(BookTopic::getIsDeleted, 0);
        List<BookTopic> bookTopics = bookTopicMapper.selectList(bookTopicWrapper);

        if (bookTopics.isEmpty()) {
            return new ArrayList<>();
        }
        // 2. 获取每个主题的name
        List<Topic> result = new ArrayList<>();
        for (BookTopic bookTopic : bookTopics) {
            Topic topic = this.getById(bookTopic.getTopicId());
            if (topic != null && topic.getParentId() == null && topic.getLevel() == null) {
                result.add(topic);
            }
        }
        return result;
    }

} 