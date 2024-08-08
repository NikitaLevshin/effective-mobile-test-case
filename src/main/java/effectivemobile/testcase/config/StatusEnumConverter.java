package effectivemobile.testcase.config;

import effectivemobile.testcase.task.model.TaskStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StatusEnumConverter implements Converter<String, TaskStatus> {

    @Override
    public TaskStatus convert(String source) {
        return TaskStatus.valueOf(source.toUpperCase());
    }
}
