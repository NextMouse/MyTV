package net.tv.service.orm;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.config.SystemConfig;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class SqliteHelper {

    interface R {
        String DRIVER_CLASS_NAME = "org.sqlite.JDBC";
    }

    public enum Type {
        TempDB(SystemConfig.TEMP_DB_FILE_PATH, "tempdb.sql");

        private final String name;

        private final String initSqlPath;

        Type(String name, String initSqlPath) {
            this.name = name;
            this.initSqlPath = initSqlPath;
        }

    }

    private final static SqliteHelper sqliteHelper = new SqliteHelper();

    private SqliteHelper() {
        try {
            Class.forName(R.DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static SqliteHelper get() {
        return sqliteHelper;
    }

    private Connection tempDBConnection;

    private void tryConnection() {
        try {
            if (tempDBConnection == null) {
                tempDBConnection = DriverManager.getConnection("jdbc:sqlite:" + Type.TempDB.name);
                tempDBConnection.createStatement()
                        .execute(FileUtil.readString(ResourceUtil.getResource(Type.TempDB.initSqlPath),
                                StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            ConsoleLog.println(e.getMessage());
        }
    }

    public Connection getConnection(Type type) {
        try {
            tryConnection();
            if (Objects.requireNonNull(type) == Type.TempDB) {
                return tempDBConnection;
            }
        } catch (Exception e) {
            ConsoleLog.println(e.getMessage());
        }
        return null;
    }


    public void close() {
        try {
            tempDBConnection.close();
            tempDBConnection = null;
            FileUtil.del(Type.TempDB.name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
