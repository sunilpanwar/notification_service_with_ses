package org.ssp.notification.repository;

import org.ssp.notification.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {

    public Template findByStatus(boolean status);
}