package gr.ntua.tutorials.mindspacelocali;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by manoliskaramanis on 05/04/16.
 */
public class AlarmManagerService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service1 = new Intent(context, LocationUpdates.class);
        context.startService(service1);

    }
}
