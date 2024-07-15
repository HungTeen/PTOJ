package love.pangteen.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.UserInfo;
import love.pangteen.api.service.IDubboJudgeService;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.exception.StatusFailException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.user.pojo.dto.EditUserInfoDTO;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.vo.*;
import love.pangteen.user.service.UserHomeService;
import love.pangteen.user.service.UserInfoService;
import love.pangteen.user.service.UserRoleService;
import love.pangteen.utils.AccountUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/7/15 15:10
 **/
@Service
public class UserHomeServiceImpl implements UserHomeService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserRoleService userRoleService;

    @DubboReference
    private IDubboJudgeService judgeService;

    @DubboReference
    private IDubboProblemService problemService;

    @Transactional
    @Override
    public UserInfoVO changeUserInfo(EditUserInfoDTO editDTO) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(editDTO, userInfo);
        AccountProfile profile = AccountUtils.getProfile();
        userInfo.setUuid(profile.getUuid());
        if(userInfoService.updateById(userInfo)){
            UserRolesVO userRolesVO = userRoleService.getUserRoles(profile.getUuid(), null);
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
        if(uid == null) {
            uid = StpUtil.getLoginIdAsString();
        }
        UserInfo userInfo = userInfoService.getUserInfo(uid);

        UserHomeVO userHomeVO = new UserHomeVO();
        BeanUtils.copyProperties(userInfo, userHomeVO);
//        List<UserAcProblem> acProblemList = userAcProblemService.getAcProblemList(userInfo.getUuid());
//        List<Long> acPidList = acProblemList.stream().map(UserAcProblem::getPid).distinct().collect(Collectors.toList());
        List<Long> acPidList = judgeService.getUserAcceptList(uid);
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
        if(uid == null) {
            uid = StpUtil.getLoginIdAsString();
        }
        UserInfo userInfo = userInfoService.getUserInfo(uid);

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
}
