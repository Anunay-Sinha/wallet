package com.deeaae.wallet.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

public class UserWalletManagerTest {

    private UserWalletManager userWalletManager;

    @BeforeEach
    void initTestCase() {
        userWalletManager = new UserWalletManagerImpl();
    }

    @Test
    void testBalance() {
        String accountId = "randomUserId";
        Assertions.assertEquals(userWalletManager.getBalance(accountId).intValue(),10);
    }

    @Test
    void testCredit() {
        String accountId = "randomUserId";
        Assertions.assertEquals(userWalletManager.getBalance(accountId).intValue(),10);
    }

    @Test
    void testDebit() {
        String accountId = "randomUserId";
        Assertions.assertEquals(userWalletManager.getBalance(accountId).intValue(),10);
    }

    @Test
    void testRefund() {
        String accountId = "randomUserId";
        Assertions.assertEquals(userWalletManager.getBalance(accountId).intValue(),10);
    }

    @Test
    void testRollback() {
        String accountId = "randomUserId";
        Assertions.assertEquals(userWalletManager.getBalance(accountId).intValue(),10);
    }
}
