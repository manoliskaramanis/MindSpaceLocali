package gr.ntua.tutorials.mindspacelocali;

import android.*;
import android.app.Application;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by manoliskaramanis on 28/11/16.
 */

public class AppController extends Application implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "AppControllerLogs";
    private static AppController mInstance;
    private LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "oncreate");
        mInstance = this;
        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 10000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FATEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.getLatitude() + " " + location.getLongitude());
        mLastLocation =location;
    }

    public Location getmLastLocation() {
        return mLastLocation;
    }

    private RequestQueue mRequestQueue;
    private RequestQueue photoRequestQueue;
    private ImageLoader mImageLoader;
    private RequestQueue photoRequestQueue1;
    private ImageLoader mImageLoader1;

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }


        return mRequestQueue;
    }

    public RequestQueue getPhotoRequestQueue() {
        if (photoRequestQueue == null) {
            photoRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return photoRequestQueue;
    }

    public RequestQueue getPhotoRequestQueue1() {
        if (photoRequestQueue1 == null) {
            photoRequestQueue1 = Volley.newRequestQueue(getApplicationContext());
        }
        return photoRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getPhotoRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.photoRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public ImageLoader getImageLoader1() {
        getPhotoRequestQueue1();
        if (mImageLoader1 == null) {
            mImageLoader1 = new ImageLoader(this.photoRequestQueue1,
                    new LruBitmapCache());
        }
        return this.mImageLoader1;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToPhotoRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getPhotoRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public <T> void addToPhotoRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getPhotoRequestQueue().add(req);
    }

    public void cancelPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    public void cancelPendingPhotoRequests() {
        if (photoRequestQueue != null) {
            photoRequestQueue.cancelAll(TAG);
        }
    }
}
