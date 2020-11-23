FROM maven:3.6.3-jdk-11
WORKDIR /usr/src/app
COPY pom.xml .
COPY src/ ./src
RUN mvn clean package
RUN apt-get update -y
RUN apt-get install -y libxext6
RUN apt-get install -y libxrender1
RUN apt-get install -y libxtst6
COPY Credentials.json .
WORKDIR /usr/src/app
CMD ["mvn","exec:java","-D", "exec.mainClass=search.Driver"]