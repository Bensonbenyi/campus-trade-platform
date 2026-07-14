package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.*;
import com.campus.trade.entity.User;
import com.campus.trade.entity.UserAddress;
import com.campus.trade.entity.UserAuth;
import com.campus.trade.mapper.UserAddressMapper;
import com.campus.trade.mapper.UserAuthMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.UserService;
import com.campus.trade.util.JwtUtils;
import com.campus.trade.util.RegexUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserAuthMapper userAuthMapper;
    private final UserAddressMapper userAddressMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private static final String MVP_CODE = "888888";

    @Override
    public void sendCode(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BusinessException("手机号不能为空");
        }
    }

    @Override
    @Transactional
    public UserVO register(UserRegisterDTO dto) {
        if (dto.getCode() == null || !dto.getCode().trim().equals(MVP_CODE)) {
            throw new BusinessException("验证码错误，请使用：" + MVP_CODE);
        }
        Long phoneCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (phoneCount > 0) {
            throw new BusinessException("该手机号已注册");
        }
        if (!RegexUtils.isValidStudentId(dto.getStudentId())) {
            throw new BusinessException("学号格式不正确");
        }
        Long studentCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStudentId, dto.getStudentId()));
        if (studentCount > 0) {
            throw new BusinessException("该学号已注册");
        }
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStudentId(dto.getStudentId());
        user.setRealName(dto.getRealName());
        user.setNickname("User" + dto.getPhone().substring(dto.getPhone().length() - 4));
        user.setRole("USER");
        user.setStatus(0);
        userMapper.insert(user);

        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(user.getId());
        userAuth.setAuthStatus("verified");
        userAuth.setStudentId(dto.getStudentId());
        userAuth.setRealName(dto.getRealName());
        userAuthMapper.insert(userAuth);

        String token = jwtUtils.generateToken(user.getId(), user.getPhone());

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setStudentId(user.getStudentId());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setToken(token);
        return vo;
    }

    @Override
    public UserVO login(UserLoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (user == null) {
            throw new BusinessException("手机号未注册");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getPhone());
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setStudentId(user.getStudentId());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setToken(token);
        return vo;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setStudentId(user.getStudentId());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        return vo;
    }

    @Override
    public UserVO updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        userMapper.updateById(user);
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setStudentId(user.getStudentId());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        return vo;
    }

    @Override
    public List<AddressDTO> getAddressList(Long userId) {
        List<UserAddress> addresses = userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getCreatedAt));
        return addresses.stream().map(this::toAddressDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDTO addAddress(Long userId, AddressDTO dto) {
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefaultAddress(userId);
        }
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setContactName(dto.getContactName());
        address.setContactPhone(dto.getContactPhone());
        address.setCampus(dto.getCampus());
        address.setBuilding(dto.getBuilding());
        address.setRoom(dto.getRoom());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
        userAddressMapper.insert(address);
        return toAddressDTO(address);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long userId, Long addressId, AddressDTO dto) {
        UserAddress address = userAddressMapper.selectOne(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getId, addressId)
                        .eq(UserAddress::getUserId, userId));
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefaultAddress(userId);
        }
        if (dto.getContactName() != null) address.setContactName(dto.getContactName());
        if (dto.getContactPhone() != null) address.setContactPhone(dto.getContactPhone());
        if (dto.getCampus() != null) address.setCampus(dto.getCampus());
        if (dto.getBuilding() != null) address.setBuilding(dto.getBuilding());
        if (dto.getRoom() != null) address.setRoom(dto.getRoom());
        if (dto.getIsDefault() != null) address.setIsDefault(dto.getIsDefault());
        userAddressMapper.updateById(address);
        return toAddressDTO(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectOne(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getId, addressId)
                        .eq(UserAddress::getUserId, userId));
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        userAddressMapper.deleteById(addressId);
    }

    private void clearDefaultAddress(Long userId) {
        UserAddress update = new UserAddress();
        update.setIsDefault(false);
        userAddressMapper.update(update,
                new LambdaUpdateWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDefault, true));
    }

    private AddressDTO toAddressDTO(UserAddress address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setContactName(address.getContactName());
        dto.setContactPhone(address.getContactPhone());
        dto.setCampus(address.getCampus());
        dto.setBuilding(address.getBuilding());
        dto.setRoom(address.getRoom());
        dto.setIsDefault(address.getIsDefault());
        return dto;
    }
    @Override
    public boolean isPhoneRegistered(String phone) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) > 0;
    }
    @Override
    public boolean isStudentIdRegistered(String studentId) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStudentId, studentId)) > 0;
    }
}