package com.kksk.sample.service;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ApplicationScoped
public class SampleService {
	final Log LOG = LogFactory.getLog(SampleService.class);

	public SampleService() {
		LOG.info("created.");
	}

	public void exec() {
		LOG.info("fired.");
	}
}
