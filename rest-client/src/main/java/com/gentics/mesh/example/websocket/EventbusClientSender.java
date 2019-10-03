package com.gentics.mesh.example.websocket;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.gentics.mesh.rest.client.MeshRestClient;
import com.gentics.mesh.rest.client.MeshWebsocket;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Simple example which will send a custom message on the
 */
public class EventbusClientSender {

	public static void main(String[] args) throws IOException {
		Vertx vertx = Vertx.vertx();
		MeshRestClient client = MeshRestClient.create("demo.getmesh.io", 443, true);
		client.setLogin("admin", "admin");
		client.login().blockingGet();

		AtomicInteger counter = new AtomicInteger();
		MeshWebsocket eb = client.eventbus();

		// Send periodically some messages on my own address
		vertx.setPeriodic(3000, th -> {
			System.out.println("Sending custom event..");
			JsonObject msg = new JsonObject();
			msg.put("counter", counter.incrementAndGet());
			eb.publishEvent("custom.my-own-event-name", msg);
		});

		System.out.println("Press any key to stop sender");
		System.in.read();

	}

}
