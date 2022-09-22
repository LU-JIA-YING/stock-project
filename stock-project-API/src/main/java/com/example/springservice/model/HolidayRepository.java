package com.example.springservice.model;

import com.example.springservice.model.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, String> {

    String findByHoliday(String holiday);
}
