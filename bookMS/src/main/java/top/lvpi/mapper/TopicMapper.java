package top.lvpi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.lvpi.model.entity.Topic;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
} 