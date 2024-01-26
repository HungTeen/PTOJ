package love.pangteen.utils;

import love.pangteen.api.enums.OJAccessType;
import love.pangteen.config.properties.OJSwitchProperties;
import love.pangteen.exception.OJAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 11:26
 **/
@Component
public class AccessUtils {

    @Resource
    private OJSwitchProperties ojSwitchProperties;

    public void validateAccess(OJAccessType hojAccessEnum) throws OJAccessException {
        switch (hojAccessEnum) {
            case PUBLIC_DISCUSSION:
                if (!ojSwitchProperties.getOpenPublicDiscussion()) {
                    throw new OJAccessException("网站当前未开启公开讨论区的功能，不可访问！");
                }
                break;
            case GROUP_DISCUSSION:
                if (!ojSwitchProperties.getOpenGroupDiscussion()) {
                    throw new OJAccessException("网站当前未开启团队讨论区的功能，不可访问！");
                }
                break;
            case CONTEST_COMMENT:
                if (!ojSwitchProperties.getOpenContestComment()) {
                    throw new OJAccessException("网站当前未开启比赛评论区的功能，不可访问！");
                }
                break;
            case PUBLIC_JUDGE:
                if (!ojSwitchProperties.getOpenPublicJudge()) {
                    throw new OJAccessException("网站当前未开启题目评测的功能，禁止提交或调试！");
                }
                break;
            case GROUP_JUDGE:
                if (!ojSwitchProperties.getOpenGroupJudge()) {
                    throw new OJAccessException("网站当前未开启团队内题目评测的功能，禁止提交或调试！");
                }
                break;
            case CONTEST_JUDGE:
                if (!ojSwitchProperties.getOpenContestJudge()) {
                    throw new OJAccessException("网站当前未开启比赛题目评测的功能，禁止提交或调试！");
                }
                break;
            case HIDE_NON_CONTEST_SUBMISSION_CODE:
                if (ojSwitchProperties.getHideNonContestSubmissionCode()) {
                    throw new OJAccessException("网站当前开启了隐藏非比赛提交代码不显示的功能！");
                }
        }
    }
}
