#!/sbin/sh
#
# OnTheOne Installer Script
# Coded by BlackMesa123
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#	  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

TMPDIR="/tmp/mesa/rejoice";
VENDORPROP="/mnt/vendor/build.prop";
BOOTLOADER=$(getprop ro.boot.bootloader);

echo "# device related props" >> "$VENDORPROP";

case $BOOTLOADER in
  G95*)
    $TMPDIR/scripts/set_prop.sh "ro.product.first_api_level" "24";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_cached_min" "6";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_cached_max" "16";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.dha_cached_max" "24";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_empty_min" "8";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_empty_init" "24";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_empty_max" "24";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.dha_empty_max" "30";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.dha_empty_init" "30";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_pwhl_key" "1536";
    $TMPDIR/scripts/set_prop.sh "ro.config.upgrade_pressure" "30";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.upgrade_pressure" "50";
    $TMPDIR/scripts/set_prop.sh "ro.config.custom_sw_limit" "250";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.custom_sw_limit" "275";
    $TMPDIR/scripts/set_prop.sh "ro.gfx.driver.0" "com.samsung.gpudriver.S8MaliG71_90";
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_COMMON_CONFIG_EDGE" "people,task,circle,panel,-edgefeeds,edgelighting_v2,debug,search,phonecolor,cleanhome"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_SPEN_VERSION" "0"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_EDGE_QUICKTOOLS_SCREEN_HEIGHT" "1422,0"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_NFC_LED_COVER_LEVEL" "30"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_MMFW_CONFIG_UHDA" "188"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_SETTINGS_CONFIG_BRAND_NAME" "Galaxy S8+"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_SETTINGS_CONFIG_FCC_ID" "A3LSMG955F"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_SYSTEMUI_CONFIG_CORNER_ROUND" "5.0"
    ;;

  N950*)
    $TMPDIR/scripts/set_prop.sh "ro.product.first_api_level" "25";
    $TMPDIR/scripts/set_prop.sh "ro.smps.enable" "true";
    $TMPDIR/scripts/set_prop.sh "ro.security.keystore.keytype" "sak";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_cached_min" "6";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_cached_max" "22";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_empty_min" "8";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_empty_init" "24";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_empty_max" "24";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.dha_empty_max" "30";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.dha_empty_init" "30";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_pwhitelist_enable" "1";
    $TMPDIR/scripts/set_prop.sh "ro.config.dha_pwhl_key" "1536";
    $TMPDIR/scripts/set_prop.sh "ro.config.fall_prevent_enable" "true";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.upgrade_pressure" "80";
    $TMPDIR/scripts/set_prop.sh "ro.cfg.custom_sw_limit" "300";
    $TMPDIR/scripts/set_prop.sh "ro.gfx.driver.0" "com.samsung.gpudriver.N8MaliG71_90";
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_COMMON_CONFIG_EDGE" "people,task,circle,panel,-edgefeeds,edgelighting_v2,debug,cornerR:3.5,search,phonecolor,cleanhome"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_COMMON_SUPPORT_SPEN_ALERT" "TRUE"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_EDGE_QUICKTOOLS_SCREEN_HEIGHT" "1445,0"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_NFC_LED_COVER_LEVEL" "40"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_SPEN_GARAGE_SPEC" "type=insert, position=right, tip_direction=top"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_SPEN_VERSION" "30"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_MMFW_CONFIG_UHDA" "172"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_SETTINGS_CONFIG_BRAND_NAME" "Galaxy Note8"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_SETTINGS_CONFIG_FCC_ID" "A3LSMN950F"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_SETTINGS_SUPPORT_S_PEN_HOVERING_N_DETACHMENT" "TRUE"
    $TMPDIR/scripts/set_floating.sh "SEC_FLOATING_FEATURE_SYSTEMUI_CONFIG_CORNER_ROUND" "3.5"
    ;;
esac;

echo "" >> "$VENDORPROP";
echo "" >> "$VENDORPROP";

