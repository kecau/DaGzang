package com.universal.recommender.controller;

import com.universal.recommender.service.StatisticService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(final StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("statistic-rating")
    public String showRatingStatistic(Model model) {
        List<Long> ratingStatistics = statisticService.getAllRatingStatistics();

        model.addAttribute("ratingStatistics", ratingStatistics);
        return "rating-statistic";
    }
}
