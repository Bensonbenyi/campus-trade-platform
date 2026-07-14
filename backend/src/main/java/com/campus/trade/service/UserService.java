package com.campus.trade.service;

import com.campus.trade.dto.*;

import java.util.List;

public interface UserService {

    void sendCode(String phone);

    UserVO register(UserRegisterDTO dto);

    UserVO login(UserLoginDTO dto);

    UserVO getUserInfo(Long userId);

    UserVO updateUserInfo(Long userId, UserUpdateDTO dto);

    List<AddressDTO> getAddressList(Long userId);

    AddressDTO addAddress(Long userId, AddressDTO dto);

    AddressDTO updateAddress(Long userId, Long addressId, AddressDTO dto);

    void deleteAddress(Long userId, Long addressId);
    boolean isPhoneRegistered(String phone);
    boolean isStudentIdRegistered(String studentId);
}
