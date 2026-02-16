public class ConversionResult {
    private final double amount;
    private final String fromCurrency;
    private final double convertedAmount;
    private final String toCurrency;
    private final double exchangeRate;

    public ConversionResult(double amount, String fromCurrency, double convertedAmount, String toCurrency, double exchangeRate) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this. convertedAmount = convertedAmount;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
    }

    public double getAmount() {
        return amount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    @Override
    public String toString() {
        return String.format("%.2f %s = %.2f %s (Rate: %.4f)", amount, fromCurrency, convertedAmount, toCurrency, exchangeRate);
    }

}