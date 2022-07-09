import { AbstractEvent } from './abstract-event.model';
import { EventDays } from './event-days.model';

export class Event extends AbstractEvent {
  constructor(
    public days?: EventDays[]
  ) {
    super();
  }
}
