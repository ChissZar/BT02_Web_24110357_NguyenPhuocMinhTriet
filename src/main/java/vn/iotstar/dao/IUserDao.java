package vn.iotstar.dao;

import vn.iotstar.entity.User;

public interface IUserDao {
    User get(String username);
    User findById(int id);
    User updateProfile(int id, String fullName, String phone, String avatar);
}
