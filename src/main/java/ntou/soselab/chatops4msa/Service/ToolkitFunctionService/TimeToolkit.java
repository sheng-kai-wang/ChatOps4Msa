package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeToolkit extends ToolkitFunction {

    /**
     * @return format "yyyy-MM-dd"
     */
    public String toolkitTimeNow() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return today.format(formatter);
    }

    /**
     * @return format "yyyy-MM-dd"
     */
    public String toolkitTimeOneWeekAgo() {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusWeeks(1);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return oneWeekAgo.format(formatter);
    }

    /**
     * convert to Taiwan (GMT+8)
     */
    public String toolkitTimeConvertToTaiwan(String time) {
        ZoneId utcZone = ZoneId.of("UTC");
        ZoneId targetZone = ZoneId.of("GMT+8");

        LocalDateTime dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        ZonedDateTime utcDateTime = ZonedDateTime.of(dateTime, utcZone);
        ZonedDateTime targetDateTime = utcDateTime.withZoneSameInstant(targetZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        return targetDateTime.format(formatter) + " (Taiwan)";
    }
}
