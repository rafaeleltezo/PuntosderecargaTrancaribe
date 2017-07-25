package com.app.master.puntosderecargatrancaribe.Presentador;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.app.master.puntosderecargatrancaribe.MapsActivity;
import com.app.master.puntosderecargatrancaribe.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Rafael p on 24/7/2017.
 */

public class notificacionesFirebase extends FirebaseMessagingService  {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        notificacion(remoteMessage.getFrom());
    }

    private void  notificacion(String mensaje){
        Intent intento =new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,12,intento,PendingIntent.FLAG_ONE_SHOT);
        Uri tono= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.punto_recarga)
                .setAutoCancel(true)
                .setContentTitle("Notificacion RecApp")
                .setContentText(mensaje)
                .setSound(tono)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notificationBuilder.build());
    }

}
