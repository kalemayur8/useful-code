/*
* Copyright (c) 2019 The Emirates Group.
* All Rights Reserved.
* 
 * The information specified here is confidential and remains property of the Emirates Group.
*/
package com.useful.activity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.emirates.audit.dto.capturehistory.rq.ActivityListTO;
import com.emirates.audit.exception.ActivityNotFoundException;
import com.emirates.audit.exception.AuditServiceException;
import com.emirates.audit.model.res.AuditResponseModel;
import com.emirates.audit.services.activity.ActivityService;
import com.emirates.audit.util.ErrorCodes.ERROR_CODE;
import com.emirates.dto.auditactivity.AuditRecordRespose;
import com.emirates.dto.auditactivity.FlightFilterTO;
import com.emirates.dto.auditactivity.SearchCriteria;
import com.emirates.egsframework.validation.exception.EGSValidationException;
import com.emirates.egsframework.web.core.EGSResponseErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ActivityResource class
 * 
 * @author S799967
 *
 */
@RestController
@RequestMapping("/app/rest")
public class ActivityResource {

    /** the auditsearchServices. */
    @Autowired
    private ActivityService activityServicesImpl;

    /** LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityResource.class);

    /**
     * This method returns list of groups and roles for Business audit based on
     * Audit type we pass.
     * 
     * @param type
     *            AUDIT_TYPE
     * @return
     * @throws AuditServiceException
     */
    @GetMapping(value = "/search/criteria/{type}")
    public ResponseEntity<FlightFilterTO> getSearchCriteria(@PathVariable("type") String type)
            throws AuditServiceException {
        FlightFilterTO flightAuditTO = activityServicesImpl.getSearchCriteria(type);
        return new ResponseEntity<>(flightAuditTO, HttpStatus.OK);
    }

    /**
     * This method returns list of Activities.
     * 
     * @param type
     *            group
     * @return
     * @throws AuditServiceException
     */
    @GetMapping(value = "/search/criteria/filter/{group}")
    public ResponseEntity<FlightFilterTO> filterSearchActivity(@PathVariable("group") String group)
            throws AuditServiceException {
        FlightFilterTO flightAuditTO = activityServicesImpl.filterSearchGroup(group, null);
        return new ResponseEntity<>(flightAuditTO, HttpStatus.OK);
    }

    /**
     * This method returns list of params.
     * 
     * @param type
     *            activity
     * @return
     * @throws AuditServiceException
     */
    @RequestMapping(value = "/search/criteria/filter/{group}/{activity}", method = RequestMethod.GET)
    public ResponseEntity<FlightFilterTO> filterSearchParam(@PathVariable("group") String group,
            @PathVariable("activity") String activity) throws AuditServiceException {
        FlightFilterTO flightAuditTO = activityServicesImpl.filterSearchGroup(group, activity);
        return new ResponseEntity<>(flightAuditTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/activity/retrieve", method = RequestMethod.POST)
    public ResponseEntity<AuditRecordRespose> getAudits(@RequestBody SearchCriteria searchCriteria)
            throws AuditServiceException {
        AuditRecordRespose auditRecords = activityServicesImpl.getAudits(searchCriteria);
        return new ResponseEntity<>(auditRecords, HttpStatus.OK);
    }

}
