package com.kamil.merchants.upflix;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class UpflixParser {


    public List<Upflix> getAllUpflixesFromWeb(String filmName, String year){
        String url = constructUpflixUrl(filmName, year);
        Document doc = connectToUrl(url);

        Elements select = doc.select(".nav.nav-tabs.sources");

        Elements children = select.get(0).children();
        List<Upflix> upflixes = new ArrayList<>();

        for (Element el: children) {
            Iterator<Element> iterator = el.getAllElements().iterator();

            String line = iterator.next().toString();
            String[] b1 = line.split("href=");
            String siteName = b1[1].substring(2, b1[1].indexOf("data-toggle")-3);

            String[] b2 = line.split("</span>");
            String distributionChoice = b2[1].substring(0, b2[1].indexOf("<strong>"));

            upflixes.add(Upflix.builder()
                    .siteName(siteName)
                    .distributionChoice(distributionChoice)
                    .build());
        }

        return upflixes;

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
