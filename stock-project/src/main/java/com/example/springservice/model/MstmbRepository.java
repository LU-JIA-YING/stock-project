package com.example.springservice.model;

import com.example.springservice.model.entity.Mstmb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MstmbRepository extends JpaRepository<Mstmb, String> {

    Mstmb findByStock(String stock);

    @Query(value = "Select CurPrice From Mstmb Where Stock = ?1", nativeQuery = true)
    public double findCurPriceByStock(String Stock);
}
