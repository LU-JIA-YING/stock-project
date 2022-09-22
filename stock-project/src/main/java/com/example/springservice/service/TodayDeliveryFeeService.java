package com.example.springservice.service;

import com.example.springservice.controller.dto.request.TodayDeliveryFeeRequest;
import com.example.springservice.model.HolidayRepository;
import com.example.springservice.model.TcnudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class TodayDeliveryFeeService {

    @Autowired
    private HolidayRepository holidayRepository;
    @Autowired
    private TcnudRepository tcnudRepository;


    //查詢交割金(建議六日也放入資料庫中)
    public String getDeliveryFee(TodayDeliveryFeeRequest request) {

        Calendar today = Calendar.getInstance();    //  初始取得現在時間
        if (1 == today.get(Calendar.DAY_OF_WEEK) || 7 == today.get(Calendar.DAY_OF_WEEK)) {
            return "今天是假日，無需付交割金";
        }

        Calendar target = Calendar.getInstance();   //  初始交易時間
        target.add(Calendar.DATE, -2);  // 交易時間減2日(交割日)

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");    //  準備輸出的格式，如：20220101

        while (0 != today.compareTo(target)) {  //  compareTo() 比較 兩個日期:如果日期相等，則返回 0
            today.add(Calendar.DATE, -1);

            if (null != holidayRepository.findByHoliday(sdf.format(today.getTime())) || 1 == today.get(Calendar.DAY_OF_WEEK) || 7 == today.get(Calendar.DAY_OF_WEEK)) {
                target.add(Calendar.DATE, -1);
            }
        }

//        int workday = 0;  //  今天日期跟購買日期中間需要兩個工作天，用0開始，往前一天推，如果前一天不是假日，＋1，當到達2代表已有兩天工作天，也就可以抓到購買日期
//        while (workday < 2) {
//            today.add(Calendar.DATE, -1);
//            if (null != holidayRepository.findByHoliday(sdf.format(today.getTime())) || 1 != today.get(Calendar.DAY_OF_WEEK) || 7 != today.get(Calendar.DAY_OF_WEEK)) {
//                workday++;
//            }
//        }

        Double deliveryFee = tcnudRepository.getDeliveryFee(request.getBranchNo(), request.getCustSeq(), sdf.format(target.getTime()));

        if (isBlank(request.getBranchNo()) || request.getBranchNo().length() != 4) {
            return "分行代碼輸入錯誤";
        }
        if (isBlank(request.getCustSeq()) || request.getCustSeq().length() != 2) {
            return "客戶帳號輸入錯誤";
        }

//        System.out.println("======1");
//        System.out.println(tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()));  //[]-->型別 List
//        System.out.println(tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).toString());   //  "[]" -->型別 String(String的[])
//        System.out.println("======2");
//
//        isEmpty在前 跟 在後
//        isEmpty(tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()))
//        跟
//        tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).toString().isEmpty()
//        差別在 在前:因輸出型別是Sring 所以要先轉String才能判斷；在後:前面型別為List來做判斷 所以不要轉型別String

        if (tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).isEmpty()) {
            return "查無符合資料";
        }
        if (null == deliveryFee) {
            return "今日無交割金需付";
        }
        return "今日需付交割金為 : " + deliveryFee;
    }
}
