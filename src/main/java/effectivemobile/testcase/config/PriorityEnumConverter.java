package effectivemobile.testcase.config;

import effectivemobile.testcase.task.model.TaskPriority;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PriorityEnumConverter implements Converter<String, TaskPriority> {

    @Override
    public TaskPriority convert(String source) {
        return TaskPriority.valueOf(source.toUpperCase());
    }
}
