package gr.ntua.tutorials.mindspacelocali;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

//import gr.locali.locali.details.activities.DetailsActivity;
//import gr.locali.locali.model.SearchOptions;
//import gr.locali.locali.results.activities.ResultsActivity;
//import gr.locali.locali.utils.LocationCheck;
//import gr.locali.locali.utils.VolleyRequests;
//import gr.locali.locali.utils.exception_handlers.ServiceExceptionHandler;


/**
 * Created by manoliskaramanis on 2/4/16.
 */
public class LocationUpdates extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public final static int MINUTE = 1000 * 60;

    public Location location;// location
    double latitude = 0; // latitude
    double longitude = 0; // longitude
    Boolean isInternetPresent;
    SharedPreferences sp;
    ConnectionDetector cd;
    String TAG = "shera";


    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 120000; // ten sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // ten meters
    public void connect() {
        mGoogleApiClient.connect();
    }

    public void startGoogleApiClient() {
        if (checkPlayServices()) {
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();

            }
            createLocationRequest();

        } else {

        }

    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    public class LocalBinder extends Binder {
        public LocationUpdates getService() {
            return LocationUpdates.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        AppController app = (AppController) getApplication();
//        Thread.setDefaultUncaughtExceptionHandler(new ServiceExceptionHandler(app));
        Log.d("service", "oncreate");
        cd = new ConnectionDetector(getApplicationContext());
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        startGoogleApiClient();
        connect();
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), AlarmManagerService.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 5 * 1000, pi);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setLocation();
        showNotification();
        return super.onStartCommand(intent, flags, startId);
    }


    public void setLocation() {
        SharedPreferences.Editor editor = sp.edit();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            sp.edit().putString("currentLatitude", String.valueOf(location.getLatitude())).apply();
            sp.edit().putString("currentLongitude", String.valueOf(location.getLongitude())).apply();
        } else {
            latitude = 0;
            longitude = 0;
        }

        if (!(sp.getString("latitude", "0").equals("0") || sp.getString("longitude", "0").equals("0") || sp.getString("lastLatitude", "0").equals("0") || sp.getString("lastLongitude", "0").equals("0")
                || latitude == 0 || longitude == 0)) {
            Location loc1 = new Location("");
            loc1.setLongitude(Double.valueOf(sp.getString("longitude", "0")));
            loc1.setLatitude(Double.valueOf(sp.getString("latitude", "0")));
//            LocationCheck locCheck = new LocationCheck();
//            if (locCheck.isInAthens(location) || locCheck.isInAmorgos(location) || locCheck.isInCyclades(location)) {
                if (loc1.distanceTo(location) < 50) {
                    Location loc2 = new Location("");
                    loc2.setLongitude(Double.valueOf(sp.getString("lastLongitude", "0")));
                    loc2.setLatitude(Double.valueOf(sp.getString("lastLatitude", "0")));
                    if (loc2.distanceTo(location) < 50) {
                        sendLocation();
                    }
                }
//            }


        } else {
        }
        editor.putString("lastLatitude", sp.getString("latitude", "0"));
        editor.putString("lastLongitude", sp.getString("longitude", "0"));
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.commit();
    }



    public void sendLocation() {
        isInternetPresent = cd.isConnectingToInternet(); // true or false
        if (isInternetPresent) {
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("userId", 0) != 0) {
                if (sp.getLong("lastNotificationMillis", 0) == 0 || (System.currentTimeMillis() - sp.getLong("lastNotificationMillis", 0)) > 1000 * 60 * 60 * 5) {
                    Location location1 = new Location("");
                    location1.setLatitude(Double.valueOf(sp.getString("lastNotificationLatitude", "0")));
                    location1.setLongitude(Double.valueOf(sp.getString("lastNotificationLongitude", "0")));
                    if (location1.distanceTo(location) > 150) {
                        if (sp.getLong("lastSendLocationMillis", 0) == 0 || (System.currentTimeMillis() - sp.getLong("lastSendLocationMillis", 0)) > 1000 * 60 * 15) {
                            Location location2 = new Location("");
                            location2.setLatitude(Double.valueOf(sp.getString("lastSendLatitude", "0")));
                            location2.setLongitude(Double.valueOf(sp.getString("lastSendLongitude", "0")));
                            if (location2.distanceTo(location) > 100) {
//                                VolleyRequests volley = new VolleyRequests();
                                JSONObject jsonBody = new JSONObject();
                                sp.edit().putLong("lastSendLocationMillis", System.currentTimeMillis()).commit();
                                sp.edit().putString("lastSendLatitude", String.valueOf(location.getLatitude())).commit();
                                sp.edit().putString("lastSendLongitude", String.valueOf(location.getLongitude())).commit();
                                try {
                                    Double minLat = location.getLatitude() - 0.0004;
                                    Double maxLat = location.getLatitude() + 0.0004;
                                    Double minLong = location.getLongitude() - 0.0004;
                                    Double maxLong = location.getLongitude() + 0.0004;

                                    Double minLatNeigh = location.getLatitude() - 0.004;
                                    Double maxLatNeigh = location.getLatitude() + 0.004;
                                    Double minLongNeigh = location.getLongitude() - 0.004;
                                    Double maxLongNeigh = location.getLongitude() + 0.004;

                                    Double minLatCity = location.getLatitude() - 0.008;
                                    Double maxLatCity = location.getLatitude() + 0.008;
                                    Double minLongCity = location.getLongitude() - 0.008;
                                    Double maxLongCity = location.getLongitude() + 0.008;

                                    jsonBody.put("latitude", location.getLatitude());
                                    jsonBody.put("longitude", location.getLongitude());
                                    jsonBody.put("minLat", minLat);
                                    jsonBody.put("maxLat", maxLat);
                                    jsonBody.put("minLong", minLong);
                                    jsonBody.put("maxLong", maxLong);
                                    jsonBody.put("minLatNeigh", minLatNeigh);
                                    jsonBody.put("maxLatNeigh", maxLatNeigh);
                                    jsonBody.put("minLongNeigh", minLongNeigh);
                                    jsonBody.put("maxLongNeigh", maxLongNeigh);
                                    jsonBody.put("minLatCity", minLatCity);
                                    jsonBody.put("maxLatCity", maxLatCity);
                                    jsonBody.put("minLongCity", minLongCity);
                                    jsonBody.put("maxLongCity", maxLongCity);
                                    jsonBody.put("userID", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("userId", 0));
                                    Log.d("updateLocationBody", jsonBody.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                volley.sendLocation(jsonBody, new VolleyRequests.VolleyCallbackLocation() {
//
//                                    @Override
//                                    public void onSuccess(JSONObject place) {
//                                        try {
//                                            if (place.getString("response").equals("ok")) {
//                                                notification(place);
//                                                sp.edit().putLong("lastNotificationMillis", System.currentTimeMillis()).commit();
//                                                sp.edit().putString("lastNotificationLatitude", String.valueOf(location.getLatitude())).commit();
//                                                sp.edit().putString("lastNotificationLongitude", String.valueOf(location.getLongitude())).commit();
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
                            }

                        }
                    }

                }
            }
        }
    }

    private void showNotification(){
        Intent intent4 = new Intent(this, SignupActivity.class);

        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent4 = PendingIntent.getActivity(this, (int) System.currentTimeMillis() + 5, intent4,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent5 = new Intent(this, SignupActivity.class);

        intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent5 = PendingIntent.getActivity(this, (int) System.currentTimeMillis() + 5, intent5,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Mindspace")
                .setContentText("Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Notification!!!!"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent4)
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Start", pendingIntent5)
                ;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(234154154, notificationBuilder.build());
    }


//    private void notification(final JSONObject place) throws IOException, JSONException {
//
//        if(place.has("welcomeToRegion")){
//            SearchOptions options = new SearchOptions();
//            options.setRegionID(place.getInt("regionID"));
//            String region = null;
//            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language", "").equals("el")){
//                region = place.getString("greekRegion");
//            }else{
//                region = place.getString("region");
//            }
//            String message = region + getString(R.string.welcomeToRegion);
//            Intent intent = new Intent(this, ResultsActivity.class);
//            intent.putExtra("parent", "search");
//            intent.putExtra("searchOptions", options);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 123456, intent,
//                    PendingIntent.FLAG_ONE_SHOT);
//
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.logo_trasparent)
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
//                    .setContentTitle("Locali")
//                    .setContentText(message)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setContentIntent(pendingIntent);
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(123456, notificationBuilder.build());
//        }else if (place.has("photos")) {
//            AppController.getInstance().getImageLoader().get(place.getJSONArray("photos").getString(0), new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    try {
//                        if(response.getBitmap() != null){
//                            notific(place, response.getBitmap());
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    try {
//                        simpleNotification(place);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } else {
//
//simpleNotification(place);
//
//        }
//    }

//    private void simpleNotification(JSONObject place) throws IOException, JSONException {
//        Intent intent = new Intent(this, DetailsActivity.class);
//        intent.putExtra("placeID", place.getInt("placeID"));
//        intent.putExtra("name", place.getString("name"));
//        intent.putExtra("parent", "searchByName");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, place.getInt("placeID"), intent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Intent intent2 = new Intent(this, DetailsActivity.class);
//        intent2.putExtra("placeID", place.getInt("placeID"));
//        intent2.putExtra("name", place.getString("name"));
//        intent2.putExtra("parent", "searchByName");
//        intent2.putExtra("notification", "addReview");
//        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent2,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Intent intent4 = new Intent(this, DetailsActivity.class);
//        intent4.putExtra("placeID", place.getInt("placeID"));
//        intent4.putExtra("name", place.getString("name"));
//        intent4.putExtra("parent", "searchByName");
//        intent4.putExtra("notification", "share");
//        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent4 = PendingIntent.getActivity(this, (int) System.currentTimeMillis() + 5, intent4,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.logo_trasparent)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
//                .setContentTitle("Locali")
//                .setContentText(place.getString("name"))
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(place.getString("name")))
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent)
//                .addAction(R.drawable.aksiologise, getString(R.string.addReview), pendingIntent2)
//                .addAction(R.drawable.koinopoiise, getString(R.string.share), pendingIntent4);
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(place.getInt("placeID"), notificationBuilder.build());
//    }
//
//    private void notific(final JSONObject place, Bitmap remote_picture) throws IOException, JSONException {
//        NotificationCompat.BigPictureStyle notiStyle = new
//                NotificationCompat.BigPictureStyle();
//        notiStyle.setBigContentTitle("Locali");
//        notiStyle.setSummaryText(place.getString("name"));
//        notiStyle.bigPicture(remote_picture);
//
//        Intent intent = new Intent(this, DetailsActivity.class);
//        intent.putExtra("placeID", place.getInt("placeID"));
//        intent.putExtra("name", place.getString("name"));
//        intent.putExtra("parent", "searchByName");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, place.getInt("placeID"), intent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Intent intent2 = new Intent(this, DetailsActivity.class);
//        intent2.putExtra("placeID", place.getInt("placeID"));
//        intent2.putExtra("name", place.getString("name"));
//        intent2.putExtra("parent", "searchByName");
//        intent2.putExtra("notification", "addReview");
//        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent2,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Intent intent4 = new Intent(this, DetailsActivity.class);
//        intent4.putExtra("placeID", place.getInt("placeID"));
//        intent4.putExtra("name", place.getString("name"));
//        intent4.putExtra("parent", "searchByName");
//        intent4.putExtra("notification", "share");
//        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent4 = PendingIntent.getActivity(this, (int) System.currentTimeMillis() + 5, intent4,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.logo_trasparent)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .setContentTitle("Locali")
//                .setContentText(place.getString("name"))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setSound(defaultSoundUri)
//                .addAction(R.drawable.aksiologise, getString(R.string.addReview), pendingIntent2)
//                .addAction(R.drawable.koinopoiise, getString(R.string.share), pendingIntent4)
//                .setStyle(notiStyle);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(place.getInt("placeID"), notificationBuilder.build());
//
//    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    protected void startLocationUpdates() {
        Log.d("location", "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("TAG", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        location = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (location != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
        setLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location1) {
        location = location1;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
    }


    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }


}

