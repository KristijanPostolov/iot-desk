import {Component, OnInit} from '@angular/core';
import {DeviceService} from "../../services/device.service";
import {CreateDeviceRequest} from "../../models/create-device-request";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-device',
  templateUrl: './create-device.component.html',
  styleUrls: ['./create-device.component.css']
})
export class CreateDeviceComponent implements OnInit {

  deviceName = "";

  constructor(private deviceService: DeviceService, private router: Router) { }

  ngOnInit(): void {
  }

  onCreate() {
    const request = new CreateDeviceRequest(this.deviceName);
    this.deviceService.createDevice(request)
      .subscribe(response => {
        this.router.navigateByUrl(`/devices/${response.id}`, { state: { afterCreation: true } });
      })
  }

}
