package net.kurage.generator;

public class ModelGenerator {
    /**
     *                sb.append(capitalize(English.singularize(part)));手動実装用クラスのテンプレートを生成する
     * 
     * @param packageName 手動実装用クラスのパッケージ名
     * @param className   クラス名（Genなし）
     * @return Javaコード
     */
    public static String generateManualClass(String packageName, String className, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import ").append(packageName).append(".gen.").append(className).append("Gen;\n");
        sb.append("import jakarta.persistence.Entity;\n");
        sb.append("import jakarta.persistence.Table;\n\n");
        sb.append("/**\n * 独自実装用クラス（自動生成クラス ").append(className).append("Gen を継承）\n */\n");
        sb.append("@Entity\n");
        sb.append("@Table(name = \"").append(tableName).append("\")\n");
        sb.append("public class ").append(className).append(" extends ").append(className).append("Gen {\n");
        sb.append("    // 独自のメソッドやフィールドをここに追加\n");
        sb.append("}\n");
        return sb.toString();
    }

    // クラス名（パスカルケース）→テーブル名（スネークケース）
    private static String toTableName(String className) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < className.length(); i++) {
            char c = className.charAt(i);
            if (Character.isUpperCase(c) && i > 0)
                sb.append('_');
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static String generateEntity(String packageName, DbMetadataReader.TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
    sb.append("import io.ebean.Model;\n");
    sb.append("import jakarta.persistence.*;\n");
    sb.append("import io.ebean.annotation.WhenCreated;\n");
    sb.append("import io.ebean.annotation.WhenModified;\n");
    sb.append("import io.ebean.annotation.DbJsonB;\n");
    sb.append("import javax.annotation.Nullable;\n\n");
        sb.append("@Entity\n");
        sb.append("@Table(name = \"").append(tableInfo.tableName).append("\")\n");
        sb.append("public class ").append(toClassName(tableInfo.tableName)).append("Gen extends Model {\n");

        // カラム名定数
        for (DbMetadataReader.ColumnInfo col : tableInfo.columns) {
            sb.append("    public static final String ")
                    .append(toConstantName(col.columnName))
                    .append(" = \"")
                    .append(col.columnName)
                    .append("\";")
                    .append("\n");
        }
        sb.append("\n");

        // フィールド定義
        for (DbMetadataReader.ColumnInfo col : tableInfo.columns) {
            if (col.isPrimaryKey) {
                sb.append("    @Id\n");
            } else if (col.isNullable) {
                sb.append("    @Nullable\n");
            }
            // created_at用のEBeanアノテーション
            if (col.columnName.equals("created_at")) {
                sb.append("    @WhenCreated\n");
            }
            // updated_at用のEBeanアノテーション
            if (col.columnName.equals("updated_at")) {
                sb.append("    @WhenModified\n");
            }
            // jsonb型用のEBeanアノテーション
            if (col.dataType != null && col.dataType.equalsIgnoreCase("jsonb")) {
                sb.append("    @DbJsonB\n");
            }
            // text型には@Lobを付与
            if (col.dataType != null && col.dataType.equalsIgnoreCase("text")) {
                sb.append("    @Lob\n");
            }
            sb.append("    @Column(name = \"").append(col.columnName).append("\")\n");
            sb.append("    protected ").append(toJavaType(col.dataType)).append(" ").append(toCamelCase(col.columnName))
                    .append(";\n\n");
        }
        // getter/setter生成
        for (DbMetadataReader.ColumnInfo col : tableInfo.columns) {
            String type = toJavaType(col.dataType);
            String camel = toCamelCase(col.columnName);
            String pascal = capitalize(camel);
            // getter
            sb.append("    public ").append(type).append(" get").append(pascal).append("() { return this.")
                    .append(camel).append("; }\n");
            // setter
            sb.append("    public void set").append(pascal).append("(").append(type).append(" ").append(camel)
                    .append(") { this.").append(camel).append(" = ").append(camel).append("; }\n\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    public static String toClassName(String tableName) {
        String[] parts = tableName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty())
                sb.append(capitalize(EnglishSingularizer.singularize(part)));
        }
        return sb.toString();
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty())
            return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String toCamelCase(String name) {
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0) {
                sb.append(part.toLowerCase());
            } else if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1)
                    sb.append(part.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private static String toConstantName(String columnName) {
        return columnName.toUpperCase().replaceAll("[^A-Z0-9]", "_");
    }

    private static String toJavaType(String pgType) {
        // カラム名を定数名（大文字スネークケース）に変換
        switch (pgType.toLowerCase()) {
            case "int2":
            case "int4":
            case "serial":
                return "Integer";
            case "int8":
            case "bigserial":
                return "Long";
            case "varchar":
            case "text":
                return "String";
            case "bool":
                return "Boolean";
            case "date":
                return "java.sql.Date";
            case "timestamp":
            case "timestamptz":
                return "java.sql.Timestamp";
            case "float4":
                return "Float";
            case "float8":
                return "Double";
            case "numeric":
                return "java.math.BigDecimal";
            default:
                System.out.println("Unsupported type: " + pgType);
                return "String";
        }
    }
}
