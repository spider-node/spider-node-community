FROM acr.ops.yunlizhi.cn/runtime/jre/1.8.0_252:latest
RUN mkdir /usr/local/spider-node/
COPY start/target/start-1.0.0-SNAPSHOT.tar.gz /usr/local/spider-node/
WORKDIR /usr/local/spider-node/
RUN tar -zxvf start-1.0.0-SNAPSHOT.tar.gz && rm -f spider-node-1.0.0.RELEASE.tar.gz
ENTRYPOINT ["/bin/bash", "-c", "/usr/local/spider-node/bin/startup.sh"]