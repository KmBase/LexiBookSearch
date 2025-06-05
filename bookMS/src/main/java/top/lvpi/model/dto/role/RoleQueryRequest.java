package top.lvpi.model.dto.role;

import lombok.Data;

/**
 * 角色查询请求
 */
@Data
public class RoleQueryRequest {
    /**
     * 角色编码
     */
    private String roleCode;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色状态
     */
    private Integer roleStatus;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页数量
     */
    private Long pageSize;
}
