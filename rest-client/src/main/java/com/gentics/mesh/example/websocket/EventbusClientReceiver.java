package com.gentics.mesh.example.websocket;

import com.gentics.mesh.Events;
import com.gentics.mesh.rest.client.MeshRestClient;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Example program which will register on node update and custom events and print these on the console.
 */
public class EventbusClientReceiver {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		MeshRestClient client = MeshRestClient.create("demo.getmesh.io", 443, true, vertx);
		client.setLogin("admin", "admin");
		client.login().blockingGet();

		client.eventbus(ws -> {
			// Register on node update events
			JsonObject msg = new JsonObject().put("type", "register").put("address", Events.EVENT_NODE_UPDATED);
			ws.writeFrame(io.vertx.core.http.WebSocketFrame.textFrame(msg.encode(), true));

			// Register on our own custom event. The EventbusClientSender program can be run in-parallel to send the messages
			JsonObject msg2 = new JsonObject().put("type", "register").put("address", "custom.my-own-event-name");
			ws.writeFrame(io.vertx.core.http.WebSocketFrame.textFrame(msg2.encode(), true));

			// Handle events
			ws.handler(buff -> {
				String str = buff.toString();
				if (!str.equals("ping")) {
					JsonObject received = new JsonObject(str);
					Object rec = received.getValue("body");
					System.out.println("Handler: " + rec.toString());
				}
			});

			// Send ping messages to keep the connection alive
			vertx.setPeriodic(800, th -> {
				JsonObject pingMsg = new JsonObject().put("type", "ping");
				ws.writeFrame(io.vertx.core.http.WebSocketFrame.textFrame(pingMsg.encode(), true));
			});

		}, fh -> {
			fh.printStackTrace();
		});
	}

}
