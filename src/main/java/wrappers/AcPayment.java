package wrappers;

import entitys.AdvcashTransaction;

public class AcPayment {
    private String date;
    private String userName;
    private String service;
    private String amountPayment;


    public AcPayment(AdvcashTransaction advcashTransaction) {
        this.date = advcashTransaction.getAc_start_date().toLocalDate().toString();
        this.userName = advcashTransaction.getUser().getPersonalData().getUserNameTelegram();
        String orderId = advcashTransaction.getAc_order_id();
        this.service = orderId.substring(orderId.indexOf("_") + 1, orderId.indexOf("-"));
        this.amountPayment = advcashTransaction.getAc_amount().toString();
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getService() {
        return service;
    }

    public String getAmountPayment() {
        return amountPayment;
    }



    @Override
    public String toString() {
        return "date: "+date+", username: "+userName+", service: "+service+"amount: "+amountPayment;
    }
}
