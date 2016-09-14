# akka-afs
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pt.tecnico.dsi/akka-afs_2.11/badge.svg?maxAge=604800)](https://maven-badges.herokuapp.com/maven-central/pt.tecnico.dsi/akka-afs_2.11)
[![Dependency Status](https://www.versioneye.com/java/pt.tecnico.dsi:akka-afs_2.11/badge.svg?style=plastic&maxAge=604800)](https://www.versioneye.com/java/pt.tecnico.dsi:akka-afs_2.11)
[![Reference Status](https://www.versioneye.com/java/pt.tecnico.dsi:akka-afs_2.11/reference_badge.svg?style=plastic&maxAge=604800)](https://www.versioneye.com/java/pt.tecnico.dsi:akka-afs_2.11/references)
[![Build Status](https://travis-ci.org/ist-dsi/akka-afs.svg?branch=master&style=plastic&maxAge=604800)](https://travis-ci.org/ist-dsi/akka-afs)
[![Codacy Badge](https://api.codacy.com/project/badge/coverage/86c0c6234ba04d32bd41c6b5cd51d4a3)](https://www.codacy.com/app/IST-DSI/akka-afs)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/86c0c6234ba04d32bd41c6b5cd51d4a3)](https://www.codacy.com/app/IST-DSI/akka-afs)
[![Scaladoc](http://javadoc-badge.appspot.com/pt.tecnico.dsi/akka-afs_2.11.svg?label=scaladoc&style=plastic&maxAge=604800)](https://ist-dsi.github.io/akka-afs/latest/api/#pt.tecnico.dsi.akka-afs.package)
[![license](http://img.shields.io/:license-MIT-blue.svg)](LICENSE)

TODO

[Latest scaladoc documentation](http://ist-dsi.github.io/akka-afs/latest/api/)

## Install
Add the following dependency to your `build.sbt`:
```sbt
libraryDependencies += "pt.tecnico.dsi" %% "akka-afs" % "0.0.4"
```
We use [semantic versioning](http://semver.org).


## Configurations
afs uses [typesafe-config](https://github.com/typesafehub/config).

The [reference.conf](src/main/resources/reference.conf) file has the following keys:
```scala
afs {

}
```


Alternatively you can pass your Config object to the afs constructor, or subclass the
[Settings](https://ist-dsi.github.io/afs/latest/api/#pt.tecnico.dsi.afs.Settings) class for a mixed approach.
The scaladoc of the Settings class has examples explaining the different options.

## How to test akka-afs
In the project root run `./test.sh`. This script will run `docker-compose up` inside the docker-afs folder.
Be sure to have [vagrant](https://www.vagrantup.com/)


## Note on the docker-afs folder
This folder is a [git fake submodule](http://debuggable.com/posts/git-fake-submodules:4b563ee4-f3cc-4061-967e-0e48cbdd56cb)
to the [docker-kerberos repository](https://github.com/ist-dsi/docker-afs).

## License
afs is open source and available under the [MIT license](LICENSE).
