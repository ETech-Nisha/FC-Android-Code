package com.fc.homescreen.util;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.SubscriptionPurchase;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class SubDetail {
    public void get(Context context) {

        HttpTransport httpTransport = new NetHttpTransport();

        JsonFactory jsonFactory = new JacksonFactory();

        List<String> scopes = new ArrayList<String>();
        scopes.add(AndroidPublisherScopes.ANDROIDPUBLISHER);


        HttpRequestInitializer credential = null;

        try {

            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(context.getAssets().open("key.p12"), "notasecret".toCharArray());
            PrivateKey pk = (PrivateKey) keystore.getKey("privatekey", "notasecret".toCharArray());






            credential = new GoogleCredential.Builder().setJsonFactory(jsonFactory)
                    .setTransport(httpTransport)
                    .setServiceAccountId("your-service@api-621**********846-16504.iam.gserviceaccount.com")
                    .setServiceAccountPrivateKey(pk)
                    .setServiceAccountScopes(scopes)
                    .build();

        } catch (
                GeneralSecurityException e) {

        } catch (
                IOException e) {

        }


        AndroidPublisher publisher = new AndroidPublisher.Builder(httpTransport, jsonFactory, credential).build();
        AndroidPublisher.Purchases purchases = publisher.purchases();


        try {

            AndroidPublisher.Purchases.Subscriptions.Get get = purchases.subscriptions()
                    .get("com.fractal.chaos", "product_001", "token");

            final SubscriptionPurchase purchase = get.execute();

            if (purchase != null && purchase.getPaymentState() != null) {

                int paymentState = purchase.getPaymentState();

                if (paymentState == 0) {
                    // Subscriber with payment pending
                } else if (paymentState == 1) {
                    // Subscriber in good standing (paid)
                } else if (paymentState == 2) {
                    // Subscriber in free trial
                }
            }


        } catch (
                IOException e) {
        }

    }
}



