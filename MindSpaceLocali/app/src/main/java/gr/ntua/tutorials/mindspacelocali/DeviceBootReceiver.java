package gr.ntua.tutorials.mindspacelocali;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by manoliskaramanis on 04/04/16.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent servIntent = new Intent(context, LocationUpdates.class);
            context.startService(servIntent);

        }
    }
}