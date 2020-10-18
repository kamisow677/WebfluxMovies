package utils

import com.kamil.merchants.upflix.Upflix

class UpflixTestBilder {

    private static Upflix.UpflixBuilder baseUpflix()
    {
        return Upflix
                .builder()
                .id("1")
                .link("Link")
                .distributionChoice("Abonament")
                .siteName("site")

    }

    static Upflix.UpflixBuilder createUpflixDistrChoice(String distrChoice) {
        return baseUpflix()
                .distributionChoice(distrChoice)

    }

    static Upflix.UpflixBuilder createUpflix(String distrChoice, String id) {
        return baseUpflix()
                .distributionChoice(distrChoice)
                .id(id)
    }
}
