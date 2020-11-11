package net.jasonchestnut.invoices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;

import lombok.Data;

@Entity
@IdClass(InvoiceId.class)
@Data
public class Invoice {
    @Id
    @CsvBindByName(column="Supplier Id")
    private String supplierId;
    @Id
    @CsvCustomBindByName(column="Invoice Id", converter=ConvertStringToUuid.class)
    private UUID invoiceId;

    @CsvBindByName(column="Invoice Date")
    @CsvDate("yyyy-MM-dd")
    private LocalDate invoiceDate;
    @CsvBindByName(column="Invoice Amount")
    private BigDecimal invoiceAmount;
    @CsvBindByName(column="Terms")
    private Long terms;
    @CsvBindByName(column="Payment Date")
    @CsvDate("yyyy-MM-dd")
    private LocalDate paymentDate;
    @CsvBindByName(column="Payment Amount")
    private BigDecimal paymentAmount;

    public Invoice() {}

    public BigDecimal getDueTotal() {
        if(this.paymentAmount == null) {
            return this.invoiceAmount;
        } else {
            return this.invoiceAmount.subtract(this.paymentAmount);
        }
    }

    public BigDecimal getLateTotal() {
        if(this.isLate()) {
            return this.getDueTotal();
        } else {
            return new BigDecimal(0.0);
        }
    }

    public LocalDate dueDate() {
        if(this.invoiceDate != null) {
            return this.invoiceDate.plusDays(this.terms);
        } else {
            return null;
        }
    }

    public boolean isOpen() {
        return this.paymentAmount == null ||
            this.paymentAmount.compareTo(this.invoiceAmount) < 0;
    }

    public boolean isLate() {
        return this.getDueTotal().compareTo(new BigDecimal(0.0)) == 1 &&
            this.dueDate().isBefore(LocalDate.now());
    }
}
