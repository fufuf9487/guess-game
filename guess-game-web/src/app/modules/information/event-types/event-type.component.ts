import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { EventTypeDetails } from '../../../shared/models/event-type-details.model';
import { EventTypeService } from '../../../shared/services/event-type.service';

@Component({
  selector: 'app-event-type',
  templateUrl: './event-type.component.html'
})
export class EventTypeComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  private id: number;
  public eventTypeDetails: EventTypeDetails = new EventTypeDetails();
  public multiSortMeta: any[] = [];

  constructor(private eventTypeService: EventTypeService, public translateService: TranslateService,
              private activatedRoute: ActivatedRoute) {
    this.multiSortMeta.push({field: 'startDate', order: -1});
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const idString: string = params['id'];
      const idNumber: number = Number(idString);

      if (!isNaN(idNumber)) {
        this.id = idNumber;
        this.loadEventType(this.id);
      }
    });
  }

  loadEventType(id: number) {
    this.eventTypeService.getEventType(id)
      .subscribe(data => {
        this.eventTypeDetails = data;
      });
  }

  onLanguageChange() {
    this.loadEventType(this.id);
  }

  isEventsListVisible() {
    return ((this.eventTypeDetails.events) && (this.eventTypeDetails.events.length > 0));
  }
}
