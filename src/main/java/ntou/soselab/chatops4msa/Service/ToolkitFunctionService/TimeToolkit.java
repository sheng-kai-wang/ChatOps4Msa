package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeToolkit extends ToolkitFunction {

    /**
     * @return format "yyyy-MM-dd"
     */
    public String toolkitTimeOneWeekAgo() {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusWeeks(1);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return oneWeekAgo.format(formatter);
    }
}
