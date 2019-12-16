package com.sp.btc.chain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Blue on 2019/11/8.
 */
public class UtxoEntity implements Serializable {
    private String txid;
    private Long vout;
    private String address;
    private String label;
    private String scriptPubKey;
    private BigDecimal amount;
    private Long confirmations;
    private Boolean spendable;
    private Boolean solvable;
    private String desc;
    private Boolean safe;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public Long getVout() {
        return vout;
    }

    public void setVout(Long vout) {
        this.vout = vout;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }

    public void setScriptPubKey(String scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Long confirmations) {
        this.confirmations = confirmations;
    }

    public Boolean getSpendable() {
        return spendable;
    }

    public void setSpendable(Boolean spendable) {
        this.spendable = spendable;
    }

    public Boolean getSolvable() {
        return solvable;
    }

    public void setSolvable(Boolean solvable) {
        this.solvable = solvable;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getSafe() {
        return safe;
    }

    public void setSafe(Boolean safe) {
        this.safe = safe;
    }


}
