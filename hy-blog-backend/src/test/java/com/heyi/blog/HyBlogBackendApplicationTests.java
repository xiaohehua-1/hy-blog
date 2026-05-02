package com.heyi.blog;

import cn.hutool.crypto.digest.BCrypt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HyBlogBackendApplicationTests {

    @Test
    void generatePassword() {
        // 这行代码会生成一个标准的 BCrypt 密文
        String password = BCrypt.hashpw("123456");
        System.out.println("====== 你的新密码哈希如下 ======");
        System.out.println(password);
        System.out.println("================================");
    }
}