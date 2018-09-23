package me.exrates.openapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvoiceBank {

    private Integer id;
    @JsonInclude(NON_NULL)
    private Integer currencyId;
    private String name;
    private String accountNumber;
    @JsonInclude(NON_NULL)
    private String recipient;
    @JsonInclude(NON_NULL)
    private String bankDetails;

    public static InvoiceBank getUnavilableInvoice(int currencyId) {
        InvoiceBank invoiceBank = new InvoiceBank();
        invoiceBank.setId(0);
        invoiceBank.setCurrencyId(currencyId);
        invoiceBank.setName("SERVICE NOT AVAILABLE");
        invoiceBank.setAccountNumber(" ");
        invoiceBank.setRecipient(" ");
        invoiceBank.setBankDetails("INVOICE SERVICE NOT AVAILBLE, YOU REFILLS WILL NOT BE CREDITED");
        return invoiceBank;
    }
}
