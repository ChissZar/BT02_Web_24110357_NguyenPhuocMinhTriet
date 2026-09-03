package vn.iotstar.service;

import vn.iotstar.entity.User;

public interface IUserService {
    User login(String username, String password);

    User get(String username);
}
