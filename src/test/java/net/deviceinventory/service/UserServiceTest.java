package net.deviceinventory.service;

import net.deviceinventory.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
class UserServiceTest {

    @Autowired
    UserDao userDao;

    @Test
    void signIn() {
    }

    @Test
    void signOut() {
    }

    @Test
    void leave() {
    }
}