package is.project3.producers;


import com.google.gson.Gson;
import com.github.javafaker.Faker;
import com.google.gson.JsonObject;
import is.project3.models.Sale;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SalesProducer {

    // Kafka topic where sales events are published
    private static final String TOPIC = "sales-topic";

    private static final String DBINFO_TOPIC = "DBInfo";

    public static void main(String[] args) {
        // Kafka consumer setup
        List<String> items = new CopyOnWriteArrayList<>();
        List<String> countries = new CopyOnWriteArrayList<>();
        Gson gson = new Gson(); // Gson converts Java objects to JSON

        Thread dbConsumerThread = new Thread(() -> {
            Properties consProps = new Properties();
            consProps.put("bootstrap.servers", "localhost:29092");
            // Losowe GroupID zapewnia, że zawsze przy starcie przeczytamy topic DBInfo od samego początku
            consProps.put("group.id", "dbinfo-sales-group-" + UUID.randomUUID());
            consProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consProps.put("auto.offset.reset", "earliest");

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consProps);
            consumer.subscribe(Collections.singletonList(DBINFO_TOPIC));

            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            JsonObject jsonObj = gson.fromJson(record.value(), JsonObject.class);
                            // Obsługa wrappera "payload" dodawanego domyślnie przez Kafka Connect
                            if (jsonObj.has("payload")) {
                                jsonObj = jsonObj.getAsJsonObject("payload");
                            }

                            String type = jsonObj.get("type").getAsString();
                            String name = jsonObj.get("name").getAsString();

                            if ("item".equals(type) && !items.contains(name)) {
                                items.add(name);
                                System.out.println("[DB Integration] Added Item to SalesProducer: " + name);
                            } else if ("country".equals(type) && !countries.contains(name)) {
                                countries.add(name);
                                System.out.println("[DB Integration] Added Country to SalesProducer: " + name);
                            }
                        } catch (Exception e) {
                            // Ignoruj błędy parsowania (np. jeśli format rekordu byłby inny)
                        }
                    }
                }
            } finally {
                consumer.close();
            }
        });
        dbConsumerThread.setDaemon(true);
        dbConsumerThread.start();

        // Kafka producer configuration
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:29092");     // Address of Kafka brokers (from docker-compose)
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");      // Key serializer (we use String for item)
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");    // Value serializer (JSON string)

        // Create Kafka producer instance
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        Faker faker = new Faker();      // Faker + Random used to generate fake data
        Random random = new Random();


        try {
            while (true) {

                while (items.isEmpty() || countries.isEmpty()) {
                    System.out.println("SalesProducer is waiting for data from DBInfo topic...");
                    Thread.sleep(2000);
                }

                String item = items.get(random.nextInt(items.size()));
                double price = 1 + (10 * random.nextDouble());
                int units = 1 + random.nextInt(5);
                String country = countries.get(random.nextInt(countries.size()));

                // Create Sale object (model)
                Sale sale = new Sale(
                        item,
                        price,
                        units,
                        country,
                        System.currentTimeMillis()
                );
                
                // Convert Sale object → JSON string
                String json = gson.toJson(sale);

                // Create Kafka record:
                // key = item (important for grouping in Streams)
                // value = JSON
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, item, json);

                producer.send(record);

                System.out.println("Sent Sale: " + json);

                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}