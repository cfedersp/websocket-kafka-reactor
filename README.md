Websocket-Kafka-Reactor

This started as a clone of https://github.com/svgagan/server-sent-events
I added websockets, event filtering, websockets, and my browser playground for websockets.

To Run, you need a kafka instance (see below) configured in the app
To Start the App:
mvn spring-boot:run
Visit:
http://localhost:8080/static/index.html


Running Kafka locally probably wont work on windows.

Install Kafka:
https://www.kafkatool.com/download.html
https://kafka.apache.org/quickstart

cd into the kafka homedir, then:

Running kafka:
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

Create a topic:
bin/kafka-topics.sh --create --topic REACTOR-WEBSOCKETS-DEMO --bootstrap-server localhost:9092
bin/kafka-topics.sh --describe --topic REACTOR-WEBSOCKETS-DEMO --bootstrap-server localhost:9092

Publish some events from your CLI:
bin/kafka-console-producer.sh --topic REACTOR-WEBSOCKETS-DEMO --bootstrap-server localhost:9092

Enter some events and if they match the filter given by the client, they will appear in your web browser!


