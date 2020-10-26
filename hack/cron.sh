#!/bin/bash
export TZ='Asia/Kolkata'
printf "Output of oc get nodes on bastion at " && date
/usr/local/bin/oc get nodes
printf "\nOutput of oc get co on bastion at " && date
/usr/local/bin/oc get co
printf "\nOutput of oc adm top nodes on bastion at " && date
/usr/local/bin/oc adm top nodes
printf "\nOutput of oc get pods --all-namespaces -o=wide | grep -vi running | grep -vi completed on bastion at " && date
/usr/local/bin/oc get pods --all-namespaces -o=wide | grep -vi running | grep -vi completed
printf "\nOutput of cat /proc/meminfo on bastion at " && date
/usr/bin/cat /proc/meminfo
printf "\nOutput of systemd-cgtop on bastion at  " && date
ssh -oStrictHostKeyChecking=no root@$(/usr/sbin/ip route get 1 | cut -d" " -f7) /usr/bin/systemd-cgtop
printf "\nOutput of oc adm top pod --all-namespaces on bastion at " && date
/usr/local/bin/oc adm top pod --all-namespaces

printf "\n Executing commands on master-0 "
printf "\n Output of systemctl status kubelet on master-0 at " && date
ssh -oStrictHostKeyChecking=no core@master-0 /bin/systemctl status kubelet
printf "\n Output of systemctl status crio on master-0 " && date
ssh core@master-0 /bin/systemctl status crio
printf "\n Output of free -g on master-0 " && date
ssh core@master-0 /usr/bin/free -g
printf "\n Output of /proc/meminfo on master-0 " && date
/bin/cat /proc/meminfo
printf "\n Output of systemd-cgtop on master-0 " && date
ssh core@master-0 systemd-cgtop

printf "\n Executing commands on worker-0"
printf "\n Output of systemctl status kubelet on worker-0 at " && date
ssh -oStrictHostKeyChecking=no core@worker-0 /bin/systemctl status kubelet
printf "\n Output of systemctl status crio on worker-0 at " && date
ssh core@worker-0 /bin/systemctl status crio
printf "\n Output of free -g on worker-0 at " && date
ssh core@worker-0 /usr/bin/free -g
printf "\n Output of /proc/meminfo on worker-0 at " && date
/bin/cat /proc/meminfo
printf "\n Output of systemd-cgtop on worker-0 at " && date
ssh core@worker-0 systemd-cgtop

printf "\n Executing commands on master-1 "
printf "\n Output of systemctl status kubelet on master-1 at " && date
ssh -oStrictHostKeyChecking=no core@master-1 /bin/systemctl status kubelet
printf "\n Output of systemctl status crio on master-1 " && date
ssh core@master-1 /bin/systemctl status crio
printf "\n Output of free -g on master-1 " && date
ssh core@master-1 /usr/bin/free -g
printf "\n Output of /proc/meminfo on master-1 " && date
/bin/cat /proc/meminfo
printf "\n Output of systemd-cgtop on master-1 " && date
ssh core@master-1 systemd-cgtop

printf "\n Executing commands on worker-1"
printf "\n Output of systemctl status kubelet on worker-1 at " && date
ssh -oStrictHostKeyChecking=no core@worker-1 /bin/systemctl status kubelet
printf "\n Output of systemctl status crio on worker-1 at " && date
ssh core@worker-1 /bin/systemctl status crio
printf "\n Output of free -g on worker-1 at " && date
ssh core@worker-1 /usr/bin/free -g
printf "\n Output of /proc/meminfo on worker-1 at " && date
/bin/cat /proc/meminfo
printf "\n Output of systemd-cgtop on worker-1 at " && date
ssh core@worker-1 systemd-cgtop

printf "\n Executing commands on master-2 "
printf "\n Output of systemctl status kubelet on master-2 at " && date
ssh -oStrictHostKeyChecking=no core@master-2 /bin/systemctl status kubelet
printf "\n Output of systemctl status crio on master-2 " && date
ssh core@master-2 /bin/systemctl status crio
printf "\n Output of free -g on master-2 " && date
ssh core@master-2 /usr/bin/free -g
printf "\n Output of /proc/meminfo on master-2 " && date
/bin/cat /proc/meminfo
printf "\n Output of systemd-cgtop on master-2 " && date
ssh core@master-2 systemd-cgtop

ssh -oStrictHostKeyChecking=no core@worker-2 exit
if [ $? -eq 0 ]; then
  printf "\n Executing commands on worker-2"
  printf "\n Output of systemctl status kubelet on worker-2 at " && date
  ssh -oStrictHostKeyChecking=no core@worker-2 /bin/systemctl status kubelet
  printf "\n Output of systemctl status crio on worker-2 at " && date
  ssh core@worker-2 /bin/systemctl status crio
  printf "\n Output of free -g on worker-2 at " && date
  ssh core@worker-2 /usr/bin/free -g
  printf "\n Output of /proc/meminfo on worker-2 at " && date
  /bin/cat /proc/meminfo
  printf "\n Output of systemd-cgtop on worker-2 at " && date
  ssh core@worker-2 systemd-cgtop
fi
