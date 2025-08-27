/*
 * This program is part of the OpenLMIS logistics management information
 * system platform software.
 *
 * Copyright © 2015 ThoughtWorks, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should
 * have received a copy of the GNU Affero General Public License along with
 * this program. If not, see http://www.gnu.org/licenses. For additional
 * information contact info@OpenLMIS.org
 */

package org.openlmis.core;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

// import com.crashlytics.android.Crashlytics;
// import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.danlew.android.joda.JodaTimeAndroid;

import org.openlmis.core.exceptions.LMISException;
import org.openlmis.core.googleAnalytics.AnalyticsTrackers;
import org.openlmis.core.googleAnalytics.ScreenName;
import org.openlmis.core.googleAnalytics.TrackerActions;
import org.openlmis.core.googleAnalytics.TrackerCategories;
import org.openlmis.core.manager.MovementReasonManager;
import org.openlmis.core.manager.SharedPreferenceMgr;
import org.openlmis.core.manager.UserInfoMgr;
import org.openlmis.core.network.LMISRestApi;
import org.openlmis.core.network.LMISRestManager;
import org.openlmis.core.network.NetworkConnectionManager;
import org.openlmis.core.utils.FileUtil;

import java.io.File;

// import io.fabric.sdk.android.Fabric;
import roboguice.RoboGuice;

public class LMISApp extends Application {

    private static LMISApp instance;

    public static long lastOperateTime = 0L;
    private static String TAG = "123";
    private final int facilityCustomDimensionKey = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        JodaTimeAndroid.init(this);
        RoboGuice.getInjector(this).injectMembersWithoutViews(this);
        RoboGuice.getInjector(this).getInstance(SharedPreferenceMgr.class);
        if(!BuildConfig.DEBUG) {
            setupFabric();
        }
        setupGoogleAnalytics();

        instance = this;
    }

    protected void setupGoogleAnalytics() {
        try {
            // Only initialize Google Analytics on Android 11 and below
            // Android 12+ has issues with PendingIntent flags in older GA versions
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
                AnalyticsTrackers.initialize(this);
            } else {
                android.util.Log.w("LMISApp", "Google Analytics disabled for Android 12+ due to PendingIntent compatibility issues");
            }
        } catch (Exception e) {
            // Catch any exceptions to prevent app crash
            android.util.Log.e("LMISApp", "Failed to initialize Google Analytics", e);
        }
    }

    public static LMISApp getInstance() {
        return instance;
    }

    protected void setupFabric() {
        // Fabric/Crashlytics disabled - deprecated
        // Fabric.with(this, new Crashlytics.Builder()
        //         .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
        //         .build());
    }

    public boolean isConnectionAvailable() {
        return NetworkConnectionManager.isConnectionAvailable(instance);
    }

    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MovementReasonManager.getInstance().refresh();
    }

    public boolean getFeatureToggleFor(int id) {
        return getResources().getBoolean(id);
    }

    public void logErrorOnFabric(LMISException exception) {
        // Crashlytics.logException(exception); // Fabric/Crashlytics disabled
    }

    public LMISRestApi getRestApi() {
        return LMISRestManager.getInstance(this).getLmisRestApi();
    }

    public void trackScreen(ScreenName screenName) {
        try {
            if (AnalyticsTrackers.hasBeenInitialized()) {
                Tracker mTracker = AnalyticsTrackers.getInstance().getDefault();
                mTracker.setScreenName(screenName.getScreenName());
                mTracker.send(new HitBuilders.ScreenViewBuilder()
                        .setCustomDimension(facilityCustomDimensionKey, getFacilityNameForGA())
                        .build());
            }
        } catch (Exception e) {
            android.util.Log.w("LMISApp", "Unable to track screen: " + screenName, e);
        }
    }

    public void trackEvent(TrackerCategories category, TrackerActions action) {
        try {
            if (AnalyticsTrackers.hasBeenInitialized()) {
                Tracker mTracker = AnalyticsTrackers.getInstance().getDefault();
                mTracker.send(new HitBuilders.EventBuilder(category.getString(), action.getString())
                        .setCustomDimension(facilityCustomDimensionKey, getFacilityNameForGA())
                        .build());
            }
        } catch (Exception e) {
            android.util.Log.w("LMISApp", "Unable to track event: " + category + "/" + action, e);
        }
    }

    private String getFacilityNameForGA() {
        String facilityName = UserInfoMgr.getInstance().getFacilityName();
        return TextUtils.isEmpty(facilityName)
                ? SharedPreferenceMgr.getInstance().getCurrentUserFacility() : facilityName;
    }

    public void wipeAppData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (new File(getCacheDir().getParent()).exists()) {
            for (String s : appDir.list()) {
                if (!s.equals("lib")) {
                    FileUtil.deleteDir(new File(appDir, s));
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public boolean isQAEnabled() {
        return SharedPreferenceMgr.getInstance().isQaDebugEnabled();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
