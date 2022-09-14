package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Component
@Aspect
@RequiredArgsConstructor
public class RequestTaskVisitedAspect {

    private final RequestTaskVisitService requestTaskVisitService;

    @AfterReturning("execution(* uk.gov.pmrv.api.workflow.request.application.taskview.RequestTaskViewService.getTaskItemInfo(..))")
    void createRequestTaskVisitAfterGetTaskItemInfo(JoinPoint joinPoint) {
        Long taskId = (Long) joinPoint.getArgs()[0];
        PmrvUser pmrvUser = (PmrvUser) joinPoint.getArgs()[1];
        requestTaskVisitService.create(taskId, pmrvUser.getUserId());
    }
}
