import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverterGUI extends JFrame {

    private JComboBox<String> baseCurrencyDropdown;
    private JComboBox<String> targetCurrencyDropdown;
    private JTextField amountField;
    private JLabel resultLabel;
    private JButton convertButton;
    private JButton refreshButton;

    private Map<String, Double> exchangeRates;
    private Map<String, String> currencySymbols;
    private String apiUrl = "https://v6.exchangerate-api.com/v6/d92e9f6d1a23cbbccbb1f385/latest/USD";

    public CurrencyConverterGUI() {
        setTitle("Currency Converter");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        baseCurrencyDropdown = new JComboBox<>();
        targetCurrencyDropdown = new JComboBox<>();
        amountField = new JTextField(10);
        resultLabel = new JLabel("Converted Amount: ");
        convertButton = new JButton("Convert");
        refreshButton = new JButton("Refresh Rates");

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(new JLabel("Base Currency:"));
        add(baseCurrencyDropdown);
        add(new JLabel("Target Currency:"));
        add(targetCurrencyDropdown);
        add(new JLabel("Amount:"));
        add(amountField);
        add(convertButton);
        add(refreshButton);
        add(resultLabel);

        initializeCurrencySymbols();

        refreshExchangeRates();

        convertButton.addActionListener(e -> convertCurrency());

        refreshButton.addActionListener(e -> refreshExchangeRates());
    }

    private void refreshExchangeRates() {
        try {
            exchangeRates = getExchangeRates(apiUrl);
            baseCurrencyDropdown.removeAllItems();
            targetCurrencyDropdown.removeAllItems();
            for (String currency : exchangeRates.keySet()) {
                baseCurrencyDropdown.addItem(currency);
                targetCurrencyDropdown.addItem(currency);
            }
            JOptionPane.showMessageDialog(this, "Exchange rates updated successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch exchange rates: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<String, Double> getExchangeRates(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed: HTTP error code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return parseJsonResponse(response.toString());
    }

    private Map<String, Double> parseJsonResponse(String jsonResponse) {
        Map<String, Double> rates = new HashMap<>();
        String searchKey = "\"conversion_rates\":{";
        int startIndex = jsonResponse.indexOf(searchKey) + searchKey.length();
        int endIndex = jsonResponse.indexOf("}", startIndex);
        String ratesString = jsonResponse.substring(startIndex, endIndex);
        String[] currencyPairs = ratesString.split(",");

        for (String pair : currencyPairs) {
            String[] currencyRate = pair.split(":");
            String currency = currencyRate[0].replace("\"", "").trim();
            double rate = Double.parseDouble(currencyRate[1].trim());
            rates.put(currency, rate);
        }
        return rates;
    }

    private void convertCurrency() {
        String baseCurrency = (String) baseCurrencyDropdown.getSelectedItem();
        String targetCurrency = (String) targetCurrencyDropdown.getSelectedItem();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount < 0) {
                throw new NumberFormatException("Amount cannot be negative.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive amount.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (baseCurrency != null && targetCurrency != null && exchangeRates.containsKey(baseCurrency) && exchangeRates.containsKey(targetCurrency)) {
            double baseRate = exchangeRates.get(baseCurrency);
            double targetRate = exchangeRates.get(targetCurrency);
            double convertedAmount = (amount / baseRate) * targetRate;

            String targetSymbol = currencySymbols.getOrDefault(targetCurrency, targetCurrency);
            resultLabel.setText(String.format("Converted Amount: %.2f %s", convertedAmount, targetSymbol));
        } else {
            JOptionPane.showMessageDialog(this, "Currency not found in exchange rates.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeCurrencySymbols() {
        currencySymbols = new HashMap<>();

        // Major Currencies
        currencySymbols.put("USD", "$");
        currencySymbols.put("EUR", "€");
        currencySymbols.put("JPY", "¥");
        currencySymbols.put("GBP", "£");
        currencySymbols.put("AUD", "A$");
        currencySymbols.put("CAD", "C$");
        currencySymbols.put("CHF", "CHF");
        currencySymbols.put("CNY", "¥");
        currencySymbols.put("HKD", "HK$");
        currencySymbols.put("NZD", "NZ$");

        // Other Currencies
        currencySymbols.put("INR", "₹");
        currencySymbols.put("RUB", "₽");
        currencySymbols.put("BRL", "R$");
        currencySymbols.put("ZAR", "R");
        currencySymbols.put("SGD", "S$");
        currencySymbols.put("KRW", "₩");
        currencySymbols.put("TRY", "₺");
        currencySymbols.put("MXN", "$");
        currencySymbols.put("IDR", "Rp");
        currencySymbols.put("MYR", "RM");
        currencySymbols.put("PHP", "₱");
        currencySymbols.put("THB", "฿");
        currencySymbols.put("VND", "₫");

        // European Currencies
        currencySymbols.put("DKK", "kr");
        currencySymbols.put("NOK", "kr");
        currencySymbols.put("SEK", "kr");
        currencySymbols.put("ISK", "kr");

        // Middle Eastern Currencies
        currencySymbols.put("AED", "د.إ");
        currencySymbols.put("SAR", "﷼");
        currencySymbols.put("QAR", "﷼");
        currencySymbols.put("OMR", "﷼");
        currencySymbols.put("KWD", "د.ك");
        currencySymbols.put("BHD", ".د.ب");

        // Latin American Currencies
        currencySymbols.put("CLP", "$");
        currencySymbols.put("COP", "$");
        currencySymbols.put("PEN", "S/");
        currencySymbols.put("ARS", "$");

        // African Currencies
        currencySymbols.put("EGP", "£");
        currencySymbols.put("NGN", "₦");
        currencySymbols.put("KES", "KSh");
        currencySymbols.put("TZS", "TSh");
        currencySymbols.put("UGX", "USh");

        // Miscellaneous Currencies
        currencySymbols.put("PLN", "zł");
        currencySymbols.put("CZK", "Kč");
        currencySymbols.put("HUF", "Ft");
        currencySymbols.put("RON", "lei");
        currencySymbols.put("ILS", "₪");
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CurrencyConverterGUI converter = new CurrencyConverterGUI();
            converter.setVisible(true);
        });
    }
}