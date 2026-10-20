package com.hollow.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码工具类。
 *
 * 数据库里只存 BCrypt 哈希，不存明文，也不存 MD5。
 * 用到的 BCrypt 来自 jbcrypt 依赖，具体算法不用自己写。
 */
public class Yoa_PasswordUtil {

    /**
     * 把明文密码加密成哈希，注册时用。
     *
     * 同一个密码每次加密出来的结果都不一样，这是正常的，
     * 因为里面加了随机盐，所以不能用两个哈希字符串直接比较。
     */
    public static String hash(String rawPassword) {
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        return hash;
    }

    /**
     * 校验密码对不对，登录时用。
     * 第一个参数是用户输入的明文，第二个是数据库里存的哈希。
     */
    public static boolean check(String rawPassword, String hash) {
        boolean same = BCrypt.checkpw(rawPassword, hash);
        return same;
    }
}
