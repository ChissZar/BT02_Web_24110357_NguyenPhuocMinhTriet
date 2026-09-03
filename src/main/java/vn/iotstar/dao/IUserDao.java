package vn.iotstar.dao;

import vn.iotstar.entity.User;

public interface IUserDao {
    User get(String username);
}
