package com.credomatic.gprod.android.gptt.webserver.handlers;

import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.util.Log;


import com.credomatic.gprod.android.gptt.R;
import com.credomatic.gprod.android.gptt.app.AppGPTT;
import com.credomatic.gprod.android.gptt.utilities.net.NetUtilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhernandezs on 18/12/13 for GProdTestingTool.
 */
public class TrxResponseHandler extends DefaultHandler {

    private static final String TAG = TrxResponseHandler.class.getSimpleName();
    private final static int NOTIFICATION_ID = 0;

    public TrxResponseHandler(AppGPTT context) {
        super(context);
    }

    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse, final HttpContext httpContext) throws HttpException, IOException {


        final Uri uri = Uri.parse(httpRequest.getRequestLine().getUri());
        final String resp = getMessage(uri, context);

        context.sendNotificationMessage("3DSecure Response message received...", NOTIFICATION_ID,
                Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL);

        Log.i(TAG, "Message URI: " + uri.toString());
        final HttpEntity entity = new EntityTemplate(new ContentProducer() {

            public void writeTo(final OutputStream outStream) throws IOException {
                final OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
                writer.write(resp);
                writer.flush();
            }
        });

        httpResponse.setHeader("Content-Type", "text/html");
        httpResponse.setEntity(entity);
    }

    private static String getMessage(final Uri uri, final Context context) throws UnsupportedEncodingException {

        String html = "";
        final String htmltemplate = NetUtilities.openHTMLString(context, R.raw.transaction_response);
        final List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();

        values.add(new BasicNameValuePair("Response:", URLDecoder.decode(uri.getQueryParameter("response"), "UTF-8")));
        values.add(new BasicNameValuePair("Response Text", URLDecoder.decode(uri.getQueryParameter("responsetext"), "UTF-8")));
        values.add(new BasicNameValuePair("Response Code:", URLDecoder.decode(uri.getQueryParameter("response_code"), "UTF-8")));
        values.add(new BasicNameValuePair("Cvv Response:", URLDecoder.decode(uri.getQueryParameter("cvvresponse"), "UTF-8")));
        values.add(new BasicNameValuePair("Transaction Id:", URLDecoder.decode(uri.getQueryParameter("transactionid"), "UTF-8")));
        values.add(new BasicNameValuePair("Authorization:", URLDecoder.decode(uri.getQueryParameter("authcode"), "UTF-8")));
        values.add(new BasicNameValuePair("Order Id:", URLDecoder.decode(uri.getQueryParameter("orderid"), "UTF-8")));
        //values.add(new BasicNameValuePair("Hash Validation:", URLDecoder.decode(uri.getQueryParameter("hash"), "UTF-8")));
        values.add(new BasicNameValuePair("OutBound Hash:", URLDecoder.decode(uri.getQueryParameter("hash"), "UTF-8")));
        values.add(new BasicNameValuePair("Time:", URLDecoder.decode(uri.getQueryParameter("time"), "UTF-8")));
        values.add(new BasicNameValuePair("Amount:", URLDecoder.decode(uri.getQueryParameter("amount"), "UTF-8")));

        for (BasicNameValuePair v : values) {
            html += "<tr>";
            html += "<td>" + v.getName() + "<br></td>";
            html += "<td>" + v.getValue() + "</td>";
            html += "</tr>";
        }
        html = htmltemplate.replace("%VALUES%", html);

        return html;
    }
}
