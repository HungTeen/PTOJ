package love.pangteen.judge.manager;

import love.pangteen.api.pojo.dto.ToJudgeDTO;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.judge.service.JudgeService;
import love.pangteen.result.CommonResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 11:11
 **/
@Component
public class JudgeManager {

    @Resource
    private JudgeService judgeService;

    public CommonResult<Void> submitProblemJudge(ToJudgeDTO toJudgeDTO) {
//        if (!Objects.equals(toJudgeDTO.getToken(), judgeToken)) {
//            return CommonResult.errorResponse("对不起！您使用的判题服务调用凭证不正确！访问受限！", ResultStatus.ACCESS_DENIED);
//        }

        Judge judge = toJudgeDTO.getJudge();

        if (judge == null || judge.getSubmitId() == null || judge.getUid() == null || judge.getPid() == null) {
            return CommonResult.clientError("调用参数错误！请检查您的调用参数！");
        }

        judgeService.judge(judge);

        return CommonResult.success("判题机评测完成！");
    }

}
