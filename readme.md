##Database Entities

StrategyExecution
- fk: List(Accounts)
- fk: strategyId
- startTime - Instant
- finishTime - Instant
- duration - Double
- restAfter - Double

Transaction
- status (executed, open, failed, cancelled) - Enum
- timestamp - Instant
- symbol - String
- volume - double
- price - Double
- fk: userId (Account)
- fk: executionId

Account
- userId - Integer
- apiKey - String
- signature - String
- nonce - String

Strategy
- durationHourMin - Integer
- durationHourMax - Integer
- restingHourMin - Integer
- restingHourMax - Integer
- volume - double
- symbol - String
- telegram username - String
- active - Boolean