package org.titanic.report.util;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.titanic.db.entity.TransactionEntity;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

public class ReportUtil {

    @SneakyThrows
    public static File GenerateTransactionReport(List<TransactionEntity> transactionEntities){
        StringBuilder sb = new StringBuilder(String.format(
                "%20s %2s %10s %2s %10s %2s %30s %2s %5s %2s %5s \n",
                "Time", "|", "Status", "|", "Side", "|", "Account", "|", "Price", "|", "Volume"));

        for (TransactionEntity transactionEntity : transactionEntities) {
            sb.append(String.format(
                    "%20s %2s %10s %2s %10s %2s %30s %2s %5.2f %2s %5.2f \n",
                    transactionEntity.getTimestamp().toString().substring(0,19), "|",
                    transactionEntity.getStatus(), "|",
                    transactionEntity.getSide() , "|",
                    transactionEntity.getAccount().getAccountName(),  "|",
                    transactionEntity.getPrice() ,  "|",
                    transactionEntity.getVolume()
            ));
        }

//        File file = File.createTempFile("./transaction-report-"+ transactionEntities.get(0).getSymbol(), ".txt");
//        file.deleteOnExit();
        File file = new File("./transaction-report-"+ transactionEntities.get(0).getSymbol()+".txt");
        FileUtils.writeStringToFile(file, sb.toString(), Charset.forName("UTF-8"));
        return file;
    }
}
