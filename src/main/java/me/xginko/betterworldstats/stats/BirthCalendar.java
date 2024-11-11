package me.xginko.betterworldstats.stats;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.betterworldstats.BetterWorldStats;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class BirthCalendar {

    private final @NotNull Calendar calendar;
    private final @NotNull Cache<CalendarKey, Integer> cache;
    private enum CalendarKey { DAYS_PART, MONTHS_PART, YEARS_PART, DAYS, MONTHS };
    private final long server_birth_time_millis;

    public BirthCalendar() {
        this.calendar = Calendar.getInstance(BetterWorldStats.config().timeZone);
        this.cache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build();
        this.server_birth_time_millis = BetterWorldStats.config().server_birth_time_millis;
    }

    private long getMillisSinceBirth() {
        return System.currentTimeMillis() - server_birth_time_millis;
    }

    private void updateCalendar() {
        calendar.setTimeInMillis(getMillisSinceBirth());
    }

    @SuppressWarnings("DataFlowIssue")
    public @NotNull Integer getDaysPart() {
        return cache.get(CalendarKey.DAYS_PART, k -> {
            updateCalendar();
            return Math.max(calendar.get(Calendar.DAY_OF_MONTH) - 1, 0);
        });
    }

    @SuppressWarnings("DataFlowIssue")
    public @NotNull Integer getMonthsPart() {
        return cache.get(CalendarKey.MONTHS_PART, k -> {
            updateCalendar();
            return Math.max(calendar.get(Calendar.MONTH), 0);
        });
    }

    @SuppressWarnings("DataFlowIssue")
    public @NotNull Integer getYearsPart() {
        return cache.get(CalendarKey.YEARS_PART, k -> {
            updateCalendar();
            return Math.max(calendar.get(Calendar.YEAR) - 1970, 0);
        });
    }

    @SuppressWarnings("DataFlowIssue")
    public @NotNull Integer asDays() {
        return cache.get(CalendarKey.DAYS, k -> {
            updateCalendar();
            return (int) TimeUnit.MILLISECONDS.toDays(getMillisSinceBirth());
        });
    }

    @SuppressWarnings("DataFlowIssue")
    public @NotNull Integer asMonths() {
        return cache.get(CalendarKey.MONTHS, k -> {
            updateCalendar();
            return (getYearsPart() * 12) + getMonthsPart();
        });
    }

    public @NotNull Integer asYears() {
        return getYearsPart();
    }
}