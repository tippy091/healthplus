package vn.com.nested.backend.common.operation.slog;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.LoggerFactory;
import vn.com.nested.backend.common.operation.slog.logging.LogMessage;
import vn.com.nested.backend.common.operation.slog.logging.LogWriter;

import java.util.List;
import java.util.Properties;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public class OPLogWriter implements LogWriter {
    private Builder configBuilder;
    private Producer<String, String> producer;
    private volatile boolean closed = true;

    public OPLogWriter(String servers, String clientId) {
        if (servers != null && !servers.trim().isEmpty()) {
            if (clientId != null && !clientId.trim().isEmpty()) {
                String appName = System.getProperty("application.name");
                if (appName != null && !appName.trim().isEmpty()) {
                    this.configBuilder = (new Builder()).setTopic(appName).setClientId(clientId);
                    String[] serverPaths = servers.split(",");
                    String[] var5 = serverPaths;
                    int var6 = serverPaths.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                        String serverPath = var5[var7];
                        String[] pathParts = serverPath.split(":");
                        this.configBuilder.addHostPort(pathParts[0], Integer.parseInt(pathParts[1]));
                    }

                    this.init(this.configBuilder);
                } else {
                    throw new IllegalArgumentException("application.name nullOrEmpty");
                }
            } else {
                throw new IllegalArgumentException("[servers]client-id");
            }
        } else {
            throw new IllegalArgumentException("[servers]servers");
        }
    }

    public OPLogWriter(Builder builder) {
        this.init(builder);
    }

    public void write(LogMessage message) {
        if (this.closed) {
            LoggerFactory.getLogger("warn").warn("Producer already closed: {} - {}", message.getRequestId(), message);
        } else {
            try {
                this.producer.send(new ProducerRecord(this.configBuilder.topic, this.configBuilder.partition, (Object)null, message.toString()));
            } catch (Throwable var3) {
                LoggerFactory.getLogger("error").error("{} - {}", new Object[]{message.getRequestId(), message, var3});
            }
        }

    }

    private void init(Builder builder) {
        this.configBuilder = builder;
        Properties props = new Properties();
        props.put("bootstrap.servers", this.configBuilder.hostPorts);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        props.put("acks", "1");
        props.put("client.id", this.configBuilder.clientId);
        props.put("max.block.ms", 20000);
        this.producer = new KafkaProducer(props);
        List<PartitionInfo> partitions = this.producer.partitionsFor(this.configBuilder.topic);
        this.closed = false;
        if (this.configBuilder.partition > 0 && this.configBuilder.partition > partitions.size() - 1) {
            this.producer.close();
            this.closed = true;
            String var10002 = this.configBuilder.topic;
            throw new IllegalArgumentException("Topic '" + var10002 + "' has only " + partitions.size() + " partition(s)");
        } else {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (!this.closed) {
                    this.closed = true;
                    this.producer.flush();
                    this.producer.close();
                }

            }));
        }
    }

    public static class Builder {
        String hostPorts = "";
        String topic;
        Integer partition = 0;
        String clientId;

        public Builder() {
        }

        public Builder addHostPort(String host, int port) {
            if (!this.hostPorts.isEmpty()) {
                this.hostPorts = this.hostPorts + ",";
            }

            this.hostPorts = this.hostPorts + host + ":" + port;
            return this;
        }

        public Builder setPartition(int partition) {
            if (partition < 0) {
                throw new IllegalArgumentException("partition must be a positive integer");
            } else {
                this.partition = partition;
                return this;
            }
        }

        public Builder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public String getHostPorts() {
            return this.hostPorts;
        }

        public String getTopic() {
            return this.topic;
        }

        public Integer getPartition() {
            return this.partition;
        }

        public String getClientId() {
            return this.clientId;
        }
    }
}
