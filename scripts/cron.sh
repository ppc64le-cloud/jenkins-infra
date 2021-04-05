#!/bin/bash
export TZ='Asia/Kolkata'
printf "Login to the cluster "
/usr/local/bin/oc login -u kubeadmin -p provide_cluster_passwd
printf "Output of oc get nodes on bastion at " && date
/usr/local/bin/oc get nodes
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

declare -a array=("worker-0" "master-0"  "worker-1" "master-1" "worker-2" "master-2")
arraylength=${#array[@]}
for (( i=0; i<${arraylength}; i++ ));
do	
 if ssh -oStrictHostKeyChecking=no core@${array[i]} exit; then	
 	printf "\n***************************************************************\n"
 	printf "\n Executing commands on ${array[i]}\n"

 	printf "\n Output of systemctl status kubelet on ${array[i]} at " && date
 	ssh core@${array[i]} /bin/systemctl status kubelet

 	printf "\n Output of systemctl status crio on ${array[i]} at " && date
 	ssh core@${array[i]} /bin/systemctl status crio

 	printf "\n Output of free -g on ${array[i]} at " && date
 	ssh core@${array[i]} /usr/bin/free -g

 	printf "\n Output of /proc/meminfo on ${array[i]} at " && date
 	ssh core@${array[i]} /bin/cat /proc/meminfo

 	printf "\n Output of systemd-cgtop on ${array[i]} at " && date
 	ssh core@${array[i]} systemd-cgtop

 	printf "\n***************************************************************\n"
 fi	
done
