#!/bin/bash

source `dirname $0`/configureKerberosClient.sh

# The afs kernel module cannot be used simultaneously by two (or more) containers.
# To overcome this limitation we employ the following tactic:
#  · We load the module right away.
#  · Then we wait for it to no longer be loaded. This will happen when the AFS server
#    invokes openafs-client stop.
# The fact that the module is no longer being used means that the installation of the AFS server
# has been completed and we can now continue to configure the AFS client in this container.
modprobe openafs
while [[ $(lsmod | grep openafs) ]]; do
  >&2 echo "Module still in use - sleeping 5 secs"
  sleep 5
done
echo ""

source `dirname $0`/configureAFSClient.sh

echo "==================================================================================="
echo "==== Tests ========================================================================"
echo "==================================================================================="
cd /tmp/afs

#sbt <<<"testOnly pt.tecnico.dsi.afs.QuotaSpec"
sbt test

# Be sure to stop the openafs-client otherwise the afs kernel module will not be unloaded and you
# will have problems stopping the containers.
/etc/init.d/openafs-client stop
