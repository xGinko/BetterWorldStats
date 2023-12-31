package me.xGinko.betterworldstats.stats;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class ServerAge {

    private final Calendar calendar;
    private final Cache<Key, Integer> cache;
    private enum Key { DAYS_PART, MONTHS_PART, YEARS_PART, DAYS, MONTHS };
    private final long server_birth_time_millis;

    public ServerAge(final long server_birth_time_millis) {
        this.calendar = Calendar.getInstance(TimeZone.getDefault());
        this.cache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(15)).build();
        this.server_birth_time_millis = server_birth_time_millis;
    }

    private long getMillisSinceBirth() {
        return System.currentTimeMillis() - server_birth_time_millis;
    }

    private void updateCalenderTime() {
        this.calendar.setTimeInMillis(this.getMillisSinceBirth());
    }

    public Integer getDaysPart() {
        Integer daysPart = this.cache.getIfPresent(Key.DAYS_PART);
        if (daysPart == null) {
            this.updateCalenderTime();
            daysPart = Math.max(this.calendar.get(Calendar.DAY_OF_MONTH) - 1, 0);
            this.cache.put(Key.DAYS_PART, daysPart);
        }
        return daysPart;
    }

    public Integer getMonthsPart() {
        Integer monthsPart = this.cache.getIfPresent(Key.MONTHS_PART);
        if (monthsPart == null) {
            this.updateCalenderTime();
            monthsPart = Math.max(this.calendar.get(Calendar.MONTH), 0);
            this.cache.put(Key.MONTHS_PART, monthsPart);
        }
        return monthsPart;
    }

    public Integer getYearsPart() {
        Integer yearsPart = this.cache.getIfPresent(Key.YEARS_PART);
        if (yearsPart == null) {
            this.updateCalenderTime();
            yearsPart = Math.max(this.calendar.get(Calendar.YEAR) - 1970, 0);
            this.cache.put(Key.YEARS_PART, yearsPart);
        }
        return yearsPart;
    }

    public Integer asDays() {
        Integer daysAmount = this.cache.getIfPresent(Key.DAYS);
        if (daysAmount == null) {
            this.updateCalenderTime();
            daysAmount = (int) TimeUnit.MILLISECONDS.toDays(this.getMillisSinceBirth());
            this.cache.put(Key.DAYS, daysAmount);
        }
        return daysAmount;
    }

    public Integer asMonths() {
        Integer monthsAmount = this.cache.getIfPresent(Key.MONTHS);
        if (monthsAmount == null) {
            monthsAmount = (this.getYearsPart() * 12) + this.getMonthsPart();
            this.cache.put(Key.DAYS, monthsAmount);
        }
        return monthsAmount;
    }

    public Integer asYears() {
        return this.getYearsPart();
    }
}