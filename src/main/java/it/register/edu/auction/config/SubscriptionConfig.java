package it.register.edu.auction.config;

import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.entity.Bid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Configuration
public class SubscriptionConfig {

  @Bean
  public Many<AuctionNotification> notificationSink() {
    return Sinks.many().multicast().directBestEffort();
  }

  @Bean
  public Flux<AuctionNotification> notificationPublisher(Many<AuctionNotification> sink) {
    return sink.asFlux();
  }

  @Bean
  public Many<Bid> bidSink() {
    return Sinks.many().multicast().directBestEffort();
  }

  @Bean
  public Flux<Bid> bidPublisher(Many<Bid> sink) {
    return sink.asFlux();
  }

}
