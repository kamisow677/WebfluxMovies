package merchants

import com.kamil.merchants.upflix.Upflix

class UpflixTestBilder {

    private static Upflix.UpflixBuilder baseUpflix()
    {
        return Upflix
                .builder()
                .link("Link")
                .distributionChoice("Abonament")
                .siteName("site")

    }

    static Upflix.UpflixBuilder createUpflixDistrChoice(String distrChoice) {
        return baseUpflix()
                .distributionChoice(distrChoice)

    }
}
