package it.onchain.javaballot.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class BallotApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();

	public Set<Object> getSingletons() {
		if (singletons.isEmpty()) {
			singletons.add(new BallotResource());
		}
		return singletons;
	}
}
