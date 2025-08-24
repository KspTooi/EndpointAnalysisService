package com.ksptooi.biz.statistics.controller;

import com.ksptooi.biz.statistics.mapper.StatisticsMapper;
import com.ksptooi.commons.annotation.PrintLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@RestController
@RequestMapping("/statistics")
@Tag(name = "数据统计", description = "数据统计")
@Slf4j
public class StatisticsController {

    @Autowired
    private StatisticsMapper mapper;

}
