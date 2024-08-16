package com.amido.stacks.tests.api;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("/cucumber/features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "io.cucumber.core.plugin.SerenityReporterParallel,pretty,timeline:target/test-results/timeline,html:target/cucumber.html")
public class CucumberTestSuite {}
