package it.register.edu.auction.builder;

import org.dataloader.DataLoader;

public interface DataLoaderBuilder<K, V> {

  DataLoader<K, V> build();

}
