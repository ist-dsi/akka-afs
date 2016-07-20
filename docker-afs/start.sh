#!/bin/bash

if [[ ! $(vagrant status | grep running) ]]; then
  vagrant up
fi

vagrant rsync

vagrant ssh <<"EOF" # These quotes are VERY important. Without them the line EXIT= wont work
# We want to ensure the docker runs with root, so that we can leverage the folders /root/.ivy2 and /root/.sbt
sudo su

# With quiet it won't throw an error in the module is already removed
#modprobe --remove --quiet openafs
rmmod openafs

# Remove any previously existing containers
docker rm -f `docker ps -qa | xargs`

cd /vagrant
#sbt test:compile

cd /vagrant/docker-afs

# Build the containers
docker-compose build

# Run them. The --abort-on-container-exit stops all containers if any container was stopped.
docker-compose up --abort-on-container-exit
EXIT_CODE=$(docker-compose ps "afs-client" | grep -oP "(?<=Exit )\d+")

# Remove the containers to ensure a clean slate the next time this script in ran.
docker-compose rm -f

exit $EXIT_CODE
EOF