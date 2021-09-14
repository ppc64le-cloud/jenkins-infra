import logging
import subprocess
import cerberus.invoke.command as runcommand

def check_name():
    logging.info("Check if CPU/memory usage on any node exceeds 90%\n")

def check():
    node_usage = subprocess.check_output("oc adm top  nodes --no-headers | awk '{ print $5 }' | tr -d '%' | sort -rn | head -1", shell=True,
                                         universal_newlines=True)
    if int(node_usage) < 90:
         logging.info("No abnormalities found in node resource utilization")
         message = "No abnormalities found in node resource utilization\n"
         return True, message
    else:
         logging.info("Node resource utilization exceeded the limit")
         message = "Node resource utilization exceeded the limit\n" 
         node_usage_details = runcommand.invoke("oc adm top nodes")
         logging.info("Output of: oc adm top nodes\n%s" % (node_usage_details))
         message = "\n Output of : oc adm top nodes:\n %s" % (node_usage_details)
         return False, message


def main():
    check_name()
    output,message = check()
    return {'status':output, 'message':message}
