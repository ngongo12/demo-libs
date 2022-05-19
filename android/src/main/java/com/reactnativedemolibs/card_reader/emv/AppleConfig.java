package com.reactnativedemolibs.card_reader.emv;

import android.os.Bundle;

import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.POIEmvCoreManager.AppleTerminalConstraints;
import com.reactnativedemolibs.card_reader.utils.GlobalData;
import com.reactnativedemolibs.card_reader.utils.SPUtils;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTag;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTlv;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTlvBuilder;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTlvParser;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTlvs;

import java.util.ArrayList;
import java.util.List;

public class AppleConfig {

    private final String APPLE_VAS_PROTOCOL_URL_ONLY = "URL Only";
    private final String APPLE_VAS_PROTOCOL_FULL_VAS = "Full VAS";

    private final String APPLE_VAS_CAPABILITY_SINGLE_MODE  = "Single Mode (VAS app OR payment)";
    private final String APPLE_VAS_CAPABILITY_DUAL_MODE    = "Dual Mode (VAS app AND payment)";
    private final String APPLE_VAS_CAPABILITY_VAS_ONLY     = "VAS Only (no payment all)";
    private final String APPLE_VAS_CAPABILITY_PAYMENT_ONLY = "Payment Only";

    private String[] APPLE_VAS_PROTOCOL   = {APPLE_VAS_PROTOCOL_URL_ONLY, APPLE_VAS_PROTOCOL_FULL_VAS};
    private String[] APPLE_VAS_CAPABILITY = {APPLE_VAS_CAPABILITY_SINGLE_MODE, APPLE_VAS_CAPABILITY_DUAL_MODE,
            APPLE_VAS_CAPABILITY_VAS_ONLY, APPLE_VAS_CAPABILITY_PAYMENT_ONLY};

    private POIEmvCoreManager   emvCoreManager;
    private List<AppleMerchant> vasMerchant;
    private String              appleVasProtocol;
    private String              appleVasCapability;

    public AppleConfig() {
        Bundle bundle;
        int protocol, capability;
        byte[] data;

        emvCoreManager = POIEmvCoreManager.getDefault();
        vasMerchant = new ArrayList<>();

        bundle = new Bundle();
        emvCoreManager.AppleGetTerminal(bundle);
        protocol = bundle.getInt(AppleTerminalConstraints.PROTOCOL, AppleTerminalConstraints.PROTOCOL_FULL_VAS);
        capability = bundle.getInt(AppleTerminalConstraints.CAPABILITY, AppleTerminalConstraints.CAPABILITY_DUAL_MODE);

        bundle = new Bundle();
        emvCoreManager.AppleGetMerchant(bundle);
        data = bundle.getByteArray(AppleTerminalConstraints.DATA);

        vasMerchant.clear();
        switch (protocol) {
            case AppleTerminalConstraints.PROTOCOL_URL_ONLY:
                appleVasProtocol = APPLE_VAS_PROTOCOL_URL_ONLY;
                break;
            case AppleTerminalConstraints.PROTOCOL_FULL_VAS:
                appleVasProtocol = APPLE_VAS_PROTOCOL_FULL_VAS;
                break;
            default:
                break;
        }

        switch (capability) {
            case AppleTerminalConstraints.CAPABILITY_SINGLE_MODE:
                appleVasCapability = APPLE_VAS_CAPABILITY_SINGLE_MODE;
                break;
            case AppleTerminalConstraints.CAPABILITY_DUAL_MODE:
                appleVasCapability = APPLE_VAS_CAPABILITY_DUAL_MODE;
                break;
            case AppleTerminalConstraints.CAPABILITY_VAS_ONLY:
                appleVasCapability = APPLE_VAS_CAPABILITY_VAS_ONLY;
                break;
            default:
                appleVasCapability = APPLE_VAS_CAPABILITY_PAYMENT_ONLY;
                break;
        }

        if (data != null) {
            BerTlvParser tlvParser = new BerTlvParser();
            List<BerTlv> tlvs = tlvParser.parse(data).findAll(new BerTag(AppleTerminalConstraints.TAG_APPLE_SET_DELIMITER));

            for (int i = 0; i < tlvs.size(); i++) {
                AppleMerchant merchant = new AppleMerchant();

                BerTlvs merchantTlvs = new BerTlvParser().parse(tlvs.get(i).getBytesValue());

                BerTlv tlv = merchantTlvs.find(new BerTag(AppleTerminalConstraints.TAG_APPLE_SET_MERCHANT_URL));
                if (tlv != null) {
                    merchant.setMerchantUrl(tlv.getBytesValue());
                }
                tlv = merchantTlvs.find(new BerTag(AppleTerminalConstraints.TAG_APPLE_SET_FILTER));
                if (tlv != null) {
                    merchant.setVasFilter(tlv.getBytesValue());
                }
                tlv = merchantTlvs.find(new BerTag(AppleTerminalConstraints.TAG_APPLE_SET_MERCHANT_ID));
                if (tlv != null) {
                    merchant.setMerchantId(tlv.getBytesValue());
                    vasMerchant.add(merchant);
                }
            }
        }
    }

    public String[] getProtocol() {
        return APPLE_VAS_PROTOCOL;
    }

    public String[] getCapability() {
        return APPLE_VAS_CAPABILITY;
    }

    public String getAppleVasProtocol() {
        return appleVasProtocol;
    }

    public void setAppleVasProtocol(String appleVasProtocol) {
        this.appleVasProtocol = appleVasProtocol;
        Bundle bundle = new Bundle();
        switch (appleVasProtocol) {
            case APPLE_VAS_PROTOCOL_URL_ONLY:
                bundle.putInt(AppleTerminalConstraints.PROTOCOL, AppleTerminalConstraints.PROTOCOL_URL_ONLY);
                break;
            case APPLE_VAS_PROTOCOL_FULL_VAS:
                bundle.putInt(AppleTerminalConstraints.PROTOCOL, AppleTerminalConstraints.PROTOCOL_FULL_VAS);
                break;
            default:
                break;
        }
        emvCoreManager.AppleSetTerminal(bundle);
    }

    public String getAppleVasCapability() {
        return appleVasCapability;
    }

    public void setAppleVasCapability(String appleVasCapability) {
        this.appleVasCapability = appleVasCapability;
        Bundle bundle = new Bundle();
        switch (appleVasCapability) {
            case APPLE_VAS_CAPABILITY_SINGLE_MODE:
                bundle.putInt(AppleTerminalConstraints.CAPABILITY, AppleTerminalConstraints.CAPABILITY_SINGLE_MODE);
                break;
            case APPLE_VAS_CAPABILITY_DUAL_MODE:
                bundle.putInt(AppleTerminalConstraints.CAPABILITY, AppleTerminalConstraints.CAPABILITY_DUAL_MODE);
                break;
            case APPLE_VAS_CAPABILITY_VAS_ONLY:
                bundle.putInt(AppleTerminalConstraints.CAPABILITY, AppleTerminalConstraints.CAPABILITY_VAS_ONLY);
                break;
            case APPLE_VAS_CAPABILITY_PAYMENT_ONLY:
                bundle.putInt(AppleTerminalConstraints.CAPABILITY, AppleTerminalConstraints.CAPABILITY_PAYMENT_ONLY);
                break;
            default:
                break;
        }
        emvCoreManager.AppleSetTerminal(bundle);
    }

    public List<AppleMerchant> getMerchant() {
        return vasMerchant;
    }

    public void setMerchant(List<AppleMerchant> merchant) {
        vasMerchant = merchant;

        emvCoreManager.AppleDeleteMerchant();

        if (merchant.size() == 0) {
            return;
        }

        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        for (AppleMerchant vasMerchant : merchant) {
            byte[] merchantId = vasMerchant.getMerchantId();
            byte[] merchantUrl = vasMerchant.getMerchantUrl();
            byte[] vasFilter = vasMerchant.getVasFilter();
            if (merchantId == null) {
                continue;
            }
            tlvBuilder.addBerTlv(new BerTlv(new BerTag(AppleTerminalConstraints.TAG_APPLE_SET_DELIMITER),
                    addAppleVasMerchant(new String(merchantId), merchantUrl == null ? null : new String(merchantUrl),
                            vasFilter == null ? null : new String(vasFilter))));
        }

        bundle.putByteArray(AppleTerminalConstraints.DATA, tlvBuilder.buildArray());
        emvCoreManager.AppleSetMerchant(bundle);
    }

    private byte[] addAppleVasMerchant(String merchantId, String merchantUrl, String filter) {
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();

        if (merchantId != null) {
            tlvBuilder.addBerTlv(new BerTlv(new BerTag(AppleTerminalConstraints.TAG_APPLE_SET_MERCHANT_ID), merchantId.getBytes()));
        }
        if (merchantUrl != null) {
            tlvBuilder.addBerTlv(new BerTlv(new BerTag(AppleTerminalConstraints.TAG_APPLE_SET_MERCHANT_URL), merchantUrl.getBytes()));
        }
        if (filter != null) {
            tlvBuilder.addBerTlv(new BerTlv(new BerTag(AppleTerminalConstraints.TAG_APPLE_SET_FILTER), filter.getBytes()));
        }

        return tlvBuilder.buildArray();
    }

    private static final String APPLE_CONFIG_INDEX = "apple_config_index";

    private static final String SCENARIO_1     = "Scenario 01 - 04";
    private static final String SCENARIO_5     = "Scenario 05";
    private static final String SCENARIO_6     = "Scenario 06";
    private static final String SCENARIO_7     = "Scenario 07 - 08";
    private static final String SCENARIO_9     = "Scenario 09";
    private static final String SCENARIO_10    = "Scenario 10";
    private static final String SCENARIO_11    = "Scenario 11";
    private static final String SCENARIO_12    = "Scenario 12 - 13";
    private static final String SCENARIO_14    = "Scenario 14";
    private static final String SCENARIO_15    = "Scenario 15 - 16";
    private static final String SCENARIO_17    = "Scenario 17";
    private static final String SCENARIO_18    = "Scenario 18";
    private static final String STRESS_01      = "Stress 01";
    private static final String STRESS_02      = "Stress 02";
    private static final String STRESS_03      = "Stress 03";
    private static final String PERFORMANCE_01 = "Performance 01";
    private static final String PERFORMANCE_02 = "Performance 02";

    private String[] APPLE_VAS_CONFIG = {SCENARIO_1, SCENARIO_5, SCENARIO_6, SCENARIO_7, SCENARIO_9, SCENARIO_10,
            SCENARIO_11, SCENARIO_12, SCENARIO_14, SCENARIO_15, SCENARIO_17, SCENARIO_18, STRESS_01, STRESS_02,
            STRESS_03, PERFORMANCE_01, PERFORMANCE_02};

    public String[] getConfigs() {
        return APPLE_VAS_CONFIG;
    }

    public String getConfig() {
        return SPUtils.getInstance().getString(APPLE_CONFIG_INDEX);
    }

    public void setConfig(String name) {
        SPUtils.getInstance().put(APPLE_CONFIG_INDEX, name);
        GlobalData.setSupportAppleVas(true);
        List<AppleMerchant> merchants = new ArrayList<>();
        switch (name) {
            case STRESS_03:
            case SCENARIO_1:
            case PERFORMANCE_02:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_DUAL_MODE);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), "www.xchengtech.com".getBytes(), null));
                merchants.add(new AppleMerchant("pass.com.apple.passman1".getBytes(), "www.xchengtech.com".getBytes(), null));
                setMerchant(merchants);
                break;
            case SCENARIO_5:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_URL_ONLY);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_VAS_ONLY);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), "www.xchengtech.com".getBytes(), null));
                setMerchant(merchants);
                break;
            case STRESS_01:
            case SCENARIO_6:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_VAS_ONLY);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), "www.xchengtech.com".getBytes(), null));
                merchants.add(new AppleMerchant("pass.com.apple.passman1".getBytes(), "www.xchengtech.com".getBytes(), null));
                setMerchant(merchants);
                break;
            case SCENARIO_7:
            case SCENARIO_15:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_VAS_ONLY);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), null, null));
                setMerchant(merchants);
                break;
            case SCENARIO_9:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_SINGLE_MODE);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), "www.xchengtech.com".getBytes(), null));
                setMerchant(merchants);
                break;
            case SCENARIO_10:
            case SCENARIO_12:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_SINGLE_MODE);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), null, null));
                setMerchant(merchants);
                break;
            case STRESS_02:
            case SCENARIO_11:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_SINGLE_MODE);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), null, null));
                merchants.add(new AppleMerchant("pass.com.apple.passman1".getBytes(), null, null));
                setMerchant(merchants);
                break;
            case SCENARIO_14:
                setAppleVasCapability(APPLE_VAS_CAPABILITY_PAYMENT_ONLY);
                setMerchant(merchants);
                break;
            case SCENARIO_17:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_DUAL_MODE);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), null, null));
                setMerchant(merchants);
                break;
            case SCENARIO_18:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_VAS_ONLY);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), "www.xchengtech.com".getBytes(), null));
                setMerchant(merchants);
                break;
            case PERFORMANCE_01:
                setAppleVasProtocol(APPLE_VAS_PROTOCOL_FULL_VAS);
                setAppleVasCapability(APPLE_VAS_CAPABILITY_DUAL_MODE);
                merchants.add(new AppleMerchant("pass.com.apple.passman".getBytes(), "www.xchengtech.com".getBytes(), null));
                setMerchant(merchants);
                break;
            default:
                break;
        }
    }
}
