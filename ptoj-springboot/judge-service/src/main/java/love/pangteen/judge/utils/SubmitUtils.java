package love.pangteen.judge.utils;

import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.exception.StatusForbiddenException;
import love.pangteen.judge.pojo.entity.Judge;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 15:39
 **/
@Component
public class SubmitUtils {

    @DubboReference
    private IDubboProblemService problemService;

    public void initCommonSubmission(String problemId, Long gid, Judge judge) throws StatusForbiddenException {
        Problem problem = problemService.getByProblemId(problemId);

        if (problem == null){
            throw new StatusForbiddenException("错误！当前题目已不存在，不可提交！");
        }

        if (problem.getAuth() == 2) {
            throw new StatusForbiddenException("错误！当前题目不可提交！");
        }

//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        // TODO 团队题目。
        if (problem.getIsGroup()) {
            if (gid == null){
                throw new StatusForbiddenException("提交失败，该题目为团队所属，请你前往指定团队内提交！");
            }
//            if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), problem.getGid())) {
//                throw new StatusForbiddenException("对不起，您并非该题目所属的团队内成员，无权进行提交！");
//            }
            judge.setGid(problem.getGid());
        }

        judge.setCpid(0L).setPid(problem.getId()).setDisplayPid(problem.getProblemId());

//        trainingManager.checkAndSyncTrainingRecord(problem.getId(), judge.getSubmitId(), judge.getUid());
    }

}
