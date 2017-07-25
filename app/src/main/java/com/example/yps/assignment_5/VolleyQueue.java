package com.example.yps.assignment_5;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by yPs on 3/19/2017.
 */

public class VolleyQueue {

        private static VolleyQueue mInstance;
        private RequestQueue mRequestQueue;
        private static Context mContext;
        private VolleyQueue(Context context) {
            mContext = context;
            mRequestQueue = queue();
        }
        public static synchronized VolleyQueue instance(Context context) {
            if ( mInstance == null ) {
                mInstance = new VolleyQueue(context);
            }
            return mInstance;
        }
    public RequestQueue queue() {
        if ( mRequestQueue == null ) {
// getApplicationContext() is key, it keeps you from leaking the
// Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void add(Request<T> req) {
        queue().add(req);
    }

}
