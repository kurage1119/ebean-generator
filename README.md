
# Ebean Model Auto Generator Tool

This tool is a CLI application that automatically generates Java Ebean model classes (@Entity POJO) from a PostgreSQL database schema.

## Main Features
- Automatically generates Ebean Java entity classes from PostgreSQL table information
- Manages DB connection info, output directory, and Java package name via config file
- Adds annotations such as @Entity, @Table, @Id, @Column automatically to generated classes

## Setup & Usage

### 1. Prepare the config file

`config.yaml` is included in the repository. Edit its contents as needed.

```yaml
db:
  host: localhost
  port: 5432
  database: mydb
  user: myuser
  password: your_password

output:
  directory: ./output
  packageName: com.example.models
```

### 2. Build the JAR file
Run the following command in the project root:

```pwsh
./create_jar.bat
```

The JAR file (`ebean-generator.jar`) will be generated in the `build/libs/` folder.

### 3. How to run
Execute the generated JAR file from the command line:

```pwsh
java -jar build/libs/ebean-generator.jar
```

## Example Generated Code
```java
package com.example.models;

import io.ebean.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "sample_table")
public class SampleTable extends Model {
    @Id
    Long id;

    @Column(name = "name")
    String name;
    // ...
}
```

## Notes
- Requires Java (JDK 11 or later)
- Do not publish confidential information (such as passwords)
- For questions or requests, please use Issues or Pull Requests


# Ebeanモデル自動生成ツール

このツールは、PostgreSQLのデータベーススキーマからJavaのEbeanモデルクラス（@Entity付きPOJO）を自動生成するCLIアプリケーションです。

## 主な機能
- PostgreSQLのテーブル情報からEbean用のJavaエンティティクラスを自動生成
- 設定ファイルでDB接続情報・出力先ディレクトリ・Javaパッケージ名を管理
- 生成クラスには@Entity, @Table, @Id, @Column等のアノテーションを自動付与

## セットアップと使い方

### 1. 設定ファイルの準備

`config.yaml`はリポジトリに含まれています。必要に応じて内容を書き換えてご利用ください。

```yaml
db:
  host: localhost
  port: 5432
  database: mydb
  user: myuser
  password: your_password

output:
  directory: ./output
  packageName: com.example.models
```

### 2. JARファイルの作成
プロジェクトルートで以下のコマンドを実行します。

```pwsh
./create_jar.bat
```

 `build/libs/` フォルダにJARファイル（`ebean-generator.jar`）が生成されます。

### 3. 実行方法
作成したJARファイルをコマンドラインから実行してください。

```pwsh
java -jar build/libs/ebean-generator.jar
```

## 生成されるコード例
```java
package com.example.models;

import io.ebean.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "sample_table")
public class SampleTable extends Model {
    @Id
    Long id;

    @Column(name = "name")
    String name;
    // ...
}
```

## 注意事項
- Java（JDK 11以上）が必要です。
- 機密情報（パスワード等）は絶対に公開しないでください。
- ご質問・要望はIssueまたはPull Requestでお知らせください。