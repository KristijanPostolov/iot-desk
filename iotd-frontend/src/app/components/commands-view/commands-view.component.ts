import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {TimeRange} from "../../models/time-range";
import {CommandService} from "../../services/command.service";
import {DeviceCommand} from "../../models/device-command";

@Component({
  selector: 'app-commands-view',
  templateUrl: './commands-view.component.html',
  styleUrls: ['./commands-view.component.css']
})
export class CommandsViewComponent implements OnChanges {

  @Input()
  deviceId: number | undefined;
  @Input()
  previewRange: TimeRange | undefined;

  deviceCommands: DeviceCommand[] = [];
  options: Object | undefined;
  selectedCommand: DeviceCommand | undefined;

  constructor(private commandService: CommandService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.deviceId && this.previewRange) {
      this.commandService.getCommands(this.deviceId, this.previewRange.beginRange, this.previewRange.endRange)
        .subscribe(deviceCommands => {
          this.deviceCommands = deviceCommands;
          this.reloadChart();
        });
    }
  }

  reloadChart() {
    const ackSuccessful = this.filterCommandsByStatus(this.deviceCommands, 'ACK_SUCCESSFUL');
    const ackFailed = this.filterCommandsByStatus(this.deviceCommands, 'ACK_FAILED');
    const noAck = this.filterCommandsByStatus(this.deviceCommands, 'NO_ACK');

    const from = Date.parse(this.previewRange!.beginRange.toString());
    const to = Date.parse(this.previewRange!.endRange.toString());
    this.options = {
      title: { text: 'Commands' },
      chart: {
        width: '800'
      },
      xAxis: {
        type: 'datetime',
        title: { text: 'Timestamp' },
        min: from,
        max: to
      },
      yAxis: {
        title: { text: 'Value' },
        visible: false
      },
      tooltip: {
        pointFormat: 'Command id: {point.command.commandId}'
      },
      plotOptions: {
        series: {
          lineWidth: 0,
          marker: {
            enabled: true,
            radius: 3,
          },
          events: {
            click: (event: any) => this.onCommandClick(event)
          }
        }
      },
      legend: { enabled: false },
      series: [
        {
          marker: {
            fillColor: '#00cc03'
          },
          data: ackSuccessful
        },
        {
          marker: {
            fillColor: '#cc0018'
          },
          data: ackFailed
        },
        {
          marker: {
            fillColor: '#ccc900'
          },
          data: noAck
        },
      ]
    };
  }

  filterCommandsByStatus(commands: DeviceCommand[], ackStatus: string) {
    return commands.filter(command => command.ackStatus === ackStatus)
      .map(command => ({x: Date.parse(command.sentAt.toString()), y: 0, command: command}));
  }

  onCommandClick(event: any) {
    this.selectedCommand = event.point.command;
  }
}
