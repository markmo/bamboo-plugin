package com.metamorphichq.plugins;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class TestThatTask implements TaskType {

    private final TestCollationService testCollationService;

    public TestThatTask(final TestCollationService testCollationService) {
        this.testCollationService = testCollationService;
    }

    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException {

        final BuildLogger buildLogger = taskContext.getBuildLogger();

        final String rscriptPath = taskContext.getConfigurationMap().get("rscriptPath");
        buildLogger.addBuildLogEntry("RScript path: " + rscriptPath);

        final boolean isTestThatFile = taskContext.getConfigurationMap().getAsBoolean("isTestThatFile");
        buildLogger.addBuildLogEntry("TestThat file? " + isTestThatFile);

        String testPath = taskContext.getConfigurationMap().get("testPath");
        buildLogger.addBuildLogEntry("Test path: " + testPath);

        String workingDirPath = taskContext.getConfigurationMap().get("workingDirPath");
        buildLogger.addBuildLogEntry("Working Dir path: " + workingDirPath);

        TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);

        String temp = String.format("testresults%s.tap", System.currentTimeMillis());
        String resultsPath = taskContext.getWorkingDirectory() + "/" + temp;
        buildLogger.addBuildLogEntry("Results path: " + resultsPath);

        if (!testPath.startsWith("/")) {
            testPath = taskContext.getWorkingDirectory() + "/" + testPath;
        }
        boolean isFile = testPath.endsWith(".R");
        boolean isRegularScript = isFile && !isTestThatFile;

        String content = null;
        if (!isRegularScript) {
            String setwdCommand = "";
            if (!workingDirPath.isEmpty()) {
                setwdCommand = String.format("setwd(\"%s\");", workingDirPath);
            }
            if (isFile) {
                content = String.format("%stest_file(\"%s\", reporter=\"Tap\")", setwdCommand, testPath);
            } else {
                content = String.format("%stest_dir(\"%s\", reporter=\"Tap\")", setwdCommand, testPath);
            }
            buildLogger.addBuildLogEntry(content);
        }

        // Executing an expression doesn't produce any output
        //String command = String.format("%s/RScript --default-packages=methods,testthat -e 'test_dir(\"%s\", reporter=\"Tap\")'",
        //        rscriptPath, testPath);

        try {
            String testRunnerPath = null;
            String command;
            if (isRegularScript) {
                command = String.format("%s/RScript --default-packages=methods,testthat %s", rscriptPath, testPath);
            } else {
                temp = String.format("testrunner%s.R", System.currentTimeMillis());
                testRunnerPath = taskContext.getWorkingDirectory() + "/" + temp;
                buildLogger.addBuildLogEntry("Test runner path: " + testRunnerPath);

                PrintWriter out = new PrintWriter(testRunnerPath);
                out.println(content);
                out.close();

                command = String.format("%s/RScript --default-packages=methods,testthat %s", rscriptPath, testRunnerPath);
            }
            buildLogger.addBuildLogEntry(command);

            Process p = Runtime.getRuntime().getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;

            while ((line = stdError.readLine()) != null) {
                buildLogger.addBuildLogEntry(line);
            }

            FileWriter fileWriter = new FileWriter(resultsPath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Sometimes the task is failing because the results are being
            // written before/as the task starts
            Thread.sleep(1000);     // wait one second

            buildLogger.addBuildLogEntry("Printing output...");
            while ((line = stdInput.readLine()) != null) {
                buildLogger.addBuildLogEntry(line);
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

            testCollationService.collateTestResults(taskContext, "*.tap", new TestThatReportCollector());

            if (!isRegularScript) {
                File testRunner = new File(testRunnerPath);
                testRunner.delete();
            }
            File results = new File(resultsPath);
            results.delete();

        } catch (IOException e) {
            buildLogger.addBuildLogEntry(e.getMessage());
            e.printStackTrace();
            throw new TaskException(e.getMessage(), e);
        } catch (InterruptedException ex) {
            buildLogger.addBuildLogEntry(ex.getMessage());
            ex.printStackTrace();
            throw new TaskException(ex.getMessage(), ex);
        }

        return builder.checkTestFailures().build();
    }
}