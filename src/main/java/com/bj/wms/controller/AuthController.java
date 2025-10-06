/*
 * @Author: Marlon
 * @Date: 2025-10-06 15:53:29
 * @Description: 
 */
package com.bj.wms.controller;

import com.bj.wms.service.NumericCaptchaService;
import com.bj.wms.service.UserService;
import com.bj.wms.service.DevTokenService;
import com.bj.wms.entity.User;
import com.bj.wms.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final NumericCaptchaService numericCaptchaService;
    private final UserService userService;
    private final DevTokenService devTokenService;

    public AuthController(NumericCaptchaService numericCaptchaService, UserService userService, DevTokenService devTokenService) {
        this.numericCaptchaService = numericCaptchaService;
        this.userService = userService;
        this.devTokenService = devTokenService;
    }

    /** 初始化数字验证码（图片+token） */
    @GetMapping("/captcha/init")
    public ResponseEntity<Map<String, Object>> initNumericCaptcha() {
        NumericCaptchaService.InitResult result = numericCaptchaService.init();
        return ResponseUtil.success(result);
    }

    /** 校验数字验证码 */
    @PostMapping("/captcha/verify")
    public ResponseEntity<Map<String, Object>> verifyNumericCaptcha(
            @RequestParam String token,
            @RequestParam String code) {
        boolean pass = numericCaptchaService.verify(token, code);
        if (pass) return ResponseUtil.successMsg("ok");
        return ResponseUtil.error("captcha verify failed");
    }

    /** 登录：先校验验证码，再校验用户名密码，成功返回 token */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req) {
        boolean pass = numericCaptchaService.verify(req.getToken(), req.getCode());
        if (!pass) {
            return ResponseUtil.error("captcha verify failed");
        }
        return userService.getUserByUsername(req.getUsername())
                .filter(u -> userService.validatePassword(req.getPassword(), u.getPassword()))
                .map(u -> ResponseUtil.success(Map.of("token", devTokenService.issueToken(u.getId()))))
                .orElseGet(() -> ResponseUtil.error("username or password incorrect"));
    }

    public static class LoginRequest {
        private String username;
        private String password;
        private String code;  // 数字验证码
        private String token; // 验证码 token
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}


