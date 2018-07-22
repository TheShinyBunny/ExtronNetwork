package com.extron.network.api.utils;

import org.bukkit.ChatColor;

import java.time.LocalDateTime;
import java.time.YearMonth;

public class TimeStamp {

    private int date;
    private int month;
    private int year;
    private int second;
    private int minute;
    private int hour;
    private boolean never;

    public TimeStamp() {
        this(true,true,true,true,true,true);
    }

    public TimeStamp(boolean withHours, boolean withMinutes, boolean withSeconds, boolean withDate, boolean withMonth, boolean withYear) {
        this.date = withDate ? LocalDateTime.now().getDayOfMonth() : 1;
        this.month = withMonth ? LocalDateTime.now().getMonth().getValue(): 1;
        this.year = withYear ? LocalDateTime.now().getYear(): 2018;
        this.second = withSeconds ? LocalDateTime.now().getSecond(): 0;
        this.minute = withMinutes ? LocalDateTime.now().getMinute(): 0;
        this.hour = withHours ? LocalDateTime.now().getHour(): 0;
        this.never = false;
    }

    public TimeStamp(String timeString) {
        if (timeString.equalsIgnoreCase("never")) {
            this.never = true;
        } else {
            String[] dateHour = timeString.split(",");
            String[] date = dateHour[0].split("/");
            String[] hour = dateHour[1].split(":");
            this.month = Integer.parseInt(date[0]);
            this.date = Integer.parseInt(date[1]);
            this.year = Integer.parseInt(date[2]);
            this.hour = Integer.parseInt(hour[0]);
            this.minute = Integer.parseInt(hour[1]);
            this.second = Integer.parseInt(hour[2]);
            this.never = false;
        }
    }

    public TimeStamp addFromNiceString(String s) throws Exception {
        int i;
        int num = 0;
        for (i = 0; i < s.length(); i++) {
            try {
                int x = Integer.parseInt(Character.toString(s.charAt(i)));
                num *= 10;
                num += x;
            } catch (NumberFormatException e) {
                break;
            }
        }
        if (i >= s.length()) {
            throw new Exception("out of bounds!");
        }
        s = s.substring(i,s.length()).toLowerCase();
        switch (s) {
            case "s":
                this.addSeconds(num);
                return this;
            case "mi":
                this.addSeconds(num * 60);
                return this;
            case "h":
                this.addSeconds(num * 60 * 60);
                return this;
            case "d":
                this.addSeconds(num * 60 * 60 * 24);
                return this;
            case "mo":
                this.addSeconds(num * 60 * 60 * 24 * 30);
                return this;
            case "y":
                this.addSeconds(num * 60 * 60 * 24 * 365);
                return this;
                default:
                    throw new Exception("invalid getValue type " + s);
        }
    }

    public void addSeconds(int seconds) {
        this.second += seconds;
        while (this.second >= 60) {
            this.minute++;
            this.second -= 60;
            while (this.minute >= 60) {
                this.hour++;
                this.minute -= 60;
                while (this.hour >= 24) {
                    this.date++;
                    this.hour -= 24;
                    while (this.date >= getDaysInMonth(this.year,this.month)) {
                        this.date -= getDaysInMonth(this.year,this.month);
                        this.month++;
                        while (this.month >= 12) {
                            this.year++;
                            this.month -= 12;
                        }
                    }
                }
            }
        }
    }

    public static int getDaysInMonth(int year, int month) {
        return YearMonth.of(year,month).lengthOfMonth();
    }

    public int getYear() {
        return year;
    }

    public int getSecond() {
        return second;
    }

    public int getMonth() {
        return month;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static TimeStamp now() {
        return new TimeStamp();
    }

    public static TimeStamp today() {
        return new TimeStamp(false,false,false,true,true,true);
    }

    @Override
    public String toString() {
        return never ? "never" : String.format("%d/%d/%d,%d:%d:%d",month,date,year,hour,minute,second);
    }

    public String howLongAgo(TimeStamp other, int paramsCount) {
        if (other.never || this.never) {
            return ChatColor.ITALIC + "Unknown";
        }
        if (other.isBefore(this)) {
            StringBuilder b = new StringBuilder();
            int year = this.year - other.year;
            int month = this.month - other.month;
            if (month < 0) {
                month += 12;
                year -= 1;
            }
            int date = this.date - other.date;
            if (date < 0) {
                date += getDaysInMonth(other.year, other.month);
                month -= 1;
            }
            int hour = this.hour - other.hour;
            if (hour < 0) {
                hour += 24;
                date -= 1;
            }
            int minute = this.minute - other.minute;
            if (minute < 0) {
                minute += 60;
                hour -= 1;
            }
            int second = this.second - other.second;
            if (second < 0) {
                second += 60;
                minute -= 1;
            }
            boolean hb = false;
            int i = 0;
            if (year > 0) {
                b.append(year + " " + TextUtils.addNeededS(year, "year"));
                hb = true;
                i++;
            }
            if (i < paramsCount) {
                if (month > 0) {
                    if (hb) b.append(", ");
                    b.append(month + " " + TextUtils.addNeededS(month, "month"));
                    hb = true;
                    i++;
                }
                if (i < paramsCount) {
                    if (date > 0) {
                        if (hb) b.append(", ");
                        b.append(date + " " + TextUtils.addNeededS(date, "day"));
                        hb = true;
                        i++;
                    }
                    if (i < paramsCount) {
                        if (hour > 0) {
                            if (hb) b.append(", ");
                            b.append(hour + " " + TextUtils.addNeededS(hour, "hour"));
                            hb = true;
                            i++;
                        }
                        if (i < paramsCount) {
                            if (minute > 0) {
                                if (hb) b.append(", ");
                                b.append(minute + " " + TextUtils.addNeededS(minute, "minute"));
                                hb = true;
                                i++;
                            }
                            if (i < paramsCount) {
                                if (second > 0) {
                                    if (hb) b.append(", ");
                                    b.append(second + " " + TextUtils.addNeededS(second, "second"));
                                }
                            }
                        }
                    }
                }
            }
            return b.toString();
        } else {
            return "0 seconds";
        }
    }

    public boolean isBefore(TimeStamp time) {
        if (time.isNever()) return true;
        if (this.isNever()) return false;
        if (this.year == time.year) {
            if (this.month == time.month) {
                if (this.date == time.date) {
                    if (this.hour == time.hour) {
                        if (this.minute == time.minute) {
                            return this.second < time.second;
                        } else {
                            return this.minute < time.minute;
                        }
                    } else {
                        return this.hour < time.hour;
                    }
                } else {
                    return this.date < time.date;
                }
            } else {
                return this.month < time.month;
            }
        } else {
            return this.year < time.year;
        }
    }

    public void setNever(boolean never) {
        this.never = never;
    }

    public boolean isNever() {
        return never;
    }

    public static TimeStamp never() {
        TimeStamp t = new TimeStamp();
        t.setNever(true);
        return t;
    }

    public int secondDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.secondDifference(this);
        int secs = this.second - timeBefore.second;
        if (secs < 0) {
            secs += 60 * minuteDifference(timeBefore);
        }
        return secs;
    }

    public int minuteDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.minuteDifference(this);
        int mins = this.minute - timeBefore.minute;
        if (mins < 0) {
            mins += 60 * hourDifference(timeBefore);
        }
        return mins;
    }

    public int hourDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.hourDifference(this);
        int hours = this.hour - timeBefore.hour;
        if (hours < 0) {
            hours += 24 * dayDifference(timeBefore);
        }
        return hours;
    }

    public int dayDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.dayDifference(this);
        int days = this.date - timeBefore.date;
        if (days < 0) {
            days += getDaysInMonth(timeBefore.year,timeBefore.month) * monthDifference(timeBefore);
        }
        return days;
    }

    public int monthDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.monthDifference(this);
        int months = this.month - timeBefore.month;
        if (months < 0) {
            months += 12 * yearDifference(timeBefore);
        }
        return months;
    }

    public int yearDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.yearDifference(this);
        return this.year - timeBefore.year;
    }

    public String difference(TimeStamp other) {
        StringBuilder b = new StringBuilder();
        int years = other.yearDifference(this);
        int months = other.monthDifference(this);
        int days = other.dayDifference(this);
        int hours = other.hourDifference(this);
        int minutes = other.minuteDifference(this);
        int seconds = other.secondDifference(this);
        if (years > 0) {
            b.append(years + "y, ");
            months -= years * 12;
        }

        if (months > 0) {
            b.append(months + "mo, ");
            days -= months * 30;
        }

        if (days > 0) {
            b.append(days + "d, ");
            hours -= days * 24;
        }

        if (hours > 0) {
            b.append(hours + "h, ");
            minutes -= hours * 60;
        }

        if (minutes > 0) {
            b.append(minutes + "mi, ");
            seconds -= minutes * 60;
        }
        if (seconds > 0) {
            b.append(seconds + "s, ");
        }
        b.delete(b.length() - 2, b.length());
        return b.toString();
    }

    public boolean isInPast() {
        return this.isBefore(new TimeStamp());
    }

    public boolean isInFuture() {
        return new TimeStamp().isBefore(this);
    }
}
