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

# Created by @hanspampel from Android-Hilfe
# Credit also goes to @arter97
TMPDIR="/tmp/mesa/rejoice";
BOOTLOADER=$(getprop ro.boot.bootloader);

if [ ! -e /data/media/0/UP_PARAM.bak ]; then
    cat /dev/block/platform/11120000.ufs/by-name/UP_PARAM > /data/media/0/UP_PARAM.bak;
    chown 1023:1023 /data/media/0/UP_PARAM.bak;
    chmod 664 /data/media/0/UP_PARAM.bak;
fi;

mkdir $TMPDIR/splash/param;
cd $TMPDIR/splash/param;

$TMPDIR/bin/tar -xf /dev/block/platform/11120000.ufs/by-name/UP_PARAM;

case $BOOTLOADER in
  G950*)
    mv $TMPDIR/splash/dreamlte.bin $TMPDIR/splash/param/logo.jpg;
    ;;

  G955*)
    mv $TMPDIR/splash/dream2lte.bin $TMPDIR/splash/param/logo.jpg;
    ;;

  N950*)
    mv $TMPDIR/splash/greatlte.bin $TMPDIR/splash/param/logo.jpg;
    ;;
esac;

chown root:root $TMPDIR/splash/param/*;
chmod 444 $TMPDIR/splash/param/logo.jpg;
touch $TMPDIR/splash/param/*;

$TMPDIR/bin/tar -pcvf $TMPDIR/splash/up_param.bin *;

cd -;

cat $TMPDIR/splash/up_param.bin > /dev/block/platform/11120000.ufs/by-name/UP_PARAM;

