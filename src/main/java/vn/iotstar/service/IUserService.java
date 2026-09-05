package vn.iotstar.service;

import vn.iotstar.entity.User;

public interface IUserService {
    User login(String username, String password);

    User get(String username);
    User findById(int id);
    User updateProfile(int id, String fullName, String phone, String avatar);
}
