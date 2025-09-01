package com.rles.simulator;

import java.io.IOException;

import com.rles.simulator.sensors.Sensor;
import com.rles.simulator.sensors.environment.AmbientHumiditySensor;
import com.rles.simulator.sensors.environment.AmbientTemperatureSensor;
import com.rles.simulator.sensors.environment.DewPointSensor;
import com.rles.simulator.telemetry.Encoder;
import com.rles.simulator.telemetry.Streamer;
import com.rles.simulator.telemetry.Transport;
import com.rles.simulator.telemetry.ClientTransport;
import com.rles.simulator.telemetry.ServerTransport;


public class SimulatorMain {

	public static void main(String[] args) {
		// Args pull
		final String host = (args.length >= 1) ? args[0] : "127.0.0.1";
		final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 7000;
		final Simulator.OutputMode mode = (args.length >= 3) ? Simulator.OutputMode.valueOf(args[2].toUpperCase()) : Simulator.OutputMode.BOTH;
		
		// Set-up
		SimulatorContext context = new SimulatorContext();
		//Transport transport = new ClientTransport(host, port);
		Transport transport = new ServerTransport(port);
		Encoder encoder = new Encoder();
		Streamer streamer = new Streamer(encoder, transport, 1024, 64, 20);
		Simulator sim = new Simulator(context, streamer, transport, mode);
		
		// Create sensors
		Sensor temp = new AmbientTemperatureSensor(1001, "Ambient Temp");
		Sensor dew = new DewPointSensor(1002, "Dew Point");
		
		// Create contextual sensors - Require dependent sensors to exist first
		Sensor humidity = new AmbientHumiditySensor(1003, "Relative Humidity", context, temp.getSensorId(), dew.getSensorId());
		
		
		
		// Register sensors
		sim.registerSensor(temp, 100);
		sim.registerSensor(dew, 100);
		sim.registerSensor(humidity, 100);
		
		try {
			sim.start();
			System.out.printf("[Simulator] Mode=%s; %s%n", mode,
					(mode == Simulator.OutputMode.LOG) ? "Logging locally..." : String.format("Streaming to %s:%d...", host, port));
			
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("\n[Simulator] Shutting down...");
				sim.stop();
				System.out.println("[Simualtor] Done.");
			}));
			
			Thread.currentThread().join();
		} catch (IOException ex) {
			System.err.println("[Simulator] Failed to start: " + ex.getMessage());
			sim.stop();
		} catch (InterruptedException ex) {
			sim.stop();
		}
	}
	
}
