package top.lvpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.lvpi.mapper.BookSectionMapper;
import top.lvpi.model.entity.BookSection;
import top.lvpi.service.BookSectionService;
import top.lvpi.service.BookSectionEsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookSectionServiceImpl extends ServiceImpl<BookSectionMapper, BookSection> implements BookSectionService {

    private final BookSectionEsService bookSectionEsService;
    
    @Override
    public IPage<BookSection> page(Integer current, Integer size, Long bookId,Integer pageNum, String content) {
        LambdaQueryWrapper<BookSection> wrapper = new LambdaQueryWrapper<>();
        
        // 添加图书ID查询条件
        if (bookId != null) {
            wrapper.eq(BookSection::getBookId, bookId);
        }
        // 添加图书页码查询条件
        if (pageNum != null) {
            wrapper.eq(BookSection::getPageNum, pageNum);
        }
        
        // 添加内容模糊查询条件
        if (StringUtils.isNotBlank(content)) {
            wrapper.like(BookSection::getContent, content);
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(BookSection::getCreateTime);
        
        return page(new Page<>(current, size), wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithEs(BookSection bookSection) {
        // 1. 更新MySQL数据
        boolean success = updateById(bookSection);
        
        if (success) {
            // 2. 同步更新ES索引
            bookSectionEsService.importById(bookSection.getId().toString());
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWithEs(Long bookSectionId) {
        // 1. 删除MySQL数据
        boolean success = removeById(bookSectionId);

        if (success) {
            // 2. 同步删除ES索引
            bookSectionEsService.deleteById(String.valueOf(bookSectionId));
        }

        return success;
    }
} 