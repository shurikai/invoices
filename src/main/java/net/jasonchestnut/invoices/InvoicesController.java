package net.jasonchestnut.invoices;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jasonchestnut.invoices.Invoice;
import net.jasonchestnut.invoices.InvoiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@RestController
public class InvoicesController {
    private static final Logger log = LoggerFactory.getLogger(InvoicesApplication.class);

    @Autowired
    private InvoiceRepository repository;

    @RequestMapping(value = "/")
    public String index() {
        return "Root URL";
    }

    @PostMapping(value = "/upload-csv-file")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file, Model model) {
            log.info("UploadCSV called.");
            if(file.isEmpty()) {
                    model.addAttribute("message", "Please select a CSV file to upload.");
                    model.addAttribute("status", false);
                    log.info("CSV file to upload was empty.");
                    return ResponseEntity.badRequest().build();
            } else {
                    try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                            CsvToBean<Invoice> csvToBean = new CsvToBeanBuilder(reader)
                                    .withType(Invoice.class)
                                    .withIgnoreLeadingWhiteSpace(true)
                                    .build();

                            List<Invoice> invoices = csvToBean.parse();

                            for(Invoice invoice : invoices) {
                                InvoiceId iid = new InvoiceId(invoice.getSupplierId(), invoice.getInvoiceId());
                                if(repository.existsById(iid)) {
                                    String duplicate = invoice.getInvoiceId().toString();
                                    log.info("Duplicate entity found: " + duplicate);
                                    return ResponseEntity.unprocessableEntity().body(
                                            "Duplicate entity found: " + duplicate);
                                }
                            }
                            
                            repository.saveAll(invoices);
                            log.info("Saved invoices to database.");

                            model.addAttribute("invoices", invoices);
                            model.addAttribute("status", true);
                    } catch(Exception e) {
                            model.addAttribute("message", "An error occured while processing the CSV file.");
                            model.addAttribute("status", false);
                            log.info("Exception caught trying to read CSV.");
                            return ResponseEntity.unprocessableEntity().build();
                    }
            }

            return ResponseEntity.ok().body("CSV successfully processed and saved.");
    }

    @GetMapping(path = "/ListSupplierSummary/{id}", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public SupplierSummary listSupplierSummary(@PathVariable("id") String id) {
        List<Invoice> invoices = repository.findBySupplierId(id);

        if(invoices.isEmpty()) {
            // No matching invoices found.
            return null; // TODO: Throw/404.
        }

        BigDecimal openTotal = new BigDecimal(0.0);
        BigDecimal lateTotal = new BigDecimal(0.0);

        List<Invoice> openInvoices = invoices.stream().filter(i -> i.isOpen()).collect(Collectors.toList());
        List<Invoice> lateInvoices = invoices.stream().filter(i -> i.isLate()).collect(Collectors.toList());
        //openInvoices.forEach(i -> openTotal.add(i.getDueTotal()));
        //openInvoices.stream().map(i -> openTotal = openTotal.add(i.getDueTotal()));
        //lateInvoices.stream().map(i -> lateTotal = lateTotal.add(i.getLateTotal()));
        for(Invoice i : openInvoices) {
            openTotal = openTotal.add(i.getDueTotal());
        }
        for(Invoice i : lateInvoices) {
            lateTotal = lateTotal.add(i.getLateTotal());
        }

        SupplierSummary summary = new SupplierSummary();
        summary.setSupplierId(id);
        summary.setTotalInvoices(invoices.size());
        summary.setOpenInvoices(openInvoices.size());
        summary.setLateInvoices(lateInvoices.size());
        summary.setTotalOpenInvoiceAmount(openTotal.doubleValue());
        summary.setTotalLateInvoiceAmount(lateTotal.doubleValue());

        log.info("Found related invoices: " + invoices.size());
        log.info("Found open invoices: " + openInvoices.size());
        log.info("Found late invoices: " + lateInvoices.size());

        return summary;
    }
    
}
