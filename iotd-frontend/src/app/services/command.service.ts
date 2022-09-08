import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {CreateCommand} from "../models/create-command";
import {Observable} from "rxjs";
import {DeviceCommand} from "../models/device-command";

@Injectable({
  providedIn: 'root'
})
export class CommandService {

  constructor(private http: HttpClient) { }

  postCommand(command: CreateCommand) : Observable<any> {
    return this.http.post('/api/commands', command);
  }

  getCommands(deviceId: number, beginRange: Date, endRange: Date) : Observable<DeviceCommand[]>{
    const queryParams = new HttpParams()
      .set("deviceId", deviceId)
      .set("beginRange", beginRange.toISOString())
      .set("endRange", endRange.toISOString());

    return this.http.get<DeviceCommand[]>('/api/commands', { params: queryParams });
  }
}
