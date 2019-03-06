package kr.or.yongin.transporthelp.common.FCM;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.util.GlobalValues;

/**
 * Created by IONEMAX on 2017-01-16.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
    private static final String TAG = "FirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Logview.Logwrite(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logview.Logwrite(TAG, "Message data payload: " + remoteMessage.getData());
            //BusEventProvider.getInstance().post(new PushMessageEvent(remoteMessage.getData()));
            Intent intent = new Intent(GlobalValues.ACTION_MAIN_SERVICE);
            intent.setPackage("kr.or.yongin.transporthelp");
            intent.putExtra("type", "PUSH");
            intent.putExtra("payload", remoteMessage.getData().get("msg"));
            startService(intent);
            //sendBroadcast(intent);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logview.Logwrite(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
