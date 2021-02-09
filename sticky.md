[TOC]


```SHELL
bash ../bin/main.sh

mvn dependency:resolve
mvn dependency:tree

mvn dependency:resolve dependency:sources

mvn resources:resources compiler:compile

mvn appassembler:assemble

mvn resources:resources compiler:compile appassembler:assemble

mvn dependency:resolve dependency:sources appassembler:assemble

mvn clean:clean

mvn clean:clean resources:resources compiler:compile appassembler:assemble

mvn clean:clean resources:resources compiler:compile
```

JEE

```
mvn compiler:compile resources:resources war:exploded
```