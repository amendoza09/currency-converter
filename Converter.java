import javax.swing.*;
import java.awt.*;

public class Converter extends JFrame {
    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JTextField amountField;
    private JLabel resultLabel;
    private JButton convertButton;
    private JButton swapButton;

    private ExchangeRate ExchangeService;

    private static final String[] CURRENCIES = {
        "USD", "EUR", "GBP", "JPY", "CAD", "CHF", "CNY",
        "INR", "MXN", "BRL", "ZAR", "NZD", "SGD", "HKD",
        "AUD", "NOK", "SEK", "DKK", "KRW", "TRY"
    };

    public Converter() {
        this.ExchangeService = new ExchangeRate();

        setTitle("Currency Converter");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        createComponents();
        setVisible(true);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        amountField = new JTextField("1.00");
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField.addActionListener(e -> performConversion());
        mainPanel.add(amountField, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("From:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        fromCurrency = new JComboBox<>(CURRENCIES);
        fromCurrency.setSelectedItem("USD");
        fromCurrency.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(fromCurrency, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        swapButton = new JButton("Swap Currencies");
        swapButton.setFont(new Font("Arial", Font.BOLD, 12));
        swapButton.addActionListener(e -> swapCurrencies());
        mainPanel.add(swapButton, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 4;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("To:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        toCurrency = new JComboBox<>(CURRENCIES);
        toCurrency.setSelectedItem("EUR");
        toCurrency.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(toCurrency, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth= 3;
        convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBackground(new Color(70, 130, 180));
        convertButton.setFocusPainted(false);
        convertButton.addActionListener(e -> performConversion());
        mainPanel.add(convertButton, gbc);


        gbc.gridy = 6;
        resultLabel = new JLabel(" ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setForeground(new Color(0, 100, 0));
        mainPanel.add(resultLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void swapCurrencies() {
        String temp = (String) fromCurrency.getSelectedItem();
        fromCurrency.setSelectedItem(toCurrency.getSelectedItem());
        toCurrency.setSelectedItem(temp);
    }

    private void performConversion() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = (String) fromCurrency.getSelectedItem();
            String to = (String) toCurrency.getSelectedItem();

            setConvertingState(true);

            new Thread(() -> {
                try {
                    ConversionResult result = ExchangeService.convert(amount, from, to);

                    SwingUtilities.invokeLater(() -> {
                        displayResult(result);
                        setConvertingState(false);
                    });
                } catch(IllegalArgumentException e) {
                    SwingUtilities.invokeLater(() -> {
                        showError(e.getMessage());
                        setConvertingState(false);
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        showError("Failed to fetch rates: " + e.getMessage());
                        setConvertingState(false);
                    });
                }
            }).start();
        } catch(NumberFormatException e) {
            showError("Please enter a valid number");
        }
    }

    private void setConvertingState(boolean converting) {
        convertButton.setEnabled(!converting);
        convertButton.setText(converting ? "Converting..." : "Convert");
        if(converting) {
            resultLabel.setText("Fetching rates...");
        }
    }

    private void displayResult(ConversionResult result) {
        resultLabel.setText(String.format("%.2f %s = %.2f %s", result.getAmount(), result.getFromCurrency(),
            result.getConvertedAmount(), result.getToCurrency()));
    }

    private void showError(String message) {
        resultLabel.setText("");
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}