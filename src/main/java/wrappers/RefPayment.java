package wrappers;

import entitys.AdvcashTransaction;
import entitys.LocalTransaction;

public class RefPayment {
    private String date;
    private String userName;
    private String amountPayment;


    public RefPayment(LocalTransaction localTransaction) {
        this.date = localTransaction.getDateTime().toLocalDate().toString();
        this.userName = localTransaction.getUser().getPersonalData().getUserNameTelegram();
        this.amountPayment = localTransaction.getAmount().toString();
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getAmountPayment() {
        return amountPayment;
    }

    @Override
    public String toString() {
        return "date: "+date+", username: "+userName+",amount: "+amountPayment;
    }
}
