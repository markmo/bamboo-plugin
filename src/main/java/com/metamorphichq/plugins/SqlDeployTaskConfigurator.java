package com.metamorphichq.plugins;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.struts.TextProvider;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by markmo on 28/11/2014.
 */
public class SqlDeployTaskConfigurator extends AbstractTaskConfigurator {

    private TextProvider textProvider;

    public Map<String, String> generateTaskConfigMap(final ActionParametersMap params,
                                                     final TaskDefinition previousTaskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put("jdbcConn", params.getString("jdbcConn"));
        config.put("uid", params.getString("uid"));
        config.put("pwd", params.getString("pwd"));
        config.put("path", params.getString("path"));
        config.put("sql", params.getString("sql"));
        return config;
    }

    public void populateContextForCreate(final Map<String, Object> context) {
        super.populateContextForCreate(context);
        context.put("jdbcConn", "jdbc:teradata://10.66.15.10/CAAIL");
        context.put("uid", "");
        context.put("pwd", "");
        context.put("path", "");
    }

    public void populateContextForEdit(final Map<String, Object> context,
                                       final TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        context.put("jdbcConn", taskDefinition.getConfiguration().get("jdbcConn"));
        context.put("uid", taskDefinition.getConfiguration().get("uid"));
        context.put("pwd", taskDefinition.getConfiguration().get("pwd"));
        context.put("path", taskDefinition.getConfiguration().get("path"));
        context.put("sql", taskDefinition.getConfiguration().get("sql"));
    }

    public void populateContextForView(final Map<String, Object> context,
                                       final TaskDefinition taskDefinition) {
        super.populateContextForView(context, taskDefinition);
        context.put("jdbcConn", taskDefinition.getConfiguration().get("jdbcConn"));
        context.put("uid", taskDefinition.getConfiguration().get("uid"));
        context.put("pwd", taskDefinition.getConfiguration().get("pwd"));
        context.put("path", taskDefinition.getConfiguration().get("path"));
        context.put("sql", taskDefinition.getConfiguration().get("sql"));
    }

    public void validate(final ActionParametersMap params,
                         final ErrorCollection errorCollection) {
        super.validate(params, errorCollection);
        final String path = params.getString("path");
        final String sql = params.getString("sql");
        if (StringUtils.isEmpty(path) && StringUtils.isEmpty(sql)) {
            errorCollection.addError("path", textProvider.getText("sqldeploy.invalid.location"));
        }
    }

    public void setTextProvider(final TextProvider textProvider) {
        this.textProvider = textProvider;
    }
}
