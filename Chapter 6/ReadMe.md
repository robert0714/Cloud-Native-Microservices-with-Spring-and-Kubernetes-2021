# chapter 6

### Documentation


The Docker-compose file contains: single kafka and zookeeper. just simply run the following command

```shell
docker-compose up -d
```

or 
- Control Center UI
Runs a https://docs.confluent.io/platform/current/control-center/index.html[Confluent Control Center] that exposes a UI at http://locahost:9021.

```shell
docker-compose -f ./kafka-cluster.yml -f ./control-center-ui.yml up
```
To stop the brokers and the Control Center UI run the following command:
```shell
docker-compose -f ./kafka-cluster.yml -f ./control-center-ui.yml down
```

_Note: docker-compose file is at the root of this project "/scs-kafka-intro"_
