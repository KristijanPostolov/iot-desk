import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-send-command',
  templateUrl: './send-command.component.html',
  styleUrls: ['./send-command.component.css']
})
export class SendCommandComponent implements OnInit {

  commandContent = '';

  @Output()
  send = new EventEmitter<string>()

  @Output()
  cancel = new EventEmitter();

  ngOnInit(): void {
  }

  onSend() {
    this.send.emit(this.commandContent);
  }

  onCancel() {
    this.cancel.emit();
  }

}
