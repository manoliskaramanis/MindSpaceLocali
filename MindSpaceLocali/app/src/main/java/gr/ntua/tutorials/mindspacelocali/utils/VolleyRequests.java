package gr.ntua.tutorials.mindspacelocali.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.ntua.tutorials.mindspacelocali.AppController;

/**
 * Created by manoliskaramanis on 30/11/16.
 */

public class VolleyRequests {

    public void login(final JSONObject jsonBody, final VolleyCallbackLogin callback, final Activity activity) throws JSONException {
        String url = "http://192.168.1.2:8080/locali_v3" + "/FastLogin";
        Log.d("VolleyLogs", jsonBody.toString());
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        try {
                            String response = jsonResponse.getString("response");

                            if(response.equals("ok")){
                                String id = jsonResponse.getString("id");

                                callback.onSuccess(id);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        String error;
                        volleyError.printStackTrace();
                        if (volleyError instanceof NoConnectionError) {
                            error = "No internet Access";

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    activity);
                            alertDialogBuilder.setTitle(error);
                            alertDialogBuilder
                                    .setMessage("Check your internet connection.")
                                    .setCancelable(false)
                                    .setNegativeButton("Οκ", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("site", "code");
                params.put("network", "tutsplus");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json;charset=utf-8");
                headers.put("Accept", "application/json;charset=utf-8");

                return headers;
            }

            @Override
            public byte[] getBody() {

                try {
                    byte[] bytes = jsonBody.toString().getBytes("UTF-8");
                    return bytes;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;

            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                response.headers.put("Content-Type", "application/json;charset=utf-8");

                return super.parseNetworkResponse(response);
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
//        AppController.getInstance().addToRequestQueue(postRequest, "json_obj_req");
    }

    public interface VolleyCallbackLogin {
        void onSuccess(String response) throws JSONException;
    }
}
