package com.reactnativedemolibs.card_reader.emv;

import com.pos.sdk.emvcore.POIEmvCoreManager;

import java.util.ArrayList;
import java.util.List;

public enum TransType {

    GOODS(POIEmvCoreManager.EMV_GOODS, "Goods"),
    SERVICE(POIEmvCoreManager.EMV_SERVICE, "Service"),
    CASH(POIEmvCoreManager.EMV_CASH, "Cash"),
    CASHBACK(POIEmvCoreManager.EMV_CASHBACK, "Cashback"),
    TRANSFER(POIEmvCoreManager.EMV_TRANSFER, "Transfer"),
    PAYMENT(POIEmvCoreManager.EMV_PAYMENT, "Payment"),
    ADMINISTRATIVE(POIEmvCoreManager.EMV_ADMINISTRATIVE, "Administrative"),
    DISBURSEMENT(POIEmvCoreManager.EMV_DISBURSEMENT, "Disbursement"),
    RETURN(POIEmvCoreManager.EMV_REFUND, "Refund"),
    DEPOSIT(POIEmvCoreManager.EMV_DEPOSIT, "Deposit"),
    INQUIRY(POIEmvCoreManager.EMV_INQUIRY, "Inquiry"),
    MONEY_ADD(POIEmvCoreManager.EMV_MONEY_ADD, "Money Add"),
    BALANCE_ENQUIRY(POIEmvCoreManager.EMV_BALANCE_ENQUIRY, "Balance Enquiry"),
    BALANCE_UPDATE(POIEmvCoreManager.EMV_BALANCE_UPDATE, "Balance Update"),
    VOID(POIEmvCoreManager.EMV_VOID, "Void"),
    SERVICE_CREATION(POIEmvCoreManager.EMV_SERVICE_CREATION, "Service Creation");

    private final int    type;
    private final String name;

    TransType(final int type, final String name) {
        this.type = type;
        this.name = name;
    }

    public static List<String> getTransType() {
        List<String> list = new ArrayList<>();
        for (TransType val : TransType.values()) {
            list.add(val.name);
        }
        return list;
    }

    public static int getTransType(String name) {
        int ret = GOODS.type;
        for (TransType val : TransType.values()) {
            if (val.name.equals(name)) {
                ret = val.type;
                break;
            }
        }
        return ret;
    }

    public static String getTransType(int type) {
        String ret = GOODS.name;
        for (TransType val : TransType.values()) {
            if (val.type == type) {
                ret = val.name;
                break;
            }
        }
        return ret;
    }
}
