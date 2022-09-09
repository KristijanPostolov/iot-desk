import {Component, OnInit} from '@angular/core';
import {Device} from "../../models/device";
import {DeviceService} from "../../services/device.service";
import {CreateDeviceComponent} from "../create-device/create-device.component";
import {MatDialog} from "@angular/material/dialog";
import {CreateDeviceRequest} from "../../models/create-device-request";
import {Router} from "@angular/router";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  devices: Device[] = [];

  constructor(private deviceService: DeviceService, private router: Router, public dialog: MatDialog) { }

  ngOnInit(): void {
    this.deviceService.getDevices()
        .subscribe(devices => this.devices = devices);
  }

  openCreateDeviceDialog() {
    const dialogRef = this.dialog.open(CreateDeviceComponent, { width: '33vw' })
    dialogRef.afterClosed().subscribe(deviceName => {
      if (deviceName) {
        this.createNewDevice(deviceName);
      }
    });
  }

  createNewDevice(deviceName: string) {
    const request = new CreateDeviceRequest(deviceName);
    this.deviceService.createDevice(request)
      .subscribe(response => {
        this.router.navigateByUrl(`/devices/${response.id}`, { state: { afterCreation: true } });
      })
  }

}
