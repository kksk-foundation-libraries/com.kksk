package com.kksk.sample.web;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kksk.sample.service.SampleService;

@ApplicationPath("/")
public class SampleApp extends Application {
	@Inject
	SampleService sampleService;
	final Log LOG = LogFactory.getLog(SampleApp.class);

	public SampleApp() {
		LOG.info("created.");
	}
}
