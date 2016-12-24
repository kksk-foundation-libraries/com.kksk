package com.kksk.sample.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.kksk.sample.service.SampleService;

@Produces(value = MediaType.APPLICATION_JSON)
@Consumes(value = MediaType.APPLICATION_JSON)
@Path("sample")
@RequestScoped
public class SampleResource {
	@Inject
	SampleService sampleService;

	public SampleResource() {
	}

	@GET()
	public Response getAll() {
		Map<String, List<Map<String, String>>> resp = new HashMap<>();
		List<Map<String, String>> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("id", "" + i);
			map.put("name", "id" + i);
			list.add(map);
		}
		resp.put("list", list);
		sampleService.exec();
		return Response.ok(resp).build();
	}

//	@GET()
//	@Path("sample/{id}")
//	public Response getById(@PathParam("id") String id) {
//		Map<String, String> resp = new HashMap<>();
//		resp.put("id", id);
//		resp.put("name", "id" + id);
//		return Response.ok(resp).build();
//	}
}
