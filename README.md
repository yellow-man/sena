# Sena-batchとは（Overview）

日本国内株式の情報を、ウェブスクレイピング技術を使用し収集する、  
Play Framework 2.3ベースのバッチアプリケーションです。  
収集する情報につきましては、下記をご確認ください。

* 銘柄一覧情報
* 企業決算スケジュール情報
    * 収集した情報を特定のGoogle カレンダーへイベント登録します。
        * [決算スケジュール | Google カレンダー 実運用のサンプル](https://calendar.google.com/calendar/embed?src=24qrq6gcmnq39tep0bvjfjf9o8%40group.calendar.google.com&ctz=Asia/Tokyo "決算スケジュール | Google カレンダー 実運用のサンプル")
* 信用残高情報
* 指標情報
* 財務業績情報
    * 収集した財務業績情報より四半期ごとの前年比を算出し登録します。



## ダウンロード（Download）

バイナリをダウンロードする場合は [こちら](https://github.com/yellow-man/sena-batch/releases) です。



## インストール（Install）

Sena-batchの動作環境、セットアップ方法についてです。

* 動作環境  
基本的にJava 8が動作する環境であれば起動します。  
（※以降、テスト運用を行ってるCentOS 7.2に合わせて説明を進めていきます。）
    * 実行環境
        * Java 8が動作する環境
    * データベース
        * MariaDB 5.5（MySQL互換）

* バイナリセットアップ  
ダウンロードしたzipファイルを解凍するだけです。
    * sena-1.0.0-batch1.0.zipを展開  
    ```
    # unzip ./sena-1.0.0-batch1.0.zip
    ```

* DBセットアップ
    * DBユーザ作成
        * ユーザ作成用DDLを用意しています。DBユーザ名、パスワードを置き換えて実行してください。  
        [00_init_ddl.sql](https://github.com/yellow-man/sena-batch/blob/master/sql/ddl/00_init_ddl.sql)
    * 各テーブル作成
        * テーブル作成用DDLを用意しています。  
        [01_create_table_ddl.sql](https://github.com/yellow-man/sena-batch/blob/master/sql/ddl/01_create_table_ddl.sql)

* 設定ファイルの調整
    * 展開したzipフォルダに設定ファイルも展開されます。  
    環境に合わせてDB接続先、ログ出力先、実行結果をメール送信するためのSMTPサーバ、メール配信先の設定を調整してください。  
    ```
    ./sena-1.0.0-batch1.0/conf/application.conf
    ```

* テストバッチの起動方法
    * 設定内容確認用に、テストバッチを用意しています。  
    ログ出力、実行結果を設定ファイルで指定したメールアドレスに配信するだけのバッチです。  
    ```
    # cd ./sena-1.0.0-batch1.0
    # java -cp './lib/*' -Dconfig.file=./conf/application-production.conf -Dlogger.file=./conf/logger.xml -Dfile.encoding=utf-8 yokohama.yellow_man.sena.jobs.JobExecutor yokohama.yellow_man.sena.jobs.TestJob
    ```



## 使い方・実行可能なバッチ一覧（Usage）
実行可能なバッチのクラス名と、バッチの名前を記載しています。

* 銘柄一覧情報  
（銘柄一覧インポートバッチ - [yokohama.yellow_man.sena.jobs.ImportStocks](http://sena.yellow-man.yokohama/javadoc/batch/yokohama/yellow_man/sena/jobs/ImportStocks.html)）

* 企業決算スケジュール情報  
（企業スケジュールインポートバッチ - [yokohama.yellow_man.sena.jobs.ImportCompanySchedules](http://sena.yellow-man.yokohama/javadoc/batch/yokohama/yellow_man/sena/jobs/ImportCompanySchedules.html)）
    * 収集した情報を特定のGoogle カレンダーへイベント登録します。  
    （企業スケジュールエクスポートバッチ - [yokohama.yellow_man.sena.jobs.ExportCompanySchedules](http://sena.yellow-man.yokohama/javadoc/batch/yokohama/yellow_man/sena/jobs/ExportCompanySchedules.html)）
        * [決算スケジュール | Google カレンダー 実運用のサンプル](https://calendar.google.com/calendar/embed?src=24qrq6gcmnq39tep0bvjfjf9o8%40group.calendar.google.com&ctz=Asia/Tokyo "決算スケジュール | Google カレンダー 実運用のサンプル")

* 信用残高情報  
（信用残インポートバッチ - [yokohama.yellow_man.sena.jobs.ImportDebitBalances](http://sena.yellow-man.yokohama/javadoc/batch/yokohama/yellow_man/sena/jobs/ImportDebitBalances.html)）

* 指標情報  
（企業指標インポートバッチ - [yokohama.yellow_man.sena.jobs.ImportIndicators](http://sena.yellow-man.yokohama/javadoc/batch/yokohama/yellow_man/sena/jobs/ImportIndicators.html)）

* 財務業績情報  
（企業財務インポートバッチ - [yokohama.yellow_man.sena.jobs.ImportFinances](http://sena.yellow-man.yokohama/javadoc/batch/yokohama/yellow_man/sena/jobs/ImportFinances.html)）
    * 収集した財務業績情報より四半期ごとの前年比を算出し登録します。  
    （企業財務情報前年比更新バッチ - [yokohama.yellow_man.sena.jobs.ImportFinances](http://sena.yellow-man.yokohama/javadoc/batch/yokohama/yellow_man/sena/jobs/UpdateFinancesSetRate.html)）



## ドキュメント（Document）

* [Sena-batch アプリケーション API仕様](http://yellow-man.github.io/sena-batch/javadoc/sena-1.0.0-batch1.0/)



## 更新履歴（Version history）

* [sena-1.0.0-batch1.0](https://office.yellow-man.yokohama/redmine/versions/1) リリース 2016/05/20
    * 新機能
        * タスク [#12](https://office.yellow-man.yokohama/redmine/issues/12): 銘柄一覧インポートバッチの作成 - Sena - yellow-man.yokohama - Redmine
        * タスク [#13](https://office.yellow-man.yokohama/redmine/issues/13): 企業スケジュールインポートバッチの作成 - Sena - yellow-man.yokohama - Redmine
        * タスク [#14](https://office.yellow-man.yokohama/redmine/issues/14): 企業スケジュールエクスポートバッチの作成 - Sena - yellow-man.yokohama - Redmine
        * タスク [#15](https://office.yellow-man.yokohama/redmine/issues/15): 企業財務インポートバッチの作成 - Sena - yellow-man.yokohama - Redmine
        * タスク [#16](https://office.yellow-man.yokohama/redmine/issues/16): 企業財務情報前年比更新バッチの作成 - Sena - yellow-man.yokohama - Redmine
        * タスク [#20](https://office.yellow-man.yokohama/redmine/issues/20): 信用残インポートバッチの作成 - Sena - yellow-man.yokohama - Redmine
        * タスク [#21](https://office.yellow-man.yokohama/redmine/issues/21): 企業指標インポートバッチの作成 - Sena - yellow-man.yokohama - Redmine



## ライセンス（License）

Copyright 2016 yellow-man.yokohama
This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with
the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
