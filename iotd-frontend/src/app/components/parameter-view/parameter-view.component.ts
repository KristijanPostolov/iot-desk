import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {Parameter} from "../../models/parameter";
import {ParameterSnapshot} from "../../models/parameter-snapshot";
import {TimeRange} from "../../models/time-range";
import {ParameterService} from "../../services/parameter.service";

@Component({
  selector: 'app-parameter-view',
  templateUrl: './parameter-view.component.html',
  styleUrls: ['./parameter-view.component.css']
})
export class ParameterViewComponent implements OnChanges {

  @Input()
  parameter: Parameter | undefined;
  @Input()
  previewRange: TimeRange | undefined;

  parameterSnapshots: ParameterSnapshot[] = [];
  options: Object | undefined;

  constructor(private parameterService: ParameterService) {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.parameter && this.previewRange) {
      this.parameterService.getParameterValues(this.parameter.id, this.previewRange.beginRange, this.previewRange.endRange)
        .subscribe(snapshots => {
          this.parameterSnapshots = snapshots;
          this.reloadChart();
      });
    }
  }

  reloadChart() {
    const data = this.parameterSnapshots
      .map(snapshot => ([Date.parse(snapshot.timestamp.toString()), snapshot.value]));

    const from = Date.parse(this.previewRange!.beginRange.toString());
    const to = Date.parse(this.previewRange!.endRange.toString());
    this.options = {
      title: { text: this.parameter!.name },
      xAxis: {
        type: 'datetime',
        title: { text: 'Timestamp' },
        min: from,
        max: to
      },
      yAxis: {
        title: { text: 'Value' }
      },
      tooltip: {
        pointFormat: 'Value: {point.y:.2f}'
      },
      plotOptions: {
        series: {
          marker: {
            enabled: true,
            radius: 3,
            fillColor: '#06C'
          }
        }
      },
      legend: { enabled: false },
      series: [
        {
          color: '#6CF',
          data: data
        }
      ]
    };
  }

}
