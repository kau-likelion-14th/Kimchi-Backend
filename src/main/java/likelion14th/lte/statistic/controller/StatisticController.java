package likelion14th.lte.statistic.controller;

import io.swagger.v3.oas.annotations.Operation;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.SuccessCode;
import likelion14th.lte.statistic.dto.response.StatisticResponse;
import likelion14th.lte.statistic.service.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping
    @Operation(summary = "통계 조회", description = "유저 아이디를 받아 통계(연속 성공일, 최근 30일 완료율, 가장 투두를 많이 완료한 요일)를 조회하는 api입니다.")
    public ApiResponse<StatisticResponse> getStatistic(
            @RequestParam Long userId
    ) {
        StatisticResponse response = statisticService.getStatistic(userId);
        return ApiResponse.onSuccess(SuccessCode.STATISTICS_GET_SUCCESS, response);
    }
}
