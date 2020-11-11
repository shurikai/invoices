package net.jasonchestnut.invoices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Log
@Component
public class SetupRunner implements CommandLineRunner {
    private final InvoiceRepository repository;

    @Autowired
    public SetupRunner(InvoiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        UUID uuid = UUID.fromString("8E74BED1-D7C0-4655-A528-0C9B67102C31"); 
        String supplier = new String("supplier_1");
        log.info("Created new UUID: " + uuid.toString());

        Invoice inv = new Invoice();
        inv.setInvoiceId(uuid);
        inv.setSupplierId(supplier);
        inv.setInvoiceDate(LocalDate.now());
        inv.setInvoiceAmount(new BigDecimal(1000.0));
        inv.setTerms(new Long(30));
        inv.setPaymentDate(LocalDate.now().plusDays(20));
        inv.setPaymentAmount(new BigDecimal(1000.0));
        this.repository.save(inv);

        log.info("Db contains: " + this.repository.findBySupplierId(supplier));
    }
}
