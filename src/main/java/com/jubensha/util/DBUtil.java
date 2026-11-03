package com.jubensha.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC 连接工具：默认读取 classpath 下的 db.properties。
 * 如需在本机调试时切换数据库，可通过 JVM 参数指定外部配置文件：
 * -Djubensha.config=C:/path/to/db.properties，避免修改打包后的 war。
 */
public final class DBUtil {

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        String external = System.getProperty("jubensha.config");
        try (InputStream in = external != null
                ? new FileInputStream(external)
                : DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("找不到数据库配置文件 db.properties");
            }
            props.load(in);
            Class.forName(props.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
        URL = props.getProperty("jdbc.url");
        USERNAME = props.getProperty("jdbc.username");
        PASSWORD = props.getProperty("jdbc.password");
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
