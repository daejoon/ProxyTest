package com.example.proxytest.repository;

import com.example.proxytest.domain.City;
import org.springframework.data.repository.CrudRepository;

public interface CityRepository extends CrudRepository<City, Long> {
}
