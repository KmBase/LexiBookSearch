package top.lvpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lvpi.common.ErrorCode;
import top.lvpi.common.exception.BusinessException;
import top.lvpi.mapper.UserMapper;
import top.lvpi.model.entity.Role;
import top.lvpi.model.entity.User;
import top.lvpi.service.RoleService;
import top.lvpi.service.UserRoleService;

/**
 * 用户角色关联服务实现类
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoleToUser(Long userId, String roleCode) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 检查角色是否存在
        Role role = roleService.getRoleByCode(roleCode);
        if (role == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "角色不存在");
        }

        // 更新用户角色
        user.setUserRole(roleCode);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public String getUserRole(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        return user.getUserRole();
    }

    @Override
    public boolean hasPermission(Long userId, String path) {
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 获取用户角色
        String roleCode = user.getUserRole();
        if (roleCode == null) {
            return false;
        }

        // 获取角色信息
        Role role = roleService.getRoleByCode(roleCode);
        if (role == null) {
            return false;
        }

        // 检查角色是否有访问权限
        return roleService.hasPermission(role.getRoleId(), path);
    }

    @Override
    public boolean isUserInRole(Long userId, String roleCode) {
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 检查用户角色
        return roleCode.equals(user.getUserRole());
    }
}
