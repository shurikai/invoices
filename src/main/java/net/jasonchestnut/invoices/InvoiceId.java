package net.jasonchestnut.invoices;

import java.io.Serializable;
import java.util.UUID;

public class InvoiceId implements Serializable {
    private String supplierId;
 
    private UUID invoiceId;
 
    public InvoiceId() {}
 
    public InvoiceId(String supplierId, UUID invoiceId) {
        this.supplierId = supplierId;
        this.invoiceId = invoiceId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }
 
    // equals() and hashCode()
}

