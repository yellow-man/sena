# Senaとは

日本国内株式の情報をウェブスクレイピング技術を使用し収集するバッチアプリケーションです。  
収集する情報は、

* 銘柄一覧情報
* 企業決算スケジュール情報
    * 収集した情報を特定のGoogle カレンダーへイベント登録します。
        * [決算スケジュール | Google カレンダー 実運用のサンプル](https://calendar.google.com/calendar/embed?src=24qrq6gcmnq39tep0bvjfjf9o8%40group.calendar.google.com&ctz=Asia/Tokyo "決算スケジュール | Google カレンダー 実運用のサンプル")
* 信用残高情報
* 指標情報
* 財務業績情報
    * 収集した財務業績情報より四半期ごとの前年比を算出し登録します。

を収集します。
