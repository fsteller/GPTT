package com.credomatic.gprod.android.gptt.fragments.ecommerce.cardinal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.credomatic.gprod.android.gptt.R;
import com.credomatic.gprod.android.gptt.app.AppGPTT;
import com.credomatic.gprod.android.gptt.utilities.net.NetUtilities;
import com.credomatic.gprod.android.gptt.utilities.security.Md5;

import org.apache.http.util.EncodingUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by fhernandezs on 18/12/13 for GProdTestingTool.
 */
public class FragmentCardinalAuth extends BaseFragmentCardinal implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = FragmentCardinalAuth.class.getSimpleName();

    private boolean isBrowserRedirect = false;
    private RadioButtonSwitcher mRadioButtonSwitcher = new RadioButtonSwitcher();
    private RadioButton browserRedirect = null;
    private RadioButton directPos = null;

    public FragmentCardinalAuth() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_cardinal_auth, container, false);
        if (rootView != null) {

            directPos = ((RadioButton) rootView.findViewById(R.id.directPos_rb));
            directPos.setOnClickListener(mRadioButtonSwitcher);

            browserRedirect = ((RadioButton) rootView.findViewById(R.id.browserRedirect_rb));
            browserRedirect.setOnClickListener(mRadioButtonSwitcher);

            sendButton = ((Button) rootView.findViewById(R.id.send_button));
            sendButton.setOnClickListener(this);

            country_spinner = (Spinner) rootView.findViewById(R.id.countrySpinner);
            state_spinner = (Spinner) rootView.findViewById(R.id.stateSpinner);
            city_spinner = (Spinner) rootView.findViewById(R.id.citySpinner);

            setupSpinner(country_spinner, R.array.countries, true);
        }

        setupUI(rootView);
        setupType(Types.Auth);

        return rootView;
    }

    @Override
    public void onClick(final View v) {
        AsyncTask<Void, Void, String> task = isBrowserRedirect ? new BrowserRedirect() : new DirectPos();
        task.execute();
    }

    @Override
    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {

        city_spinner.setAdapter(null);
        if (parent.equals(country_spinner)) {
            state_spinner.setAdapter(null);
            switch (position) {
                case 0:
                    setupSpinner(state_spinner, R.array.us_states, true);
                    break;
                case 1:
                    setupSpinner(state_spinner, R.array.cr_states, true);
                    break;
                default:
            }
        } else if (parent.equals(state_spinner)) {
            final TextView countryTxt = ((TextView) country_spinner.getSelectedView());
            final TextView stateTxt = ((TextView) state_spinner.getSelectedView());

            if (countryTxt != null && stateTxt != null) {
                final String countryStr = countryTxt.getText().toString();
                final String stateStr = stateTxt.getText().toString();
                setupUSCities(countryStr, stateStr);
            }
        }

    }

    @Override
    public void onNothingSelected(final AdapterView<?> parent) {

    }

    private void setupUSCities(final String country, final String state) {
        if (country.equals("US")) {
            if (state.equals("FL")) {
                setupSpinner(city_spinner, R.array.fl_cities, false);
            }
        } else if (country.equals("CR")) {
            if (state.equals("San Jose")) {
                setupSpinner(city_spinner, R.array.san_jose_cities, false);
            } else if (state.equals("Alajuela")) {
                setupSpinner(city_spinner, R.array.alajuela_cities, false);
            } else if (state.equals("Heredia")) {
                setupSpinner(city_spinner, R.array.heredia_cities, false);
            } else if (state.equals("cartago")) {
                setupSpinner(city_spinner, R.array.cartago_cities, false);
            } else if (state.equals("Puntarenas")) {
                setupSpinner(city_spinner, R.array.puntarenas_cities, false);
            } else if (state.equals("Guanacaste")) {
                setupSpinner(city_spinner, R.array.guanacaste_cities, false);
            } else if (state.equals("Limon")) {
                setupSpinner(city_spinner, R.array.limon_cities, false);
            }
        }
    }

    private void setupSpinner(final Spinner view, final int arrayResId, final boolean attachListener) {
        if (view != null) {
            final String[] data = getResources().getStringArray(arrayResId);
            final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.simple_spinner_item, data);
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            view.setAdapter(mAdapter);
            if (attachListener)
                view.setOnItemSelectedListener(this);
        }
    }

    private final class DirectPos extends BaseTrxOperations {

        @Override
        protected final String validate() {
            if (username.isEmpty())
                return getResources().getString(R.string.emptyUsernameError);
            else if (password.isEmpty())
                return getResources().getString(R.string.emptyPasswordError);
            else if (ccNumber.isEmpty())
                return getResources().getString(R.string.emptyCcNumbereError);
            else if (expDate.isEmpty())
                return getResources().getString(R.string.emptyExpDateError);
            else if (amount.isEmpty())
                return getResources().getString(R.string.emptyAmountError);
            else if (!NetUtilities.hasConnection(getActivity()))
                return getResources().getString(R.string.noInternetConnection);
            else return null;
        }

        @Override
        protected final String sendTrx() {

            final Map<String, String> params = new HashMap<String, String>();

            params.put("username", username);
            params.put("password", password);
            params.put("amount", amount);
            params.put("orderid", orderId);
            params.put("processor_id", processor);
            params.put("ccnumber", ccNumber);
            params.put("ccexp", expDate);
            params.put("cvv", cvv);
            params.put("type", "auth");
            params.put("fisrtname", firstName);
            params.put("lastname", lastName);
            params.put("address1", addressLine1);
            params.put("address2", addressLine2);
            params.put("country", country);
            params.put("state", state);
            params.put("city", city);
            params.put("zip", zip);

            return NetUtilities.doPost(params, URL);
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            Log.i(TAG, "DirectPOS result: " + result);
            //Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }

    private final class BrowserRedirect extends BaseTrxOperations implements DialogInterface.OnClickListener {

        private final String redirect = ((AppGPTT) getActivity().getApplication()).getHttpServerAddress();
        private final WebViewClient mWebViewClient = new WebViewClient() {

            @Override
            public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                view.loadUrl(url);
                return true;
            }
        };

        @Override
        public final String validate() {

            if (key.isEmpty())
                return getResources().getString(R.string.emptyKeyError);
            else if (keyId.isEmpty())
                return getResources().getString(R.string.emptyKeyIdError);
            else if (ccNumber.isEmpty())
                return getResources().getString(R.string.emptyCcNumbereError);
            else if (expDate.isEmpty())
                return getResources().getString(R.string.emptyExpDateError);
            else if (amount.isEmpty())
                return getResources().getString(R.string.emptyAmountError);
            else if (!NetUtilities.hasConnection(getActivity()))
                return getResources().getString(R.string.noInternetConnection);
            else return null;
        }

        @Override
        public final String sendTrx() {
            final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"));
            final String time = String.valueOf((cal.getTimeInMillis() / 1000));

            final String hash = new Md5().getMd5(orderId + PIPE + amount + PIPE + time + PIPE + key);
            return "hash=" + hash + "&key_id=" + keyId + "&type=auth" +
                    "&time=" + time + "&amount=" + amount + "&orderid=" + orderId +
                    "&processor=" + processor + "&ccnumber=" + ccNumber + "&ccexp=" + expDate + "&cvv=" + cvv +
                    "&redirect=" + redirect + "&firstname=" + firstName + "&lastname=" + lastName +
                    "&address1=" + addressLine1 + "&address2=" + addressLine2 +
                    "&city=" + city + "&state=" + state + "&zip=" + zip + "&country=" + country;
        }

        @Override
        @SuppressLint("SetJavaScriptEnabled")
        protected final void onPostExecute(final String params) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            final WebView webView = new WebView(getActivity(), null, android.R.attr.webViewStyle);
            final WebSettings webSettings = webView.getSettings();

            alert.setView(webView);
            alert.setCancelable(false);
            alert.setNegativeButton(getResources().getString(R.string.closeButton), this);
            alert.setTitle(getResources().getString(R.string.browserRedirectTitle));

            webView.setWebViewClient(mWebViewClient);
            webView.postUrl(URL, EncodingUtils.getBytes(params, "BASE64"));

            webSettings.setSupportZoom(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(true);

            alert.show();
        }

        @Override
        public void onClick(final DialogInterface dialog, final int id) {
            dialog.dismiss();
        }
    }

    private class RadioButtonSwitcher implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            isBrowserRedirect = v == browserRedirect;
            browserRedirect.setChecked(isBrowserRedirect);
            directPos.setChecked(!isBrowserRedirect);
            setupIntegrationMethod(isBrowserRedirect);
        }
    }


}

