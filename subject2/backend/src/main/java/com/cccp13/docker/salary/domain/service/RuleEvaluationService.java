package com.cccp13.docker.salary.domain.service;

import com.cccp13.docker.salary.domain.model.Assignment;
import com.cccp13.docker.salary.domain.model.SalaryCalculation;
import com.cccp13.docker.salary.domain.model.SalaryRule;
import com.cccp13.docker.salary.domain.repository.SalaryRuleRepository;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleEvaluationService {
    private final KieContainer kieContainer;
    @Autowired
    private final SalaryRuleRepository salaryRuleRepository;

    @Cacheable(value = "applicableRules", key = "#salaryCalculation?.id ?: T(java.util.UUID).randomUUID().toString()")
    public List<SalaryRule> evaluateRules(SalaryCalculation salaryCalculation) {
        try (KieSession kieSession = kieContainer.newKieSession()) {

            Assignment assignment = salaryCalculation.getAssignment();

            // Insert all relevant objects into Drools
            kieSession.insert(salaryCalculation);
            kieSession.insert(assignment.getDocker());
            kieSession.insert(assignment.getWorkShift());
            kieSession.insert(assignment.getWorkSite());
            kieSession.insert(assignment.getCargoType());

            List<SalaryRule> rulesList = new ArrayList<>();
            kieSession.setGlobal("rules", rulesList); // Ensure the global is not null

            kieSession.fireAllRules();

            kieSession.dispose();

            // **💡 Persist SalaryRule instances before saving AppliedRule**
            List<SalaryRule> persistedRules = new ArrayList<>();
            for (SalaryRule rule : rulesList) {
                SalaryRule savedRule = salaryRuleRepository.save(rule); // Save rule to DB
                persistedRules.add(savedRule);
            }

            // Convert to AppliedRules
            List<SalaryCalculation.AppliedRule> appliedRules = new ArrayList<>();
            for (SalaryRule savedRule : persistedRules) {
                appliedRules.add(new SalaryCalculation.AppliedRule(
                        null, salaryCalculation, savedRule, savedRule.getBonusAmount(), savedRule.getName()
                ));
            }

            salaryCalculation.setAppliedRules(appliedRules);

            return persistedRules;
        }
    }

}
