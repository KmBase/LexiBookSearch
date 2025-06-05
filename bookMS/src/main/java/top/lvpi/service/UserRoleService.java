package top.lvpi.service;

/**
 * 用户角色关联服务接口
 */
public interface UserRoleService {
    
    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否成功
     */
    boolean assignRoleToUser(Long userId, String roleCode);
    
    /**
     * 获取用户角色
     * 
     * @param userId 用户ID
     * @return 角色编码
     */
    String getUserRole(Long userId);
    
    /**
     * 检查用户是否有访问指定路径的权限
     * 
     * @param userId 用户ID
     * @param path 访问路径
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, String path);
    
    /**
     * 检查用户是否属于指定角色
     * 
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否属于
     */
    boolean isUserInRole(Long userId, String roleCode);
}
