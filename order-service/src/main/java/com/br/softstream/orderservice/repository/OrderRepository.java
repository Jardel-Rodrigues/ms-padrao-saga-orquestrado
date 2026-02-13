package com.br.softstream.orderservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.br.softstream.orderservice.document.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

}
