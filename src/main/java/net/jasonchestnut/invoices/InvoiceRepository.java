package net.jasonchestnut.invoices;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface InvoiceRepository extends JpaRepository<Invoice, InvoiceId> {
    //public Invoice findById(long id);
    public List<Invoice> findAll();

    public List<Invoice> findBySupplierId(String supplierId);
}
