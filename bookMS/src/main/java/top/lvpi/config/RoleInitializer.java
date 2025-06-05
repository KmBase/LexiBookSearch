package top.lvpi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.lvpi.model.entity.Role;
import top.lvpi.service.RoleService;

import java.util.Arrays;
import java.util.List;

/**
 * 角色初始化配置类
 * 系统启动时自动初始化默认角色
 */
@Slf4j
@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化默认角色...");
        
        // 初始化管理员角色
        initAdminRole();
        
        // 初始化普通用户角色
        initUserRole();
        
        log.info("默认角色初始化完成");
    }
    
    /**
     * 初始化管理员角色
     */
    private void initAdminRole() {
        String roleCode = "admin";
        Role adminRole = roleService.getRoleByCode(roleCode);
        
        // 如果管理员角色不存在，则创建
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setRoleCode(roleCode);
            adminRole.setRoleName("管理员");
            adminRole.setRoleDescription("系统管理员，拥有所有权限");
            adminRole.setRoleStatus(0); // 启用状态
            
            Long roleId = roleService.createRole(adminRole);
            log.info("创建管理员角色成功，角色ID: {}", roleId);
            
            // 为管理员角色分配权限
            List<String> adminPermissions = Arrays.asList(
                "/api/user/*",      // 用户管理相关权限
                "/api/book/*",      // 图书管理相关权限
                "/api/file/*",      // 文件管理相关权限
                "/api/role/*",      // 角色管理相关权限
                "/api/system/*",    // 系统管理相关权限
                "/api/stats/*"      // 统计数据相关权限
            );
            
            roleService.assignPermissions(roleId, adminPermissions);
            log.info("为管理员角色分配权限成功");
        } else {
            log.info("管理员角色已存在，角色ID: {}", adminRole.getRoleId());
        }
    }
    
    /**
     * 初始化普通用户角色
     */
    private void initUserRole() {
        String roleCode = "user";
        Role userRole = roleService.getRoleByCode(roleCode);
        
        // 如果普通用户角色不存在，则创建
        if (userRole == null) {
            userRole = new Role();
            userRole.setRoleCode(roleCode);
            userRole.setRoleName("普通用户");
            userRole.setRoleDescription("普通用户，拥有基本权限");
            userRole.setRoleStatus(0); // 启用状态
            
            Long roleId = roleService.createRole(userRole);
            log.info("创建普通用户角色成功，角色ID: {}", roleId);
            
            // 为普通用户角色分配权限
            List<String> userPermissions = Arrays.asList(
                "/api/book/list",       // 图书列表查询
                "/api/book/detail/*",   // 图书详情查询
                "/api/file/view/*",     // 文件查看
                "/api/user/current",    // 当前用户信息
                "/api/user/change-password" // 修改密码
            );
            
            roleService.assignPermissions(roleId, userPermissions);
            log.info("为普通用户角色分配权限成功");
        } else {
            log.info("普通用户角色已存在，角色ID: {}", userRole.getRoleId());
        }
    }
}
