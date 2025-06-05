package top.lvpi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.lvpi.model.entity.Role;
import top.lvpi.model.dto.role.RoleQueryRequest;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 创建角色
     * 
     * @param role 角色信息
     * @return 角色ID
     */
    Long createRole(Role role);
    
    /**
     * 更新角色
     * 
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(Role role);
    
    /**
     * 删除角色
     * 
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long roleId);
    
    /**
     * 根据角色编码获取角色
     * 
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Role getRoleByCode(String roleCode);
    
    /**
     * 分页查询角色列表
     * 
     * @param roleQueryRequest 查询条件
     * @return 角色分页列表
     */
    IPage<Role> listRoles(RoleQueryRequest roleQueryRequest);
    
    /**
     * 获取所有角色列表
     * 
     * @return 角色列表
     */
    List<Role> getAllRoles();
    
    /**
     * 为角色分配权限
     * 
     * @param roleId 角色ID
     * @param permissionPaths 权限路径列表
     * @return 是否成功
     */
    boolean assignPermissions(Long roleId, List<String> permissionPaths);
    
    /**
     * 获取角色的权限路径列表
     * 
     * @param roleId 角色ID
     * @return 权限路径列表
     */
    List<String> getRolePermissions(Long roleId);
    
    /**
     * 检查角色是否有访问指定路径的权限
     * 
     * @param roleId 角色ID
     * @param path 访问路径
     * @return 是否有权限
     */
    boolean hasPermission(Long roleId, String path);
}
