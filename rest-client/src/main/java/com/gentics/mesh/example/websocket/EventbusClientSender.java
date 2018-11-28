package com.gentics.mesh.example.websocket;

import java.util.concurrent.atomic.AtomicInteger;

import com.gentics.mesh.rest.client.MeshRestClient;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Simple example which will send a custom message on the
 */
public class EventbusClientSender {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		MeshRestClient client = MeshRestClient.create("demo.getmesh.io", 443, true, vertx);
		client.setLogin("admin", "admin");
		client.login().blockingGet();

		AtomicInteger counter = new AtomicInteger();
		client.eventbus(ws -> {

			// Send periodically some messages on my own address
			vertx.setPeriodic(3000, th -> {
				System.out.println("Sending custom event..");
				JsonObject msg = new JsonObject().put("type", "publish").put("address", "custom.my-own-event-name").put("body",
					counter.incrementAndGet());
				ws.writeFrame(io.vertx.core.http.WebSocketFrame.textFrame(msg.encode(), true));
				
			});

			// Send ping messages to keep the connection alive
			vertx.setPeriodic(800, th -> {
				JsonObject pingMsg = new JsonObject().put("type", "ping");
				ws.writeFrame(io.vertx.core.http.WebSocketFrame.textFrame(pingMsg.encode(), true));
			});

		});
	}

}
