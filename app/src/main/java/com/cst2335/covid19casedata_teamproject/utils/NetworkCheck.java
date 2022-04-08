package com.cst2335.covid19casedata_teamproject.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * To check network connectivity in device
 */
public class NetworkCheck {
    //Network information variables
    private ConnectivityManager mNetworkConnection;
    private Context mNetworkContext;
    private boolean mNetworkConnectingFlag=false;

    /**
     * Function to check network connection
     */
    public boolean connectivityCheck(Context context) {
        try {
            //Network connectivity check
            mNetworkConnection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(mNetworkConnection != null && mNetworkConnection.getActiveNetworkInfo()!=null && mNetworkConnection.getActiveNetworkInfo().isAvailable() && mNetworkConnection.getActiveNetworkInfo().isConnectedOrConnecting()){
                if(mNetworkConnection.getActiveNetworkInfo().isConnected()){
                    mNetworkConnectingFlag=true;
                }else if(mNetworkConnection.getActiveNetworkInfo().isFailover()){
                    mNetworkConnectingFlag=false;
                }
            }else{
                mNetworkConnectingFlag=false;
            }
            return mNetworkConnectingFlag;
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }


}




