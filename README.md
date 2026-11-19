# Bank Teller Chat

A Java 17 console demonstration for basic teller workflows:

- Cash deposit
- Cash withdrawal
- Account-to-account transfer
- Cheque booking request
- Balance inquiry

> This demo is in-memory and is **not** suitable for real banking transactions.

## Run

```bash
mvn package
java -jar target/bank-teller-chat-1.0.0.jar
```

Example session:

```text
> balance ACC1001
> deposit ACC1001 100.00
> transfer ACC1001 ACC1002 50.00
> cheque ACC1001 250.00 John Doe
```
