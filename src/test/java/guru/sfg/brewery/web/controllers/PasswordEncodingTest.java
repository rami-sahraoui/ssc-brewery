package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

public class PasswordEncodingTest {
    static final String PASSWORD = "password";

    @Test
    void hashingExample() {
        System.out.println("MD5 Hash: " + DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
        System.out.println("\nRepeated: " + DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));

        String salted = PASSWORD + "ThisIsMySaltValue";
        System.out.println("\nSalted: " + DigestUtils.md5DigestAsHex(salted.getBytes()));
    }

}
