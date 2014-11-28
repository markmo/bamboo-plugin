package com.metamorphichq.plugins;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;
import com.google.common.base.Charsets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by markmo on 28/11/2014.
 */
public class SqlDeployTask implements TaskType {

    public TaskResult execute(final TaskContext taskContext) throws TaskException {

        final BuildLogger buildLogger = taskContext.getBuildLogger();

        final String path = taskContext.getConfigurationMap().get("path");
        final String sqlin = taskContext.getConfigurationMap().get("sql");
        final String jdbcConn = taskContext.getConfigurationMap().get("jdbcConn");
        final String uid = taskContext.getConfigurationMap().get("uid");
        final String pwd = taskContext.getConfigurationMap().get("pwd");

        TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);

        String sql;
        if (path.isEmpty()) {
            sql = sqlin;
        } else {
            try {
                sql = readFile(path, Charsets.UTF_8);
            } catch (IOException e) {
                buildLogger.addBuildLogEntry(e.getMessage());
                e.printStackTrace();
                throw new TaskException(e.getMessage(), e);
            }
        }
        buildLogger.addBuildLogEntry(sql);

        try {
            invokeSql(sql, jdbcConn, uid, pwd);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e2) {
            e2.printStackTrace();
        }

        return builder.checkTestFailures().build();
    }

    private String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private void invokeSql(String sql, String jdbcConn, String uid, String pwd) throws ClassNotFoundException, SQLException {
        if (jdbcConn == null || jdbcConn.isEmpty()) {
            jdbcConn = "jdbc:teradata://10.66.15.10/CAAIL";
        }
        if (uid == null || uid.isEmpty()) {
            uid = "sysdba";
        }
        if (pwd == null || pwd.isEmpty()) {
            pwd = "Nbc_tera1234";
        }
        Class.forName("com.teradata.jdbc.TeraDriver");
        Connection conn = DriverManager.getConnection(jdbcConn, uid, pwd);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
    }
}
