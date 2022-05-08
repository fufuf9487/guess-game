package guess.util.load;

import java.time.LocalDate;

/**
 * Factory of CMS data loader.
 */
public class CmsDataLoaderFactory {
    private static final LocalDate CONTENTFUL_AND_DATE = LocalDate.of(2022, 1, 1);

    private CmsDataLoaderFactory() {
    }

    static CmsDataLoader createDataLoader(LocalDate startDate) {
        if (startDate.isBefore(CONTENTFUL_AND_DATE)) {
            return new ContentfulDataLoader();
        } else {
            return new JugRuGroupCmsDataLoader();
        }
    }
}
