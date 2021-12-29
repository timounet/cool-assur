# cool-assur Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/cool-assur-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin
- SmallRye Health ([guide](https://quarkus.io/guides/microprofile-health)): Monitor service health

## Provided Code

### SmallRye Health

Monitor your application's health using SmallRye Health

[Related guide section...](https://quarkus.io/guides/smallrye-health)


# Run in kubernets
Cool Assur Application can be deployed in standalone or with Prometheus / Grafana stack
Pre-requisite: 
- Kubernetes files required an existing dedicated namespace named : `ns-az-training`

To deploy a demo of prometheus / grafana
```shell script
# from internet
kubectl create -k https://github.com/timounet/cool-assur/tree/main/src/main/kubernetes
# from source code
kubectl create -k src/main/kubernetes
```

kustomization will instantiate 
- Prometheus
  - pvc
  - svc : with nodePort 30877
  - deployment
  - configMap
- grafana
  - pvc
  - svc: with nodePort 30878
  - deployment

To deploy cool assur app on kubernetes, the best way is to retrieve latest build artifacts with generated kubernetes files
- stable version from [artifact of the latest main build](https://github.com/timounet/cool-assur/actions?query=branch%3Amain)
- dev version from [artifact of the latest develop build](https://github.com/timounet/cool-assur/actions?query=branch%3Adevelop)

Artifact is a zip files with kubernetes files (json and yml)
```shell script
# unzip artifact and execute
kubectl apply -f kubernetes.yml

# Or on development environment
.\mvnw package
kubectl apply -f target/kubernetes/kubernetes.yml
```