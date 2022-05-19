package com.reactnativedemoemvcard.card_reader.utils.tlv;

public interface IBerTlvLogger {

    boolean isDebugEnabled();

    void debug(String aFormat, Object... args);
}
