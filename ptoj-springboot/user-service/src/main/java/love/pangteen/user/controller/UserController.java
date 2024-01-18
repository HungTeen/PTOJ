package love.pangteen.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/15 13:24
 **/
@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping("/login")
    public void login(){
        System.out.println("666");
    }
}
