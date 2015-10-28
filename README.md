serviceDiscoveryAgent
==========
This is the service discovery agent. If for some reason your application or service can't report to zookeper about itself, you can leverage this code to do so. It runs on the server/instance and monitors configured target(plugin), if target running/available this agent creates ephemeral zookeeper node in configured zookeeper. If healtcheck fails for some reason, zookeeper node gets removed. Same behaviour occurs if agent for some reason crash or lost connection with zk. It's also place information about your service into that zk node for later usage by others services.

### IMPORTANT
This piece of software was written for fun and never being used in production.

### How it works?
It checks the target and if getting success response, creates ephemeral zookeeper node(you have to have zookeeper server being installed) with data from `zookeeper/zkData` config value. If target for some reason became unavailable or agent dies - that node is getting removed. The interval between check as configured in `interval` config key.

### Twitter-server
It's works on top of [twitter server](https://twitter.github.io/twitter-server/index.html), so you can get all goodies
twitter-server provides for free. Check out http://localhost:9990/admin

### Pre-requirements
1. Installed [sbt](http://www.scala-sbt.org/)
2. Installed java machine

### Requirements
1. [Apache Zookepeer](https://zookeeper.apache.org/)
2. Java Machine

### Build jar file

To build fat jar file with all dependencies:
```
sbt assembly
```

new jar will be placed into `target/scala-2.11`

Running from jar
---------

You have to have java installed on that server.
```
java -jar target/scala-2.11/serviceDiscoveryAgent-assembly-1.0.jar -config example.json
```


Configuration
--------

By default agent looks for config at `/etc/serviceDiscovery/serviceDiscovery.json`, but this behaviour could be override by option `-config`. It works with json format and _not_ automatically re-read by agent on the change. So you have to restart it manually every each change.   
    
| Name | Value(s) | Description |
| -----|:--------:| -----------:|
| zookeeper/Base | "/services/web" | path to zookeeper base |
| zookeeper/zkData | "{\"127.0.0.1\": \"80\"}" | value to put into new node |
| target/ProcessCheck/processPidFile |  "/tmp/123.pid" | path to process pid file (ProcessCheck only) |
| target/checkName | "ProcessCheck|HttpCheck" | name of checker |
| target/target | localhost:80/health" | http endpoint (HttpCheck only) |
| target/responseCode |  200 | Success HTTP response code (HttpCheck only) |
| target/requestTimeout |  5 | Request timeout (HttpCheck only) |
| interval | 10 | interval between check in seconds |

### Macros
Is not implemented yet.

### Targets

each instance can have only one target setup. So far there are only two targets available `ProcessCheck` and http `HttpCheck`.

## Process
This plugin is monitoring configured process.
It has extra parameter mandatory `processPidFile`. It must be pointed to monitored process pid file. Example:
```
  "processPidFile": "/tmp/123.pid",
  "checkName": "ProcessCheck",
```

## HTTP healthcheck
This plugin is checking http page setup in `checkTarget` and expects to get `checkResponseCode`.
two config params should be set `checkTarget` and `checkResponseCode`. Example:

```
  "checkName": "HttpCheck",
  "checkTarget": "localhost:80/health",
  "checkResponseCode": 200,
```

Configuration examples

### Local web server
----------
```
  "zookeeper": {
    "base": "/services/web",
    "connectString": "localhost:2181",
    "connectTimeout": 1,
    "sessionTimeout": 10,
    "zkData": "{\"%%hostname%%\": \"8080\"}"
  },
  "target": {
    "name": "HttpCheck",
    "params": {
      "target": "localhost:8080/health",
      "responseCode": 200,
      "requestTimeout": 5
    }
  },
  "interval": 10
```

### Local process

```
  "zookeeper": {
    "base": "/services/web",
    "connectString": "localhost:2181",
    "connectTimeout": 1,
    "sessionTimeout": 10,
    "zkData": "{\"%%hostname%%\": \"12345\"}"
  },
  "target": {
    "name": "ProcessCheck",
    "params": {
      "processPidFile": "/tmp/123.pid"
    }
  },
  "interval": 10
```

TODO
----------
- better handle zookeeper failure
- implement ip macro for Linux
- fix application metrics
- add unit testing

Development
----------

### Compile and run

```
cd serviceDiscoveryAgent
sbt "run -config example.json -log.level=DEBUG"
```

