import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { Event } from '../models/event.model';
import { EventType } from '../models/event-type.model';
import { MessageService } from '../../modules/message/message.service';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private baseUrl = 'api/event';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  getEvents(eventType: EventType, isConferences?: boolean, isMeetups?: boolean): Observable<Event[]> {
    let params = new HttpParams()
      .set('conferences', ((isConferences) ? isConferences : true).toString())
      .set('meetups', ((isMeetups) ? isMeetups : true).toString());
    if (eventType?.id) {
      params = params.set('eventTypeId', eventType.id.toString());
    }

    return this.http.get<Event[]>(`${this.baseUrl}/events`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getDefaultEvent(): Observable<Event> {
    return this.http.get<Event>(`${this.baseUrl}/default-event`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
