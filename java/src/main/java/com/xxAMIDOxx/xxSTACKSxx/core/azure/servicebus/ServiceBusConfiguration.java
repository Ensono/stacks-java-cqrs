package com.xxAMIDOxx.xxSTACKSxx.core.azure.servicebus;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.servicebus.TopicClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ServiceBusProperties.class)
@ConditionalOnProperty(
    value = "azure.servicebus.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class ServiceBusConfiguration {

  private final ServiceBusProperties properties;

  public ServiceBusConfiguration(ServiceBusProperties properties) {
    this.properties = properties;
  }

  @Bean
  public TopicClient topicSender() throws ServiceBusException, InterruptedException {
    return new TopicClient(
        new ConnectionStringBuilder(properties.getConnectionString(), properties.getTopicName()));
  }

  @Bean
  public JsonMapper jsonMapper() {
    return JsonMapper.builder()
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .addModule(new JavaTimeModule())
        .build();
  }
}
