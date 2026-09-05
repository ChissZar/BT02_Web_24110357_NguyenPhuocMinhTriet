package vn.iotstar.service.impl;

import vn.iotstar.dao.IUserDao;
import vn.iotstar.dao.impl.UserDao;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;

public class UserServiceImpl implements IUserService {
    @Override
    public User findById(int id) {
        return userDao.findById(id);
    }

    @Override
    public User updateProfile(int id, String fullName, String phone, String avatar) {
        fullName = fullName == null ? "" : fullName.trim();
        phone = phone == null ? "" : phone.trim();
        if (fullName.isEmpty() || fullName.length() > 100)
            throw new IllegalArgumentException("Họ tên phải có từ 1 đến 100 ký tự");
        if (phone.length() > 20 || (!phone.isEmpty() && !phone.matches("[+0-9() .-]{3,20}")))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (tối đa 20 ký tự)");
        return userDao.updateProfile(id, fullName, phone, avatar);
    }

    private final IUserDao userDao = new UserDao();

    @Override
    public User login(String username, String password) {
        return new vn.iotstar.service.AccountService().login(username, password);
    }

    @Override
    public User get(String username) {
        return userDao.get(username);
    }
}
