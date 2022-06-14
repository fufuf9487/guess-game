package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.source.Event;
import guess.domain.source.EventDays;
import guess.domain.source.EventList;
import guess.util.yaml.YamlUtils;

import java.io.IOException;
import java.util.List;

public class MigrationUtils {
    private static void fillEventDays() throws SpeakerDuplicatedException, IOException, NoSuchFieldException {
        var resourceSourceInformation = YamlUtils.readSourceInformation();
        List<Event> events = resourceSourceInformation.getEvents();

        events.forEach(e -> e.setDays(List.of(
                new EventDays(e.getStartDate(), e.getEndDate(), e.getPlace())
        )));

        YamlUtils.save(new EventList(events), "events-to-update.yml");
    }

    private static void saveEventDays() throws SpeakerDuplicatedException, IOException, NoSuchFieldException {
        var resourceSourceInformation = YamlUtils.readSourceInformation();
        List<Event> events = resourceSourceInformation.getEvents();

        YamlUtils.save(new EventList(events), "events-to-update.yml");
    }

    public static void main(String[] args) throws SpeakerDuplicatedException, IOException, NoSuchFieldException {
//        fillEventDays();
        saveEventDays();
    }
}
