package budget;

import java.io.*;
import java.util.*;

public class Main {
    private static Scanner scan;
    private static ArrayList<Item> purchases;
    private static HashMap<String, ArrayList<Item>> categorizedPurchases;
    private static double balance;
    private static double purchaseTotal;

    private enum Status {
        STANDBY,
        ADD_INCOME,
        ADD_PURCHASE,
        DISPLAY_PURCHASES,
        DISPLAY_BALANCE,
        SAVING,
        LOADING,
        OFF
    }

    public static void main(String[] args) {
        scan = new Scanner(System.in);
        purchases = new ArrayList<>();
        categorizedPurchases = new HashMap<>() {{
            put("Food", new ArrayList<>());
            put("Clothes", new ArrayList<>());
            put("Entertainment", new ArrayList<>());
            put("Other", new ArrayList<>());
        }};
        balance = 0;
        purchaseTotal = 0;
        Status status = Status.STANDBY;

        while (status != Status.OFF) {
            System.out.println("Choose  your action: \n1) Add income\n2) Add purchase\n" +
                    "3) Show list of purchases\n4) Balance\n5) Save\n6) Load\n7) Analyze (Sort)\n0) Exit");
            int selection = Integer.parseInt(scan.nextLine());
            System.out.println();

            switch (selection) {
                case 1:
                    status = Status.ADD_INCOME;
                    addIncome();
                    break;
                case 2:
                    status = Status.ADD_PURCHASE;
                    addPurchase();
                    break;
                case 3 :
                    status = Status.DISPLAY_PURCHASES;
                    displayPurchases();
                    break;
                case 4:
                    status = Status.DISPLAY_BALANCE;
                    displayBalance();
                    break;
                case 5:
                    status = Status.SAVING;
                    saveInformation();
                    break;
                case 6:
                    status = Status.LOADING;
                    loadInformation();
                    break;
                case 7:
                    analyzePurchases();
                    break;
                case 0:
                    status = Status.OFF;
                    System.out.println("Bye!");
                    break;
                default:
                    System.out.println("Please select an option between 1 and 4.");
                    break;
            }
        }

    }

    private static String getPurchaseCategory() {
        int selection;
        String type;
        do {
            selection = 0;
            type = "";

            System.out.println("Choose the type of purchases\n1) Food\n" +
                    "2) Clothes\n3) Entertainment\n4) Other\n5) Back");

            try {
                selection = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please select a valid option!");
            }

            switch (selection) {
                case 1:
                    return "Food";
                case 2:
                    return "Clothes";
                case 3:
                    return "Entertainment";
                case 4:
                    return "Other";
                case 5:
                    type = "back";
                    break;
                default:
                    System.out.println("Please select a valid option!");
            }
        } while (selection != 5);

        System.out.println();
        return type;
    }

    private static void analyzePurchases() {
        int selection;
        do {
            System.out.println("How do you want to sort?\n1) Sort all purchases\n" +
                    "2) Sort by type\n3) Sort certain type\n4) Back");
            selection = Integer.parseInt(scan.nextLine());
            System.out.println();
            switch (selection) {
                case 1:
                    sortPurchases(purchases);
                    break;
                case 2:
                    sortTypes();
                    break;
                case 3:
                    //String type = getPurchaseCategory();
                    //if (!type.isBlank()) {
                    sortPurchases(categorizedPurchases.getOrDefault(getPurchaseCategory(), new ArrayList<>()));
                    //}
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Please select a valid option!");
                    System.out.println();
            }
        } while (selection != 4);
    }

    private static void sortPurchases(ArrayList<Item> items) {
        System.out.println();
        if (items.isEmpty()) {
            System.out.println();
            System.out.println("Purchase list is empty!");
        } else {
            double total = 0;
            sortArray(items);
            for (Item item : items) {
                double price = item.getPrice();
                System.out.println(item.getName() + " $" + String.format("%.2f", price));
                total += price;
            }
            System.out.println("Total: " + total);
        }
        System.out.println();
    }

    private static void sortTypes() {
        ArrayList<Item> totals = new ArrayList<>();
        for (var entry : categorizedPurchases.entrySet()) {
            ArrayList<Item> category = entry.getValue();
            double categoryTotal = 0;
            for (Item item : category) {
                categoryTotal += item.getPrice();
            }
            Item total = new Item(entry.getKey(), categoryTotal, entry.getKey());
            totals.add(total);
        }

        sortArray(totals);

        System.out.println("Types:");
        for (Item item : totals) {
            System.out.print(item.getName() + " - $");
            System.out.println(String.format("%.2f", item.getPrice()));
        }
        System.out.println();
    }

    private static void sortArray(ArrayList<Item> arr) {
        for (int i = 0; i < arr.size() - 1; i++) {
            for (int j = 0; j < arr.size() - i - 1; j++) {
                if (arr.get(j).getPrice() < arr.get(j + 1).getPrice()) {
                    Item temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
    }

    private static void addIncome() {
        boolean valid = false;
        while (!valid) {
            try {
                System.out.println("Enter income:");
                double income = Double.parseDouble(scan.nextLine());
                balance += income;
                valid = true;
                System.out.println("Income was added!");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number!");
            }
        }
        System.out.println();
    }

    private static void addPurchase() {
        String type = getPurchaseCategory();
        do {
            System.out.println("Enter purchase name:");
            String name = scan.nextLine();
            System.out.println("Enter its price:");
            double price = Double.parseDouble(scan.nextLine());
            purchaseTotal += price;
            balance -= price >= 0 ? price : 0;

            Item item = new Item(name, price, type);

            purchases.add(item);
            categorizedPurchases.putIfAbsent(type, new ArrayList<>());
            categorizedPurchases.get(type).add(item);
            System.out.println("Purchase was added!");
            System.out.println();
            type = getPurchaseCategory();
        } while (!type.equals("back"));
    }

    private static void displayPurchases() {
        if (purchases.isEmpty()) {
            System.out.println("Purchase list is empty\n");
        } else {
            int selection = 0;
            do {
                try {
                    System.out.println("Choose the type of purchase\n1) Food\n" +
                            "2) Clothes\n3) Entertainment\n4) Other\n5) All\n6) Back");
                    selection = Integer.parseInt(scan.nextLine());
                    System.out.println();
                    String type = "";

                    switch (selection) {
                        case 1:
                            type = "Food";
                            break;
                        case 2:
                            type = "Clothes";
                            break;
                        case 3:
                            type = "Entertainment";
                            break;
                        case 4:
                            type = "Other";
                            break;
                        case 5:
                            type = "All";
                            break;
                    }
                    if (!type.isBlank()) {
                        System.out.println(type + ":");
                        double total = 0;
                        if (type.equals("All")) {
                            for (Item item : purchases) {
                                double price = item.getPrice();
                                System.out.print(item.getName() + " $");
                                System.out.println(String.format("%.2f", price));
                            }
                            total = purchaseTotal;
                        } else {
                            for (Item item : categorizedPurchases.get(type)) {
                                double price = item.getPrice();
                                System.out.print(item.getName() + " $");
                                System.out.println(String.format("%.2f", price));

                                total += price;
                            }
                        }
                        System.out.println("Total sum: $" + String.format("%.2f", total));
                        System.out.println();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid selection");
                }
            } while (selection != 6);
        }
    }

    private static void displayBalance() {
        System.out.println("Balance: $" + String.format("%.2f", balance));
        System.out.println();
    }

    private static void saveInformation() {
        try (FileWriter writer = new FileWriter(new File("purchases.txt"))) {
            writer.write("Balance: " + balance + "\n");
            for (var entry : categorizedPurchases.entrySet()) {
                StringBuilder str = new StringBuilder();
                str.append(entry.getKey()).append(": ");
                ArrayList<Item> items = entry.getValue();
                for (int i = 0; i < items.size(); i++) {
                    String name = items.get(i).getName();
                    double price = items.get(i).getPrice();
                    str.append(name).append(", $").append(price);
                    if (i < items.size() - 1) {
                        str.append(" | ");
                    }
                }
                writer.write(str.append("\n").toString());
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadInformation() {
        try (Scanner fileScanner = new Scanner(new File("purchases.txt"))) {
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                String category = line.substring(0, line.indexOf(":"));
                String contents = line.substring(line.indexOf(": ") + 2);
                if (category.equals("Balance")) {
                    balance = Double.parseDouble(contents);
                } else {
                    //categorizedPurchases.putIfAbsent(category, new ArrayList<>());
                    ArrayList<String> items = new ArrayList<>(Arrays.asList(contents.split(" \\| ")));
                    for (String item : items) {
                        String name = item.substring(0, item.indexOf(", $"));
                        double price = Double.parseDouble(item.substring(item.indexOf(", $") + 3));
                        Item entry = new Item(name, price, category);
                        purchaseTotal += price;
                        categorizedPurchases.get(category).add(entry);
                        purchases.add(entry);
                    }
                }

            }
            System.out.println("Purchases were loaded!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
