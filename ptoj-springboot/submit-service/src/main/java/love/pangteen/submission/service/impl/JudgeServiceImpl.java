package love.pangteen.submission.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.TestJudgeResult;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.exception.StatusFailException;
import love.pangteen.submission.mapper.JudgeMapper;
import love.pangteen.submission.pojo.dto.SubmitIdListDTO;
import love.pangteen.submission.pojo.vo.JudgeCaseVO;
import love.pangteen.submission.pojo.vo.JudgeVO;
import love.pangteen.submission.pojo.vo.TestJudgeVO;
import love.pangteen.submission.service.JudgeService;
import love.pangteen.submission.utils.ValidateUtils;
import love.pangteen.utils.RedisUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:11
 **/
@Service
public class JudgeServiceImpl extends MPJBaseServiceImpl<JudgeMapper, Judge> implements JudgeService {

    @DubboReference
    private IDubboProblemService problemService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ValidateUtils validateUtils;

    /**
     * 通用查询判题记录列表。
     * @param onlyMine 只查看当前用户的提交。
     */
    @Override
    public IPage<JudgeVO> getSubmissionList(Integer limit, Integer currentPage, Boolean onlyMine, String searchPid, Integer searchStatus, String searchUsername, Boolean completeProblemID, Long gid) {
        IPage<JudgeVO> judgeList = lambdaQuery()
                .eq(Judge::getCid, 0)
                .eq(Judge::getCpid, 0)
                .like(StrUtil.isNotEmpty(searchUsername), Judge::getUsername, searchUsername)
                .eq(searchStatus != null, Judge::getStatus, searchStatus)
                .eq(onlyMine, Judge::getUid, StpUtil.getLoginIdAsString())
                .eq(gid != null, Judge::getGid, gid)
                .and(StrUtil.isNotEmpty(searchPid), wrapper -> {
                    if(completeProblemID){
                        wrapper.eq(Judge::getDisplayPid, searchPid);
                    } else {
                        wrapper.like(Judge::getDisplayPid, searchPid);
                    }
                })
                .orderByDesc(Judge::getSubmitTime, Judge::getSubmitId)
                .page(new Page<>(currentPage, limit))
                .convert(judge -> {
                    JudgeVO judgeVO = new JudgeVO();
                    BeanUtils.copyProperties(judge, judgeVO);
                    return judgeVO;
                });
        List<JudgeVO> records = judgeList.getRecords();
        if(CollUtil.isNotEmpty(records)){
            List<Long> pidList = records.stream().map(JudgeVO::getPid).collect(Collectors.toList());
            Map<Long, String> problemTitleMap = problemService.getProblemTitleMap(pidList);
            records.forEach(judgeVO -> judgeVO.setTitle(problemTitleMap.get(judgeVO.getPid())));
        }
        return judgeList;
    }

    @Override
    public TestJudgeVO getTestJudgeResult(String testJudgeKey) {
        TestJudgeResult testJudgeRes = (TestJudgeResult) redisUtils.get(testJudgeKey);
        if (testJudgeRes == null) {
            throw new StatusFailException("查询错误！当前在线调试任务不存在！");
        }
        TestJudgeVO testJudgeVo = new TestJudgeVO();
        testJudgeVo.setStatus(testJudgeRes.getStatus());
        if (JudgeStatus.STATUS_PENDING.getStatus().equals(testJudgeRes.getStatus())) {
            return testJudgeVo;
        }
        testJudgeVo.setUserInput(testJudgeRes.getInput());
        testJudgeVo.setUserOutput(testJudgeRes.getStdout());
        testJudgeVo.setExpectedOutput(testJudgeRes.getExpectedOutput());
        testJudgeVo.setMemory(testJudgeRes.getMemory());
        testJudgeVo.setTime(testJudgeRes.getTime());
        testJudgeVo.setStderr(testJudgeRes.getStderr());
        testJudgeVo.setProblemJudgeMode(testJudgeRes.getProblemJudgeMode());
        redisUtils.del(testJudgeKey);
        return testJudgeVo;
    }

    @Override
    public HashMap<Long, Object> checkCommonJudgeResult(SubmitIdListDTO submitIdListDto) {
        return null;
    }

    @Override
    public HashMap<Long, Object> checkContestJudgeResult(SubmitIdListDTO submitIdListDto) {
        return null;
    }

    @Override
    public JudgeCaseVO getALLCaseResult(Long submitId) {
        return null;
    }
}
