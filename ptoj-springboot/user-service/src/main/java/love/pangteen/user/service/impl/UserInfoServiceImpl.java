package love.pangteen.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.enums.OJRole;
import love.pangteen.exception.StatusFailException;
import love.pangteen.user.mapper.UserInfoMapper;
import love.pangteen.user.pojo.dto.AdminEditUserDTO;
import love.pangteen.user.pojo.dto.CheckUsernameOrEmailDTO;
import love.pangteen.user.pojo.dto.DeleteUserDTO;
import love.pangteen.user.pojo.dto.GenerateUserDTO;
import love.pangteen.user.pojo.entity.UserInfo;
import love.pangteen.user.pojo.vo.CheckUsernameOrEmailVO;
import love.pangteen.user.pojo.vo.GenerateKeyVO;
import love.pangteen.user.service.UserInfoService;
import love.pangteen.user.service.UserRoleService;
import love.pangteen.user.utils.FileUtils;
import love.pangteen.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 22:30
 **/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private FileUtils fileUtils;

    @Resource
    private UserRoleService userRoleService;

    @Override
    public UserInfo getUserInfoByUid(String uid) {
        return getById(uid);
    }

    @Override
    public UserInfo getUserInfoByName(String username) {
        return lambdaQuery().eq(UserInfo::getUsername, username).one();
    }

    @Transactional
    @Override
    public void editUser(AdminEditUserDTO userDTO) {
        // 更新UserInfo表。
        UserInfo userInfo = getById(userDTO.getUid());
        userDTO.setPassword(SaSecureUtil.md5(userDTO.getPassword()));
        boolean updateUserInfo = lambdaUpdate().eq(UserInfo::getUuid, userDTO.getUid())
                .set(StrUtil.isNotEmpty(userDTO.getUsername()), UserInfo::getUsername, userDTO.getUsername())
                .set(StrUtil.isNotEmpty(userDTO.getRealname()), UserInfo::getRealname, userDTO.getRealname())
                .set(StrUtil.isNotEmpty(userDTO.getEmail()), UserInfo::getEmail, userDTO.getEmail())
                .set(userDTO.getSetNewPwd(), UserInfo::getPassword, userDTO.getPassword())
                .set(StrUtil.isNotEmpty(userDTO.getTitleName()), UserInfo::getTitleName, userDTO.getTitleName())
                .set(StrUtil.isNotEmpty(userDTO.getTitleColor()), UserInfo::getTitleColor, userDTO.getTitleColor())
                .set(userDTO.getStatus() != null, UserInfo::getStatus, userDTO.getStatus())
                .set(userDTO.getType() != null, UserInfo::getRoleId, userDTO.getType())
                .update();

        // 密码更改需要重新登录
        if (updateUserInfo && userDTO.getSetNewPwd()) {
            StpUtil.logout(userDTO.getUid());
        }

        // 角色变更需要重新授权
        if (userDTO.getType() != null && userInfo.getRoleId().intValue() != userDTO.getType()) {
//            userRoleEntityService.deleteCache(uid, false);
        }
//
//        if (changeUserRole) {
//            // 获取当前登录的用户
//            AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//            String title = "权限变更通知(Authority Change Notice)";
//            String content = userRoleEntityService.getAuthChangeContent(oldType, type);
//            adminNoticeManager.addSingleNoticeToUser(userRolesVo.getUid(), uid, title, content, "Sys");
//        }
    }

    @Transactional
    @Override
    public GenerateKeyVO generateUser(GenerateUserDTO userDTO) {
        HashMap<String, Object> userInfoMap = new HashMap<>();
        List<UserInfo> userInfoList = new ArrayList<>();
        for (int num = userDTO.getNumberFrom(); num <= userDTO.getNumberTo(); num++) {
            String uuid = IdUtil.simpleUUID();
            String password = RandomUtil.randomString(userDTO.getPasswordLength());
            String username = userDTO.getPrefix() + num + userDTO.getSuffix();
            UserInfo userInfo = new UserInfo()
                    .setUuid(uuid)
                    .setUsername(username)
                    .setPassword(SaSecureUtil.md5(password))
                    .setRoleId(OJRole.DEFAULT_USER.getRoleId())
                    ;
            userInfoList.add(userInfo);
            userInfoMap.put(username, password);
        }
        boolean result = saveBatch(userInfoList);
        if(result){
            String key = IdUtil.simpleUUID();
            redisUtils.hmset(key, userInfoMap, 1800); // 存储半小时
            // 异步同步系统通知
//            List<String> uidList = userInfoList.stream().map(UserInfo::getUuid).collect(Collectors.toList());
//            adminNoticeManager.syncNoticeToNewRegisterBatchUser(uidList);
            return new GenerateKeyVO(key);
        } else {
            throw new StatusFailException("生成指定用户失败！注意查看组合生成的用户名是否已有存在的！");
        }
    }

    @Override
    public void generateUserExcel(String key, HttpServletResponse response) throws IOException {
        fileUtils.generateUserExcel(key, response);
    }

    @Override
    public void deleteUser(DeleteUserDTO deleteUserDTO) {
        boolean isOk = removeByIds(deleteUserDTO.getIds());
        if (!isOk) {
            throw new StatusFailException("删除失败！");
        }
    }

    @Transactional
    @Override
    public CheckUsernameOrEmailVO checkUsernameOrEmail(CheckUsernameOrEmailDTO checkDTO) {
        String username = checkDTO.getUsername();

        CheckUsernameOrEmailVO vo = new CheckUsernameOrEmailVO();
        if (Validator.isEmail(checkDTO.getEmail())) {
            vo.setEmail(lambdaQuery().eq(UserInfo::getEmail, checkDTO.getEmail()).oneOpt().isEmpty());
        }
        vo.setUsername(lambdaQuery().eq(UserInfo::getUsername, username).oneOpt().isEmpty());
        return vo;
    }

}
