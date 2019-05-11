/*
* Copyright (c) 2019 The Emirates Group.
* All Rights Reserved.
* 
 * The information specified here is confidential and remains property of the Emirates Group.
*/


import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.audit.model.entity.ActivityMaster;
import com.audit.model.entity.ActivityParamMaster;
import com.audit.model.entity.ActivityRegistry;
import com.audit.model.entity.ActivityRegistryDetail;
import com.audit.util.AuditConstants;
import com.audit.util.DateFormatUtil;
import com.dto.auditactivity.SearchCriteria;
import com.dto.auditactivity.TimeRange;

/**
 * @author S717335
 *
 */
public class BussinessAuditSearchSpecification {

    /**
     * Function to apply activity registry filters
     * 
     * @param role
     * @param staff
     * @param flt
     * @param customerRef
     * @param auditRangeTo
     * @param auditRangeFrom
     * @param flightRangeTo
     * @param flightRangeFrom
     * @return
     */
    public static Specification<ActivityRegistry> applyActivityRegistryFilter(SearchCriteria searchCriteria) {
        return new Specification<ActivityRegistry>() {
            /**
             * 
             */
            private static final long serialVersionUID = 7666355347615478324L;

            @Override
            public Predicate toPredicate(Root<ActivityRegistry> root, CriteriaQuery<?> criteria,
                    CriteriaBuilder builder) {
                List<Predicate> predicates = getActivityRegistryPredicate(searchCriteria, root, criteria, builder);
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

        };
    }

    /**
     * Gets the activity registry predicate.
     *
     * @param searchCriteria
     *            the search criteria
     * @param root
     *            the root
     * @param criteria
     *            the criteria
     * @param builder
     *            the builder
     * @return the activity registry predicate
     */
    public static List<Predicate> getActivityRegistryPredicate(SearchCriteria searchCriteria,
            Root<ActivityRegistry> root, CriteriaQuery<?> criteria, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();
        String role = searchCriteria.getRole();
        String flt = searchCriteria.getFlightNumber();
        String staff = searchCriteria.getStaffNumber();
        String customerRef = searchCriteria.getCustomerRefNumber();
        TimeRange flightRange = searchCriteria.getFlightDateRange();
        TimeRange auditTime = searchCriteria.getAuditTimeRange();
        String flightRangeTo = null;
        String flightRangeFrom = null;
        String auditRangeFrom = null;
        String auditRangeTo = null;
        if (flightRange != null) {
            flightRangeTo = flightRange.getTo();
            flightRangeFrom = flightRange.getFrom();
        }

        if (auditTime != null) {
            auditRangeFrom = auditTime.getFrom();
            auditRangeTo = auditTime.getTo();
        }

        String groupName = searchCriteria.getGroupName();
        String activityName = searchCriteria.getActivityName();
        String boardPoint = searchCriteria.getBoardPoint();

        addConditionsToPredicate(root, builder, predicates, role, flt, staff, customerRef, flightRangeTo,
                flightRangeFrom, boardPoint);

        addActivityConditions(root, builder, predicates, auditRangeFrom, auditRangeTo, groupName, activityName);
        // To ignore the Catering records audited from ACP side after PD, to
        // avoid duplicate records as after PD both ACP and MACS
        // catering records will be there in DB
        predicates.add(
                builder.or(builder.notEqual(root.get(AuditConstants.SHOW_AUDIT_DETAILS), AuditConstants.NOT_ELIGIBLE),
                        root.get(AuditConstants.SHOW_AUDIT_DETAILS).isNull()));
        criteria.orderBy(builder.desc(root.get(AuditConstants.EXECUTION_DATE_UTC)),
                builder.desc(root.get("activityRegId")));
        return predicates;
    }

    private static void addActivityConditions(Root<ActivityRegistry> root, CriteriaBuilder builder,
            List<Predicate> predicates, String auditRangeFrom, String auditRangeTo, String groupName,
            String activityName) {
        if (StringUtils.isNotEmpty(groupName))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_MASTER).get(AuditConstants.TYPE), groupName));
            }
        if (StringUtils.isNotEmpty(activityName))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_MASTER).get(AuditConstants.NAME), activityName));
            }
        if (StringUtils.isNotEmpty(auditRangeFrom) && StringUtils.isNotEmpty(auditRangeTo)) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(AuditConstants.EXECUTION_DATE_UTC),
                    DateFormatUtil.getTypeTwoDate(auditRangeFrom)));
            predicates.add(builder.lessThanOrEqualTo(root.get(AuditConstants.EXECUTION_DATE_UTC),
                    DateFormatUtil.addDays(DateFormatUtil.getTypeTwoDate(auditRangeTo), 1)));
        }
    }

    private static void addConditionsToPredicate(Root<ActivityRegistry> root, CriteriaBuilder builder,
            List<Predicate> predicates, String role, String flt, String staff, String customerRef, String flightRangeTo,
            String flightRangeFrom, String boardPoint) {
        if (StringUtils.isNotEmpty(role))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ROLE), role));
            }
        if (StringUtils.isNotEmpty(boardPoint))
            {
                predicates.add(builder.equal(root.get(AuditConstants.FLIGHT_BOARD_POINT), boardPoint));
            }
        if (StringUtils.isNotEmpty(flt))
            {
                predicates.add(builder.equal(builder.concat(root.get("carrierCode"), root.get("fltNumber")), flt));
            }
        if (StringUtils.isNotEmpty(staff))
            {
                predicates.add(builder.equal(root.get(AuditConstants.AGENT_ID), staff));
            }
        if (StringUtils.isNotEmpty(customerRef))
            {
                predicates.add(builder.equal(root.get(AuditConstants.PAX_REF_NO), customerRef));
            }
        if (StringUtils.isNotEmpty(flightRangeFrom) && StringUtils.isNotEmpty(flightRangeTo)) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(AuditConstants.FLT_DATE),
                    DateFormatUtil.getTypeTwoDate(flightRangeFrom)));
            predicates.add(builder.lessThanOrEqualTo(root.get(AuditConstants.FLT_DATE),
                    DateFormatUtil.getTypeTwoDate(flightRangeTo)));
        }
    }

    public static Specification<ActivityMaster> applyActivityMasterFilter(String groupName, String activityName) {
        return new Specification<ActivityMaster>() {
            /**
             * 
             */
            private static final long serialVersionUID = 8492472070193945958L;

            @Override
            public Predicate toPredicate(Root<ActivityMaster> root, CriteriaQuery<?> criteria,
                    CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<>();
                if (StringUtils.isNotEmpty(groupName))
                    {
                        predicates.add(builder.equal(root.get(AuditConstants.TYPE), groupName));
                    }
                if (StringUtils.isNotEmpty(activityName))
                    {
                        predicates.add(builder.equal(root.get(AuditConstants.NAME), activityName));
                    }
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    /**
     * Apply activity param master filter.
     *
     * @param activityMaster
     *            the activity master
     * @param paramName
     *            the param name
     * @return the specification
     */
    public static Specification<ActivityParamMaster> applyActivityParamMasterFilter(ActivityMaster activityMaster,
            String paramName) {
        return new Specification<ActivityParamMaster>() {
            /**
             * 
             */
            private static final long serialVersionUID = -7086893569354678995L;

            @Override
            public Predicate toPredicate(Root<ActivityParamMaster> root, CriteriaQuery<?> criteria,
                    CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_MASTER), activityMaster));
                predicates.add(builder.equal(root.get(AuditConstants.NAME), paramName));
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    /**
     * Apply registry details filter.
     *
     * @param searchCriteria
     *            the search criteria
     * @return the specification
     */
    public static Specification<ActivityRegistryDetail> applyRegistryDetailsFilter(SearchCriteria searchCriteria) {
        return new Specification<ActivityRegistryDetail>() {
            /**
             * 
             */
            private static final long serialVersionUID = -334175852021099552L;

            @Override
            public Predicate toPredicate(Root<ActivityRegistryDetail> root, CriteriaQuery<?> criteria,
                    CriteriaBuilder builder) {
                List<Predicate> predicates = getActivityRegistryDetlPredicate(searchCriteria, root, criteria, builder);
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

        };
    }

    /**
     * Gets the activity registry detl predicate.
     *
     * @param searchCriteria
     *            the search criteria
     * @param root
     *            the root
     * @param criteria
     *            the criteria
     * @param builder
     *            the builder
     * @return the activity registry detl predicate
     */
    public static List<Predicate> getActivityRegistryDetlPredicate(SearchCriteria searchCriteria,
            Root<ActivityRegistryDetail> root, CriteriaQuery<?> criteria, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();
        String role = searchCriteria.getRole();
        String flt = searchCriteria.getFlightNumber();
        // so That user can search with carrier code as well
        if (StringUtils.isNotEmpty(flt) && flt.contains(AuditConstants.CARRIER_CODE))
            {
                flt = flt.replace(AuditConstants.CARRIER_CODE, "");
            }
        String staff = searchCriteria.getStaffNumber();
        String boardPoint = searchCriteria.getBoardPoint();
        String customerRef = searchCriteria.getCustomerRefNumber();
        TimeRange flightRange = searchCriteria.getFlightDateRange();
        TimeRange auditTime = searchCriteria.getAuditTimeRange();
        String flightRangeTo = flightRange != null ? flightRange.getTo() : null;
        String flightRangeFrom = flightRange != null ? flightRange.getFrom() : null;
        String auditRangeFrom = auditTime != null ? auditTime.getFrom() : null;
        String auditRangeTo = auditTime != null ? auditTime.getTo() : null;
        String groupName = searchCriteria.getGroupName();
        String activityName = searchCriteria.getActivityName();
        String parameterName = searchCriteria.getParameterName();

        addConditions(root, builder, predicates, role, flt, staff, boardPoint, customerRef, flightRangeTo,
                flightRangeFrom, auditRangeFrom, auditRangeTo, groupName, activityName, parameterName);
        criteria.orderBy(builder.desc(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.EXECUTION_DATE)));
        return predicates;
    }

    private static void addConditions(Root<ActivityRegistryDetail> root, CriteriaBuilder builder,
            List<Predicate> predicates, String role, String flt, String staff, String boardPoint, String customerRef,
            String flightRangeTo, String flightRangeFrom, String auditRangeFrom, String auditRangeTo, String groupName,
            String activityName, String parameterName) {
        if (StringUtils.isNotEmpty(role))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.ROLE), role));
            }
        if (StringUtils.isNotEmpty(flt))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.FLT_NUMBER), flt));
            }
        if (StringUtils.isNotEmpty(boardPoint))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.FLIGHT_BOARD_POINT), boardPoint));
            }
        if (StringUtils.isNotEmpty(staff))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.AGENT_ID), staff));
            }
        if (StringUtils.isNotEmpty(customerRef))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.PAX_REF_NO), customerRef));
            }
        addActivityParams(root, builder, predicates, flightRangeTo, flightRangeFrom, auditRangeFrom, auditRangeTo,
                groupName, activityName, parameterName);
    }

    private static void addActivityParams(Root<ActivityRegistryDetail> root, CriteriaBuilder builder,
            List<Predicate> predicates, String flightRangeTo, String flightRangeFrom, String auditRangeFrom,
            String auditRangeTo, String groupName, String activityName, String parameterName) {
        if (StringUtils.isNotEmpty(groupName))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.ACTIVITY_MASTER).get(AuditConstants.TYPE), groupName));
            }
        if (StringUtils.isNotEmpty(activityName))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.ACTIVITY_MASTER).get(AuditConstants.NAME), activityName));
            }
        if (StringUtils.isNotEmpty(parameterName))
            {
                predicates.add(builder.equal(root.get(AuditConstants.ACTIVITY_PARAM_MASTER).get(AuditConstants.NAME), parameterName));
            }
        if (StringUtils.isNotEmpty(flightRangeFrom) && StringUtils.isNotEmpty(flightRangeTo)) {
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.FLT_DATE),
                    DateFormatUtil.getTypeTwoDate(flightRangeFrom)));
            predicates.add(
                    builder.lessThanOrEqualTo(root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.FLT_DATE),
                            DateFormatUtil.getTypeTwoDate(flightRangeTo)));
        }
        if (StringUtils.isNotEmpty(auditRangeFrom) && StringUtils.isNotEmpty(auditRangeTo)) {
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.EXECUTION_DATE),
                    DateFormatUtil.getTypeTwoDate(auditRangeFrom)));
            predicates.add(builder.lessThanOrEqualTo(
                    root.get(AuditConstants.ACTIVITY_REGISTRY).get(AuditConstants.EXECUTION_DATE),
                    DateFormatUtil.addDays(DateFormatUtil.getTypeTwoDate(auditRangeTo), 1)));
        }
    }

}
