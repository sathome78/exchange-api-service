 package me.exrates.openapi.model;

 import com.fasterxml.jackson.databind.annotation.JsonSerialize;
 import lombok.EqualsAndHashCode;
 import lombok.Getter;
 import lombok.Setter;
 import lombok.ToString;
 import me.exrates.model.enums.invoice.InvoiceStatus;
 import me.exrates.model.serializer.LocalDateTimeSerializer;

 import java.time.LocalDateTime;

 /**
  * @author Denis Savin (pilgrimm333@gmail.com)
  */
 @Getter @Setter
 @EqualsAndHashCode
 @ToString
 public class PendingPayment {
     private int invoiceId;
     private String transactionHash;
     private String address;
     private Integer userId;
     private String userEmail;
     private Integer acceptanceUserId;
     private String acceptanceUserEmail;
     private String hash;
     @JsonSerialize(using = LocalDateTimeSerializer.class)
     private LocalDateTime acceptanceTime;
     private Transaction transaction;
     private InvoiceStatus pendingPaymentStatus;
     @JsonSerialize(using = LocalDateTimeSerializer.class)
     private LocalDateTime statusUpdateDate;


 }
