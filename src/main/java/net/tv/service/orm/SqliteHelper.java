package net.tv.service.orm;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import net.tv.view.config.SystemConfig;
import net.tv.view.arm.ConsoleLog;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteHelper {

    interface R {
        String DRIVER_CLASS_NAME = "org.sqlite.JDBC";
    }

    public enum Type {
        TempDB(SystemConfig.TEMP_DB_FILE_PATH, "tempdb.sql"), TvDB(SystemConfig.DB_FILE_PATH, "tvdb.sql");

        private String name;

        private String initSqlPath;

        Type(String name, String initSqlPath) {
            this.name = name;
            this.initSqlPath = initSqlPath;
        }

        public String getName() {
            return name;
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
    private Connection tvDBConnection;

    private void tryConnection() {
        try {
            if (tempDBConnection == null) {
                tempDBConnection = DriverManager.getConnection("jdbc:sqlite:" + Type.TempDB.name);
                tempDBConnection.createStatement()
                                .execute(FileUtil.readString(ResourceUtil.getResource(Type.TempDB.initSqlPath),
                                                             StandardCharsets.UTF_8));
            }
            if (tvDBConnection == null) {
                tvDBConnection = DriverManager.getConnection("jdbc:sqlite:" + Type.TvDB.name);
                tvDBConnection.createStatement()
                              .execute(FileUtil.readString(ResourceUtil.getResource(Type.TvDB.initSqlPath),
                                                           StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            ConsoleLog.println(e.getMessage());
        }
    }

    public Connection getConnection(Type type) {
        try {
            tryConnection();
            switch (type) {
                case TvDB:
                    return tvDBConnection;
                case TempDB:
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
