package com.hollow.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类。
 *
 * 连接信息写在 db.properties 里，类第一次被用到时读一次，之后一直复用。
 * 全项目的数据库连接都从这里拿，不要在别处自己写 DriverManager。
 */
public class Yoa_DBUtil {

    // 从 db.properties 读出来的三项配置
    private static String url;
    private static String user;
    private static String password;

    // 静态代码块：类第一次被加载时执行一次，把配置读进来
    static {
        try {
            // 从 classpath 下读 db.properties
            InputStream in = Yoa_DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            Properties props = new Properties();
            props.load(in);

            url = props.getProperty("jdbc.url");
            user = props.getProperty("jdbc.user");
            password = props.getProperty("jdbc.password");

            // 加载 MySQL 驱动，MySQL 8 的驱动类名是 com.mysql.cj.jdbc.Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (Exception e) {
            // 读不到配置，多半是忘了复制 db.properties，这里直接报出来方便排查
            throw new RuntimeException("读取 db.properties 失败，请确认该文件存在且内容正确", e);
        }
    }

    /**
     * 获取一个数据库连接。
     *
     * 调用方要自己关闭，推荐写在 try-with-resources 里：
     *
     *   try (Connection conn = Yoa_DBUtil.getConnection()) {
     *       ...
     *   }
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
}
