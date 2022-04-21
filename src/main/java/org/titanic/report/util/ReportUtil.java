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
        StringBuilder sb = new StringBuilder("Time,Status,Side,Account,Price,Volume\n");
        for (TransactionEntity transactionEntity : transactionEntities) {
            sb.append(transactionEntity.getTimestamp().toString().substring(0,19) +","+
                    transactionEntity.getStatus() +","+
                    transactionEntity.getSide() +","+
                    transactionEntity.getAccount().getAccountName()+ ","+
                    transactionEntity.getPrice() +  ","+
                    transactionEntity.getVolume()+ "\n");
        }

//        File file = File.createTempFile("./transaction-report-"+ transactionEntities.get(0).getSymbol(), ".txt");
//        file.deleteOnExit();
        File file = new File("./transaction-report-"+ transactionEntities.get(0).getSymbol()+".csv");
        FileUtils.writeStringToFile(file, sb.toString(), Charset.forName("UTF-8"));
        return file;
    }
}
