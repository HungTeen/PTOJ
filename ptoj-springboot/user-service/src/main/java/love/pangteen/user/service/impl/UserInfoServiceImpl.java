package love.pangteen.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.enums.OJRole;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.service.IDubboJudgeService;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.exception.StatusFailException;
import love.pangteen.user.mapper.UserInfoMapper;
import love.pangteen.user.pojo.dto.*;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.entity.UserAcProblem;
import love.pangteen.user.pojo.entity.UserInfo;
import love.pangteen.user.pojo.vo.*;
import love.pangteen.user.service.UserAcProblemService;
import love.pangteen.user.service.UserInfoService;
import love.pangteen.user.service.UserRoleService;
import love.pangteen.user.utils.ExcelUtils;
import love.pangteen.utils.RedisUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    private ExcelUtils excelUtils;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserAcProblemService userAcProblemService;

    @DubboReference
    private IDubboJudgeService judgeService;

    @DubboReference
    private IDubboProblemService problemService;

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
        excelUtils.generateUserExcel(key, response);
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

    @Transactional
    @Override
    public UserInfoVO changeUserInfo(EditUserInfoDTO editDTO) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(editDTO, userInfo);
        userInfo.setUuid(editDTO.getUid());
        if(updateById(userInfo)){
            UserRolesVO userRolesVO = userRoleService.getUserRoles(editDTO.getUid(), null);
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(userRolesVO, userInfoVO);
            userInfoVO.setRoleList(userRolesVO.getRoles().stream().map(Role::getRole).collect(Collectors.toList()));
            return userInfoVO;
        } else {
            throw new StatusFailException("个人信息修改失败！");
        }
    }

    @Override
    public UserHomeVO getUserHomeInfo(String uid, String username) {
        UserInfo userInfo = getUserInfo(uid, username);

        UserHomeVO userHomeVO = new UserHomeVO();
        BeanUtils.copyProperties(userInfo, userHomeVO);
        List<UserAcProblem> acProblemList = userAcProblemService.getAcProblemList(userInfo.getUuid());
        List<Long> acPidList = acProblemList.stream().map(UserAcProblem::getPid).distinct().collect(Collectors.toList());
        List<Problem> problems = problemService.getProblems(acPidList);
        Map<Integer, List<UserHomeProblemVO>> acMap = problems.stream().map(problem -> {
            UserHomeProblemVO problemVO = new UserHomeProblemVO();
            BeanUtils.copyProperties(problem, problemVO);
            return problemVO;
        }).collect(Collectors.groupingBy(UserHomeProblemVO::getDifficulty));
        userHomeVO.setSolvedList(problems.stream().map(Problem::getProblemId).collect(Collectors.toList()));
        userHomeVO.setSolvedGroupByDifficulty(acMap);

        userHomeVO.setTotal(judgeService.getUserTotalSubmitCount(userInfo.getUuid()));
        return userHomeVO;
    }

    @Override
    public UserCalendarHeatmapVO getUserCalendarHeatmap(String uid, String username) {
        UserInfo userInfo = getUserInfo(uid, username);

        UserCalendarHeatmapVO userCalendarHeatmapVo = new UserCalendarHeatmapVO();
        userCalendarHeatmapVo.setEndDate(DateUtil.format(new Date(), "yyyy-MM-dd"));

        List<Judge> lastYearUserJudgeList = judgeService.getLastYearUserJudgeList(userInfo.getUuid());
        HashMap<String, Integer> tmpRecordMap = new HashMap<>();
        for (Judge judge : lastYearUserJudgeList) {
            Date submitTime = judge.getSubmitTime();
            String dateStr = DateUtil.format(submitTime, "yyyy-MM-dd");
            tmpRecordMap.merge(dateStr, 1, Integer::sum);
        }
        List<HashMap<String, Object>> dataList = new ArrayList<>();
        for (Map.Entry<String, Integer> record : tmpRecordMap.entrySet()) {
            HashMap<String, Object> tmp = new HashMap<>(2);
            tmp.put("date", record.getKey());
            tmp.put("count", record.getValue());
            dataList.add(tmp);
        }
        userCalendarHeatmapVo.setDataList(dataList);
        return userCalendarHeatmapVo;
    }

    private UserInfo getUserInfo(String uid, String username) {
        // 如果没有uid和username，默认查询当前登录用户的。
        if (StrUtil.isEmpty(uid) && StrUtil.isEmpty(username)) {
            if (StpUtil.isLogin()) {
                uid = StpUtil.getLoginIdAsString();
            } else {
                throw new StatusFailException("请求参数错误：uid和username不能都为空！");
            }
        }

        UserInfo userInfo = lambdaQuery()
                .eq(StrUtil.isNotEmpty(uid), UserInfo::getUuid, uid)
                .eq(StrUtil.isNotEmpty(username), UserInfo::getUsername, username)
                .one();
        if (userInfo == null) {
            throw new StatusFailException("用户不存在！");
        }
        return userInfo;
    }

}
