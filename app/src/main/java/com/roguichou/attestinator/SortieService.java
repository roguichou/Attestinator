package com.roguichou.attestinator;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.Calendar;
import java.util.Locale;

public class SortieService extends Service {

    private static final int NOTIFICATION_ID = 0x10;
    private Handler handler = null;
    private Runnable runnableCode = null;
    private NotificationManager mNotificationManager;
    MainActivity currentActivity = null;
    Calendar heureSortie;

    private LocationCallback locationCallback;

    Notification notification;
    NotificationCompat.Builder notification_builder;
    RemoteViews notificationLayout;
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            heureSortie = (Calendar)intent.getSerializableExtra("heureSortie");
            currentActivity = (MainActivity)((MyApp)this.getApplicationContext()).getCurrentActivity();

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            notificationLayout = new RemoteViews(getPackageName(), R.layout.notif_layout);
            notification_builder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setContent(notificationLayout)
                    .setSmallIcon(R.drawable.ic_attestinator)
                    .setContentIntent(pendingIntent);

            notification = notification_builder.build();

            notificationLayout.setImageViewResource(R.id.imageView7,R.drawable.ic_baseline_av_timer_24);
            notificationLayout.setImageViewResource(R.id.imageView8,R.drawable.ic_fb2de6e2bfe43558df3892839dd64a5c);
            notificationLayout.setImageViewResource(R.id.imageButton,R.drawable.ic_baseline_restore_24);
            notificationLayout.setImageViewResource(R.id.imageButton2,R.drawable.ic_baseline_close_24);

            Intent switchIntent = new Intent(this, ButtonListener.class);
            switchIntent.setAction(ButtonListener.ACTION_CLOSE);
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                    switchIntent, 0);

            notificationLayout.setOnClickPendingIntent(R.id.imageButton2,
                    pendingSwitchIntent);


            Intent switchIntent2 = new Intent(this, ButtonListener.class);
            switchIntent2.setAction(ButtonListener.ACTION_RESTART);
            switchIntent2.putExtra("heureSortie", heureSortie);
            PendingIntent pendingSwitchIntent2 = PendingIntent.getBroadcast(this, 0,
                    switchIntent2, 0);

            notificationLayout.setOnClickPendingIntent(R.id.imageButton,
                    pendingSwitchIntent2);

            handler = new Handler(Looper.getMainLooper());

            // Define the code block to be executed
            runnableCode = () -> {
                // Do something here on the main thread
                Calendar maintenant = Calendar.getInstance();
                long delta = (maintenant.getTimeInMillis() - heureSortie.getTimeInMillis()) / 1000;
                delta = Constants.DUREE_SORTIE*60 - delta;
                int h = (int) delta/(60*60);
                delta -= h*60*60;
                int min = (int) (delta / 60);
                delta -= min*60;
                int sec = (int) delta;

                notificationLayout.setTextViewText(R.id.notif_timer_txt,String.format(Locale.FRANCE,"%d:%02d:%02d", h, min, sec));

                if (h ==0 && min < 5 ) {
                    notificationLayout.setTextColor(R.id.notif_timer_txt, Color.RED);
                } else {
                    notificationLayout.setTextColor(R.id.notif_timer_txt, Color.LTGRAY);
                }

                mNotificationManager.notify(NOTIFICATION_ID,notification_builder.build());

                // Repeat this the same runnable code block again another 1 seconds
                handler.postDelayed(runnableCode, 1000);
            };
            // Start the initial runnable task by posting through the handler
            handler.post(runnableCode);

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(60 * 1000);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            Location home = currentActivity.getHome();

                            int dist = (int) location.distanceTo(home);
                            currentActivity.getLog().log(Logger.LOG_INFO, "home:"+home.toString()+"loc:" + location.toString() + " dist=" + dist);
                            notificationLayout.setTextViewText(R.id.notif_dist_txt, String.format(Locale.FRENCH,"%d m", dist));

                            if (dist > Constants.DISTANCE_MAX) {
                                notificationLayout.setTextColor(R.id.notif_dist_txt, Color.RED);
                            } else {
                                notificationLayout.setTextColor(R.id.notif_dist_txt, Color.LTGRAY);
                            }
                            mNotificationManager.notify(NOTIFICATION_ID,notification_builder.build());
                        }
                    }
                }
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

                currentActivity.getFusedLocationClient().requestLocationUpdates(locationRequest, locationCallback, null);
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                startForeground(NOTIFICATION_ID, notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
            }
            else
            {
                startForeground(NOTIFICATION_ID, notification);
            }

            return START_NOT_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't provide binding, so return null
            return null;
        }

        @Override
        public void onDestroy() {
            handler.removeCallbacks(runnableCode);
            currentActivity.getFusedLocationClient().removeLocationUpdates(locationCallback);
        }

        static public class ButtonListener extends BroadcastReceiver {
            public static final String ACTION_CLOSE = "Attestinator.closeService";
            public static final String ACTION_RESTART = "Attestinator.restartTimer";
            @Override
            public void onReceive(Context context, Intent intent) {
                MainActivity currentActivity = (MainActivity)((MyApp)context.getApplicationContext()).getCurrentActivity();
                currentActivity.getLog().log(Logger.LOG_INFO, "Button click received from notification");
                if (intent != null && intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case ACTION_CLOSE:
                            currentActivity.getLog().log(Logger.LOG_INFO, "Close service request");
                            Intent serviceIntent = new Intent(context, SortieService.class);
                            context.stopService(serviceIntent);
                            break;
                        case ACTION_RESTART:
                            currentActivity.getLog().log(Logger.LOG_INFO, "Regenerate request" + context.toString());
                            Calendar heureSortie =(Calendar) intent.getSerializableExtra("heureSortie");
                            heureSortie.add(Calendar.MINUTE, Constants.DUREE_SORTIE/2);
                            currentActivity.prolongerSortie(heureSortie);
                            break;
                    }
                }

            }
        }
    }