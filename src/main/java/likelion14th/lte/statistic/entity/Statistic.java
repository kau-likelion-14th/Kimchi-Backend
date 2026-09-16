package likelion14th.lte.statistic.entity;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.todo.entity.WeekEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "statistic")
public class Statistic extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistic_id")
    private Long id;

    @Column(nullable = false)
    private int streak;

    @Column(name = "month_percent", nullable = false)
    private int monthPercent;

    @OneToMany(mappedBy = "statistic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatWeek> statWeeks = new ArrayList<>();

    public static Statistic createDefault() {
        Statistic statistic = new Statistic();
        statistic.streak = 0;
        statistic.monthPercent = 0;
        for (WeekEnum week : WeekEnum.values()) {
            statistic.statWeeks.add(StatWeek.create(statistic, week));
        }
        return statistic;
    }

    public WeekEnum getMostTodoWeek() {
        return statWeeks.stream()
                .max(Comparator.comparingInt(StatWeek::getCount))
                .map(StatWeek::getWeek)
                .orElse(null);
    }

    public void increaseStreakIfSuccess(boolean success) {
        this.streak = success ? this.streak + 1 : 0;
    }

    public void updateMonthPercent(int monthPercent) {
        this.monthPercent = monthPercent;
    }

    public void increaseCountOf(DayOfWeek dayOfWeek) {
        statWeeks.stream()
                .filter(statWeek -> statWeek.getWeek().toDayOfWeek() == dayOfWeek)
                .findFirst()
                .ifPresent(StatWeek::increaseCount);
    }
}
