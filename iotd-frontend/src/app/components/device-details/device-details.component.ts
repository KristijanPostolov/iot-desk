import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {DeviceService} from "../../services/device.service";
import {DeviceDetails} from "../../models/device-details";

@Component({
  selector: 'app-device-details',
  templateUrl: './device-details.component.html',
  styleUrls: ['./device-details.component.css']
})
export class DeviceDetailsComponent implements OnInit {

  device?: DeviceDetails;

  constructor(private route: ActivatedRoute, private deviceService: DeviceService) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.deviceService.getDeviceById(id)
      .subscribe(device => this.device = device);
  }

}
