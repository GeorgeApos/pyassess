FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

WORKDIR /app

RUN apt-get update && \
    apt-get install -y git python3 python3-pip python3-numpy && \
    pip3 install pylint pytest pytest-cov && \
    apt-get install -y python3-astor && \
    pip3 install --user nltk gensim && \
    python3 -m nltk.downloader punkt && \
    pip3 install pipreqs && \
    git clone https://github.com/platisd/duplicate-code-detection-tool.git && \
    rm -rf /var/lib/apt/lists/*


ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
