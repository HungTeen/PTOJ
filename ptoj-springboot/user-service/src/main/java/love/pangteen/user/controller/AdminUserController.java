package love.pangteen.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.AdminEditUserDTO;
import love.pangteen.user.pojo.dto.DeleteUserDTO;
import love.pangteen.user.pojo.dto.GenerateUserDTO;
import love.pangteen.user.pojo.vo.GenerateKeyVO;
import love.pangteen.user.pojo.vo.UserRolesVO;
import love.pangteen.user.service.UserInfoService;
import love.pangteen.user.service.UserRoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/15 13:24
 **/
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/get-user-list")
    public CommonResult<IPage<UserRolesVO>> getUserList(@RequestParam(value = "limit", required = false) Integer limit,
                                                        @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                        @RequestParam(value = "onlyAdmin", defaultValue = "false") Boolean onlyAdmin,
                                                        @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(userRoleService.getUserList(limit, currentPage, keyword, onlyAdmin));
    }

    @PutMapping("/edit-user")
    public CommonResult<Void> editUser(@RequestBody @Validated AdminEditUserDTO adminEditUserDto) {
        userInfoService.editUser(adminEditUserDto);
        return CommonResult.success();
    }

    @DeleteMapping("/delete-user")
    public CommonResult<Void> deleteUser(@RequestBody @Validated DeleteUserDTO deleteUserDTO) {
        userInfoService.deleteUser(deleteUserDTO);
        return CommonResult.success();
    }

    @PostMapping("/insert-batch-user")
    public CommonResult<Void> insertBatchUser(@RequestBody Map<String, Object> params) {
//        return adminUserService.insertBatchUser((List<List<String>>) params.get("users"));
        //TODO Excel导入用户。
        return CommonResult.clientError();
    }

    @PostMapping("/generate-user")
    public CommonResult<GenerateKeyVO> generateUser(@RequestBody @Validated GenerateUserDTO generateUserDTO) {
        return CommonResult.success(userInfoService.generateUser(generateUserDTO));
    }

}
