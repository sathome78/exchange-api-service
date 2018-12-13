package me.exrates.model.dto.achain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Maks on 14.06.2018.
 */
@Data
public class ActBlock implements Serializable {

    private static final long serialVersionUID = 1L;


    protected Long id;

    /**
     * Block hash
     */
    protected String blockId;

    /**
     * Block number
     */
    protected Long blockNum;

    /**
     * Block size (bytes)
     */
    protected Long blockSize;

    /**
     * Previous block id
     */
    protected String previous;

    /**
     * Abstract of the transaction in the block
     */
    protected String trxDigest;

    /**
     * Last round of secret
     */
    protected String prevSecret;

    /**
     * This round of secret hash
     */
    protected String nextSecretHash;

    /**
     * Random seed
     */
    protected String randomSeed;

    /**
     * Producer(signer)
     */
    protected String signee;

    /**
     * Block time
     */
    protected Date blockTime;

    /**
     * Number of transactions within the block
     */
    protected Integer transNum;

    /**
     * Intra-block transaction total
     */
    protected Long transAmount;

    /**
     * Intra-block transaction fee
     */
    protected Long transFee;

    protected Integer status;

    protected Date createTime;

    protected Date updateTime;
}
