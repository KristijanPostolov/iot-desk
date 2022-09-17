import {Parameter} from "../../models/parameter";

export class EditParametersData {
  deviceId: number;
  parameters: Parameter[];

  constructor(deviceId: number, parameters: Parameter[]) {
    this.deviceId = deviceId;
    this.parameters = parameters;
  }
}