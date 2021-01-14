import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { Organizer } from '../../../shared/models/organizer/organizer.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { OrganizerService } from '../../../shared/services/organizer.service';
import { findOrganizerById, getEventTypesWithSortName } from '../../general/utility-functions';

@Component({
  selector: 'app-event-types-search',
  templateUrl: './event-types-search.component.html'
})
export class EventTypesSearchComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public isConferences = true;
  public isMeetups = true;

  public organizers: Organizer[] = [];
  public selectedOrganizer: Organizer;
  public organizerSelectItems: SelectItem[] = [];

  public eventTypes: EventType[] = [];
  public multiSortMeta: any[] = [];

  constructor(private eventTypeService: EventTypeService, public organizerService: OrganizerService,
              public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'sortName', order: 1});
  }

  ngOnInit(): void {
    this.loadOrganizers(this.isConferences, this.isMeetups);
  }

  loadOrganizers(isConferences: boolean, isMeetups: boolean) {
    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.organizers = organizerData;
        this.organizerSelectItems = this.organizers.map(o => {
            return {label: o.name, value: o};
          }
        );

        if (this.organizers.length > 0) {
          this.organizerService.getDefaultOrganizer()
            .subscribe(defaultOrganizer => {
              const selectedOrganizer = (defaultOrganizer) ? findOrganizerById(defaultOrganizer.id, this.organizers) : null;

              if (selectedOrganizer) {
                this.selectedOrganizer = selectedOrganizer;
              } else {
                this.selectedOrganizer = null;
              }

              this.loadEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer);
            });
        } else {
          this.selectedOrganizer = null;
          this.loadEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer);
        }
      });
  }

  loadEventTypes(isConferences: boolean, isMeetups: boolean, organizer: Organizer) {
    this.eventTypeService.getEventTypes(isConferences, isMeetups)
      .subscribe(data => {
        this.eventTypes = getEventTypesWithSortName(data);
      });
  }

  onOrganizerChange(organizer: Organizer) {
    this.loadEventTypes(this.isConferences, this.isMeetups, organizer);
  }

  onEventTypeKindChange() {
    this.loadEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer);
  }

  onLanguageChange() {
    this.loadOrganizers(this.isConferences, this.isMeetups);
  }

  isNoEventTypesFoundVisible() {
    return (this.eventTypes && (this.eventTypes.length === 0));
  }

  isEventTypesListVisible() {
    return (this.eventTypes && (this.eventTypes.length > 0));
  }
}
