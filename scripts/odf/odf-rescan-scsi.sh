#!/bin/bash
for i in `oc get nodes | grep worker | awk '{print $1}'`;
do
    echo "--------------------lsblock before -------------------";echo
    oc debug nodes/$i -- chroot /host lsblk ;
    oc debug nodes/$i -- chroot /host scsi-rescan -a ;
    echo "--------------------lsblock after -------------------";echo
    oc debug nodes/$i -- chroot /host lsblk ;
done


