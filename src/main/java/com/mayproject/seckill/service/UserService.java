package com.mayproject.seckill.service;

import com.mayproject.seckill.dao.UserDao;
import com.mayproject.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
        return userDao.getById(id);

    }

    @Transactional
    public boolean tx() {
        User u1 = new User();
        User u2 = new User();
        u1.setId(1);
        u1.setName("ddw");
        u2.setId(2);
        u2.setName("wzz");
        userDao.insert(u2);
        userDao.insert(u1);
        return true;
    }
}
