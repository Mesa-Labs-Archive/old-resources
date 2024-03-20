/*
 * Copyright (C) 2024 BlackMesa123
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.mesalabs.unica;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.Arrays;

@RequiresApi(34)
public class SamsungPropsHooks {
    private static final String AUTO_BLOCKER_PACKAGE_NAME = "com.samsung.android.rampart";
    private static final String FM_RADIO_PACKAGE_NAME = "com.sec.android.app.fm";
    private static final String SAMSUNG_HEALTH_PACKAGE_NAME = "com.sec.android.app.shealth";
    private static final String SECURE_WIFI_PACKAGE_NAME = "com.samsung.android.fast";
    private static final String SMART_VIEW_PACKAGE_NAME = "com.samsung.android.smartmirroring";

    private static final String TAG = "KnoxPatchHooks";
    private static final boolean DEBUG = true;

    private static final String[] sTargetPackages = {
            AUTO_BLOCKER_PACKAGE_NAME,
            SAMSUNG_HEALTH_PACKAGE_NAME,
            SECURE_WIFI_PACKAGE_NAME
    };

    private static volatile String sPackageName;
    private static volatile boolean sSpoofBuildType, sSpoofDeviceToA525G;
    private static volatile boolean sSpoofProps, sSpoofKeystore;
    private static volatile boolean sSpoofKnox;

    public static void init(Context context) {
        final String packageName = context.getPackageName();
        if (packageName == null || packageName.isEmpty()) {
            return;
        }

        sPackageName = packageName;

        sSpoofBuildType = packageName.equals(SMART_VIEW_PACKAGE_NAME);
        sSpoofDeviceToA525G = packageName.equals(FM_RADIO_PACKAGE_NAME);
        sSpoofProps = Arrays.asList(sTargetPackages).contains(packageName);
        sSpoofKeystore = packageName.equals(SAMSUNG_HEALTH_PACKAGE_NAME);

        try {
            final ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA));
            final int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            sSpoofKnox = (ai.flags & mask) == 0;
        } catch (Exception e) {
            dlog("sSpoofKnox = false; " + e.getClass().getName());
            sSpoofKnox = false;
        }

        if (sSpoofBuildType) {
            setBuildField("TYPE", "userdebug");
        }
        if (sSpoofDeviceToA525G) {
            setBuildField("MODEL", "SM-A526B");
        }
    }

    public static int onEDMGetAPILevelHook() {
        if (!sSpoofKnox) {
            // Knox 3.10
            return Integer.parseInt("37");
        }

         return -1;
    }

    public static String onSPGetHook(String key) {
        String value = null;

        if (sSpoofProps)  {
            if (key.equals("ro.boot.flash.locked")) {
                value = "1";
            } else if (key.equals("ro.boot.verifiedbootstate")) {
                value = "green";
            } else if (key.equals("ro.boot.warranty_bit")) {
                value = "0";
            } else if (key.equals("ro.security.keystore.keytype")) {
                if (sSpoofKeystore) {
                    value = "";
                }
            }
        }

        if (value != null) {
            dlog("Spoofing \"" + key + "\" to \"" + value + "\"");
        }
        return value;
    }

    private static void setBuildField(String key, String value) {
        try {
            // Unlock
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);

            // Edit
            field.set(null, value);

            // Lock
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }

    public static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, "[" + sPackageName + "] " + msg);
    }

    private SamsungPropsHooks() {
    }

}
