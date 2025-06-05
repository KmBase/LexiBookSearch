package top.lvpi.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 角色权限关联实体类
 */
@Data
@TableName("lp_role_permission")
public class RolePermission {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限路径
     */
    private String permissionPath;

    /**
     * 权限描述
     */
    private String permissionDescription;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifiedTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDeleted;
}
