import { AbstractEvent } from './abstract-event.model';
import { EventDays } from './event-days.model';

export class Event extends AbstractEvent {
  constructor(
    public days?: EventDays[],
    public startDate?: Date,            // TODO: delete
    public endDate?: Date,              // TODO: delete
    public placeCity?: string,          // TODO: delete
    public placeVenueAddress?: string,  // TODO: delete
    public mapCoordinates?: string      // TODO: delete
  ) {
    super();
  }
}
