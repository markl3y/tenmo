package com.techelevator.tenmo.security.dao;

import com.techelevator.tenmo.security.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    User findUserById(int id);
}
