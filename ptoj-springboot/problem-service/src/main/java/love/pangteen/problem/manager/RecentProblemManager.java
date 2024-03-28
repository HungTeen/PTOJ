package love.pangteen.problem.manager;

import cn.hutool.core.lang.Pair;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.problem.pojo.vo.RecentUpdatedProblemVO;
import love.pangteen.problem.service.ProblemService;
import love.pangteen.utils.RedisUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/27 21:49
 **/
@Slf4j
@Component
public class RecentProblemManager {

    private static final int PROBLEM_COUNT = 5;
    private static final String KEY = "problem:recent";
    private static final String RANK_LOCK = "lock:problem";

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ProblemService problemService;

    @PostConstruct
    void init(){
        if(! initialized()){
            redisUtils.lock(RANK_LOCK);
            try{
                if(! initialized()){
                    List<Long> userList = problemService.getProblemsByCreateDate();
                    userList.forEach(this::createProblem);
                }
            } catch (Exception ignored){
                log.error(ignored.getMessage());
            } finally {
                redisUtils.unlock(RANK_LOCK);
            }
        }
    }

    public void createProblem(Long pid){
        if(! redisUtils.lContains(KEY, pid)){
            redisUtils.lPushRight(KEY, pid);
        }
    }

    public void removeProblem(Long pid){
        if(! redisUtils.lContains(KEY, pid)){
            redisUtils.lPushRight(KEY, pid);
        }
    }

    public boolean initialized(){
        return redisUtils.hasKey(KEY);
    }

    public List<RecentUpdatedProblemVO> getRecentUpdatedProblemList() {
        return redisUtils.lRange(KEY, 0, PROBLEM_COUNT).stream()
                .map(pid -> {
                    RecentUpdatedProblemVO vo = new RecentUpdatedProblemVO();
                    BeanUtils.copyProperties(problemService.getProblem(Long.valueOf(pid.toString())), vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

}
