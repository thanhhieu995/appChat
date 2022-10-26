package vnd.hieunguyenUpdate.chatapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log


class NetworkConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//
//        val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//
//        if (wifi!!.isAvailable || mobile!!.isConnected) {
//            Toast.makeText(context, "network oke", Toast.LENGTH_LONG).show()
//            Log.d("Network Available ", "Flag No 1")
//        }

        try {
            if (isOnline(context!!)) {
//                dialog(true)
                Log.e("keshav", "Online Connect Intenet ")
            } else {
//                dialog(false)
                Log.e("keshav", "Conectivity Failure !!! ")
            }
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }
}