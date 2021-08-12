import { CubeType } from './cube-type.model';
import { MeasureType } from './measure-type.model';

export class OlapParameters {
  constructor(
    public cubeType?: CubeType,
    public measureType?: MeasureType,
    public organizerId?: number,
    public eventTypeId?: number,
    public speakerIds?: number[],
    public companyIds?: number[]
  ) {
  }
}
