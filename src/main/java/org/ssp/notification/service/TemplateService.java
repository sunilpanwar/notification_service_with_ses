package org.ssp.notification.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssp.notification.entity.Template;
import org.ssp.notification.repository.TemplateRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    @Autowired
    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    public Optional<Template> getTemplateById(Integer id) {
        return templateRepository.findById(id);
    }

    public Template saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    public void deleteTemplateById(Integer id) {
        templateRepository.deleteById(id);
    }

    public Template findByStatus(boolean status) {
        return templateRepository.findByStatus(status);
    }
}