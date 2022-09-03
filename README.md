# IOT-Desk

![Build status](https://github.com/KristijanPostolov/iot-desk/actions/workflows/maven.yml/badge.svg?branch=main)

## Description
Application for management of IoT devices. The main functionality is to view sensor data and 
manage actuators.

## Components
- **Rest API Server** - Spring boot application for providing the functionalities described above
- **Device Simulator** - Simulator that acts as a device and publishes random measurements. 
    In order to use it, you need to create a device via the Rest API and enter the channel_id of 
    the newly created device in the simulator.
    Then follow the instructions in the logs and fill in the needed input.
- **Docker images for essential infrastructure** - Docker compose files for essential infrastructure components like: 
    - Mosquitto broker

## How to run
The Run configurations are stored in the .idea/runConfigurations folder. You can Run them via the Run/Debug Configurations dropdown.
1. You need to start a Mosquitto broker. You can do this by running the "Start Mosquitto" run configuration.
2. Run the "IoT Desk Server" run configuration.
3. [Optional] To start a device simulator: Run the "Device Simulator" run configuration and follow the instructions given in the logs.
