FROM openjdk:8

RUN touch /usr/lib/jvm/java-8-openjdk-amd64/release

ENV SCALA_VERSION 2.11.11
ENV SBT_VERSION 0.13.15

# Install SBT
RUN \
    curl -L -o sbt-0.13.15.deb https://dl.bintray.com/sbt/debian/sbt-0.13.15.deb && \
    dpkg -i sbt-0.13.15.deb && \
    rm sbt-0.13.15.deb && \
    apt-get update && \
    apt-get install sbt && \
    sbt sbtVersion

EXPOSE 9000
RUN mkdir /app
COPY . /app
WORKDIR /app
CMD ["sbt", "compile", "run"]