package com.example.springservice.model;

import com.example.springservice.model.entity.HcmioAndTcnudCompositePK;
import com.example.springservice.model.entity.Tcnud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TcnudRepository extends JpaRepository<Tcnud, HcmioAndTcnudCompositePK> {

    Tcnud findByTradeDateAndBranchNoAndCustSeqAndDocSeq(String tradeDate,String branchNo,String CustSeq,String Docseq);

    List<Tcnud> findByBranchNoAndCustSeq(String branchNo, String custSeq);

    List<Tcnud> findByBranchNoAndCustSeqAndStock(String branchNo, String custSeq,String stock);

    //  抓指定用戶的購買股票，重複不抓
    @Query(value = "select distinct stock from tcnud where branchNo= ?1 AND custSeq= ?2", nativeQuery = true)
    List<String> findDistinctStock(String branchNo, String custSeq);

    // 計算交割金額
    @Query(value = "select sum(cost) from tcnud where branchNo= ?1 AND custSeq= ?2 And tradeDate= ?3", nativeQuery = true)
    Double getDeliveryFee(String branchNo, String custSeq,String tradeDate);

}
