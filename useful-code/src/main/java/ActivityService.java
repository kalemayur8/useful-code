
public class ActivityService {
	
	@Override
    @Loggable(serviceId = "ACP:BSAD:BS:ActivityServicesImpl:getAudits", auditServiceReturnValues = false)
    public AuditRecordRespose getAudits(SearchCriteria searchCriteria) throws AuditServiceException {
        return filterActivitiesForAudit(searchCriteria);
    }
	
	
	@SuppressWarnings("deprecation")
    private AuditRecordRespose filterActivitiesForAudit(SearchCriteria searchCriteria) {
        AuditRecordRespose auditRecordRespose = new AuditRecordRespose();
        long totalCount = activityRegistryRepository
                .count(BussinessAuditSearchSpecification.applyActivityRegistryFilter(searchCriteria));
        auditRecordRespose.setTotalCount((int) totalCount);
        List<ActivityRegistry> actRegistries = null;
        if (totalCount > 0) {
            if (searchCriteria.isExport()) {
                actRegistries = activityRegistryRepository
                        .findAll(BussinessAuditSearchSpecification.applyActivityRegistryFilter(searchCriteria));
            } else {
                auditRecordRespose.setCurrentPage(searchCriteria.getCurrentPage());
                auditRecordRespose.setPageSize(searchCriteria.getPageSize());
                actRegistries = activityRegistryRepository
                        .findAll(BussinessAuditSearchSpecification.applyActivityRegistryFilter(searchCriteria),
                                new PageRequest(searchCriteria.getCurrentPage(), searchCriteria.getPageSize()))
                        .getContent();
            }
            if (StringUtils.isNotEmpty(searchCriteria.getParameterName())) {
                actRegistries.stream()
                        .forEach(activityReg -> activityReg.getActivityRegistryDetails().removeIf(regDet -> !regDet
                                .getActivityParamMaster().getName().equals(searchCriteria.getParameterName())));
            }
            actRegistries.forEach(
                    activityRegistry -> activityRegistry.setAdditionalDetails(getAdditionalDetails(activityRegistry)));

            List<AuditRecordTO> auditRecords = new ArrayList<>();
            actRegistries.forEach(activityReg -> 
                activityReg.getActivityRegistryDetails().forEach(acRegDet -> 
                    auditRecords.add(AuditActivityTransformer.toAuditRecordTO(activityReg, acRegDet))
                )
            );
            auditRecordRespose.setAuditRecords(auditRecords);
        }

        return auditRecordRespose;
    }

	
}
