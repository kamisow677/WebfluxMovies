package com.kamil.merchants.infrastructure.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class DocumentDownloader {

    public Document getUpflixDocument(String filmName, String year){
        String url = constructUpflixUrl(filmName, year);
        return connectToUrl(url);
    }

    public Document getUpflixDocument(String pathname){
        File in = new File(pathname);
        Document doc = null;
        try {
            doc =  Jsoup.parse(in, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
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
