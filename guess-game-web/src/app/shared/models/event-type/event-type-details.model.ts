import { EventType } from './event-type.model';
import { EventPart } from '../event/event-part.model';

export class EventTypeDetails {
  constructor(
    public eventType?: EventType,
    public eventParts?: EventPart[]
  ) {
  }
}
