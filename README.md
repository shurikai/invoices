# invoices
LSQ take-home assignment

## To Run

- Clone this repository
- Build the application (./mvnw clean package)
- Start the docker composition (docker-compose up)

Begin by uploading a csv file 

```curl -v -F 'file=@invoice_data_1.csv' localhost:8080/upload-csv-file

Uploading the second file will cause an error to be returned (duplicates).
If you hit the `ListSupplierSummary/{supplierId}` endpoint, you should see JSON with the summary.



