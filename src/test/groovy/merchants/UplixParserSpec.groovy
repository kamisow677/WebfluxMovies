package com.kamil.merchants


import utils.BaseIntegration
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.when

@SpringBootTest
class UplixParserSpec extends BaseIntegration {

    public static final String FILMNAME_VALUE = "filmName_value"
    public static final String FILMYEAR_VALUE = "filmYear_value"
    public static final String DOCUMENT_RESOURCE_PATH = "src/test/resources/document.txt"

    public static final String SITENAME_IPLA = "vod-ipla"
    public static final String SITENAME_CDA = "vod-cdapremium"
    public static final String DISTCHOICE_ABONAMENT = "ABONAMENT"

    @Autowired
    com.kamil.merchants.infrastructure.parser.UpflixParserImpl upflixParser

    @MockBean
    com.kamil.merchants.infrastructure.parser.DocumentDownloader documentDownloader

    def "when context is loaded then all expected beans are created"() {
        expect: "the context is created"
        upflixParser
    }

    def cleanupper() {
        movieRepository.deleteAll().block()
    }

    def "getAllUpflixesFromWeb"() {
        given:
            cleanupper()
            Path path = Paths.get(DOCUMENT_RESOURCE_PATH)
            String html = Files.readString(path)
            Document doc = Jsoup.parse(html)
            when(documentDownloader.getUpflixDocument(anyString(), anyString())).thenReturn(doc)
        when:
            def parsedUpflixes = upflixParser.getAllUpflixesFromWeb(FILMNAME_VALUE, FILMYEAR_VALUE)
        then:
            assert parsedUpflixes.get(0).siteName == SITENAME_IPLA
            assert parsedUpflixes.get(0).distributionChoice == DISTCHOICE_ABONAMENT
            assert parsedUpflixes.get(1).siteName == SITENAME_CDA
            assert parsedUpflixes.get(1).distributionChoice == DISTCHOICE_ABONAMENT

    }
}

