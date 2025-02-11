package top.lvpi.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lp_book_section")
public class BookSection {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;

    private Integer pageNum;

    @TableField(value = "section_text")
    private String content;

    //创建时间
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", name = "createTime", type = "Date")
    private Date createTime;
    //最后更新时间
    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "最后更新时间", name = "modifiedTime", type = "Date")
    private Date modifiedTime;
    //是否删除
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @Schema(description = "是否删除", name = "isDelete", type = "Integer")
    private Integer isDeleted;

} 