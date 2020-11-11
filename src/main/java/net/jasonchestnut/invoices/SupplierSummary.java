package net.jasonchestnut.invoices;

import lombok.Data;

@Data
public class SupplierSummary {
    private String supplierId;
    private int totalInvoices;
    private int openInvoices;
    private int lateInvoices;
    private double totalOpenInvoiceAmount;
    private double totalLateInvoiceAmount;

    public String getSupplierId() {
        return this.supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String toString() {
        return " hello, world ";
    }

}
