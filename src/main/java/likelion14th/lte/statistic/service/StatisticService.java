package likelion14th.lte.statistic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.statistic.dto.response.StatisticResponse;
import likelion14th.lte.statistic.entity.Statistic;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.user.entity.User;
import likelion14th.lte.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticService {
    private static final int BATCH_SIZE = 500;

    private final UserRepository userRepository;
    private final TodoDateRepository todoDateRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public StatisticResponse getStatistic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        return StatisticResponse.from(user.getStatistic());
    }

    @Transactional
    public void updateStatistic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        applyStatisticUpdate(user);
    }

    @Transactional
    public void updateAllStatistics() {
        int page = 0;
        Page<User> userPage;

        do {
            userPage = userRepository.findAll(PageRequest.of(page, BATCH_SIZE));

            for (User user : userPage.getContent()) {
                applyStatisticUpdate(user);
            }

            entityManager.flush();
            entityManager.clear();

            page++;
        } while (userPage.hasNext());
    }

    private void applyStatisticUpdate(User user) {
        Statistic statistic = user.getStatistic();
        Long userId = user.getId();

        LocalDate day = LocalDate.now().minusDays(1);

        boolean hasCompleted = todoDateRepository.existsByTodo_User_IdAndDateAndCompleted(userId, day, true);
        boolean hasFailed = todoDateRepository.existsByTodo_User_IdAndDateAndCompleted(userId, day, false);
        boolean isSuccess = hasCompleted && !hasFailed;

        statistic.increaseStreakIfSuccess(isSuccess);
        if (isSuccess) {
            statistic.increaseCountOf(day.getDayOfWeek());
        }

        LocalDate monthStart = day.minusDays(30);
        long completedCount = todoDateRepository.countByTodo_User_IdAndDateBetweenAndCompleted(userId, monthStart, day, true);
        long incompleteCount = todoDateRepository.countByTodo_User_IdAndDateBetweenAndCompleted(userId, monthStart, day, false);
        long totalCount = completedCount + incompleteCount;

        int monthPercent = totalCount == 0 ? 0 : (int) (completedCount * 100 / totalCount);
        statistic.updateMonthPercent(monthPercent);
    }
}
