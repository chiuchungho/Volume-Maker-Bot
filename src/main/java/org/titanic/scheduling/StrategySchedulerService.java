package org.titanic.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.titanic.cryptosx.dto.CryptosxAccount;
import org.titanic.cryptosx.gateway.AccountHandler;
import org.titanic.db.entity.AccountEntity;
import org.titanic.db.entity.StrategyEntity;
import org.titanic.db.entity.StrategyExecutionEntity;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.enums.Side;
import org.titanic.enums.TransactionStatus;
import org.titanic.db.gateway.*;
import org.titanic.scheduling.task.CreateStrategyReportTask;
import org.titanic.scheduling.task.TradeCheckTask;
import org.titanic.scheduling.task.TransactionExecutionTask;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StrategySchedulerService implements StrategyScheduler {

    private static final int MIN_NUMBER_OF_TRADES = 250;
    private static final int MAX_NUMBER_OF_TRADES = 250;
    private static final int MIN_REST = 6;
    private static final int MAX_REST = 12;

    private final StrategyReader strategyReader;
    private final StrategyWriter strategyWriter;
    private final StrategyExecutionWriter strategyExecutionWriter;
    private final StrategyExecutionReader strategyExecutionReader;
    private final TransactionWriter transactionWriter;
    private final TransactionReader transactionReader;
    private final AccountReader accountReader;

    private final AccountHandler accountHandler;

    private ScheduledExecutorService executorService;

    private List<StrategyEntity> activeStrategies;

    @PostConstruct
    private void init() {
        //TODO: Check for transactions that are SCHEDULED and have not been executed to add them to the executorService
        executorService = Executors.newSingleThreadScheduledExecutor();
        activeStrategies = strategyReader.getAllActiveStrategies();

        log.info("The scheduler has picked up following strategies to be run: {}", activeStrategies.stream().map(StrategyEntity::getId).toList());
    }

    //TODO: hong kong time?
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    private void scheduleStrategies() {
        log.info("Schedule daily strategies..");
        activeStrategies = strategyReader.getAllActiveStrategies();
        log.info("The scheduler has picked up following strategies to be run: {}", activeStrategies.stream().map(StrategyEntity::getId).toList());

        for (StrategyEntity strategy : activeStrategies) {
            scheduleExecution(strategy);
        }
    }

    @Override
    public StrategyEntity addStrategy(StrategyEntity strategyEntity) {
        strategyEntity.setCreated(Instant.now());
        strategyEntity = strategyWriter.saveStrategy(strategyEntity);
        log.info("A new strategy was added to the database: {}", strategyEntity.getId());
        if (strategyEntity.isActive()) {
            activeStrategies.add(strategyEntity);
            if (Instant.now().atZone(ZoneOffset.UTC).withHour(23).withMinute(59).withSecond(59).toInstant().isAfter(Instant.now().plusSeconds(strategyEntity.getDurationHourMax() * 3600L))) {
                log.info("The new strategy {} will immediately scheduled for today.", strategyEntity.getId());
                this.scheduleExecution(strategyEntity);
            } else {
                log.info("The new strategy {} will be scheduled tomorrow.", strategyEntity.getId());
            }
        }
        return strategyEntity;
    }

    @Override
    public void deactivateStrategy(int strategyId) {
        log.info("Deactivating strategy {}", strategyId);

        Optional<StrategyEntity> strategyEntity = strategyReader.getById(strategyId);
        if (strategyEntity.isPresent() && activeStrategies.contains(strategyEntity.get())){
            StrategyExecutionEntity latestExecutionOfStrategy = strategyExecutionReader.getLatestExecutionOfStrategy(strategyId).orElseThrow();
            List<TransactionEntity> transactionEntities = transactionReader.getAllTransactionsByStrategyExecution(latestExecutionOfStrategy.getId());
            for (TransactionEntity transaction : transactionEntities) {
                if (transaction.getStatus() == TransactionStatus.SCHEDULED) {
                    transaction.setStatus(TransactionStatus.CANCELLED);

                    transactionWriter.saveTransaction(transaction);
                }
            }
            activeStrategies.removeIf(s -> s.getId() == strategyId);
        }

        strategyWriter.deactivateById(strategyId);
    }

    @Override
    public void deactivateAllStrategies() {
        log.info("Deactivating all strategies");
        List<StrategyEntity> strategies = strategyReader.getAllActiveStrategies();
        for (StrategyEntity strategy : strategies) {
            List<TransactionEntity> transactionEntities = transactionReader.getAllTransactionsByStrategyExecution(strategy.getId());

            for (TransactionEntity transaction : transactionEntities) {
                if (transaction.getStatus() == TransactionStatus.SCHEDULED) {
                    transaction.setStatus(TransactionStatus.CANCELLED);

                    transactionWriter.saveTransaction(transaction);
                }
            }
            log.info("Deactivating strategy {}", strategy.getId());
            deactivateStrategy(strategy.getId());
        }

        activeStrategies = new ArrayList<>();
    }

    @Override
    public void activateStrategy(int strategyId) {
        log.info("Activating strategy {}", strategyId);

        Optional<StrategyEntity> strategy = strategyWriter.activateById(strategyId);
        strategy.ifPresent(strategyEntity -> activeStrategies.add(strategyEntity));
    }

    private void scheduleTradeCheck(long buyTransactionId, long sellTransactionId, long delay) {
        executorService.schedule(new TradeCheckTask(buyTransactionId, sellTransactionId), delay, TimeUnit.MILLISECONDS);
    }

    private StrategyExecutionEntity scheduleExecution(StrategyEntity strategy) {

        StrategyExecutionEntity strategyExecution = createAndSaveStrategyExecution(strategy);

        int numberOfTrades = this.nextInt(strategy.getNumberOfTradesMin(), strategy.getNumberOfTradesMax());
        int[] volumeArray = calcRandomIntegerVolumeArray(numberOfTrades, (int) strategy.getVolume());


        Instant latestTransaction = Instant.now();
        for (int i = 0; i < numberOfTrades; i++) {
            if (volumeArray[i] <= 0) {
                continue;
            }
            Instant timestamp = Instant.ofEpochSecond(this.nextLong(strategyExecution.getStartTime().getEpochSecond(), strategyExecution.getFinishTime().getEpochSecond()));

            if (latestTransaction.isBefore(timestamp)) {
                latestTransaction = timestamp;
            }
            //TODO: Make sure that the price range is around the currently traded price
            double price = Math.floor(this.nextDouble(strategy.getPriceMin(), strategy.getPriceMax()) * 10000) / 10000;
            int buyingAccount = this.nextInt(0,2);

            TransactionEntity buyingTransactionEntity = TransactionEntity.builder().timestamp(timestamp).symbol(strategy.getSymbol()).volume(volumeArray[i]).price(price).side(Side.Buy).status(TransactionStatus.SCHEDULED).account(strategyExecution.getAccounts().get(buyingAccount)).execution(strategyExecution).build();
            TransactionEntity sellingTransactionEntity = TransactionEntity.builder().timestamp(timestamp).symbol(strategy.getSymbol()).volume(volumeArray[i]).price(price).side(Side.Sell).status(TransactionStatus.SCHEDULED).account(strategyExecution.getAccounts().get((buyingAccount + 1) % 2)).execution(strategyExecution).build();

            buyingTransactionEntity = transactionWriter.saveTransaction(buyingTransactionEntity);
            sellingTransactionEntity = transactionWriter.saveTransaction(sellingTransactionEntity);

            long delay = Instant.now().until(timestamp, ChronoUnit.MILLIS);
            executorService.schedule(new TransactionExecutionTask(buyingTransactionEntity.getId()), delay, TimeUnit.MILLISECONDS);
            executorService.schedule(new TransactionExecutionTask(sellingTransactionEntity.getId()), delay, TimeUnit.MILLISECONDS);

            scheduleTradeCheck(buyingTransactionEntity.getId(), sellingTransactionEntity.getId(), delay + 30000);
        }

        executorService.schedule(new CreateStrategyReportTask(strategyExecution), Instant.now().until(latestTransaction.plusSeconds(300), ChronoUnit.MILLIS), TimeUnit.MILLISECONDS);

        // update strategy and save
        strategy.setLastExecution(strategy.getNextExecution());
        strategy.setNextExecution(strategyExecution.getStartTime());

        strategyWriter.saveStrategy(strategy);

        return strategyExecution;
    }

    private StrategyExecutionEntity createAndSaveStrategyExecution(StrategyEntity strategy) {
        double durationHour = this.nextDouble(strategy.getDurationHourMin(), strategy.getDurationHourMax());
        double restHour = this.nextDouble(MIN_REST, MAX_REST);
        Optional<StrategyExecutionEntity> latestExecution = strategyExecutionReader.getLatestExecutionOfStrategy(strategy.getId());

        Instant earliestStartTime = latestExecution.map(executionEntity -> executionEntity.getFinishTime().plusSeconds((long) (restHour * 3600))).orElseGet(Instant::now);
        Instant latestStartTime = Instant.now().atZone(ZoneOffset.UTC).withHour(23).withMinute(59).withSecond(59).toInstant().minusSeconds((long) (durationHour * 3600));

        if (earliestStartTime.isAfter(latestStartTime)) {
            earliestStartTime = latestStartTime;
        }
//        Instant startTime = Instant.now().plusSeconds(30);
//        Instant finishTime = startTime.plusSeconds(120);
        Instant startTime = Instant.ofEpochSecond(this.nextLong(earliestStartTime.getEpochSecond(), latestStartTime.getEpochSecond()));
        Instant finishTime = startTime.plusSeconds((int) (durationHour * 3600));

        List<CryptosxAccount> accounts = accountHandler.getAccounts(2);
        List<AccountEntity> accountEntities = accountReader.getAccounts(accounts.stream().map(CryptosxAccount::getUserId).toList());

        StrategyExecutionEntity strategyExecution = StrategyExecutionEntity.builder().startTime(startTime).finishTime(finishTime).duration(durationHour).strategy(strategy).accounts(accountEntities).build();
        return strategyExecutionWriter.saveStrategyExecution(strategyExecution);
    }

    private int[] calcRandomIntegerVolumeArray(int count, int sum) {
        int[] values = new int[count];

        for (int i = 0; i < count - 1; i++) {
            values[i] = ThreadLocalRandom.current().nextInt(sum);
        }
        values[count - 1] = sum;
        Arrays.sort(values);
        for (int i = count - 1; i > 0; i--) {
            values[i] -= values[i - 1];
        }

        return values;
    }

    private int nextInt(int origin, int bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextInt(origin, bound);
    }

    private double nextDouble(double origin, double bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextDouble(origin, bound);
    }

    private long nextLong(long origin, long bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextLong(origin, bound);
    }
}