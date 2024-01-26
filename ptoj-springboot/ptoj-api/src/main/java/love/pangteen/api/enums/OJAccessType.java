package love.pangteen.api.enums;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 11:20
 **/
public enum OJAccessType {
    /**
     * 公共讨论区
     */
    PUBLIC_DISCUSSION,

    /**
     * 团队讨论区
     */
    GROUP_DISCUSSION,

    /**
     * 比赛评论
     */
    CONTEST_COMMENT,

    /**
     * 公共评测
     */
    PUBLIC_JUDGE,

    /**
     * 团队评测
     */
    GROUP_JUDGE,

    /**
     * 比赛评测
     */
    CONTEST_JUDGE,

    /**
     * 隐藏非比赛提交详情的代码
     */
    HIDE_NON_CONTEST_SUBMISSION_CODE
}
