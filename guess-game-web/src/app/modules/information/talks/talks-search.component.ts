import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type.model';
import { Event } from '../../../shared/models/event.model';
import { Talk } from '../../../shared/models/talk.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import {
  findEventByDefaultEvent,
  findEventTypeByDefaultEvent,
  getEventsWithDisplayName,
  isStringEmpty
} from '../../general/utility-functions';

@Component({
  selector: 'app-talks-search',
  templateUrl: './talks-search.component.html'
})
export class TalksSearchComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public eventTypes: EventType[] = [];
  public selectedEventType: EventType;
  public eventTypeSelectItems: SelectItem[] = [];

  public events: Event[] = [];
  public selectedEvent: Event;
  public eventSelectItems: SelectItem[] = [];

  private defaultEvent: Event;

  public talkName: string;
  public speakerName: string;

  public talks: Talk[] = [];

  private searched = false;

  constructor(private eventTypeService: EventTypeService, private eventService: EventService,
              public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.loadEventTypes();
  }

  loadEventTypes() {
    this.eventTypeService.getEventTypes()
      .subscribe(eventTypesData => {
        this.eventTypes = eventTypesData;
        this.eventTypeSelectItems = this.eventTypes.map(et => {
            return {label: et.name, value: et};
          }
        );

        if (this.eventTypes.length > 0) {
          this.eventService.getDefaultEvent()
            .subscribe(defaultEventData => {
              this.defaultEvent = defaultEventData;

              const selectedEventType = findEventTypeByDefaultEvent(this.defaultEvent, this.eventTypes);

              if (selectedEventType) {
                this.selectedEventType = selectedEventType;
              } else {
                this.selectedEventType = this.eventTypes[0];
              }

              this.loadEvents(this.selectedEventType);
            });
        } else {
          this.selectedEventType = null;
          this.loadEvents(this.selectedEventType);
        }
      });
  }

  onEventTypeChange(eventType: EventType) {
    this.loadEvents(eventType);
    this.searched = false;
  }

  loadEvents(eventType: EventType) {
    if (eventType) {
      this.eventService.getEvents(eventType.id)
        .subscribe(data => {
          this.events = getEventsWithDisplayName(data, this.translateService);
          this.eventSelectItems = this.events.map(e => {
            return {label: e.displayName, value: e};
          });

          if (this.events.length > 0) {
            const selectedEvent = findEventByDefaultEvent(this.defaultEvent, this.events);

            if (selectedEvent) {
              this.selectedEvent = selectedEvent;
            } else {
              this.selectedEvent = this.events[0];
            }
          } else {
            this.selectedEvent = null;
          }
        });
    } else {
      this.events = [];
      this.eventSelectItems = [];
      this.selectedEvent = undefined;
    }
  }

  onEventChange(event: Event) {
    this.searched = false;
  }

  loadTalks(eventType: EventType, event: Event, talkName: string, speakerName: string) {
    // TODO: implement
    console.log('eventType: ' + eventType + ', event: ' + event, ', talkName: ' + talkName + ', speakerName: ' + speakerName);
    this.searched = true;
  }

  onLanguageChange() {
    this.loadEventTypes();

    this.talkName = undefined;
    this.speakerName = undefined;

    this.searched = false;
  }

  onFilterChange(value: any) {
    this.searched = false;
  }

  search() {
    if (!this.isSearchDisabled()) {
      this.loadTalks(this.selectedEventType, this.selectedEvent, this.talkName, this.speakerName);
    }
  }

  clear() {
    this.selectedEventType = undefined;
    this.events = [];
    this.eventSelectItems = [];
    this.selectedEvent = undefined;
    this.talkName = undefined;
    this.speakerName = undefined;

    this.searched = false;
  }

  isSearchDisabled(): boolean {
    return (!this.selectedEventType &&
      !this.selectedEvent &&
      isStringEmpty(this.talkName) &&
      isStringEmpty(this.speakerName));
  }

  isNoTalksFoundVisible() {
    return (this.searched && (this.talks.length === 0));
  }

  isTalksListVisible() {
    return (this.searched && (this.talks.length > 0));
  }
}
