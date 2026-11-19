import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class BankTellerChat {
    private final Map<String, Account> accounts = new HashMap<>();

    public BankTellerChat() {
        accounts.put("ACC1001", new Account("ACC1001", new BigDecimal("5000.00")));
        accounts.put("ACC1002", new Account("ACC1002", new BigDecimal("2500.00")));
    }

    public static void main(String[] args) {
        new BankTellerChat().run();
    }

    private void run() {
        System.out.println("Demo Teller Chat — type 'help' for commands; 'exit' to quit.");
        System.out.println("Demo accounts: ACC1001, ACC1002");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye.");
                    break;
                }

                try {
                    handle(input);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Unable to process: " + e.getMessage());
                }
            }
        }
    }

    private void handle(String input) {
        String[] parts = input.split("\\s+", 4);
        String command = parts[0].toLowerCase();

        switch (command) {
            case "help" -> printHelp();
            case "balance" -> {
                requireParts(parts, 2, "balance <account>");
                Account account = findAccount(parts[1]);
                System.out.println(account.id + " balance: " + account.balance);
            }
            case "deposit" -> {
                requireParts(parts, 3, "deposit <account> <amount>");
                Account account = findAccount(parts[1]);
                BigDecimal amount = parseAmount(parts[2]);
                account.balance = account.balance.add(amount);
                System.out.println("Demo deposit recorded. New balance: " + account.balance);
            }
            case "withdraw" -> {
                requireParts(parts, 3, "withdraw <account> <amount>");
                Account account = findAccount(parts[1]);
                BigDecimal amount = parseAmount(parts[2]);
                ensureFunds(account, amount);
                account.balance = account.balance.subtract(amount);
                System.out.println("Demo withdrawal recorded. New balance: " + account.balance);
            }
            case "transfer" -> {
                requireParts(parts, 4, "transfer <from-account> <to-account> <amount>");
                Account from = findAccount(parts[1]);
                Account to = findAccount(parts[2]);
                if (from == to) {
                    throw new IllegalArgumentException("source and destination must differ");
                }
                BigDecimal amount = parseAmount(parts[3]);
                ensureFunds(from, amount);
                from.balance = from.balance.subtract(amount);
                to.balance = to.balance.add(amount);
                System.out.println("Demo transfer recorded. Reference: " + reference());
            }
            case "cheque" -> {
                requireParts(parts, 4, "cheque <account> <amount> <payee>");
                findAccount(parts[1]);
                BigDecimal amount = parseAmount(parts[2]);
                String payee = parts[3].trim();
                if (payee.isEmpty()) {
                    throw new IllegalArgumentException("payee is required");
                }
                System.out.println("Cheque request booked as PENDING. Reference: " + reference()
                        + "; account: " + parts[1] + "; amount: " + amount
                        + "; payee: " + payee);
            }
            default -> System.out.println("Unknown command. Type 'help' for commands.");
        }
    }

    private Account findAccount(String id) {
        Account account = accounts.get(id.toUpperCase());
        if (account == null) {
            throw new IllegalArgumentException("unknown demo account: " + id);
        }
        return account;
    }

    private static BigDecimal parseAmount(String text) {
        try {
            BigDecimal amount = new BigDecimal(text).setScale(2, RoundingMode.UNNECESSARY);
            if (amount.signum() <= 0) {
                throw new IllegalArgumentException("amount must be greater than zero");
            }
            return amount;
        } catch (NumberFormatException | ArithmeticException e) {
            throw new IllegalArgumentException("enter a positive amount with at most 2 decimal places");
        }
    }

    private static void ensureFunds(Account account, BigDecimal amount) {
        if (account.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("insufficient demo balance");
        }
    }

    private static void requireParts(String[] parts, int expected, String usage) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("usage: " + usage);
        }
    }

    private static String reference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private static void printHelp() {
        System.out.println("""
                Commands:
                  balance <account>
                  deposit <account> <amount>
                  withdraw <account> <amount>
                  transfer <from-account> <to-account> <amount>
                  cheque <account> <amount> <payee>
                  help
                  exit
                """);
    }

    private static final class Account {
        private final String id;
        private BigDecimal balance;

        private Account(String id, BigDecimal balance) {
            this.id = id;
            this.balance = balance;
        }
    }
}
