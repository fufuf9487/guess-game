import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { EventType } from '../../../shared/models/event-type.model';
import { SpeakerStatistics } from '../../../shared/models/speaker-statistics.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { QuestionService } from '../../../shared/services/question.service';
import { findEventTypeById } from '../../general/utility-functions';

@Component({
  selector: 'app-speaker-statistics',
  templateUrl: './speaker-statistics.component.html'
})
export class SpeakerStatisticsComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;

  public isConferences = true;
  public isMeetups = true;

  public eventTypes: EventType[] = [];
  public selectedEventType: EventType;
  public eventTypeSelectItems: SelectItem[] = [];

  public speakerStatistics = new SpeakerStatistics();
  public multiSortMeta: any[] = [];

  constructor(private statisticsService: StatisticsService, private questionService: QuestionService,
              private eventTypeService: EventTypeService, private eventService: EventService,
              public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'talksQuantity', order: -1});
    this.multiSortMeta.push({field: 'eventsQuantity', order: -1});
    this.multiSortMeta.push({field: 'eventTypesQuantity', order: -1});
  }

  ngOnInit(): void {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  loadEventTypes(isConferences: boolean, isMeetups: boolean) {
    this.eventTypeService.getFilterEventTypes(isConferences, isMeetups)
      .subscribe(eventTypesData => {
        this.eventTypes = eventTypesData;
        this.eventTypeSelectItems = this.eventTypes.map(et => {
            return {label: et.name, value: et};
          }
        );

        if (this.eventTypes.length > 0) {
          this.eventService.getDefaultEvent()
            .subscribe(defaultEventData => {
              const selectedEventType = (defaultEventData) ? findEventTypeById(defaultEventData.eventTypeId, this.eventTypes) : null;

              if (selectedEventType) {
                this.selectedEventType = selectedEventType;
              } else {
                this.selectedEventType = null;
              }

              this.loadSpeakerStatistics(this.selectedEventType);
            });
        } else {
          this.selectedEventType = null;
          this.loadSpeakerStatistics(this.selectedEventType);
        }
      });
  }

  loadSpeakerStatistics(eventType: EventType) {
    this.statisticsService.getSpeakerStatistics(this.isConferences, this.isMeetups, eventType)
      .subscribe(data => {
          this.speakerStatistics = data;
        }
      );
  }

  onEventTypeChange(eventType: EventType) {
    this.loadSpeakerStatistics(eventType);
  }

  onEventTypeKindChange(checked: boolean) {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  onLanguageChange() {
    this.loadSpeakerStatistics(this.selectedEventType);
  }

  isNoSpeakersFoundVisible() {
    return (this.speakerStatistics?.speakerMetricsList && (this.speakerStatistics.speakerMetricsList.length === 0));
  }

  isSpeakersListVisible() {
    return (this.speakerStatistics?.speakerMetricsList && (this.speakerStatistics.speakerMetricsList.length > 0));
  }
}
