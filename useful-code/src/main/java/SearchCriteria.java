/*
 * 
 */


import com.emirates.audit.dto.common.BaseResponse;
import com.emirates.audit.util.AuditConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class SearchCriteria.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchCriteria extends BaseResponse {

    private String role;
    private String staffNumber;
    private String flightNumber;
    private String customerRefNumber;
    private String groupName;
    private String activityName;
    private String parameterName;
    private String searchType;
    private TimeRange auditTimeRange;
    private TimeRange flightDateRange;
    @JsonProperty("currentPage")
    private int currentPage;
    @JsonProperty("pageSize")
    private int pageSize;
    private String boardPoint;
    private boolean export;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize == 0 ? AuditConstants.DEFAULT_PAGESIZE : pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getCustomerRefNumber() {
        return customerRefNumber;
    }

    public void setCustomerRefNumber(String customerRefNumber) {
        this.customerRefNumber = customerRefNumber;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public TimeRange getAuditTimeRange() {
        return auditTimeRange;
    }

    public void setAuditTimeRange(TimeRange auditTime) {
        this.auditTimeRange = auditTime;
    }

    public TimeRange getFlightDateRange() {
        return flightDateRange;
    }

    public void setFlightDateRange(TimeRange flightDateRange) {
        this.flightDateRange = flightDateRange;
    }

    public String getBoardPoint() {
        return boardPoint;
    }

    public void setBoardPoint(String boardPoint) {
        this.boardPoint = boardPoint;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }
}
