package com.main.controller;

import com.main.entity.vo.ResponseVO;
import com.main.service.AuthService;
import com.main.service.UsageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usage")
public class UsageController extends ABaseController {

    private final UsageService usageService;
    private final AuthService authService;

    public UsageController(UsageService usageService, AuthService authService) {
        this.usageService = usageService;
        this.authService = authService;
    }

    /** 当日用量；serverTimeMs 和 resetAtMs 分别为服务器当前时间及下次额度重置的 Unix 毫秒时间戳。 */
    @GetMapping
    public ResponseVO usage(HttpServletRequest request) {
        Long userId = authService.resolveToken(request.getHeader("X-Auth-Token"));
        String visitorUid = request.getHeader("X-Visitor-Id");
        var info = usageService.usageInfo(userId, visitorUid);
        info.put("closed", serviceClosed);
        return getSuccessResponseVO(info);
    }

    /** 服务总开关（与解析/下载联动） */
    @org.springframework.beans.factory.annotation.Value("${service.closed:false}")
    private boolean serviceClosed;
}
