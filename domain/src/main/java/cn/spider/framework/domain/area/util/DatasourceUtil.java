package cn.spider.framework.domain.area.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatasourceUtil {
    public static List<TableInfo> queryTableInfos(String url,String user,String password) {
        // 数据库URL，用户名和密码
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 查询数据库元数据获取表信息
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = null; // 数据库名，对于MySQL可以设置为null
            String schemaPattern = null; // schema模式名，MySQL中为null
            String tableNamePattern = "%"; // 表名匹配模式
            String[] types = {"TABLE"};
            List<TableInfo> tableInfos = new ArrayList<>();
            try (ResultSet tables = metaData.getTables(catalog, schemaPattern, tableNamePattern, types)) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    String tableComment = tables.getString("REMARKS"); // 对于MySQL是REMARKS
                    TableInfo tableInfo = new TableInfo(tableName,tableComment);
                    tableInfos.add(tableInfo);
                }
            } finally {
                conn.close();
            }
            return tableInfos;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
