package top.lvpi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.lvpi.model.entity.RolePermission;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    
    /**
     * 根据角色ID查询权限路径列表
     *
     * @param roleId 角色ID
     * @return 权限路径列表
     */
    @Select("SELECT permission_path FROM lp_role_permission WHERE role_id = #{roleId} AND is_deleted = 0")
    List<String> selectPermissionPathsByRoleId(@Param("roleId") Long roleId);
}
