import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {CreateCommand} from "../../models/create-command";
import {CommandService} from "../../services/command.service";

@Component({
  selector: 'app-send-command',
  templateUrl: './post-command.component.html',
  styleUrls: ['./post-command.component.css']
})
export class PostCommandComponent {

  deviceId: number;
  commandContent = '';

  constructor(private dialogRef: MatDialogRef<PostCommandComponent>,
              @Inject(MAT_DIALOG_DATA) private deviceData: number, private commandService: CommandService) {
    this.deviceId = deviceData;
  }

  onPost() {
    this.commandService.postCommand(new CreateCommand(this.deviceId, this.commandContent))
      .subscribe(() => this.dialogRef.close(this.commandContent));
  }

}
