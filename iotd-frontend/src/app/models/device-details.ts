import {Device} from "./device";
import {Parameter} from "./parameter";

export interface DeviceDetails extends Device {
  createdAt: Date;
  parameters: Parameter[];
}