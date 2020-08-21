package psu.ru.esshell.service;

import org.jooq.util.maven.example.tables.pojos.ReasonTree;
import org.jooq.util.maven.example.tables.pojos.WorkingMemory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.WorkingMemoryPojo;
import psu.ru.esshell.repository.ConsultationRepository;

import java.util.List;

@Service
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final VariableService variableService;
    private final DomainService domainService;
    private final FactService factService;

    public ConsultationService(ConsultationRepository consultationRepository, VariableService variableService, DomainService domainService, FactService factService) {
        this.consultationRepository = consultationRepository;
        this.variableService = variableService;
        this.domainService = domainService;
        this.factService = factService;
    }

    public void clear() {
        consultationRepository.clear();
    }

    public boolean variableExistsInMemory(Long id) {
        return consultationRepository.variableExistsInMemory(id);
    }

    public void insert(WorkingMemory item) {
        consultationRepository.insert(item);
    }

    public Long getDomainValueId(Long id) {
        return consultationRepository.getDomainValue(id);
    }

    public void insertIntoReasonTree(Long ruleId, Long parentRuleId) {
        consultationRepository.insertIntoReasonTree(ruleId, parentRuleId);
    }

    public List<ReasonTree> getTree() {
        return consultationRepository.getTree();
    }

    public List<WorkingMemoryPojo> getFacts() {
        return consultationRepository.getFacts();
    }

    public void removeWithRuleId(Long ruleId) {
        consultationRepository.removeWithRuleId(ruleId);
    }

    public void updateWithRuleId(Long ruleId, Long lvl) {
        consultationRepository.updateWithRuleId(ruleId, lvl);
    }

    public void updateOnlyWorkingMemory(Long ruleId) {
        consultationRepository.updateOnlyWorkingMemory(ruleId);
    }

}
