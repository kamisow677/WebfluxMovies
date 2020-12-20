//package com.kamil.merchants.feign;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.netflix.feign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@FeignClient(url = "http://localhost:8081") //1
//public interface FeignMovies {
//
//    @GetMapping(value = "/movie/{id}")
//    public ResponseEntity<MovieFeign> getById(@PathVariable Integer id);
//
//    @GetMapping(value = "/movie")
//    public ResponseEntity<MovieFeign> getById(@RequestParam String title);
//
//    @GetMapping(value = "/movieAll")
//    public ResponseEntity<Iterable<MovieFeign>> getAll();
//
//}