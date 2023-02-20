FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

RUN apt-get update && \
    apt-get install -y git python3 python3-pip python3-numpy && \
    pip3 install pylint pytest pytest-cov && \
    apt-get install -y python3-astor && \
    pip3 install --user nltk gensim && \
    python3 -m nltk.downloader punkt && \
    pip3 install pipreqs && \
    mkdir -p /home/Documents && \
    cd /home/Documents && \
    git clone https://github.com/platisd/duplicate-code-detection-tool.git && \
    git clone https://github.com/CT83/SmoothStream.git && \
    rm -rf /var/lib/apt/lists/*

WORKDIR app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
