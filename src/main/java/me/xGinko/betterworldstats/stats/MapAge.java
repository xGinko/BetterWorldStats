package me.xGinko.betterworldstats.stats;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.Statistics;

import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public final class MapAge extends Statistics {

    private final Calendar calendar;
    private final Cache<Key, Integer> cache;
    private enum Key { DAYS_PART, MONTHS_PART, YEARS_PART, DAYS, MONTHS };
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
        Integer daysPart = this.cache.getIfPresent(Key.DAYS_PART);
        if (daysPart == null) {
            this.updateCalendar();
            daysPart = Math.max(this.calendar.get(Calendar.DAY_OF_MONTH) - 1, 0);
            this.cache.put(Key.DAYS_PART, daysPart);
        }
        return daysPart;
    }

    public Integer getMonthsPart() {
        Integer monthsPart = this.cache.getIfPresent(Key.MONTHS_PART);
        if (monthsPart == null) {
            this.updateCalendar();
            monthsPart = Math.max(this.calendar.get(Calendar.MONTH), 0);
            this.cache.put(Key.MONTHS_PART, monthsPart);
        }
        return monthsPart;
    }

    public Integer getYearsPart() {
        Integer yearsPart = this.cache.getIfPresent(Key.YEARS_PART);
        if (yearsPart == null) {
            this.updateCalendar();
            yearsPart = Math.max(this.calendar.get(Calendar.YEAR) - 1970, 0);
            this.cache.put(Key.YEARS_PART, yearsPart);
        }
        return yearsPart;
    }

    public Integer asDays() {
        Integer daysAmount = this.cache.getIfPresent(Key.DAYS);
        if (daysAmount == null) {
            this.updateCalendar();
            daysAmount = (int) TimeUnit.MILLISECONDS.toDays(this.getMillisSinceBirth());
            this.cache.put(Key.DAYS, daysAmount);
        }
        return daysAmount;
    }

    public Integer asMonths() {
        Integer monthsAmount = this.cache.getIfPresent(Key.MONTHS);
        if (monthsAmount == null) {
            monthsAmount = (this.getYearsPart() * 12) + this.getMonthsPart();
            this.cache.put(Key.MONTHS, monthsAmount);
        }
        return monthsAmount;
    }

    public Integer asYears() {
        return this.getYearsPart();
    }
}