export interface DeviceCommand {
  commandId: string;
  content: string;
  sentAt: Date;
  acknowledgedAt: Date;
  ackStatus: string;
}