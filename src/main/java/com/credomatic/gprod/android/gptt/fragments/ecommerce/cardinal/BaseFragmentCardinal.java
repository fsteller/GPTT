package com.credomatic.gprod.android.gptt.fragments.ecommerce.cardinal;


import android.view.View;
import android.widget.Button;

import com.credomatic.gprod.android.gptt.fragments.ecommerce.BaseFragmentEcommerce;

/**
 * Created by fhernandezs on 18/12/13 for GProdTestingTool.
 */
public abstract class BaseFragmentCardinal extends BaseFragmentEcommerce {

    protected static enum Types {Auth, Capture, Sale, Void}

    protected static String URL = "https://credomatic.compassmerchantsolutions.com/api/transact.php";
    protected Button sendButton = null;

    @Override
    public final void restoreDefaultValues() {
        key = "53ZdjXS3g4ASw9Jatf885nqkTnJT83s7";
        keyId = "3905547";
        amount = "1.00";
        orderId = "MobileEcommerceTest";
        username = "monitorweb";
        password = "monitorweb01";
        processor = "";
        ccNumber = "4012001011000771";
        expDate = "1222";
        cvv = "123";
        trxId = "";
        firstName = "Test";
        lastName = "Credomatic";
        addressLine1 = "";
        addressLine2 = "";
        city = "Doral";
        state = "FL";
        zip = "";
        country = "US";
    }

    protected final void setupType(final Types type) {

        final int newTrx = type == Types.Auth || type == Types.Sale ? View.VISIBLE : View.GONE;
        final int updateTrx = type == Types.Auth || type == Types.Sale ? View.GONE : View.VISIBLE;

        trxId_editText.setVisibility(updateTrx);
        processor_editText.setVisibility(newTrx);
        ccNumber_editText.setVisibility(newTrx);
        orderId_editText.setVisibility(newTrx);
        expDate_editText.setVisibility(newTrx);
        cvv_editText.setVisibility(newTrx);

        trxId_label.setVisibility(updateTrx);
        processor_label.setVisibility(newTrx);
        ccNumber_label.setVisibility(newTrx);
        orderId_label.setVisibility(newTrx);
        expDate_label.setVisibility(newTrx);
        cvv_label.setVisibility(newTrx);
    }

}
