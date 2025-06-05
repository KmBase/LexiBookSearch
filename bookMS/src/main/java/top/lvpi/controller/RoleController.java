package top.lvpi.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lvpi.common.BaseResponse;
import top.lvpi.common.ErrorCode;
import top.lvpi.common.BusinessException;
import top.lvpi.model.dto.role.RoleQueryRequest;
import top.lvpi.model.entity.Role;
import top.lvpi.service.RoleService;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/role")
@Tag(name = "角色管理", description = "角色管理相关接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "创建角色", description = "创建新角色")
    @PostMapping("/create")
    @SaCheckRole("admin")
    public BaseResponse<Long> createRole(@RequestBody Role role) {
        if (role == null) {
            return BaseResponse.error(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        try {
            Long roleId = roleService.createRole(role);
            return BaseResponse.success(roleId);
        } catch (BusinessException e) {
            return BaseResponse.error(e.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "更新角色", description = "更新角色信息")
    @PutMapping("/update")
    @SaCheckRole("admin")
    public BaseResponse<Boolean> updateRole(@RequestBody Role role) {
        if (role == null || role.getRoleId() == null) {
            return BaseResponse.error(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        try {
            boolean result = roleService.updateRole(role);
            return BaseResponse.success(result);
        } catch (BusinessException e) {
            return BaseResponse.error(e.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "删除角色", description = "根据ID删除角色")
    @DeleteMapping("/delete/{id}")
    @SaCheckRole("admin")
    public BaseResponse<Boolean> deleteRole(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return BaseResponse.error(ErrorCode.PARAMS_ERROR, "ID不合法");
        }
        try {
            boolean result = roleService.deleteRole(id);
            return BaseResponse.success(result);
        } catch (BusinessException e) {
            return BaseResponse.error(e.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
    @GetMapping("/get/{id}")
    @SaCheckRole("admin")
    public BaseResponse<Role> getRole(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return BaseResponse.error(ErrorCode.PARAMS_ERROR, "ID不合法");
        }
        Role role = roleService.getById(id);
        if (role == null) {
            return BaseResponse.error(ErrorCode.NOT_FOUND_ERROR, "角色不存在");
        }
        return BaseResponse.success(role);
    }

    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    @GetMapping("/list")
    @SaCheckRole("admin")
    public BaseResponse<IPage<Role>> listRoles(RoleQueryRequest roleQueryRequest) {
        if (roleQueryRequest == null) {
            return BaseResponse.error(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        try {
            IPage<Role> rolePage = roleService.listRoles(roleQueryRequest);
            return BaseResponse.success(rolePage);
        } catch (BusinessException e) {
            return BaseResponse.error(e.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "获取所有角色", description = "获取所有启用的角色")
    @GetMapping("/all")
    @SaCheckLogin
    public BaseResponse<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return BaseResponse.success(roles);
    }

    @Operation(summary = "分配权限", description = "为角色分配权限")
    @PostMapping("/assign-permissions/{roleId}")
    @SaCheckRole("admin")
    public BaseResponse<Boolean> assignPermissions(@PathVariable("roleId") Long roleId, 
                                                 @RequestBody List<String> permissionPaths) {
        if (roleId == null || roleId <= 0) {
            return BaseResponse.error(ErrorCode.PARAMS_ERROR, "角色ID不合法");
        }
        try {
            boolean result = roleService.assignPermissions(roleId, permissionPaths);
            return BaseResponse.success(result);
        } catch (BusinessException e) {
            return BaseResponse.error(e.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "获取角色权限", description = "获取角色的权限路径列表")
    @GetMapping("/permissions/{roleId}")
    @SaCheckRole("admin")
    public BaseResponse<List<String>> getRolePermissions(@PathVariable("roleId") Long roleId) {
        if (roleId == null || roleId <= 0) {
            return BaseResponse.error(ErrorCode.PARAMS_ERROR, "角色ID不合法");
        }
        List<String> permissions = roleService.getRolePermissions(roleId);
        return BaseResponse.success(permissions);
    }
}
