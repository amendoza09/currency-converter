import javax.swing.*;

public class Main {
    public static void main(String [] args) {

        System.setProperty("java.awt.headless", "false");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Converter());
    }
}
