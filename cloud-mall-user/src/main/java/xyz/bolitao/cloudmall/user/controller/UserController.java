package xyz.bolitao.cloudmall.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;
import xyz.bolitao.cloudmall.common.common.Constant;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;
import xyz.bolitao.cloudmall.user.model.entity.User;
import xyz.bolitao.cloudmall.user.service.UserService;

import javax.servlet.http.HttpSession;

/**
 * @author boli.tao
 */
@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping("/register")
    public ApiRestResponse<String> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws ImoocMallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USERNAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }

        userService.register(username, password);
        return ApiRestResponse.success();
    }

    @ResponseBody
    @PostMapping("/login")
    public ApiRestResponse<User> login(@RequestParam("userName") String userName,
                                       @RequestParam("password") String password,
                                       @ApiIgnore HttpSession session) throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USERNAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }

        User loginUser = userService.login(userName, password);
        session.setAttribute(Constant.IMOOC_MALL_USER, loginUser);
        loginUser.setPassword(null);
        return ApiRestResponse.success(loginUser);
    }

    @ResponseBody
    @PostMapping("/user/update")
    public ApiRestResponse<String> updateUserInfo(HttpSession session, @RequestParam("signature") String signature) throws ImoocMallException {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }

        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    @ResponseBody
    @PostMapping("/user/logout")
    public ApiRestResponse<String> logout(HttpSession session) {
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    @ResponseBody
    @PostMapping("/adminLogin")
    public ApiRestResponse<User> adminLogin(@RequestParam("userName") String userName,
                                            @RequestParam("password") String password,
                                            HttpSession session) throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USERNAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }

        User loginUser = userService.login(userName, password);

        if (userService.checkAdminRole(loginUser)) {
            session.setAttribute(Constant.IMOOC_MALL_USER, loginUser);
            loginUser.setPassword(null);
            return ApiRestResponse.success(loginUser);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.WRONG_ROLE);
        }
    }

    @ResponseBody
    @PostMapping("/checkAdminRole")
    public Boolean checkAdminRole(@RequestBody User user) {
        return userService.checkAdminRole(user);
    }

    @GetMapping("/getUser")
    @ResponseBody
    public User getUser(HttpSession session) {
        return (User) session.getAttribute(Constant.IMOOC_MALL_USER);
    }
}
