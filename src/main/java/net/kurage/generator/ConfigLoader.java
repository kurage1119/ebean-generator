package net.kurage.generator;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.yaml.snakeyaml.LoaderOptions;

public class ConfigLoader {
    public static class DbConfig {
        public String host;
        public int port;
        public String database;
        public String user;
        public String password;
    }
    public static class OutputConfig {
        public String directory;
        public String packageName;
    }
    public DbConfig db;
    public OutputConfig output;

    public static ConfigLoader load(String path) {
        try (InputStream in = Files.newInputStream(Paths.get(path))) {
            Yaml yaml = new Yaml(new Constructor(ConfigLoader.class, new LoaderOptions()));
            return yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("設定ファイルの読み込みに失敗しました: " + e.getMessage(), e);
        }
    }
} 