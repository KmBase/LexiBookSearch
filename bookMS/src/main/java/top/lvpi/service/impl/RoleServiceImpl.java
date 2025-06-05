package top.lvpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lvpi.common.ErrorCode;
import top.lvpi.common.BusinessException;
import top.lvpi.mapper.RoleMapper;
import top.lvpi.mapper.RolePermissionMapper;
import top.lvpi.model.dto.role.RoleQueryRequest;
import top.lvpi.model.entity.Role;
import top.lvpi.model.entity.RolePermission;
import top.lvpi.service.RoleService;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Long createRole(Role role) {
        // 检查角色编码是否已存在
        Role existRole = getRoleByCode(role.getRoleCode());
        if (existRole != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色编码已存在");
        }
        
        // 设置默认状态
        if (role.getRoleStatus() == null) {
            role.setRoleStatus(0); // 默认启用
        }
        
        // 保存角色
        boolean result = save(role);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建角色失败");
        }
        
        return role.getRoleId();
    }

    @Override
    public boolean updateRole(Role role) {
        // 检查角色是否存在
        Role existRole = getById(role.getRoleId());
        if (existRole == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "角色不存在");
        }
        
        // 如果修改了角色编码，检查新编码是否已存在
        if (!StringUtils.equals(existRole.getRoleCode(), role.getRoleCode())) {
            Role codeExistRole = getRoleByCode(role.getRoleCode());
            if (codeExistRole != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色编码已存在");
            }
        }
        
        // 更新角色
        return updateById(role);
    }

    @Override
    public boolean deleteRole(Long roleId) {
        // 检查角色是否存在
        Role existRole = getById(roleId);
        if (existRole == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "角色不存在");
        }
        
        // 删除角色
        return removeById(roleId);
    }

    @Override
    public Role getRoleByCode(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            return null;
        }
        
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleCode, roleCode);
        return getOne(queryWrapper);
    }

    @Override
    public IPage<Role> listRoles(RoleQueryRequest roleQueryRequest) {
        // 创建查询条件
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.isNotBlank(roleQueryRequest.getRoleCode())) {
            queryWrapper.like(Role::getRoleCode, roleQueryRequest.getRoleCode());
        }
        if (StringUtils.isNotBlank(roleQueryRequest.getRoleName())) {
            queryWrapper.like(Role::getRoleName, roleQueryRequest.getRoleName());
        }
        if (roleQueryRequest.getRoleStatus() != null) {
            queryWrapper.eq(Role::getRoleStatus, roleQueryRequest.getRoleStatus());
        }
        
        // 处理分页参数
        long current = roleQueryRequest.getCurrent() != null ? roleQueryRequest.getCurrent() : 1L;
        long size = roleQueryRequest.getPageSize() != null ? roleQueryRequest.getPageSize() : 10L;
        
        // 创建分页对象
        Page<Role> page = new Page<>(current, size);
        
        // 执行查询
        return page(page, queryWrapper);
    }

    @Override
    public List<Role> getAllRoles() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleStatus, 0); // 只查询启用的角色
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<String> permissionPaths) {
        // 检查角色是否存在
        Role existRole = getById(roleId);
        if (existRole == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "角色不存在");
        }
        
        // 先删除该角色的所有权限
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(queryWrapper);
        
        // 如果权限列表为空，直接返回成功
        if (permissionPaths == null || permissionPaths.isEmpty()) {
            return true;
        }
        
        // 批量添加权限
        List<RolePermission> rolePermissions = new ArrayList<>();
        for (String permissionPath : permissionPaths) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionPath(permissionPath);
            rolePermission.setPermissionDescription("路径权限：" + permissionPath);
            rolePermissions.add(rolePermission);
        }
        
        // 批量保存
        for (RolePermission rolePermission : rolePermissions) {
            rolePermissionMapper.insert(rolePermission);
        }
        
        return true;
    }

    @Override
    public List<String> getRolePermissions(Long roleId) {
        return rolePermissionMapper.selectPermissionPathsByRoleId(roleId);
    }

    @Override
    public boolean hasPermission(Long roleId, String path) {
        // 获取角色的所有权限路径
        List<String> permissionPaths = getRolePermissions(roleId);
        
        // 检查是否包含指定路径
        for (String permissionPath : permissionPaths) {
            // 精确匹配
            if (permissionPath.equals(path)) {
                return true;
            }
            
            // 通配符匹配，如 /api/user/* 可以匹配 /api/user/list
            if (permissionPath.endsWith("/*")) {
                String prefix = permissionPath.substring(0, permissionPath.length() - 1);
                if (path.startsWith(prefix)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
