# Sena-batchとは（Overview）

日本国内株式の情報を、ウェブスクレイピング技術を使用し収集する  
Play Framework 2.3ベースのバッチアプリケーションです。  
収集する情報につきましては、下記をご覧ください。

1. 銘柄一覧情報
2. 企業決算スケジュール情報
    * 収集した情報を特定のGoogle カレンダーへイベント登録します。
        * [決算スケジュール | Google カレンダー 実運用のサンプル](https://calendar.google.com/calendar/embed?src=24qrq6gcmnq39tep0bvjfjf9o8%40group.calendar.google.com&ctz=Asia/Tokyo "決算スケジュール | Google カレンダー 実運用のサンプル")
3. 信用残高情報
4. 指標情報
5. 財務業績情報
    * 収集した財務業績情報より四半期ごとの前年比を算出し登録します。



## ダウンロード（Download）

※TODO



## インストール（Install）

1. 動作環境

※TODO

2. DBセットアップ

※TODO

3. バイナリセットアップ

※TODO

* 設定ファイルの調整

4. テストバッチの起動方法

※TODO



## 使い方（Usage）

1. 各バッチの紹介

※TODO



## ドキュメント（Document）

* [Sena-batch アプリケーション API仕様](http://sena.yellow-man.yokohama/javadoc/batch/)



## 更新履歴（Version history）

* [sena-1.0.0-batch1.0](https://office.yellow-man.yokohama/redmine/versions/1) リリース yyyy/MM/dd
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
