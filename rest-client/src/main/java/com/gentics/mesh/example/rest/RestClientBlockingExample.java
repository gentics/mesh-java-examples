package com.gentics.mesh.example.rest;

import com.gentics.mesh.core.rest.node.NodeCreateRequest;
import com.gentics.mesh.core.rest.node.NodeResponse;
import com.gentics.mesh.core.rest.node.field.impl.HtmlFieldImpl;
import com.gentics.mesh.core.rest.node.field.impl.NodeFieldImpl;
import com.gentics.mesh.core.rest.node.field.impl.NumberFieldImpl;
import com.gentics.mesh.core.rest.node.field.impl.StringFieldImpl;
import com.gentics.mesh.parameter.LinkType;
import com.gentics.mesh.parameter.client.NodeParametersImpl;
import com.gentics.mesh.rest.client.MeshRestClient;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

/**
 * Basic REST Client Example
 */
public class RestClientBlockingExample {

	final static String PROJECT_NAME = "demo";

	final static String AUTOMOBILES_FOLDER_UUID = "ca6c7df3f45b48d4ac7df3f45ba8d42f";
	final static String VEHICLE_SCHEMA_UUID = "2aa83a2b3cba40a1a83a2b3cba90a1de";
	final static String IMAGES_FOLDER_UUID = "15d5ef7a9abf416d95ef7a9abf316d68";

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		MeshRestClient client = MeshRestClient.create("demo.getmesh.io", 443, true, vertx);
		client.setLogin("admin", "admin");
		client.login().blockingGet();

		// 1. Create the image node
		NodeCreateRequest request = new NodeCreateRequest();
		request.setLanguage("en");
		request.setParentNodeUuid(IMAGES_FOLDER_UUID);
		request.setSchemaName("vehicleImage");
		request.getFields().put("name", new StringFieldImpl().setString("Volkswagen Beetle"));
		NodeResponse imageNode = client.createNode(PROJECT_NAME, request).toSingle().blockingGet();

		// 2. Upload the image data - by updating the image field of the node
		Buffer buffer = vertx.fileSystem().readFileBlocking("images/vw-beetle.jpeg");
		client.updateNodeBinaryField(PROJECT_NAME, imageNode.getUuid(), imageNode.getLanguage(), imageNode.getVersion(), "image", buffer,
			"vw-beetle.jpeg", "image/jpeg").toSingle().blockingGet();

		// 2. Create the vehicle node
		NodeCreateRequest nodeRequest = new NodeCreateRequest();
		nodeRequest.setSchemaName("vehicle");
		nodeRequest.setLanguage("en");
		nodeRequest.setParentNodeUuid(AUTOMOBILES_FOLDER_UUID);
		nodeRequest.getFields().put("name", new StringFieldImpl().setString("Volkswagen Beetle"));
		nodeRequest.getFields().put("slug", new StringFieldImpl().setString("vw-beetle"));
		nodeRequest.getFields().put("weight", new NumberFieldImpl().setNumber(840));
		nodeRequest.getFields().put("description", new HtmlFieldImpl().setHTML(
			"The Volkswagen Beetle - officially the Volkswagen Type 1, informally in German the Käfer (literally \"beetle\"), in parts of the English-speaking world the Bug, and known by many other nicknames in other languages—is a two-door, rear-engine economy car, intended for five passengers, that was manufactured and marketed by German automaker Volkswagen (VW) from 1938 until 2003."));
		nodeRequest.getFields().put("stocklevel", new NumberFieldImpl().setNumber(3));
		nodeRequest.getFields().put("price", new NumberFieldImpl().setNumber(6500));
		nodeRequest.getFields().put("SKU", new NumberFieldImpl().setNumber(42));
		nodeRequest.getFields().put("vehicleImage", new NodeFieldImpl().setUuid(imageNode.getUuid()));
		NodeResponse node = client.createNode(PROJECT_NAME, nodeRequest, new NodeParametersImpl().setResolveLinks(LinkType.FULL)).toSingle()
			.blockingGet();
		System.out.println("Created node with path: " + node.getPath());
	}
}
