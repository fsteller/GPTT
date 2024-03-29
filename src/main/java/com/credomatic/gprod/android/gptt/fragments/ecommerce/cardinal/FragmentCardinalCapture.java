package com.credomatic.gprod.android.gptt.fragments.ecommerce.cardinal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.credomatic.gprod.android.gptt.R;
import com.credomatic.gprod.android.gptt.utilities.net.NetUtilities;
import com.credomatic.gprod.android.gptt.utilities.security.Md5;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fhernandezs on 18/12/13 for GProdTestingTool.
 */
public class FragmentCardinalCapture extends BaseFragmentCardinal implements View.OnClickListener {

    private static final String TAG = FragmentCardinalCapture.class.getSimpleName();

    public FragmentCardinalCapture() {
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_cardinal_capture, container, false);

        if (rootView != null) {
            sendButton = ((Button) rootView.findViewById(R.id.send_button));
            sendButton.setOnClickListener(this);
        }

        setupUI(rootView);
        setupType(Types.Capture);

        return rootView;
    }

    @Override
    public void onClick(final View v) {
        new DirectPos().execute();
    }

    private final class DirectPos extends BaseTrxOperations {

        @Override
        protected final String validate() {
            if (username.isEmpty())
                return getResources().getString(R.string.emptyUsernameError);
            else if (password.isEmpty())
                return getResources().getString(R.string.emptyPasswordError);
            else if (amount.isEmpty())
                return getResources().getString(R.string.emptyAmountError);
            else if (trxId.isEmpty())
                return getResources().getString(R.string.emptyTrxIdError);
            else if (!NetUtilities.hasConnection(getActivity()))
                return getResources().getString(R.string.noInternetConnection);
            else return null;
        }

        @Override
        protected final String sendTrx() {

            final Map<String, String> params = new HashMap<String, String>();

            params.put("amount", amount);
            params.put("transaction_id", trxId);
            params.put("username", username);
            params.put("password", password);
            params.put("type", "capture");

            return NetUtilities.doPost(params, URL);
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            Log.i(TAG, "DirectPOS result: " + result);
            //Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }
}

