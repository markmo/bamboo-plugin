package com.metamorphichq.plugins;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TestThatTaskConfigurator extends AbstractTaskConfigurator {

    private TextProvider textProvider;

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params,
                                                     @Nullable final TaskDefinition previousTaskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put("rscriptPath", params.getString("rscriptPath"));
        config.put("isTestThatFile", params.getString("isTestThatFile"));
        config.put("testPath", params.getString("testPath"));
        config.put("workingDirPath", params.getString("workingDirPath"));

        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context) {
        super.populateContextForCreate(context);
        context.put("rscriptPath", "/usr/local/bin");
        context.put("isTestThatFile", false);
        context.put("testPath", "tests");
        context.put("workingDirPath", "");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context,
                                       @NotNull final TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        context.put("rscriptPath", taskDefinition.getConfiguration().get("rscriptPath"));
        context.put("isTestThatFile", taskDefinition.getConfiguration().get("isTestThatFile"));
        context.put("testPath", taskDefinition.getConfiguration().get("testPath"));
        context.put("workingDirPath", taskDefinition.getConfiguration().get("workingDirPath"));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context,
                                       @NotNull final TaskDefinition taskDefinition) {
        super.populateContextForView(context, taskDefinition);
        context.put("rscriptPath", taskDefinition.getConfiguration().get("rscriptPath"));
        context.put("isTestThatFile", taskDefinition.getConfiguration().get("isTestThatFile"));
        context.put("testPath", taskDefinition.getConfiguration().get("testPath"));
        context.put("workingDirPath", taskDefinition.getConfiguration().get("workingDirPath"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection) {
        super.validate(params, errorCollection);
        final String rscriptPath = params.getString("rscriptPath");
        final String testPath = params.getString("testPath");
        if (StringUtils.isEmpty(rscriptPath)) {
            errorCollection.addError("rscriptPath", textProvider.getText("btt.invalid.location"));
        }
        if (StringUtils.isEmpty(testPath)) {
            errorCollection.addError("testPath", textProvider.getText("btt.invalid.location"));
        }
    }

    public void setTextProvider(final TextProvider textProvider) {
        this.textProvider = textProvider;
    }
}