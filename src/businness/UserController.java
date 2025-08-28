package businness;

import core.Helper;
import dao.UserDao;
import entity.User;


public class UserController {

    private final UserDao userDao = new UserDao();

    public User findBYLogin(String mail, String password) {
        if(!Helper.isEmailValid(mail)) return null;
        return this.userDao.findByLogin(mail, password);
    }

}
