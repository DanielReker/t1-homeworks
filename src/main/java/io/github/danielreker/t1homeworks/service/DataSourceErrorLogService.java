package io.github.danielreker.t1homeworks.service;

import io.github.danielreker.t1homeworks.model.DataSourceErrorLog;
import io.github.danielreker.t1homeworks.repository.DataSourceErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DataSourceErrorLogService {
    private final DataSourceErrorLogRepository repository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDataSourceError(DataSourceErrorLog dataSourceErrorLog) {
        repository.save(dataSourceErrorLog);
    }
}
