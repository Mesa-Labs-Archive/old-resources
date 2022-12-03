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
BOOTLOADER=$(getprop ro.boot.bootloader);
BOOT="/dev/block/platform/11120000.ufs/by-name/BOOT";

case $BOOTLOADER in
  G950*)
    mv $TMPDIR/kernel/dreamlte.bin $TMPDIR/kernel/boot.img;
    ;;

  G955*)
    $TMPDIR/bin/bspatch $TMPDIR/kernel/dreamlte.bin $TMPDIR/kernel/boot.img $TMPDIR/kernel/dream2lte.p;
    ;;

  N950*)
    $TMPDIR/bin/bspatch $TMPDIR/kernel/dreamlte.bin $TMPDIR/kernel/boot.img $TMPDIR/kernel/greatlte.p;
    ;;
esac;

if [ -f $TMPDIR/kernel/boot.img ] ; then
   cat $TMPDIR/kernel/boot.img > $BOOT;
else
   exit 1;
fi;

exit 0;

