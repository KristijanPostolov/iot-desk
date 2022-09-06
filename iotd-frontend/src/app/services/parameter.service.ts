import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {ParameterSnapshot} from "../models/parameter-snapshot";

@Injectable({
  providedIn: 'root'
})
export class ParameterService {

  constructor(private http: HttpClient) { }

  getParameterValues(id: number, beginRange: Date, endRange: Date) : Observable<ParameterSnapshot[]> {
    const queryParams = new HttpParams()
      .set("beginRange", beginRange.toISOString())
      .set("endRange", endRange.toISOString());

    return this.http.get<ParameterSnapshot[]>(`/api/parameters/${id}/snapshots`, { params: queryParams });
  }
}
