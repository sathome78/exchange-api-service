package me.exrates.model.dto.mobileApiDto;

/**
 * Created by OLEG on 09.02.2017.
 */
public class InvoiceResponseDto {
    private Integer invoiceId;
    private String walletNumber;
    private String notification;

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "InvoiceResponseDto{" +
                "invoiceId='" + invoiceId + '\'' +
                ", walletNumber='" + walletNumber + '\'' +
                ", notification='" + notification + '\'' +
                '}';
    }
}
