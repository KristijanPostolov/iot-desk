import {Component} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-create-device',
  templateUrl: './create-device.component.html',
  styleUrls: ['./create-device.component.css']
})
export class CreateDeviceComponent {

  deviceName = "";

  constructor(public dialogRef: MatDialogRef<CreateDeviceComponent>) { }

  onCancel() {
    this.dialogRef.close();
  }

}
