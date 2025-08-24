package net.kurage.generator;

import net.kurage.exceptions.SystemException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("設定ファイルを読み込み中...");
            ConfigLoader config = ConfigLoader.load("config.yaml");
            System.out.println("設定ファイルの読み込み完了");
            String url = String.format("jdbc:postgresql://%s:%d/%s", config.db.host, config.db.port, config.db.database);
            System.out.println("DBへ接続中: " + url);
            try (Connection conn = DriverManager.getConnection(url, config.db.user, config.db.password)) {
                System.out.println("DB接続成功");
                System.out.println("スキーマ情報を取得中...");
                List<DbMetadataReader.TableInfo> tables = DbMetadataReader.readMetadata(conn, "public");
                System.out.println("テーブル数: " + tables.size());
                // genサブパッケージ（自動生成クラス）
                String genPackage = config.output.packageName + ".gen";
                String genPackagePath = genPackage.replace('.', '/');
                Path genOutDir = Paths.get(config.output.directory, genPackagePath);
                Files.createDirectories(genOutDir);
                // 手動実装用パッケージ
                String manualPackagePath = config.output.packageName.replace('.', '/');
                Path manualOutDir = Paths.get(config.output.directory, manualPackagePath);
                Files.createDirectories(manualOutDir);
                System.out.println("自動生成クラス出力ディレクトリ: " + genOutDir.toAbsolutePath());
                System.out.println("手動実装クラス出力ディレクトリ: " + manualOutDir.toAbsolutePath());
                int count = 0;
                for (DbMetadataReader.TableInfo table : tables) {
                    System.out.println("テーブル処理中: " + table.tableName);
                    // クラス名生成
                    String baseClassName = ModelGenerator.toClassName(table.tableName);
                    // --- 自動生成クラス ---
                    String genCode = ModelGenerator.generateEntity(genPackage, table);
                    String genClassName = baseClassName + "Gen";
                    Path genFilePath = genOutDir.resolve(genClassName + ".java");
                    Files.write(genFilePath, genCode.getBytes());
                    System.out.println("自動生成クラス生成完了: " + genFilePath);
                    // --- 手動実装クラス（未存在時のみ） ---
                    String manualClassName = baseClassName;
                    Path manualFilePath = manualOutDir.resolve(manualClassName + ".java");
                    if (!Files.exists(manualFilePath)) {
                        String manualCode = ModelGenerator.generateManualClass(
                            config.output.packageName,
                            manualClassName,
                            table.tableName
                        );
                        Files.write(manualFilePath, manualCode.getBytes());
                        System.out.println("手動実装クラス生成完了: " + manualFilePath);
                    } else {
                        System.out.println("手動実装クラス既存のためスキップ: " + manualFilePath);
                    }
                    count++;
                }
                System.out.println("全テーブル・ビューのモデル生成が完了しました。生成数: " + count);
            }
        } catch (SystemException e) {
            System.out.println("データベースへの接続に失敗しました: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
