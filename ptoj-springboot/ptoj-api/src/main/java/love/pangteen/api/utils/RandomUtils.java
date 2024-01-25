package love.pangteen.api.utils;

import java.util.Random;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 9:41
 **/
public class RandomUtils {

    public static Random get(){
        long seed = System.currentTimeMillis();
        return new Random(seed);
    }

}
