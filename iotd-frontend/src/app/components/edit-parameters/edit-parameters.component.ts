import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {EditParametersData} from "./edit-parameters-data";
import {MatSnackBar} from "@angular/material/snack-bar";
import {DeviceService} from "../../services/device.service";
import {ParameterRenameRequest} from "../../models/parameter-rename-request";

@Component({
  selector: 'app-edit-parameters',
  templateUrl: './edit-parameters.component.html',
  styleUrls: ['./edit-parameters.component.css']
})
export class EditParametersComponent {

  deviceId: number;
  parameters: any[] = [];

  constructor(private dialogRef: MatDialogRef<EditParametersComponent>,
              @Inject(MAT_DIALOG_DATA) private data: EditParametersData, private snackBar: MatSnackBar,
              private deviceService: DeviceService) {
    this.deviceId = data.deviceId;
    data.parameters.forEach(parameter => {
      this.parameters.push({ anchor: parameter.anchor, name: parameter.name, oldName: parameter.name });
    });
  }

  onRename(parameter: any) {
    const request = new ParameterRenameRequest(parameter.name);
    this.deviceService.renameDeviceParameter(this.deviceId, parameter.anchor, request).subscribe(() => {
      parameter.oldName = parameter.name;
      this.snackBar.open('Renamed parameter with anchor ' + parameter.anchor + ' to "' + parameter.name + '"',
        undefined, { duration: 3000, verticalPosition: "top", panelClass: 'green-snackbar' });
    });
  }

  onRevert(parameter: any) {
    parameter.name = parameter.oldName;
  }
}
