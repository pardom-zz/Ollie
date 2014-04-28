package com.example.ollie.content;

import ollie.OllieProvider;

public class OllieSampleProvider extends OllieProvider {
	@Override
	protected String getDatabaseName() {
		return "OllieSample.db";
	}

	@Override
	protected int getDatabaseVersion() {
		return 1;
	}
}
