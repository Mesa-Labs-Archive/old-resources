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

package io.mesalabs.unica.pif;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.StringRes;
import androidx.preference.Preference;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.mesalabs.myapplication.R;

public class PIFUtils {
    private static final String URL = "https://raw.githubusercontent.com/BlackMesa123/UN1CA/fourteen/unica/pif.json";

    private static final String TAG = "PIFUtils";

    private static final int STATUS_CHECKING = 0;
    private static final int STATUS_UPDATED = 1;
    private static final int STATUS_UP_TO_DATE = 2;
    private static final int STATUS_ERROR = 3;

    public static void updatePIF(Context context, Preference preference) {
        showToast(context, STATUS_CHECKING);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                JSONObject json = getJson();
                handler.post(() -> {
                    setPIFConfig(context, json);
                    preference.setSummary(SystemProperties.get("persist.sys.pif.version", "Unknown"));
                });

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Exception: ", e);
                handler.post(() -> showToast(context, STATUS_ERROR));
            }
        });
    }

    private static void setPIFConfig(Context context, JSONObject json) {
        try {
            final int propVersion = Integer.parseInt(SystemProperties.get("persist.sys.pif.version", "0"));
            final int jsonVersion = Integer.parseInt(json.getString("VERSION"));
            if (propVersion < jsonVersion) {
                SystemProperties.set("persist.sys.pif.version", json.getString("VERSION"));
                SystemProperties.set("persist.sys.pif.product", json.getString("PRODUCT"));
                SystemProperties.set("persist.sys.pif.device", json.getString("DEVICE"));
                SystemProperties.set("persist.sys.pif.manufacturer", json.getString("MANUFACTURER"));
                SystemProperties.set("persist.sys.pif.brand", json.getString("BRAND"));
                SystemProperties.set("persist.sys.pif.model", json.getString("MODEL"));
                SystemProperties.set("persist.sys.pif.fingerprint", json.getString("FINGERPRINT"));
                SystemProperties.set("persist.sys.pif.security_patch", json.getString("SECURITY_PATCH"));
                SystemProperties.set("persist.sys.pif.first_api_level", json.getString("DEVICE_INITIAL_SDK_INT"));

                showToast(context, STATUS_UPDATED);
            } else {
                showToast(context, STATUS_UP_TO_DATE);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception: ", e);
            showToast(context, STATUS_ERROR);
        }
    }

    private static JSONObject getJson() throws IOException, JSONException {
        URL url = new URL(URL);
        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return new JSONObject(json.toString());
        }
    }

    private static void showToast(Context context, int status) {
        @StringRes int resId;
        switch (status) {
            case STATUS_CHECKING:
                resId = R.string.unica_pif_checking;
                break;
            case STATUS_UPDATED:
                resId = R.string.unica_pif_updated;
                break;
            case STATUS_UP_TO_DATE:
                resId = R.string.unica_pif_up_to_date;
                break;
            case STATUS_ERROR:
                resId = R.string.unica_pif_error;
                break;
            default:
                return;
        }

        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
