package com.kamil.merchants.upflix;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DocumentDownloader {

    public Document getAllUpflixesFromWeb(String filmName, String year){
        String url = constructUpflixUrl(filmName, year);
        return connectToUrl(url);
    }

    private Document connectToUrl(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String constructUpflixUrl(String filmName, String year) {
       return String.format("https://upflix.pl/film/zobacz/%s-%d",
                filmName,  Integer.valueOf(year) );
    }

}
