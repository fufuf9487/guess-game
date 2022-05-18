package guess.util.load;

import guess.domain.source.load.CmsType;

import java.time.LocalDate;

/**
 * Factory of CMS data loader.
 */
public class CmsDataLoaderFactory {
    private static final LocalDate CONTENTFUL_AND_DATE = LocalDate.of(2022, 1, 1);

    private CmsDataLoaderFactory() {
    }

    static CmsDataLoader createDataLoader(CmsType cmsType) {
        return switch (cmsType) {
            case CONTENTFUL -> new ContentfulDataLoader();
            case JUGRUGROUP_CMS -> new JrgCmsDataLoader();
        };
    }

    static CmsDataLoader createDataLoader(LocalDate startDate) {
        if (startDate.isBefore(CONTENTFUL_AND_DATE)) {
            return new ContentfulDataLoader();
        } else {
            return new JrgCmsDataLoader();
        }
    }
}
