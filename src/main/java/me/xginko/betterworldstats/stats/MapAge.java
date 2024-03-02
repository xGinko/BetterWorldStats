package me.xginko.betterworldstats.stats;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.betterworldstats.BetterWorldStats;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MapAge {

    private final @NotNull Calendar calendar;
    private final @NotNull Cache<CalendarKey, Integer> cache;
    private enum CalendarKey { DAYS_PART, MONTHS_PART, YEARS_PART, DAYS, MONTHS };
    private final long server_birth_time_millis;

    public MapAge() {
        this.calendar = Calendar.getInstance(BetterWorldStats.getConfiguration().timeZone);
        this.cache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build();
        this.server_birth_time_millis = BetterWorldStats.getConfiguration().server_birth_time_millis;
    }

    private long getMillisSinceBirth() {
        return System.currentTimeMillis() - server_birth_time_millis;
    }

    private void updateCalendar() {
        this.calendar.setTimeInMillis(this.getMillisSinceBirth());
    }

    public Integer getDaysPart() {
        Integer daysPart = this.cache.getIfPresent(CalendarKey.DAYS_PART);
        if (daysPart == null) {
            this.updateCalendar();
            daysPart = Math.max(this.calendar.get(Calendar.DAY_OF_MONTH) - 1, 0);
            this.cache.put(CalendarKey.DAYS_PART, daysPart);
        }
        return daysPart;
    }

    public Integer getMonthsPart() {
        Integer monthsPart = this.cache.getIfPresent(CalendarKey.MONTHS_PART);
        if (monthsPart == null) {
            this.updateCalendar();
            monthsPart = Math.max(this.calendar.get(Calendar.MONTH), 0);
            this.cache.put(CalendarKey.MONTHS_PART, monthsPart);
        }
        return monthsPart;
    }

    public Integer getYearsPart() {
        Integer yearsPart = this.cache.getIfPresent(CalendarKey.YEARS_PART);
        if (yearsPart == null) {
            this.updateCalendar();
            yearsPart = Math.max(this.calendar.get(Calendar.YEAR) - 1970, 0);
            this.cache.put(CalendarKey.YEARS_PART, yearsPart);
        }
        return yearsPart;
    }

    public Integer asDays() {
        Integer daysAmount = this.cache.getIfPresent(CalendarKey.DAYS);
        if (daysAmount == null) {
            this.updateCalendar();
            daysAmount = (int) TimeUnit.MILLISECONDS.toDays(this.getMillisSinceBirth());
            this.cache.put(CalendarKey.DAYS, daysAmount);
        }
        return daysAmount;
    }

    public Integer asMonths() {
        Integer monthsAmount = this.cache.getIfPresent(CalendarKey.MONTHS);
        if (monthsAmount == null) {
            monthsAmount = (this.getYearsPart() * 12) + this.getMonthsPart();
            this.cache.put(CalendarKey.MONTHS, monthsAmount);
        }
        return monthsAmount;
    }

    public Integer asYears() {
        return this.getYearsPart();
    }
}