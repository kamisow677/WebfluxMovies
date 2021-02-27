package com.kamil.merchants.infrastructure.parser;

import com.kamil.merchants.infrastructure.repository.Upflix;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class UpflixParserImpl implements UpflixParser {

    @Autowired
    DocumentDownloader documentDownloader;

    public List<Upflix> getAllUpflixesFromWeb(String filmName, String year){
//        Document doc = documentDownloader.getUpflixDocument(filmName, year);
        Document doc = documentDownloader.getUpflixDocument("filename.html");
        String cssQuery = "sc";
        Element select = doc.getElementById(cssQuery);
        return parseHtmlUpflix(select);
    }

    private List<Upflix> parseHtmlUpflix(Element elements) {
        List<Upflix> upflixes = new ArrayList<>();
        Elements children = elements.children();
        for (Element el: children) {
            Upflix upflixFromProperElement = getUpflixFromProperElement(el);
            upflixes.add(upflixFromProperElement);
        }
        return upflixes;
    }

    private Upflix getUpflixFromProperElement(Element el) {
        Iterator<Element> iterator = el.getAllElements().iterator();
        String line = iterator.next().toString();
        String siteName = extractValue(line, "href=", 2, "data-price", 2);
        String distributionChoice = extractValue(line, "tabindex", 6, "</span>", 0);

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
