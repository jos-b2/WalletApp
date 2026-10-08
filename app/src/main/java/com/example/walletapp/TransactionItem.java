package com.example.walletapp;

public class TransactionItem {
    private String title;
    private String subtitle;
    private String amount;
    private String status;

    public TransactionItem(String title, String subtitle, String amount, String status) {
        this.title = title;
        this.subtitle = subtitle;
        this.amount = amount;
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
}