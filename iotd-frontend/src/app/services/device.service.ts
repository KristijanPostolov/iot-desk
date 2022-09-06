import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {Device} from "../models/device";
import {HttpClient} from "@angular/common/http";
import {DeviceDetails} from "../models/device-details";

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
}
