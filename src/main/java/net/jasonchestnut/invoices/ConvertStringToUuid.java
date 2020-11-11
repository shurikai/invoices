package net.jasonchestnut.invoices;

import java.util.UUID;

import com.opencsv.bean.AbstractBeanField;

public class ConvertStringToUuid extends AbstractBeanField {

    public Object convert(String value) {
        return UUID.fromString(value);
    }
    public Object convertToRead(String value) {
        return UUID.fromString(value);
    }

}
