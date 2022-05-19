package com.reactnativedemoemvcard.card_reader.emv;

import com.pos.sdk.emvcore.PosEmvErrorCode;

public enum ErrorCodeType {

    APPROVED(PosEmvErrorCode.EMV_APPROVED, "Approved"),
    APPROVED_ONLINE(PosEmvErrorCode.EMV_APPROVED_ONLINE, "Online Approved"),
    DECLINED(PosEmvErrorCode.EMV_DECLINED, "Declined"),
    FORCE_APPROVED(PosEmvErrorCode.EMV_FORCE_APPROVED, "Force Approved"),
    DELAYED_APPROVED(PosEmvErrorCode.EMV_DELAYED_APPROVED, "Delayed Approved"),

    CANCEL(PosEmvErrorCode.EMV_CANCEL, "Cancel"),
    TIMEOUT(PosEmvErrorCode.EMV_TIMEOUT, "Timeout"),
    COMMAND_FAIL(PosEmvErrorCode.EMV_COMMAND_FAIL, "Command Fail"),
    FALLBACK(PosEmvErrorCode.EMV_FALLBACK, "Fallback"),
    MULTI_CONTACTLESS(PosEmvErrorCode.EMV_MULTI_CONTACTLESS, "Multi Contactless"),
    OTHER_ICC_INTERFACE(PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE, "Other ICC Interface"),
    APP_BLOCK(PosEmvErrorCode.EMV_APP_BLOCKED, "Application Blocked"),
    CARD_BLOCK(PosEmvErrorCode.EMV_CARD_BLOCKED, "Card Blocked"),
    APP_EMPTY(PosEmvErrorCode.EMV_APP_EMPTY, "No Application"),
    NOT_ALLOWED(PosEmvErrorCode.EMV_NOT_ALLOWED, "Not Allowed"),
    NOT_ACCEPTED(PosEmvErrorCode.EMV_NOT_ACCEPTED, "Not Accepted"),
    TERMINATED(PosEmvErrorCode.EMV_TERMINATED, "Terminated"),
    SEE_PHONE(PosEmvErrorCode.EMV_SEE_PHONE, "See Phone"),
    OTHER_INTERFACE(PosEmvErrorCode.EMV_OTHER_INTERFACE, "Other Interface"),
    EMV_OTHER_ERROR(PosEmvErrorCode.EMV_OTHER_ERROR, "Other Error");

    private final int    type;
    private final String name;

    ErrorCodeType(final int type, final String name) {
        this.type = type;
        this.name = name;
    }

    public static int getErrorCodeType(String name) {
        int ret = OTHER_INTERFACE.type;
        for (ErrorCodeType val : ErrorCodeType.values()) {
            if (val.name.equals(name)) {
                ret = val.type;
                break;
            }
        }
        return ret;
    }

    public static String getErrorCodeType(int type) {
        String ret = OTHER_INTERFACE.name;
        for (ErrorCodeType val : ErrorCodeType.values()) {
            if (val.type == type) {
                ret = val.name;
                break;
            }
        }
        return ret;
    }
}
