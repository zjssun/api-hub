package com.main.controller;

import com.main.entity.vo.ResponseVO;
import com.main.exception.BusinessException;
import com.main.service.AuthService;
import com.main.service.UsageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends ABaseController {

    private final AuthService authService;
    private final UsageService usageService;

    public AuthController(AuthService authService, UsageService usageService) {
        this.authService = authService;
        this.usageService = usageService;
    }

    /** 发送注册验证码 */
    @PostMapping("/register-code")
    public ResponseVO registerCode(@RequestBody Map<String, String> body) {
        authService.sendRegisterCode(body.get("email"), body.get("nickname"));
        return getSuccessResponseVO(null);
    }

    /** 注册：email + nickname + password + code → token + user */
    @PostMapping("/register")
    public ResponseVO register(@RequestBody Map<String, String> body) {
        return getSuccessResponseVO(authService.register(
                body.get("email"), body.get("nickname"), body.get("password"), body.get("code")));
    }

    /** 登录：email + password → token + user */
    @PostMapping("/login")
    public ResponseVO login(@RequestBody Map<String, String> body) {
        return getSuccessResponseVO(authService.login(body.get("email"), body.get("password")));
    }

    /** 发送重置密码验证码 */
    @PostMapping("/forgot-code")
    public ResponseVO forgotCode(@RequestBody Map<String, String> body) {
        authService.sendResetCode(body.get("email"));
        return getSuccessResponseVO(null);
    }

    /** 重置密码：email + code + password */
    @PostMapping("/reset-password")
    public ResponseVO resetPassword(@RequestBody Map<String, String> body) {
        authService.resetPassword(body.get("email"), body.get("code"), body.get("password"));
        return getSuccessResponseVO(null);
    }

    /** 当前用户信息（含当日用量） */
    @GetMapping("/me")
    public ResponseVO me(HttpServletRequest request) {
        Long userId = authService.resolveToken(request.getHeader("X-Auth-Token"));
        if (userId == null) {
            throw new BusinessException("未登录");
        }
        var user = authService.findUser(userId);
        if (user == null) {
            throw new BusinessException("账号不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("email", user.getEmail());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("plan", StringUtils.hasText(user.getPlan()) ? user.getPlan() : "free");
        data.put("usage", usageService.usageInfo(userId, request.getHeader("X-Visitor-Id")));
        return getSuccessResponseVO(data);
    }

    @PostMapping("/nickname")
    public ResponseVO updateNickname(@RequestHeader(value = "X-Auth-Token", required = false) String token,
                                     @RequestBody Map<String, String> body) {
        return getSuccessResponseVO(authService.updateNickname(token, body.get("nickname")));
    }

    /** 登出：吊销 token */
    @PostMapping("/logout")
    public ResponseVO logout(HttpServletRequest request) {
        authService.logout(request.getHeader("X-Auth-Token"));
        return getSuccessResponseVO(null);
    }
}
