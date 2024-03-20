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

import android.app.ActivityTaskManager;
import android.app.Application;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Process;
import android.os.SystemProperties;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.Arrays;

@RequiresApi(34)
public class PlayIntegrityHooks {
    private static final String PIF_VERSION = "20240128";
    private static final String PIF_PRODUCT = "pmeuhl_00709";
    private static final String PIF_DEVICE = "htc_pmeuhl";
    private static final String PIF_MANUFACTURER = "HTC";
    private static final String PIF_BRAND = "htc";
    private static final String PIF_MODEL = "HTC_M10h";
    private static final String PIF_FINGERPRINT = "htc/pmeuhl_00709/htc_pmeuhl:8.0.0/OPR1.170623.027/1012001.8:user/release-keys";
    private static final String PIF_SECURITY_PATCH = "2018-02-01";
    private static final String PIF_DEVICE_INITIAL_SDK_INT = "null";

    private static final String TAG = "PlayIntegrityHooks";
    private static final boolean DEBUG = true;

    private static final String PACKAGE_FINSKY = "com.android.vending";
    private static final String PACKAGE_GMS = "com.google.android.gms";
    private static final String PROCESS_GMS_UNSTABLE = PACKAGE_GMS + ".unstable";

    private static final ComponentName GMS_ADD_ACCOUNT_ACTIVITY = ComponentName.unflattenFromString(
            "com.google.android.gms/.auth.uiflows.minutemaid.MinuteMaidActivity");

    private static volatile boolean sIsGms, sIsFinsky;
    private static volatile String sProcessName;

    private static boolean shouldTryToCertifyDevice() {
        final String processName = Application.getProcessName();
        if (!processName.toLowerCase().contains("unstable")
                && !processName.toLowerCase().contains("pixelmigrate")
                && !processName.toLowerCase().contains("instrumentation")) {
            return false;
        }

        setPropValue("TIME", System.currentTimeMillis());

        final boolean was = isGmsAddAccountActivityOnTop();
        final String reason = "GmsAddAccountActivityOnTop";
        if (!was) {
            return true;
        }
        dlog("Skip spoofing build for GMS, because " + reason + "!");
        TaskStackListener taskStackListener = new TaskStackListener() {
            @Override
            public void onTaskStackChanged() {
                final boolean isNow = isGmsAddAccountActivityOnTop();
                if (isNow ^ was) {
                    dlog(String.format("%s changed: isNow=%b, was=%b, killing myself!", reason, isNow, was));
                    Process.killProcess(Process.myPid());
                }
            }
        };
        try {
            ActivityTaskManager.getService().registerTaskStackListener(taskStackListener);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to register task stack listener!", e);
            return true;
        }
    }

    public static void setProps(Context context) {
        final String packageName = context.getPackageName();
        final String processName = Application.getProcessName();

        if (packageName == null || processName == null || packageName.isEmpty()) {
            return;
        }

        sProcessName = processName;
        sIsGms = packageName.equals(PACKAGE_GMS) && processName.equals(PROCESS_GMS_UNSTABLE);
        sIsFinsky = packageName.equals(PACKAGE_FINSKY);

        if (sIsGms) {
            if (shouldTryToCertifyDevice()) {
                dlog("Spoofing build for GMS to pass CTS/Play Integrity API");
                spoofBuildGms();
            }
        } else if (packageName.equals(PACKAGE_GMS)) {
            setPropValue("TIME", System.currentTimeMillis());
        }
    }

    private static void spoofBuildGms() {
        final String pifVersion = SystemProperties.get("persist.sys.pif.version", "0");
        if (Integer.parseInt(pifVersion) < Integer.parseInt(PIF_VERSION)) {
            dlog("PIF is outdated. " +
                    "Current version: " + pifVersion +
                    ", Frameworks version: " + PIF_VERSION);
        }

        final String PRODUCT = SystemProperties.get("persist.sys.pif.product", PIF_PRODUCT);
        if (!PRODUCT.equals("null")) {
            dlog("PRODUCT: " + PRODUCT);
            setPropValue("PRODUCT", PRODUCT);
        }
        final String DEVICE = SystemProperties.get("persist.sys.pif.device", PIF_DEVICE);
        if (!DEVICE.equals("null")) {
            dlog("DEVICE: " + DEVICE);
            setPropValue("DEVICE", DEVICE);
        }
        final String MANUFACTURER = SystemProperties.get("persist.sys.pif.manufacturer", PIF_MANUFACTURER);
        if (!MANUFACTURER.equals("null")) {
            dlog("MANUFACTURER: " + MANUFACTURER);
            setPropValue("MANUFACTURER", MANUFACTURER);
        }
        final String BRAND = SystemProperties.get("persist.sys.pif.brand", PIF_BRAND);
        if (!BRAND.equals("null")) {
            dlog("BRAND: " + BRAND);
            setPropValue("BRAND", BRAND);
        }
        final String MODEL = SystemProperties.get("persist.sys.pif.model", PIF_MODEL);
        if (!MODEL.equals("null")) {
            dlog("MODEL: " + MODEL);
            setPropValue("MODEL", MODEL);
        }
        final String FINGERPRINT = SystemProperties.get("persist.sys.pif.fingerprint", PIF_FINGERPRINT);
        if (!FINGERPRINT.equals("null")) {
            dlog("FINGERPRINT: " + FINGERPRINT);
            setPropValue("FINGERPRINT", FINGERPRINT);
        }
        final String SECURITY_PATCH = SystemProperties.get("persist.sys.pif.security_patch", PIF_SECURITY_PATCH);
        if (!SECURITY_PATCH.equals("null")) {
            dlog("SECURITY_PATCH: " + SECURITY_PATCH);
            setVersionFieldString("SECURITY_PATCH", SECURITY_PATCH);
        }
        final String DEVICE_INITIAL_SDK_INT = SystemProperties.get("persist.sys.pif.first_api_level", PIF_DEVICE_INITIAL_SDK_INT);
        if (!DEVICE_INITIAL_SDK_INT.equals("null")) {
            dlog("DEVICE_INITIAL_SDK_INT: " + DEVICE_INITIAL_SDK_INT);
            setVersionFieldInt("DEVICE_INITIAL_SDK_INT", Integer.parseInt(DEVICE_INITIAL_SDK_INT));
        }
    }

    private static void setPropValue(String key, Object value) {
        try {
            dlog("Defining prop " + key + " to " + value.toString());
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }

    private static void setVersionFieldString(String key, String value) {
        try {
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }

    private static void setVersionFieldInt(String key, int value) {
        try {
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }

    private static boolean isGmsAddAccountActivityOnTop() {
        try {
            final ActivityTaskManager.RootTaskInfo focusedTask =
                    ActivityTaskManager.getService().getFocusedRootTaskInfo();
            return focusedTask != null && focusedTask.topActivity != null
                    && focusedTask.topActivity.equals(GMS_ADD_ACCOUNT_ACTIVITY);
        } catch (Exception e) {
            Log.e(TAG, "Unable to get top activity!", e);
        }
        return false;
    }

    public static boolean shouldBypassTaskPermission(Context context) {
        // GMS doesn't have MANAGE_ACTIVITY_TASKS permission
        final int callingUid = Binder.getCallingUid();
        final int gmsUid;
        try {
            gmsUid = context.getPackageManager().getApplicationInfo(PACKAGE_GMS, 0).uid;
            dlog("shouldBypassTaskPermission: gmsUid:" + gmsUid + " callingUid:" + callingUid);
        } catch (Exception e) {
            Log.e(TAG, "shouldBypassTaskPermission: unable to get gms uid", e);
            return false;
        }
        return gmsUid == callingUid;
    }

    private static boolean isCallerSafetyNet() {
        return shouldTryToCertifyDevice() && sIsGms && Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(elem -> elem.getClassName().contains("DroidGuard"));
    }

    public static void onEngineGetCertificateChain() {
        // Check stack for SafetyNet or Play Integrity
        if (isCallerSafetyNet() || sIsFinsky) {
            dlog("Blocked key attestation sIsGms=" + sIsGms + " sIsFinsky=" + sIsFinsky);
            throw new UnsupportedOperationException();
        }
    }

    public static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, "[" + sProcessName + "] " + msg);
    }

    private PlayIntegrityHooks() {
    }

}
