FROM criyle/go-judge:latest AS go-judge

FROM ubuntu:latest

ENV TZ=Asia/Shanghai

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    python2.7 \
    python3 \
    fpc \
    openjdk-11-jdk \
    nodejs \
    golang-go \
    php-cli \
    ghc \
    rustc \
    ruby \
    node-typescript \
    mono-mcs \
    rakudo \
    perl \
    ocaml \
    vim \
    nano \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /judge/test_case /judge/run /judge/spj /judge/log

WORKDIR /opt

COPY --from=go-judge /opt/go-judge /opt/mount.yaml /opt/

EXPOSE 5050/tcp 5051/tcp

ENTRYPOINT ["./go-judge"]