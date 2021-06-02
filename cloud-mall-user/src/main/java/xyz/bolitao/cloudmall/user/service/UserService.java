package xyz.bolitao.cloudmall.user.service;


import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.user.model.entity.User;

/**
 * @author boli.tao
 */
public interface UserService {

    void register(String username, String password) throws ImoocMallException;

    User login(String username, String password) throws ImoocMallException;

    void updateInformation(User user) throws ImoocMallException;

    boolean checkAdminRole(User user);
}
