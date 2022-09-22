package com.example.springservice.service;

import com.example.springservice.controller.dto.request.MstmbRequest;
import com.example.springservice.controller.dto.response.MstmbResponse;
import com.example.springservice.model.MstmbRepository;
import com.example.springservice.model.entity.Mstmb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import static org.apache.logging.log4j.util.Strings.isBlank;

@EnableCaching // 啟用快取
@Service
public class MstmbService {

    @Autowired
    private MstmbRepository mstmbRepository;


    //用DocSeq查詢資料
    //  @Cacheable 快取 :https://www.javazhiyin.com/4618.html  /   https://matthung0807.blogspot.com/2020/07/spring-boot-caching-simple.html
    //  cacheNames 和 value 這兩個屬性任意使用一個都可以，它們的作用可以理解為 key 的字首
    @Cacheable(cacheNames = "MstnbStock-cache", key = "#request.getStock()")
    public MstmbResponse postMstmbByStock(MstmbRequest request) {

        MstmbResponse mstmbResponse = new MstmbResponse();

        Mstmb mstmb = mstmbRepository.findByStock(request.getStock());
        if (isBlank(request.getStock()) || request.getStock().length() != 4) {
            mstmbResponse.setStatus("股票代碼輸入錯誤");
            return mstmbResponse;
        } else if (null == mstmb) {
            mstmbResponse.setStatus("查詢不到此股票");
            return mstmbResponse;
        } else if (request.getCurPrice() < 0) {
            mstmbResponse.setStatus("請輸入有效股票價格");
            return mstmbResponse;
        }

        mstmbResponse.setStatus("查詢成功");
        mstmbResponse.setMstmb(mstmb);

        return mstmbResponse;
    }

    //更改Mstmb的現價
    //  @CachePut: 按条件更新缓存 (用此方法會造成update後，快取資料改成update回傳資料，之後findByStock時都會快取到Updata回傳資料)
    //  @CachePut: 需要清除緩存元素(update前會先清除快取，之後再查詢findByStock時會重新新增快取)
    @CacheEvict(cacheNames = "MstnbStock-cache", key = "#request.getStock()")
    public MstmbResponse updateMstmb(MstmbRequest request) {

        MstmbResponse mstmbResponse = new MstmbResponse();

        //確認 DB中有沒有這筆資料
        Mstmb mstmb = mstmbRepository.findByStock(request.getStock());

        //如果修改資料找不到
        //null == mstmb (確定的值要放前面)
        if (isBlank(request.getStock()) || request.getStock().length() != 4) {
            mstmbResponse.setStatus("股票代碼輸入錯誤");
            return mstmbResponse;
        } else if (null == mstmb) {
            mstmbResponse.setStatus("查詢不到此股票");
            return mstmbResponse;
        } else if (request.getCurPrice() < 0) {
            mstmbResponse.setStatus("請輸入有效股票價格");
            return mstmbResponse;
        }

        //  小數點有幾位
        String stringIn = String.valueOf(request.getCurPrice()); // 型別double 轉成String //EX:12.234

        // indexOf():指定字符在字符串中第一次出現處的索引，如果此字符串中沒有這樣的字符，則返回 -1
        // substring():擷取字串
        int i = stringIn.substring(stringIn.indexOf(".")).length() - 1; //-1為扣掉. //EX:3(234)

        if (i > 2) {
            mstmbResponse.setStatus("現價最小單位為小數點後兩位數");
            return mstmbResponse;

        } else {
            //將要更改的值塞進去
            mstmb.setCurPrice(request.getCurPrice());

            //儲存進DB
            mstmbRepository.save(mstmb);

            //回傳 OK告訴 Controller完成儲存
            mstmbResponse.setStatus("更新成功");
            mstmbResponse.setMstmb(mstmb);

            return mstmbResponse;

        }
    }

}
