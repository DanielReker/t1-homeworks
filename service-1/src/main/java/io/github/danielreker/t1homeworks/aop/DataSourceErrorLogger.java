package io.github.danielreker.t1homeworks.aop;

import io.github.danielreker.t1homeworks.service.DataSourceErrorLogService;
import io.github.danielreker.t1homeworks.model.dto.DataSourceErrorLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DataSourceErrorLogger {
    final private DataSourceErrorLogService logService;

    @AfterThrowing(
            pointcut = "@annotation(io.github.danielreker.t1homeworks.aop.annotation.LogDataSourceError)",
            throwing = "e"
    )
    public void logDataSourceError(JoinPoint joinPoint, Throwable e) {
        try {
            StringWriter stacktraceStringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stacktraceStringWriter));
            String stacktraceString = stacktraceStringWriter.toString();

            logService.logDataSourceError(DataSourceErrorLogDto.builder()
                    .message(e.getMessage())
                    .stacktrace(stacktraceString)
                    .methodSignature(joinPoint.getSignature().toLongString())
                    .build()
            );
        } catch (Throwable ex) {
            log.error("Failed to log data source error: {}", ex.getMessage());
        }
    }
}
