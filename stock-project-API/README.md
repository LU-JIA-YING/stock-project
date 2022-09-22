# stock-project(未實現損益API實作 - 通訊格式)
(SpringBoot-MVC-Restful-MariaDB-SpringDataJPA-Query) + API XML

###### tags: `Homework`、`現股未實現損益`、`MariaDB`、`Spring Data JPA`、`@Query`

## 開發要求
* 框架: Spring Boot
* 驗證工具: Postman
* 資料庫: MySQL or MariaDB
* 提供API呼叫範例 `Postman collection`

## API 格式

### 查詢未實現損益明細
* URL: localhost:8080/api/v1/unreal/detail
 
* http method: POST

* request body:

    * branchNo - 分行（必要）
    * custSeq - 客戶帳號（必要）
    * stock - 股票（非必要）
* request body範例：
    ```=J
      {
          "branchNo":"F62S",
          "custSeq":"00",
          "stock":"2357"
      }
    ```

* response body:
    * resultList - 查詢結果列（無論查詢結果如何，都須回傳一個陣列）
    明細結果：
        1. tradeDate - 交易日期
        2. docSeq - 委託書號
        3. stock - 股票代號
        4. stockName - 股票中文名稱
        5. buyPrice - 買進價格（一律顯示小數點後兩位）
        6. nowPrice - 現價（一律顯示小數點後兩位）
        7. qty - 買進股數
        8. remainQty - 剩餘股數
        9. fee - 買進手續費（顯示整數）
        10. cost - 買進成本（顯示整數）
        11. marketValue - 市值（顯示整數）
        12. unrealProfit - 未實現損益（顯示整數）
    
    * responseCode - 回覆代號
        * 000 - 成功（message請回覆空字串）
        * 001 - 查無結果（message請回覆「查無符合資料」）
        * 002 - 參數檢核錯誤（在message中詳述）
        * 005 - 伺服器內部錯誤（message請回覆「伺服器忙碌中，請稍後嘗試」）
    
    * message - 回覆訊息

* response body範例：
    ```=J
      {
          "resultList": [
              {
                  "tradeDate": "20220801",
                  "docSeq": "BB001",
                  "stock": "2357",
                  "stockName": "華碩",
                  "buyPrice": 282.00,
                  "nowPrice": 368.00,
                  "qty": 2000,
                  "remainQty": 2000,
                  "fee": 804,
                  "cost": 564804,
                  "marketValue": 732743,
                  "unrealProfit": 167939
              }
          ],
          "responseCode": "000",
          "mesage": ""
      }
    ```

### 查詢未實現損益彙總
* URL: localhost:8080/api/v1/unreal/sum

* http method: POST

* request body:
    * branchNo - 分行（必要）
    * custSeq - 客戶帳號（必要）
    * stock - 股票（非必要）
* request body範例：
    ```=J
    {
        "branchNo":"F62S",
        "custSeq":"00",
        "stock":"2357"
    }
    ```
* response body:
    * resultList - 查詢結果列（無論查詢結果如何，都須回傳一個陣列）
    彙總結果：
        1. stock - 股票代號
        2. stockName - 股票中文名稱
        3. nowPrice - 現價（一律顯示小數點後兩位）
        4. sumRemainQty - 彙總剩餘股數
        5. sumFee - 彙總買進手續費（顯示整數）
        6. sumCost - 彙總買進成本（顯示整數）
        7. sumMarketValue - 彙總市值（顯示整數）
        8. sumUnrealProfit - 彙總未實現損益（顯示整數）
        9. detaiList - 明細列
        明細結果：
            1. tradeDate - 交易日期
            2. docSeq - 委託書號
            3. stock - 股票代號
            4. stockName - 股票中文名稱
            5. buyPrice - 買進價格（一律顯示小數點後兩位）
            6. nowPrice - 現價（一律顯示小數點後兩位）
            7. qty - 買進股數
            8. remainQty - 剩餘股數
            9. fee - 買進手續費（顯示整數）
            10. cost - 買進成本（顯示整數）
            11. marketValue - 市值（顯示整數）
            12. unrealProfit - 未實現損益（顯示整數）
            
    * responseCode - 回覆代號
        * 000 - 成功（message請回覆空字串）
        * 001 - 查無結果（message請回覆「查無符合資料」）
        * 002 - 參數檢核錯誤（在message中詳述）
        * 005 - 伺服器內部錯誤（message請回覆「伺服器忙碌中，請稍後嘗試」）
    * message - 回覆訊息
    
* response body範例：
```=J
{
    "resultList": [
        {
            "stock": "2357",
            "stockName": "華碩",
            "nowPrice": 368.00,
            "sumRemainQty": 2000,
            "sumFee": 804,
            "sumCost": 564804,
            "sumMarketValue": 732743,
            "sumUnrealProfit": 167939,
            "detailList": [
                {
                    "tradeDate": "20220801",
                    "docSeq": "BB001",
                    "stock": "2357",
                    "stockName": "華碩",
                    "buyPrice": 282.00,
                    "nowPrice": 368.00,
                    "qty": 2000,
                    "remainQty": 2000,
                    "fee": 804,
                    "cost": 564804,
                    "marketValue": 732743,
                    "unrealProfit": 167939
                }
            ]
        }
    ],
    "responseCode": "000",
    "mesage": ""
}
```

### 新增餘額
* URL: localhost:8080/api/v1/unreal/add

* http method: POST

* Model行為：須新增一筆HCMIO與一筆TCNUD
    :::danger
    明細檔和餘額檔必須同時寫入資料，
    當其中一筆資料沒有被寫入時，另一筆資料就必須rollback。
    :::

* request body:
    * tradeDate - 交易日（必要）
    * branchNo - 分行（必要）
    * custSeq - 客戶帳號（必要）
    * docSeq - 委託書號（必要）
    * stock - 股票（必要）
    * price - 買進價格（必要）
    * qty - 買進股數（必要）

* request body範例：
    ```=J
    {
        "tradeDate":"20220830",
        "branchNo":"F62S",
        "custSeq":"00",
        "docSeq":"ZZ005",
        "stock":"6214",
        "price":81.5,
        "qty":2000
    }
    ```

* response body:
    * resultList - 新增結果列（須回傳一個陣列）
    新增結果：
        1. tradeDate - 交易日期
        2. docSeq - 委託書號
        3. stock - 股票代號
        4. stockName - 股票中文名稱
        5. buyPrice - 買進價格（一律顯示小數點後兩位）
        6. nowPrice - 現價（一律顯示小數點後兩位）
        7. qty - 買進股數
        8. remainQty - 剩餘股數
        9. fee - 買進手續費（顯示整數）
        10. cost - 買進成本（顯示整數）
        11. marketValue - 市值（顯示整數）
        12. unrealProfit - 未實現損益（顯示整數）
    
    * responseCode - 回覆代號
        * 000 - 成功（message請回覆空字串）
        * 002 - 參數檢核錯誤（在message中詳述。提示：除空值外，也須檢核PK是否已經存在）
        * 005 - 伺服器內部錯誤（message請回覆「伺服器忙碌中，請稍後嘗試」）
    * message - 回覆訊息
* response body範例：
    ```=J
    {
        "resultList": [
            {
                "tradeDate": "20220801",
                "docSeq": "BB001",
                "stock": "2357",
                "stockName": "華碩",
                "buyPrice": 282.00,
                "nowPrice": 368.00,
                "qty": 2000,
                "remainQty": 2000,
                "fee": 804,
                "cost": 564804,
                "marketValue": 732743,
                "unrealProfit": 167939
            }
        ],
        "responseCode": "000",
        "mesage": ""
    }
    ```

## 加分題

### 程式碼重構
1. 請將程式碼內重複撰寫的邏輯區塊，以method的形式包裝起來，達到程式碼複用

2. 請設計一Service專門進行價金、手續費、交易稅、淨收付、損益等金額規費的計算，並在專案中使用

3. 請設計一Component專門進行小數位數的轉換，並在專案中使用。可以先只開本次實作會使用到的method，若有時間再擴充

### 更新股票現價
請設計另一個Controller，更新資料表MSTMB中的現價欄位。現價最小單位為小數點後兩位數。

* URL: localhost:8080/mstmb/updateCurPrice

* http method: POST

* request body:
    * stock - 股票
    * curPrice - 現價

* request body範例：
    ```=J
    {
        "stock": "1101",
        "curPrice": 39.11
    }
    ```

### 查詢獲利率區間
獲利率 = 未實現損益 / 成本 * 100%
彙總獲利率 = 彙總未實現損益 / 彙總成本 * 100%

請擴充未實現明細與彙總查詢API，讓使用者可以指定獲利率區間進行查詢。
獲利率回覆格式：00.00%，一律顯示小數點後兩位與%符號。

* URL: `localhost:8080/api/v1/unreal/detail` OR `localhost:8080/api/v1/unreal/sum`

* http method: POST

* request body:
    * branchNo - 分行
    * custSeq - 客戶帳號
    * stock - 股票
    * profitRateLowerLimit - 獲利率下限
    * profitRateUpperLimit - 獲利率上限

* request body範例：
    ```=J
    {
        "branchNo":"F62S",
        "custSeq":"00",
        "stock":"2357",
        "profitRateLowerLimit":-53.46,
        "profitRateUpperLimit":-12.46
    }
    ```

### 查詢交割金
交割金，為買進金融商品時需付出的金額（為求情境單純化，我們目前先只考慮現股買進就好）。

台灣證券交易所規定，股票交易的交割時間為T+2，意即：在下單買進股票的「二個交易日」後，券商才會從客戶的戶頭中扣除買進股票的費用（買進淨收付）。

例：
若在9/5買進台積電一張，則券商會在9/7從客戶的戶頭扣款。
若在9/8買進聯電一張，則券商會在9/13從客戶的戶頭扣款（因9、10、11號都是假日）。

請設計一API，讓客戶查詢「今天」需付出的交割金。

提示：如果不想每次查詢都一一推算假日，可以借助資料庫先行儲存假日資訊。

* URL: localhost:8080/TodayDeliveryFee

* http method: POST

* request body:
    * stock - 股票

* request body範例：
    ```=J
    {
        "stock": "1101"
    }
    ```

### 導入快取機制
請設計查詢股票資訊檔(MSTMB)的API，並且要應用 Spring Caching。

* URL: localhost:8080/mstmb/stock

* http method: POST

* request body:
    * branchNo - 分行
    * custSeq - 客戶帳號

* request body範例：
    ```=J
    {
      "branchNo":"F62Z",
      "custSeq":"04"
    }
    ```

快取主要的功用為「減少對DB的存取並加快查詢速度」。
舉例來說，當在查詢股票資訊檔時，當進到DB查詢過一次後，就會將該筆資料儲存到快取，後續在查詢時，就能直接從快取拿資料。

Spring Caching提供非常多的 Caching Provider，該專案只需使用Simple Provider即可，
也就是Spring Boot提供的 In-Memory Caching - ConcurrentHashMap。

常見的Caching Providers:
* Simple
* Generic
* EhCache
* Hazelcast
* Couchbase
* Redis
* Caffeine


## 常見Q&A
* 可討論，可以 google
* code review 需要解釋每一行程式
* 指導員會提供方向回答問題，詳細設計或錯誤處理盡量自行解決
* 模擬券商環境，HTTP Method都以` POST `為主
* HTTP ReponseBody Status code 都會是`200`
* 了解 float, double, BigDecimal 三者差異，自行決定價格的型別
* 自行建立股票資訊檔(MSTMB)，參考價可以空著，不影響資料
* 股票市值 = (現價 * 股數) - 賣出手續費 - 交易稅

---
    
:::info
    
    * 股價(市價、現價、單價)：1股的價格
    * 現值(市值)：餘額的預估賣出收入(賣出淨收付)
    
------------------------------------------
    現股交易淨收付計算公式：
    
        1.買進淨收付（-） 
          = 買進價金 + 買進手續費
        2.賣出淨收付（+） 
          = 賣出價金 - 賣出手續費 - 賣出交易稅    

------------------------------------------

    * 價金 = 交易股數 × 股票單價
    * 手續費 = 價金 × 手續費費率
     （手續費通用費率：0.1425%）
    * 交易稅 = 價金 × 交易稅稅率
     （交易稅通用稅率：0.3%）
    
------------------------------------------
    
    * 未實現損益計算公式：
      = [ (目前股價 x 買賣張數 x 1000) - 賣價手續費 - 賣價交易税 ] - (買進股價 × 股數 + 買進手續費)
      = 預估收入 - 買進成本
    
:::

---

* 交易明細檔(HCMIO)	
    ![](https://i.imgur.com/RufPcwi.png)

* 現股餘額資料檔(TCNUD)	
    ![](https://i.imgur.com/tWb4Cna.png)

* 股票資訊檔(MSTMB)	
    ![](https://i.imgur.com/CqLIu75.png)

---
## 查詢即時價API介接

### 說明
請實作一個方法，將第一階段中未實現損益功能的股票現價從查詢MSTMB資料表改為查詢即時價API

### 開發要求
查價API的網址要使用設定檔

### 查價API說明
* HTTP 請求方法: GET或POST都可以
* 網址: http://systexdemo.ddns.net:443/Quote/Stock.jsp
* 傳入參數: stock
* 傳入參數值: 股票代碼 (多檔股票請使用逗號分隔 Ex:1234,2330)
* request 範例:
    :::info
    http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=1234,2330
    :::
    
* response 範例:
    :::info
    ```
    <?xml version="1.0" encoding="UTF-8"?>
     <Symbols>
        <Symbol id="1234" dealprice="33.85" shortname="黑  松" mtype="T"/>
        <Symbol id="2330" dealprice="574" shortname="台積電" mtype="T"/>
     </Symbols>    
    ```
    :::
    
