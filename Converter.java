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
        titleLabel.setFont(new Font("Ariel", Font.BOLD, 24));
        titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        
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
        resultLabel.setText(String.format("%.2f %s = @.2f %s", result.getAmount(), result.getFromCurrency(),
            result.getConvertedAmount(), result.getToCurrency()));
    }

    private void showError(String message) {
        resultLabel.setText("");
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}