package org.titanic.scheduling.task;

import lombok.AllArgsConstructor;
import org.titanic.db.entity.StrategyExecutionEntity;
import org.titanic.report.ReportService;
import org.titanic.util.SpringContextUtils;

import java.util.TimerTask;

/**
 * @author Hanno Skowronek
 */

@AllArgsConstructor
public class CreateStrategyReportTask extends TimerTask {

    private StrategyExecutionEntity strategyExecution;

    @Override
    public void run() {
        ReportService reportService = SpringContextUtils.getBean(ReportService.class);
        reportService.createAndExecuteReport(strategyExecution);
    }
}
