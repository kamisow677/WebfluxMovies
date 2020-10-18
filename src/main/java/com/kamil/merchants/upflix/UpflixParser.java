package com.kamil.merchants.upflix;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class UpflixParser {

    @Autowired
    DocumentDownloader documentDownloader;

    public List<Upflix> getAllUpflixesFromWeb(String filmName, String year){
        Document doc = documentDownloader.getAllUpflixesFromWeb(filmName, year);
        String cssQuery = ".nav.nav-tabs.sources";
        Elements select = doc.select(cssQuery);
        return parseHtml(select);
    }

    private List<Upflix> parseHtml(Elements elements) {
        List<Upflix> upflixes = new ArrayList<>();
        Elements children = elements.get(0).children();
        for (Element el: children) {
            Upflix upflixFromProperElement = getUpflixFromProperElement(el);
            upflixes.add(upflixFromProperElement);
        }
        return upflixes;
    }

    private Upflix getUpflixFromProperElement(Element el) {
        Iterator<Element> iterator = el.getAllElements().iterator();
        String line = iterator.next().toString();
        String siteName = extractValue(line, "href=", 2, "data-toggle", 2);
        String distributionChoice = extractValue(line, "</span>", 0, "<strong>", 0);

        return Upflix.builder()
                .siteName(siteName)
                .distributionChoice(distributionChoice)
                .build();
    }

    private String extractValue(String line, String startRegex, Integer startOffset, String endRegex, Integer endOffset) {
        String[] b1 = line.split(startRegex);
        return b1[1].substring(startOffset, b1[1].indexOf(endRegex)-endOffset);
    }

}
