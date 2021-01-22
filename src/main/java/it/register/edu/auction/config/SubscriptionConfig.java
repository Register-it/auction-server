package it.register.edu.auction.config;

import it.register.edu.auction.domain.AuctionNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Configuration
public class SubscriptionConfig {

  @Bean
  public Many<AuctionNotification> notificationPublisher() {
    return Sinks.many().multicast().directBestEffort();
  }

  @Bean
  public Flux<AuctionNotification> notifications(Many<AuctionNotification> sink) {
    return sink.asFlux();
  }

}
