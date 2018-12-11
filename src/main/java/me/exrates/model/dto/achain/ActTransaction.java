package me.exrates.model.dto.achain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Maks on 14.06.2018.
 */
public class ActTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    /***/
    protected Long id;

    /**
     * transaction id
     */
    protected String trxId;

    /**
     * Block hash
     */
    protected String blockId;

    /**
     * Block number
     */
    protected Long blockNum;

    /**
     * The position of the transaction in the block
     */
    protected Integer blockPosition;

    /**
           * 0 - Ordinary transfers
           * 1 - Agent pays
           * 2 - Registered Account
           * 3 - Registration Agency
           * 10 - Registration Agreement
           * 11 - Contract recharge
           * 12 - Contract Upgrade
           * 13 - Destruction of Contracts
           * 14 - Invoking the contract
           * 15 - Contract Debit
     */
    protected Integer trxType;

    /***/
    protected String coinType;

    /***/
    protected String contractId;

    /**
     * from account
     */
    protected String fromAcct;

    /**
     * from address
     */
    protected String fromAddr;

    /**
     * Receive account
     */
    protected String toAcct;

    /**
     * receiving address
     */
    protected String toAddr;

    /***/
    protected String subAddress;

    /**
     * Amount
     */
    protected Long amount;

    /**
     * Fees
     * If it is a contract transaction, including gas consumption, registration margin, etc.
     */
    protected Integer fee;

    /**
     * Note
     */
    protected String memo;

    /**
     * transaction hour
     */
    protected Date trxTime;

    /**
     * Called contract function, non-contract transaction this field is empty
     */
    protected String calledAbi;

    /**
     * The parameter passed in when the contract function is called. This field is empty for non-contract transactions.
     */
    protected String abiParams;

    /***/
    protected String eventType;

    /***/
    protected String eventParam;

    /**
     * Result transaction id
     * Only for contract transactions
     */
    protected String extraTrxId;

    /**
     * Contract invocation results
     * 0 - Successful
     * 1- Failed
     */
    protected Byte isCompleted;

    /***/
    protected Date createTime;

    /***/
    protected Date updateTime;
}
