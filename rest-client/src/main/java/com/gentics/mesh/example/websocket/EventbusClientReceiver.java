package com.gentics.mesh.example.websocket;

import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gentics.mesh.core.rest.MeshEvent;
import com.gentics.mesh.rest.client.MeshRestClient;
import com.gentics.mesh.rest.client.MeshWebsocket;

/**
 * Example program which will register on node update and custom events and print these on the console.
 */
public class EventbusClientReceiver {

	public static void main(String[] args) throws IOException {
		MeshRestClient client = MeshRestClient.create("demo.getmesh.io", 443, true);
		client.setLogin("admin", "admin");
		client.login().blockingGet();

		MeshWebsocket eb = client.eventbus();
		// Register on node update events
		eb.registerEvents(MeshEvent.NODE_UPDATED.address);

		// Register on our own custom event. The EventbusClientSender program can be run in-parallel to send the messages
		eb.registerEvents("custom.my-own-event-name");

		// Handle events
		eb.events().subscribe(event -> {
			ObjectNode body = event.getBodyAsJson();
			if (body != null) {
				long count = body.get("counter").asLong(0);
				System.out.println("Handler: " + count);
			}
		});

		System.out.println("Press any key to stop receiver");
		System.in.read();

	}

}
