package com.kamil.merchants.infrastructure.parser;

import com.kamil.merchants.infrastructure.repository.Upflix;

import java.util.List;

public interface UpflixParser {

    List<Upflix> getAllUpflixesFromWeb(String filmName, String year);
}
