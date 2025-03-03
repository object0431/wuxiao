FROM harbor.dcos.xixian.unicom.local/img-freerdhn/myjdk_pinpoint:11
MAINTAINER liujun5@asiainfo.com

COPY fsip-rest/target/fsip-rest.jar fsip-rest-1.0.0.jar
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

ENV JAVA_HOME=/jdk11
ENV PATH=$JAVA_HOME/bin:$PATH
ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

ENTRYPOINT exec java $JAVA_OPTS $PP_OPTS -Dpinpoint.agentId=$AGENT_ID -Dpinpoint.applicationName=$PPAPPLICATION_NAME -Dpinpoint.profiler.profiles.active=release -jar /fsip-rest-1.0.0.jar
