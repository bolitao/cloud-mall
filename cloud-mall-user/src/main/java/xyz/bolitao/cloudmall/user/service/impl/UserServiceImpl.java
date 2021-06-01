package xyz.bolitao.cloudmall.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;
import xyz.bolitao.cloudmall.user.model.dao.UserMapper;
import xyz.bolitao.cloudmall.user.model.entity.User;
import xyz.bolitao.cloudmall.user.service.UserService;


/**
 * @author boli.tao
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String username, String password) throws ImoocMallException {
        User user = userMapper.selectByName(username);
        if (user != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_ALREADY_EXISTED);
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(BCrypt.hashpw(password));
        int count = userMapper.insertSelective(newUser);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DB_INSERT_FAILED);
        }
    }

    @Override
    public User login(String username, String password) throws ImoocMallException {
        User user = userMapper.selectLogin(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws ImoocMallException {
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user) {
        return user.getRole().equals(2);
    }
}
