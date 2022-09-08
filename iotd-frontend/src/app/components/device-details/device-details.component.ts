import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {DeviceService} from "../../services/device.service";
import {DeviceDetails} from "../../models/device-details";
import {Clipboard} from "@angular/cdk/clipboard";
import {ParameterService} from "../../services/parameter.service";
import {ParameterSnapshot} from "../../models/parameter-snapshot";
import {CommandService} from "../../services/command.service";
import {CreateCommand} from "../../models/create-command";
import {DeviceCommand} from "../../models/device-command";

@Component({
  selector: 'app-device-details',
  templateUrl: './device-details.component.html',
  styleUrls: ['./device-details.component.css']
})
export class DeviceDetailsComponent implements OnInit {

  displaySuccessfulCreation = false;
  device?: DeviceDetails;
  parametersData = new Map<number, ParameterSnapshot[]>();
  deviceCommands: DeviceCommand[] = [];

  creatingNewCommand = false;

  constructor(private route: ActivatedRoute, private router: Router, private deviceService: DeviceService,
              private clipboard: Clipboard, private parameterService: ParameterService,
              private commandService: CommandService) {
    this.displaySuccessfulCreation = Boolean(
      this.router.getCurrentNavigation()?.extras.state?['afterCreation'] : false);
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.deviceService.getDeviceById(id)
      .subscribe(device => {
        this.device = device;
        this.fetchParameterValues();
        this.fetchDeviceCommands()
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

  openNewCommandComponent() {
    this.creatingNewCommand = true;
  }

  onCancelNewCommand() {
    this.creatingNewCommand = false;
  }

  postNewCommand(command: string) {
    this.creatingNewCommand = false;
    this.commandService.postCommand(new CreateCommand(this.device!.id, command))
      .subscribe(() => this.fetchDeviceCommands());
  }

  fetchDeviceCommands() {
    if (this.device) {
      const now = new Date(Date.now());
      const beginRange = new Date(now.getTime());
      beginRange.setHours(beginRange.getHours() - 3);
      this.commandService.getCommands(this.device.id, beginRange, now)
        .subscribe(deviceCommands => this.deviceCommands = deviceCommands);
    }
  }
}
