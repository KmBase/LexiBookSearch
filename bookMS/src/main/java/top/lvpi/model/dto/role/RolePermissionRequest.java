package top.lvpi.model.dto.role;

import lombok.Data;

import java.util.List;

/**
 * 角色权限分配请求
 */
@Data
public class RolePermissionRequest {
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 权限路径列表
     */
    private List<String> permissionPaths;
}
