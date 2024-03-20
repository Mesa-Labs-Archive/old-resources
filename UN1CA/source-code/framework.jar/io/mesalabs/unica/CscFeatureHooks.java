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

import android.util.Log;

import java.util.HashMap;

public class CscFeatureHooks {
    private static final String TAG = "CscFeatureHooks";
    private static final boolean DEBUG = true;

    private static final HashMap<String, Boolean> BOOLEAN_FEATURES = new HashMap<>();
    private static final HashMap<String, String> STRING_FEATURES = new HashMap<>();
    private static final HashMap<String, Integer> INTEGER_FEATURES = new HashMap<>();

    static {
        BOOLEAN_FEATURES.put(
                "CscFeature_Common_DisableBixby", Boolean.FALSE);
        BOOLEAN_FEATURES.put(
                "CscFeature_Common_SupportPrivateMode", Boolean.TRUE);
        BOOLEAN_FEATURES.put(
                "CscFeature_Common_SupportZProjectFunctionInGlobal", Boolean.TRUE);
        BOOLEAN_FEATURES.put(
                "CscFeature_Gallery_SupportAliveZoom", Boolean.TRUE);
        BOOLEAN_FEATURES.put(
                "CscFeature_Setting_EnableHwVersionDisplay", Boolean.TRUE);
        BOOLEAN_FEATURES.put(
                "CscFeature_Setting_SupportMenuSmartTutor", Boolean.FALSE);
        BOOLEAN_FEATURES.put(
                "CscFeature_Setting_SupportRealTimeNetworkSpeed", Boolean.TRUE);

        STRING_FEATURES.put(
                "CscFeature_Common_ConfigSvcProviderForUnknownNumber",
                "whitepages,whitepages,off"
        );
        STRING_FEATURES.put(
                "CscFeature_Common_ConfigYuva",
                ""
        );
        STRING_FEATURES.put(
                "CscFeature_SmartManager_ConfigSubFeatures",
                "appdatacleaner|trafficmanager_auto|fake_base_station|badappnoti|trafficmanager|applock|riskcontrol|newdataplan"
        );
        STRING_FEATURES.put(
                "CscFeature_VoiceCall_ConfigRecording",
                "RecordingAllowed"
        );

        INTEGER_FEATURES.put(
                "CscFeature_Setting_ConfigTypeHelp", 0);
    }

    public static Boolean onGetBooleanHook(String tag) {
        Boolean value = null;

        if (BOOLEAN_FEATURES.containsKey(tag)) {
            value = BOOLEAN_FEATURES.get(tag);
        }

        if (value != null) {
            dlog("Spoofing \"" + tag + "\" to \"" + value + "\"");
        }
        return value;
    }

    public static String onGetStringHook(String tag) {
        String value = null;

        if (STRING_FEATURES.containsKey(tag)) {
            value = STRING_FEATURES.get(tag);
        }

        if (value != null) {
            dlog("Spoofing \"" + tag + "\" to \"" + value + "\"");
        }
        return value;
    }

    public static Integer onGetIntHook(String tag) {
        Integer value = null;

        if (INTEGER_FEATURES.containsKey(tag)) {
            value = INTEGER_FEATURES.get(tag);
        }

        if (value != null) {
            dlog("Spoofing \"" + tag + "\" to \"" + value + "\"");
        }
        return value;
    }

    public static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    private CscFeatureHooks() {
    }

}
