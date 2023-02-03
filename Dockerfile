FROM ubuntu:latest
RUN apt-get -y update
RUN   apt --fix-missing update
RUN apt-get -y install git
RUN apt -y install python3-pip
RUN pip install pylint
RUN pip install pytest
RUN pip install pytest-cov
RUN pip3 install --user nltk
RUN pip3 install --user gensim
RUN pip3 install --user astor
RUN python3 -m nltk.downloader punkt
RUN pip install pipreqs
RUN cd /home && mkdir Documents
RUN cd /home/Documents && git clone https://github.com/platisd/duplicate-code-detection-tool.git && git clone https://github.com/CT83/SmoothStream.git
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]