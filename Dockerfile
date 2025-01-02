FROM eclipse-temurin:8-jdk
RUN mkdir /usr/local/spider-node/
COPY start/target/spider-node-2.0.0.tar.gz /usr/local/spider-node/
WORKDIR /usr/local/spider-node/
RUN tar -zxvf spider-node-2.0.0.tar.gz
ENTRYPOINT ["/bin/bash", "-c", "/usr/local/spider-node/bin/start.sh"]