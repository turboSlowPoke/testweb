package calculates;

import java.math.BigDecimal;

public class CalculateOfPayment {
    public static BigDecimal calcForFirstLine(BigDecimal amount){
        BigDecimal procent = new BigDecimal("0.10");
        return amount.multiply(procent);
    }

    public static BigDecimal calcForSecondLine(BigDecimal amount){
        BigDecimal procent = new BigDecimal("0.05");
        return amount.multiply(procent);
    }

    public static BigDecimal calcForThirdLine(BigDecimal amount){
        BigDecimal procent = new BigDecimal("0.03");
        return amount.multiply(procent);
    }
}
