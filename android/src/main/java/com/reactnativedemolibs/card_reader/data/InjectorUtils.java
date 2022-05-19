package com.reactnativedemoemvcard.card_reader.data;

import android.content.Context;

public class InjectorUtils {

    public static TransactionRepository getTransRepository(Context context) {
        return TransactionRepository.getInstance(AppDatabase.getInstance(context.getApplicationContext()).getTransactionDao());
    }
}
