package ru.iprody.orderservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iprody.orderservice.domain.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
