package com.metamorphichq.plugins;

import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.build.test.TestCollectionResultBuilder;
import com.atlassian.bamboo.build.test.TestReportCollector;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.StatusValues;

import java.io.File;
import java.util.Collection;
import java.util.Set;

/**
 * Created by markmo on 27/11/2014.
 */
public class TestThatReportCollector implements TestReportCollector {

    public TestCollectionResult collect(File file) throws Exception {

        TestCollectionResultBuilder builder = new TestCollectionResultBuilder();

        Collection<TestResults> successfulTestResults = Lists.newArrayList();
        Collection<TestResults> failingTestResults = Lists.newArrayList();

        TapConsumer consumer = TapConsumerFactory.makeTap13Consumer();
        TestSet testSet = consumer.load(file);
        String filename = file.getName();

        for (TestResult testResult : testSet.getTestResults()) {
            String suiteName = filename.substring(0, filename.lastIndexOf('.'));
            String testName = testResult.getDescription();

            TestResults testResults = new TestResults(suiteName, testName, "0");
            if (testResult.getStatus().equals(StatusValues.OK)) {
                testResults.setState(TestState.SUCCESS);
                successfulTestResults.add(testResults);
            } else {
                testResults.setState(TestState.FAILED);
                failingTestResults.add(testResults);
            }
        }

        return builder
                .addSuccessfulTestResults(successfulTestResults)
                .addFailedTestResults(failingTestResults)
                .build();
    }

    public Set<String> getSupportedFileExtensions() {
        return Sets.newHashSet("tap"); // this will collect all *.tap files
    }
}