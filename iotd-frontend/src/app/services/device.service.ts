import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {Device} from "../models/device";
import {HttpClient} from "@angular/common/http";
import {DeviceDetails} from "../models/device-details";
import {CreateDeviceRequest} from "../models/create-device-request";
import {CreateDeviceResponse} from "../models/create-device-response";
import {DeviceChannelId} from "../models/device-channel-id";

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  constructor(private http: HttpClient) { }

  getDevices() : Observable<Device[]> {
    return this.http.get<Device[]>('api/devices');
  }

  getDeviceById(id: number) : Observable<DeviceDetails> {
    return this.http.get<DeviceDetails>(`api/devices/${id}`);
  }

  getChannelId(id: number) : Observable<DeviceChannelId> {
    return this.http.get<DeviceChannelId>(`api/devices/${id}/channelId`);
  }

  createDevice(request: CreateDeviceRequest) : Observable<CreateDeviceResponse> {
    return this.http.post<CreateDeviceResponse>('api/devices', request);
  }
}
