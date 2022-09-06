import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {DeviceService} from "../../services/device.service";
import {DeviceDetails} from "../../models/device-details";
import {Clipboard} from "@angular/cdk/clipboard";
import {ParameterService} from "../../services/parameter.service";
import {ParameterSnapshot} from "../../models/parameter-snapshot";

@Component({
  selector: 'app-device-details',
  templateUrl: './device-details.component.html',
  styleUrls: ['./device-details.component.css']
})
export class DeviceDetailsComponent implements OnInit {

  displaySuccessfulCreation = false;
  device?: DeviceDetails;
  parametersData = new Map<number, ParameterSnapshot[]>();

  constructor(private route: ActivatedRoute, private router: Router, private deviceService: DeviceService,
              private clipboard: Clipboard, private parameterService: ParameterService) {
    this.displaySuccessfulCreation = Boolean(
      this.router.getCurrentNavigation()?.extras.state?['afterCreation'] : false);
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.deviceService.getDeviceById(id)
      .subscribe(device => {
        this.device = device;
        this.fetchParameterValues();
      });
  }

  fetchAndCopyChannelId() {
    if (this.device) {
      this.deviceService.getChannelId(this.device.id)
        .subscribe(deviceChannelId => this.clipboard.copy(deviceChannelId.channelId));
    }
  }

  fetchParameterValues() {
    const now = new Date(Date.now());
    const beginRange = new Date(now.getTime());
    beginRange.setHours(beginRange.getHours() - 3);

    this.device?.parameters.forEach(parameter => {
      const id = parameter.id;
      this.parameterService.getParameterValues(id, beginRange, now)
        .subscribe(snapshots => this.parametersData.set(id, snapshots));
    });
  }
}
