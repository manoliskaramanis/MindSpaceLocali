package gr.ntua.tutorials.mindspacelocali;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by manoliskaramanis on 12/12/2016.
 */

public class YestService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        AppController app = (AppController) getApplication();
//        Thread.setDefaultUncaughtExceptionHandler(new ServiceExceptionHandler(app));
        Log.d("myservice", "oncreate");
//        cd = new ConnectionDetector(getApplicationContext());
//        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        startGoogleApiClient();
//        connect();
//        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(getApplicationContext(), AlarmManagerService.class);
//        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 5 * 1000, pi);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        setLocation();
        Log.d("myservice", "onstartcommand");

        return super.onStartCommand(intent, flags, startId);
    }

}
