package com.ticod.timesailerbackend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionUtilTest {

    @Test
    @DisplayName("Salt 생성 메서드 테스트")
    void generateSalt() {
        String salt = EncryptionUtil.generateSalt();
        assertNotNull(salt);
    }

    @Test
    @DisplayName("SHA-256 암호화 테스트")
    void generateHash() throws NoSuchAlgorithmException {
        String password = "1234";
        String salt = EncryptionUtil.generateSalt();
        String encryptedPassword = EncryptionUtil.generateHash(password, salt);
        assertNotNull(encryptedPassword);
        assertEquals(encryptedPassword, EncryptionUtil.generateHash("1234", salt));
        assertNotEquals(encryptedPassword, EncryptionUtil.generateHash("12345", salt));
    }
}