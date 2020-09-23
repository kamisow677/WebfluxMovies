package com.kamil.merchants.heaven;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
public interface HeavenRepository extends ReactiveCrudRepository<Heaven, String> {
}
