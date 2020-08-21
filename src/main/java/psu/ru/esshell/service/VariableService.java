package psu.ru.esshell.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.enums.KindVariablesEnum;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.VariablePojo;
import psu.ru.esshell.repository.DomainRepository;
import psu.ru.esshell.repository.VariableRepository;

import java.util.List;

@Service
@Transactional
public class VariableService {

    private final VariableRepository variableRepository;

    private final DomainRepository domainRepository;

    public VariableService(VariableRepository variableRepository, DomainRepository domainRepository) {
        this.variableRepository = variableRepository;
        this.domainRepository = domainRepository;
    }

    public VariablePojo getVariableById(Long id) {
        VariablePojo variablePojo = variableRepository.getById(id);
        variablePojo.setDomain(domainRepository.getDomainById(variablePojo.getDomainId()));
        variablePojo.setKindName(KindVariablesEnum.getNameByLiteral(variablePojo.getKind()));
        variablePojo.setDomainName(variablePojo.getDomain().getName());

        return variablePojo;
    }

    public void add(VariablePojo variablePojo) {
        variableRepository.insert(variablePojo);
    }

    public void save(VariablePojo variablePojo) {
        variableRepository.update(variablePojo);
    }

    public void remove(Long id) {
        variableRepository.delete(id);
    }

    public Integer getCount(Long esId) {
        return variableRepository.getCount(esId);
    }

    public List<VariablePojo> getList(Long esId) {
        List<VariablePojo> variablePojos = variableRepository.getList(esId);
        addOtherParams(variablePojos);

        return variablePojos;
    }

    public List<VariablePojo> getOnlyOutputVariables(Long esId) {
        List<VariablePojo> variablePojos = variableRepository.getOnlyOutputVariables(esId);
        addOtherParams(variablePojos);

        return variablePojos;
    }

    private void addOtherParams(List<VariablePojo> variablePojos) {
        for (VariablePojo variablePojo : variablePojos) {
            variablePojo.setDomain(domainRepository.getDomainById(variablePojo.getDomainId()));
            variablePojo.setDomainName(variablePojo.getDomain().getName());
            variablePojo.setKindName(KindVariablesEnum.getNameByLiteral(variablePojo.getKind()));
        }
    }

    public List<DomainPojo> getListDomains(Long esId) {
        return variableRepository.getListDomains(esId);
    }

    public void updateOrders(Long esId) {
        variableRepository.updateOrders(esId);
    }

    // Валидация
    public boolean checkName(String name, Long esId) {
        return variableRepository.checkName(name, esId);
    }

    public void updateOrdersAfterDragAndDrop(List<VariablePojo> variables) {
        variableRepository.updateOrdersAfterDragAndDrop(variables);
    }

    public VariablePojo getLastInsertedVariable(Long esId) {
        return variableRepository.getLastInsertedVariable(esId);
    }
}
