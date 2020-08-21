package psu.ru.esshell.service;

import org.jooq.util.maven.example.tables.pojos.DomainValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.repository.DomainRepository;

import java.util.List;

@Service
@Transactional
public class DomainService {

    private final DomainRepository domainRepository;

    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public DomainPojo getDomainById(Long id) {
        return domainRepository.getDomainById(id);
    }

    public void addDomain(DomainPojo domainPojo) {
        domainRepository.insert(domainPojo);
    }

    public void saveDomain(DomainPojo domainPojo) {
        domainRepository.update(domainPojo);
    }

    public void removeDomain(Long id) {
        domainRepository.delete(id);
    }

    public Integer getCountDomains(Long esId) {
        return domainRepository.getCount(esId);
    }

    public void updateOrders(Long esId) {
        domainRepository.updateOrders(esId);
    }

    public List<DomainPojo> getList(Long esId) {
        List<DomainPojo> domainPojos = domainRepository.getList(esId);
        for (DomainPojo domainPojo : domainPojos) {
            domainPojo.setValues(domainRepository.getValues(domainPojo.getId()));
        }

        return domainPojos;
    }

    public List<DomainValue> getDomainValues(Long domainId) {
        return domainRepository.getValues(domainId);
    }

    public DomainValue getDomainValueById(Long domainValueId) {
        return domainRepository.getDomainValueById(domainValueId);
    }

    // Валидация
    public boolean checkName(String name, Long esId) {
        return domainRepository.checkName(name, esId);
    }

    public boolean checkValue(String name, Long domainId) {
        return domainRepository.checkValue(name, domainId);
    }

    public String getNameVariableByDomainId(Long domainId) {
        return domainRepository.getNameVariableByDomainId(domainId);
    }

    public void updateOrdersAfterDragAndDrop(List<DomainPojo> domains) {
        domainRepository.updateOrdersAfterDragAndDrop(domains);
    }
}
