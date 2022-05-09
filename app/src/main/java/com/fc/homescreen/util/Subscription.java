/*
package com.fractal.chaos.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.fractal.chaos.R;

import java.util.List;

public class Subscription extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private BillingProcessor billingProcessor;
    public static final String TAG = Subscription.class.getSimpleName();
    BillingSuccess callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initialSubscription(boolean value, BillingSuccess callback) {
        this.callback = callback;
        billingProcessor = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvSp/4RXZRyBaTks+oB5hgMOATNCNqN0eT48detX4S/zLvg+yHPalxrD3CczsDG6J6RrshRAVXkSltw7sgLlRA5owPG20MYY7OwCWSGgKK/Oi6gFDOfW7w9D8yQpnj1Qmc8dVjom9xJZdJpKEWbVEexlAuJuhuac31sAVozWuC8r8dj3Ed/dq4ZdIAatXFid5vZ0dgqFiuzfEgsuWyTTSWP/pr54kNFKbxk12GorYDB8XvytcdVDAFr8ioJK/VwzKCZiAPq4GILh57Y+2jVgkecxTeuAL0VhlMsKufuCKSyOj+Ozo17ZnyDcNDl3K/iXJyLLDMQ8WSiczePrtgr0Q5QIDAQAB", this);

    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        try {
            if (billingProcessor != null) {
                setPurchasedPlan(billingProcessor);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        showLogForResponse();
        callback.onSuccess();
    }

    private void setPurchasedPlan(BillingProcessor billingProcessor) {
        //mPreferencesUtil.savePreferences(PreferenceConnector.GOOGLEPlanNAME,getResources().getString(R.string.monthly_product));


        */
/*if (billingProcessor.isSubscribed(getResources().getString(R.string.yearly_product))) {
            mPreferencesUtil.savePreferences(PreferenceConnector.GOOGLEPlanNAME, getResources().getString(R.string.yearly_product));
            //EventBus.getDefault().post(new TickSubscribedPlanEvent(TickSubscribedPlanEvent.ANNUALLY_PLAN));
        } else if (billingProcessor.isSubscribed(getResources().getString(R.string.monthly_product))) {
            mPreferencesUtil.savePreferences(PreferenceConnector.GOOGLEPlanNAME, getResources().getString(R.string.monthly_product));
            //EventBus.getDefault().post(new TickSubscribedPlanEvent(TickSubscribedPlanEvent.MONTHLY_PLAN));

        }*//*

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (errorCode == 101) {
            Toast.makeText(this,getResources().getString(R.string.no_email), Toast.LENGTH_LONG).show();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market:")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps")));
            }
        } else {

        }
        callback.onError();
    }

    @Override
    public void onBillingInitialized() {
        initializedBilling();
    }

    private void initializedBilling() {
        billingProcessor.loadOwnedPurchasesFromGoogle();
        //showLogForResponse();
        try {
            if (billingProcessor != null) {
                setPurchasedPlan(billingProcessor);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        checkSubscription();
        */
/*if (!isNotSubscriptionActivity) {
            checkSubscription();
        }*//*

    }

    private void checkSubscription() {
        //mPreferenceConnector.savePreferences("subscriptionType", "monthly");





        */
/*  List<String> ownedItemList = billingProcessor.listOwnedSubscriptions();

        if (ownedItemList.size() > 0) {

            boolean isSubscribed = false;
            for (String ownedItem : ownedItemList) {

                if (ownedItem.equals(getResources().getString(R.string.yearly_product))) {
                    if (billingProcessor.isSubscribed(ownedItem))
                        isSubscribed = true;
                    isAlreadySubscibed = true;
                    mPreferenceConnector.savePreferences("subscriptionType", "annual");
                } else if (ownedItem.equals(getResources().getString(R.string.monthly_product))) {
                    if (billingProcessor.isSubscribed(ownedItem))
                        isSubscribed = true;
                    isAlreadySubscibed = true;
                    mPreferenceConnector.savePreferences("subscriptionType", "monthly");
                }
            }
            if (isHomeActivity) {
                if (!isSubscribed) {
                    showLogoutAlertDialog(getString(R.string.message), getResources().getString(R.string.sub_end_msg), mPreferenceConnector);
                }
            }


        } else {
            if (isHomeActivity)
                showLogoutAlertDialog(getString(R.string.message), getResources().getString(R.string.sub_end_msg), mPreferenceConnector);
        }*//*

    }

    private void showLogForResponse() {
        try {
            Log.e(TAG, "initilaized in app billing");
            Log.e(TAG, "initilaized====================subscribtion status===============================");
            Log.e(TAG, "initilaized monthly status" + billingProcessor.isSubscribed(getResources().getString(R.string.monthly_product)));
            Log.e(TAG, "initilaized===================================================");
            Log.e(TAG, "initilaized list owned subscribtion" + billingProcessor.listOwnedSubscriptions());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    */
/**
     * buy subscription plan
     *//*

    public void purchaseProduct(String productId) {

        List<String> ownedItemList = billingProcessor.listOwnedSubscriptions();
        if (ownedItemList.size() > 0) {

            boolean isSubscribed = false;
            String oldProductId = "";
            for (String ownedItem : ownedItemList) {
               */
/* if (ownedItem.equals(getResources().getString(R.string.yearly_product))) {
                    if (billingProcessor.isSubscribed(ownedItem))
                        isSubscribed = true;
                    oldProductId = getResources().getString(R.string.yearly_product);


                } else *//*
if (ownedItem.equals(getResources().getString(R.string.monthly_product))) {
                    if (billingProcessor.isSubscribed(ownedItem))
                        isSubscribed = true;

                    oldProductId = getResources().getString(R.string.monthly_product);

                } else {
                    billingProcessor.subscribe(this, productId);

                }
            }
            if (isSubscribed) {
                billingProcessor.updateSubscription(this, oldProductId, productId);
            } else {
                billingProcessor.subscribe(this, productId);
            }
        } else {
            billingProcessor.subscribe(this, productId);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (billingProcessor != null) {
            if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            } else {
            }
        }
    }

    */
/*requestcode 32459
    and resultCode -1*//*


    @Override
    protected void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();

    }

    public interface BillingSuccess {
        public void onSuccess();

        public void onError();
    }
}
*/
