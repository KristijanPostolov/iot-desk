export class CreateCommand {
  deviceId: number;
  commandContent: string;

  constructor(deviceId: number, commandContent: string) {
    this.deviceId = deviceId;
    this.commandContent = commandContent;
  }
}