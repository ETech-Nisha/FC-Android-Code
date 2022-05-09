package com.fc.homescreen.util


import android.accounts.AccountManager
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import dmax.dialog.SpotsDialog

class Utility {
    companion object {
        const val JOB_ID = 1001;
        const val REFRESH_INTERVAL: Long =  24 * 60 * 60 * 1000 // 24 hr
    }

    fun Context.progressDialog(txt: String): AlertDialog {
        return SpotsDialog.Builder().setContext(this).setCancelable(false).setMessage(txt)
                .build()
    }

    lateinit var accountList: ArrayList<String>
    fun getAccountDetail(mcon: Activity): ArrayList<String> {
        accountList = ArrayList()
        var gmailPattern = Patterns.EMAIL_ADDRESS;
        var accounts = AccountManager.get(mcon).getAccounts();
        for (account in accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                accountList.add(account.name);
            }
        }


        /*val accounts =
               AccountManager.get(mcon).accounts
           val playAccounts = ArrayList<Any>()
           if (accounts != null && accounts.size > 0) {

               for (account in accounts) {
                   val name = account.name
                   val type = account.type
                   if (account.type == "com.google") {
                       playAccounts.add(name)
                   }
                   Log.d("TAG", "Account Info: $name:$type")
               }
               Log.d("tag", "Google Play Accounts present on phone are :: $playAccounts")
           }
           return playAccounts*/
        return accountList
    }


    fun showAlert(mcon: Context) {
        try {
            AlertDialog.Builder(mcon)
                    .setTitle("Add Account")
                    .setMessage("No google account found in your device.")
                    .setPositiveButton(
                            "Add Account"
                    ) { dialog, which ->
                        try {
                            val viewIntent = Intent(
                                    "android.intent.action.VIEW",
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.fc.homescreen")
                            )
                            mcon.startActivity(viewIntent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                    mcon, "Unable to Connect Try Again...",
                                    Toast.LENGTH_LONG
                            ).show()
                            e.printStackTrace()
                        }
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        } catch (e: Exception) {
            Toast.makeText(
                    mcon, "..." + e.message,
                    Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

}