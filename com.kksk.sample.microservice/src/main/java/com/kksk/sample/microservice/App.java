/*
 * Copyright 2017 Tadasuke Maruno
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.kksk.sample.microservice;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * 
 */
@ApplicationPath("/")
public class App extends ResourceConfig {
	/**
	 * 
	 */
	public App() {
		packages(true, "com.kksk.sample");
	}
}
