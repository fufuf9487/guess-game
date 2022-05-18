package guess.util.load;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JrgCmsDataLoader class tests")
class JrgCmsDataLoaderTest {
    @Test
    void getImageWidthParameterName() {
        assertEquals("width", new JrgCmsDataLoader().getImageWidthParameterName());
    }
}
