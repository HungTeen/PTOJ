package love.pangteen.user.controller;

import love.pangteen.user.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/19 9:36
 **/
@RestController
@RequestMapping
public class CommonController {

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/file/generate-user-excel")
    public void generateUserExcel(@RequestParam("key") String key, HttpServletResponse response) throws IOException {
        userInfoService.generateUserExcel(key, response);
    }

}
