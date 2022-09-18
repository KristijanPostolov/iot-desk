import {Component, Input, OnInit} from '@angular/core';
import {Parameter} from "../../models/parameter";
import {ParameterSnapshot} from "../../models/parameter-snapshot";

@Component({
  selector: 'app-parameter-view',
  templateUrl: './parameter-view.component.html',
  styleUrls: ['./parameter-view.component.css']
})
export class ParameterViewComponent implements OnInit {

  @Input()
  parameter: Parameter | undefined;

  @Input()
  parameterSnapshots: ParameterSnapshot[] | undefined;

  options: Object | undefined;

  constructor() {
  }

  ngOnInit(): void {
    if (this.parameter && this.parameterSnapshots) {
      const data = this.parameterSnapshots
        .map(snapshot => ([Date.parse(snapshot.timestamp.toString()), snapshot.value]));

      this.options = {
        title: { text: this.parameter.name },
        xAxis: {
          type: 'datetime',
          title: { text: 'Timestamp' }
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

}
