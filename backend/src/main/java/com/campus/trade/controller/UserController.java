package com.campus.trade.controller;

import com.campus.trade.common.BusinessException;
import com.campus.trade.common.Result;
import com.campus.trade.dto.*;
import com.campus.trade.service.UserService;
import com.campus.trade.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户模块", description = "注册、登录、个人信息、地址管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "发送短信验证码")
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestParam String phone) {
        userService.sendCode(phone);
        return Result.success("验证码已发送（MVP固定888888）");
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserRegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = getUserId(token);
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/info")
    public Result<UserVO> updateUserInfo(@RequestHeader("Authorization") String token,
                                          @RequestBody UserUpdateDTO dto) {
        Long userId = getUserId(token);
        return Result.success(userService.updateUserInfo(userId, dto));
    }

    @Operation(summary = "新增收货地址")
    @PostMapping("/address")
    public Result<AddressDTO> addAddress(@RequestHeader("Authorization") String token,
                                          @RequestBody AddressDTO dto) {
        Long userId = getUserId(token);
        return Result.success(userService.addAddress(userId, dto));
    }

    @Operation(summary = "获取地址列表")
    @GetMapping("/address")
    public Result<List<AddressDTO>> getAddressList(@RequestHeader("Authorization") String token) {
        Long userId = getUserId(token);
        return Result.success(userService.getAddressList(userId));
    }

    @Operation(summary = "修改地址")
    @PutMapping("/address/{id}")
    public Result<AddressDTO> updateAddress(@RequestHeader("Authorization") String token,
                                             @PathVariable Long id,
                                             @RequestBody AddressDTO dto) {
        Long userId = getUserId(token);
        return Result.success(userService.updateAddress(userId, id, dto));
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/address/{id}")
    public Result<String> deleteAddress(@RequestHeader("Authorization") String token,
                                         @PathVariable Long id) {
        Long userId = getUserId(token);
        userService.deleteAddress(userId, id);
        return Result.success("删除成功");
    }

    private Long getUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(401, "未登录");
        }
        try {
            return jwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new BusinessException(401, "token无效或已过期");
        }
    }
}
