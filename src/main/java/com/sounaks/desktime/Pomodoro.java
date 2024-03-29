package com.sounaks.desktime;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pomodoro {
    private String workLabel, breakLabel, restLabel;
    private Duration workTime, breakTime, restTime;
    private int numWorkBreakCycles;
    private boolean canRest;
    private int durationType;
    private String name;
    private Instant instant;
    private Duration position;

    public static final int OF_SECONDS = 1;
    public static final int OF_MINUTES = 2;

    public static final int SHOW_MINUTE_SECOND = 4;
    public static final int SHOW_HOUR_MINUTE_SECOND = 5;

    // Constructor
    public Pomodoro(String workLabel, Duration workTime, String breakLabel, Duration breakTime, String restLabel, Duration restTime, int numWorkBreakCycles, String name) {
        if (numWorkBreakCycles <= 0) {
            throw new IllegalArgumentException("numWorkBreakCycles should be > 0.");
        }
        this.workLabel          = workLabel;
        this.workTime           = workTime;
        this.breakLabel         = breakLabel;
        this.breakTime          = breakTime;
        this.restLabel          = restLabel;
        this.restTime           = restTime;
        this.numWorkBreakCycles = numWorkBreakCycles;
        this.canRest            = (restTime != null && !restTime.isNegative() && !restTime.isZero());
        this.durationType       = Pomodoro.OF_SECONDS;
        this.name               = name;
        this.instant            = Instant.now();
        this.position           = Duration.ZERO;
    }

    public Pomodoro(String workLabel, int workTime, String breakLabel, int breakTime, String restLabel, int restTime, int numWorkBreakCycles, int durationType, String name) {
        if (durationType == Pomodoro.OF_MINUTES && durationType == Pomodoro.OF_SECONDS) {
            throw new IllegalArgumentException("int durationType can only be Pomodoro.OF_MINUTES or Pomodoro.OF_SECONDS");
        }
        if (numWorkBreakCycles <= 0) {
            throw new IllegalArgumentException("numWorkBreakCycles should be > 0.");
        }
        this.workLabel          = workLabel;
        this.workTime           = (durationType == Pomodoro.OF_MINUTES ? Duration.ofMinutes(workTime) : Duration.ofSeconds(workTime));
        this.breakLabel         = breakLabel;
        this.breakTime          = (durationType == Pomodoro.OF_MINUTES ? Duration.ofMinutes(breakTime) : Duration.ofSeconds(breakTime));
        this.restLabel          = restLabel;
        this.restTime           = (durationType == Pomodoro.OF_MINUTES ? Duration.ofMinutes(restTime) : Duration.ofSeconds(restTime));
        this.numWorkBreakCycles = numWorkBreakCycles;
        this.canRest            = (restTime > 0);
        this.durationType       = durationType;
        this.name               = name;
        this.instant            = Instant.now();
        this.position           = Duration.ZERO;
    }

    public Pomodoro(String definition) {
        // Split the input string by comma
        String[] parts = definition.split(",");
        
        // Ensure there are 2 or 3 parts
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("Definition string must have 2 or 3 parts separated by commas.");
        }
        
        if (parts.length == 2)
            this.canRest = false;
        else
            this.canRest = true;
        
        // Define regex pattern to match an integer followed by 'min', 'minute', 'minutes', 'sec', 'second', or 'seconds'
        Pattern pattern = Pattern.compile("\\s*(\\d+)\\s*(min(?:ute|.)?s?|sec(?:ond|.)?s?)\\s*(\\S+)\\s*");
        
        for (int i = 0; i < parts.length; i++) {
            Matcher matcher = pattern.matcher(parts[i]);
            
            // Ensure the part matches the pattern
            if (!matcher.find()) {
                throw new IllegalArgumentException("Each part must start with an integer followed by 'min', 'minute', 'minutes', 'sec', 'second', or 'seconds', and end with a word.");
            }
            
            // Extract the integer, unit, and worktype
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);
            String worktype = matcher.group(3);
            
            // Store the extracted variables
            if (unit.toLowerCase().startsWith("min")) {
                this.durationType = Pomodoro.OF_MINUTES;
            } else if (unit.toLowerCase().startsWith("sec")) {
                this.durationType = Pomodoro.OF_SECONDS;
            } else {
                throw new IllegalArgumentException("Each part must start with an integer followed by 'min', 'minute', 'minutes', 'sec', 'second', or 'seconds', and end with a word.");
            }

            if (i == 0) {
                this.workLabel = worktype;
                this.workTime = this.durationType == Pomodoro.OF_MINUTES ? Duration.ofMinutes(value) : Duration.ofSeconds(value);
            } else if (i == 1) {
                this.breakLabel = worktype;
                this.breakTime = this.durationType == Pomodoro.OF_MINUTES ? Duration.ofMinutes(value) : Duration.ofSeconds(value);
            } else if (i == 2) {
                this.restLabel = worktype;
                this.restTime = this.durationType == Pomodoro.OF_MINUTES ? Duration.ofMinutes(value) : Duration.ofSeconds(value);
            } else {
                throw new IllegalArgumentException("4th part found! Input string must have 2 or 3 parts separated by commas.");
            }
        }
        // The below if condition is for hard coding world famous pomodoro cycle
        if (canRest && workTime.toMinutes() == 25 && breakTime.toMinutes() == 5 && restTime.toMinutes() == 30) {
            this.numWorkBreakCycles = 4;
        } else {
            this.numWorkBreakCycles = 1;
        }
        this.name               = definition;
        this.instant            = Instant.now();
        this.position           = Duration.ZERO;
    }

    public Pomodoro() {
        this("25 min. work, 5 min. break, 30 min. rest");
    }

    public void reset() {
        this.instant  = Instant.now();
        this.position = Duration.ZERO;
    }

    private Duration getRunningDuration() {
        return Duration.between(instant, Instant.now());
    }

    public String getRunningLabel() {
        int x = getTotalDuration().compareTo(getRunningDuration());
        if (x < 0) {
            position = getRunningDuration().minus(getTotalDuration().multipliedBy(getRunCount()));
        } else {
            position = getRunningDuration();
        }

        if (position.compareTo(getWorkTime()) < 0) {
            return getWorkLabel();
        } else if (position.compareTo(getWorkBreakDuration()) < 0) {
            return getBreakLabel();
        } else if (position.compareTo(getTotalDuration().minus(getRestTime())) < 0) {
            Duration tmpPos = Duration.ofSeconds(position.getSeconds() % getWorkBreakDuration().getSeconds());
            if (tmpPos.compareTo(getWorkTime()) < 0) {
                return getWorkLabel();
            } else if (tmpPos.compareTo(getWorkBreakDuration()) < 0) {
                return getBreakLabel();
            } else {
                throw new IllegalStateException("Duration " + tmpPos.toString() + " out of bounds.");
            }
        } else if (position.compareTo(getTotalDuration()) < 0) {
            return getRestLabel();
        } else {
            throw new IllegalStateException("Duration " + position.toString() + " out of bounds.");
        }
    }

    public Duration getRunningLabelDuration(boolean reverse) {
        int x = getTotalDuration().compareTo(getRunningDuration());
        if (x < 0) {
            position = getRunningDuration().minus(getTotalDuration().multipliedBy(getRunCount()));
        } else {
            position = getRunningDuration();
        }

        if (position.compareTo(getWorkTime()) < 0) {
            if (reverse) {
                return getWorkTime().minus(position).plus(Duration.ofSeconds(1));
            } else {
                return position.plus(Duration.ofSeconds(1));
            }
        } else if (position.compareTo(getWorkBreakDuration()) < 0) {
            if (reverse) {
                return getWorkBreakDuration().minus(position).plus(Duration.ofSeconds(1));
            } else {
                return position.minus(getWorkTime()).plus(Duration.ofSeconds(1));
            }
        } else if (position.compareTo(getTotalDuration().minus(getRestTime())) < 0) {
            Duration tmpPos = Duration.ofSeconds(position.getSeconds() % getWorkBreakDuration().getSeconds());
            if (tmpPos.compareTo(getWorkTime()) < 0) {
                if (reverse) {
                    return getWorkTime().minus(tmpPos);
                } else {
                    return tmpPos.plus(Duration.ofSeconds(1));
                }
            } else if (tmpPos.compareTo(getWorkBreakDuration()) < 0) {
                if (reverse) {
                    return getWorkBreakDuration().minus(tmpPos);
                } else {
                    return tmpPos.minus(getWorkTime()).plus(Duration.ofSeconds(1));
                }
            } else {
                throw new IllegalStateException("Duration " + tmpPos.toString() + " out of bounds.");
            }
        } else if (position.compareTo(getTotalDuration()) < 0) {
            if (reverse) {
                return getTotalDuration().minus(position).plus(Duration.ofSeconds(1));
            } else {
                return position.minus(getWorkBreakDuration().multipliedBy(numWorkBreakCycles)).plus(Duration.ofSeconds(1));
            }
        } else {
            throw new IllegalStateException("Duration " + position.toString() + " out of bounds.");
        }
    }

    public int getRunCount() {
        long runCount = 0;
        int x = getTotalDuration().compareTo(getRunningDuration());
        if (x < 0) {
            runCount = getRunningDuration().getSeconds() / getTotalDuration().getSeconds();
        }
        return (int)runCount;
    }

    // Getter and Setter methods
    public String getWorkLabel() {
        return workLabel.toUpperCase();
    }

    public void setWorkLabel(String workLabel) {
        this.workLabel = workLabel;
    }

    public Duration getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Duration workTime) {
        this.workTime = workTime;
    }

    public String getBreakLabel() {
        return breakLabel.toUpperCase();
    }

    public void setBreakLabel(String breakLabel) {
        this.breakLabel = breakLabel;
    }

    public Duration getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(Duration breakTime) {
        this.breakTime = breakTime;
    }

    public String getRestLabel() throws NullPointerException {
        if (canRest)
            return restLabel.toUpperCase();
        else
            throw new NullPointerException("No resting schedule defined for this Pomodoro.");
    }

    public void setRestLabel(String restLabel) {
        this.restLabel = restLabel;
    }

    public Duration getRestTime() {
        if (canRest)
            return restTime;
        else
            return Duration.ZERO;
    }

    public void setRestTime(Duration restTime) {
        this.restTime = restTime;
    }

    public Duration getTotalDuration() {
        if (canRest) {
            return getWorkBreakDuration().multipliedBy(numWorkBreakCycles).plus(restTime);
        } else {
            return workTime.plus(breakTime);
        }
    }

    public Duration getWorkBreakDuration() {
        return workTime.plus(breakTime);
    }

    public int getNumWorkBreakCycles() {
        return numWorkBreakCycles;
    }

    public void setNumWorkBreakCycles(int numWorkBreakCycles) {
        if (numWorkBreakCycles <= 0) {
            throw new IllegalArgumentException("numWorkBreakCycles should be > 0.");
        }
        this.numWorkBreakCycles = numWorkBreakCycles;
    }

    public boolean checkCanRest() {
        return canRest;
    }

    // No setter for canRest as it's private
    public int getDurationType() {
        return durationType;
    }

    public void setDurationType(int durationType) {
        this.durationType = durationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
