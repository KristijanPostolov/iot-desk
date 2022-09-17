import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {DeviceService} from "../../services/device.service";
import {DeviceDetails} from "../../models/device-details";
import {Clipboard} from "@angular/cdk/clipboard";
import {ParameterService} from "../../services/parameter.service";
import {ParameterSnapshot} from "../../models/parameter-snapshot";
import {CommandService} from "../../services/command.service";
import {DeviceCommand} from "../../models/device-command";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";
import {EditParametersComponent} from "../edit-parameters/edit-parameters.component";
import {EditParametersData} from "../edit-parameters/edit-parameters-data";
import {PostCommandComponent} from "../send-command/post-command.component";

@Component({
  selector: 'app-device-details',
  templateUrl: './device-details.component.html',
  styleUrls: ['./device-details.component.css']
})
export class DeviceDetailsComponent implements OnInit {

  static statusTooltipTexts = new Map([
    ["NEW", "Device is created, but no messages were received from it yet."],
    ["ACTIVE", "Device is active, at least one message was received from it."]
  ]);

  displaySuccessfulCreation = false;
  device?: DeviceDetails;
  statusTooltipText?: string = '';

  parametersData = new Map<number, ParameterSnapshot[]>();
  deviceCommands: DeviceCommand[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private deviceService: DeviceService,
              private clipboard: Clipboard, private parameterService: ParameterService,
              private commandService: CommandService, private snackBar: MatSnackBar, private dialog: MatDialog) {
    this.displaySuccessfulCreation = Boolean(
      this.router.getCurrentNavigation()?.extras.state?['afterCreation'] : false);
  }

  ngOnInit(): void {
    if (this.displaySuccessfulCreation) {
      this.snackBar.open('Successfully created new device.', undefined,
        { duration: 3000, verticalPosition: "top", panelClass: 'green-snackbar'})
    }
    this.fetchDeviceDetails();
  }

  fetchDeviceDetails() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.deviceService.getDeviceById(id)
      .subscribe(device => {
        this.device = device;
        this.device.parameters.sort((a, b) => a.anchor - b.anchor);
        this.statusTooltipText = DeviceDetailsComponent.statusTooltipTexts.get(this.device.state);
        this.fetchParameterValues();
        this.fetchDeviceCommands();
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

  fetchDeviceCommands() {
    if (this.device) {
      const now = new Date(Date.now());
      const beginRange = new Date(now.getTime());
      beginRange.setHours(beginRange.getHours() - 3);
      this.commandService.getCommands(this.device.id, beginRange, now)
        .subscribe(deviceCommands => this.deviceCommands = deviceCommands);
    }
  }

  openPostCommandComponent() {
    if (this.device) {
      const dialogRef = this.dialog.open(PostCommandComponent, { width: '33vw', data: this.device.id});
      dialogRef.afterClosed().subscribe(command => {
        if (command) {
          this.fetchDeviceDetails();
        }
      });
    }
  }

  openEditParametersDialog() {
    if (this.device) {
      const dialogRef = this.dialog.open(EditParametersComponent, { width: '60vw', autoFocus: "dialog",
        data: new EditParametersData(this.device.id, this.device.parameters)});
      dialogRef.afterClosed().subscribe(() => {
        this.fetchDeviceDetails();
      })
    }
  }
}
