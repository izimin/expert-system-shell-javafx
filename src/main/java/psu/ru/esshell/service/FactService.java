package psu.ru.esshell.service;

import org.jooq.util.maven.example.tables.pojos.DomainValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.FactPojo;
import psu.ru.esshell.repository.DomainRepository;
import psu.ru.esshell.repository.FactRepository;

import java.util.List;

@Service
@Transactional
public class FactService {

    private final DomainRepository domainRepository;
    private final FactRepository factRepository;

    public FactService(DomainRepository domainRepository, FactRepository factRepository) {
        this.domainRepository = domainRepository;
        this.factRepository = factRepository;
    }

    public boolean checkExists(FactPojo factPojo) {
        return factRepository.checkExists(factPojo);
    }

    public Long insert(FactPojo factPojo) {
        return factRepository.insert(factPojo);
    }

    public Long getId(FactPojo factPojo) {
        return factRepository.getId(factPojo);
    }

    public FactPojo getById(Long id) {
        return factRepository.getById(id);
    }
}
