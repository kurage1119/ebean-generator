package net.kurage.generator;

import net.kurage.exceptions.SystemException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DbMetadataReader {
    public static class TableInfo {
        public String tableName;
        public List<ColumnInfo> columns;
    }
    public static class ColumnInfo {
        public String columnName;
        public String dataType;
        public boolean isPrimaryKey;
        public boolean isNullable;
    }

    public static List<TableInfo> readMetadata(Connection conn, String schema) {
        List<TableInfo> tables = new ArrayList<>();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rsTables = meta.getTables(null, schema, "%", new String[]{"TABLE", "VIEW", "MATERIALIZED VIEW"});
            while (rsTables.next()) {
                String tableName = rsTables.getString("TABLE_NAME");
                TableInfo table = new TableInfo();
                table.tableName = tableName;
                table.columns = new ArrayList<>();

                // 主キー取得
                List<String> pkList = new ArrayList<>();
                ResultSet rsPK = meta.getPrimaryKeys(null, schema, tableName);
                while (rsPK.next()) {
                    pkList.add(rsPK.getString("COLUMN_NAME"));
                }
                rsPK.close();

                // カラム取得
                ResultSet rsCols = meta.getColumns(null, schema, tableName, "%");
                while (rsCols.next()) {
                    ColumnInfo col = new ColumnInfo();
                    col.columnName = rsCols.getString("COLUMN_NAME");
                    col.dataType = rsCols.getString("TYPE_NAME");
                    col.isPrimaryKey = pkList.contains(col.columnName);
                    String nullable = rsCols.getString("IS_NULLABLE");
                    col.isNullable = nullable != null && nullable.equalsIgnoreCase("YES");
                    table.columns.add(col);
                }
                rsCols.close();
                tables.add(table);
            }
            rsTables.close();
        } catch (Exception e) {
            throw new SystemException("メタデータ取得に失敗しました: " + e.getMessage(), e);
        }
        return tables;
    }
} 