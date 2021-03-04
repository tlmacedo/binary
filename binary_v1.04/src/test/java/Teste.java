public class Teste {

    public static void main(String[] args) {
        String longcode0 = "Win payout if Volatility 10 Index is strictly lower than entry spot at 58 seconds after contract start time.";
        String longcode1 = "Win payout if Volatility 10 Index is strictly lower than entry spot at 1 minute 58 seconds after contract start time.";
        String text = longcode1;
        int indexOf = text.indexOf("entry spot at ");
        System.out.printf("index: %s\n", indexOf);
        System.out.printf("%s\n", text.substring(indexOf + 14, indexOf + 25));
    }
}
