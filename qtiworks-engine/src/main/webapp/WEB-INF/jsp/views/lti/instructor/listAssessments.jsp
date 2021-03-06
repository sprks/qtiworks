<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists Assessments in current LTI context

Additional Model attrs:

assessmentAndPackageList
assessmentListRouting (aid -> action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Assessment library">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
    </nav>
    <h2>Assessment library</h2>
    <div class="hints">
      <p>
        The assessment library contains all of the the Assessments you have
        uploaded into <c:out value="${utils:formatLtiContextTitle(ltiContext)}"/>.
        You can view, edit and upload new Assessments here. You can also choose
        which Assessment should be run for this launch.
    </div>
  </header>

  <ul class="menu">
    <li><a href="${utils:escapeLink(primaryRouting['uploadAssessment'])}">Upload a new assessment</a></li>
  </ul>

  <c:choose>
    <c:when test="${!empty assessmentAndPackageList}">
      <table class="listTable">
        <thead>
          <tr>
            <th></th>
            <th colspan="2">Actions</th>
            <th>Name &amp; Title</th>
            <th>Assessment Type</th>
            <th>Created</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="assessmentAndPackage" items="${assessmentAndPackageList}" varStatus="loopStatus">
            <c:set var="assessment" value="${assessmentAndPackage.assessment}"/>
            <c:set var="assessmentPackage" value="${assessmentAndPackage.assessmentPackage}"/>
            <c:set var="assessmentRouting" value="${assessmentListRouting[assessment.id]}"/>
            <c:set var="isSelectedAssessment" value="${!empty thisAssessment && thisAssessment.id==assessment.id}"/>
            <tr class="${isSelectedAssessment ? 'selected' : ''}">
              <td align="center">
                <div class="workflowStep">${loopStatus.index + 1}</div>
              </td>
              <td align="center">
                <c:if test="${assessmentPackage.launchable}">
                  <page:buttonLink path="${assessmentRouting['try']}" title="Quick Try"/>
                </c:if>
              </td>
              <td align="center">
                <c:choose>
                  <c:when test="${!isSelectedAssessment}">
                    <page:buttonLink path="${assessmentRouting['select']}" title="Select for this launch"
                    confirmCondition="${thisDeliveryStatusReport.sessionCount>0}"
                    confirm="Are you sure? Selecting a different assessment would terminate ${thisDeliveryStatusReport.nonTerminatedSessionCount} candidate session(s) currently running on this launch, and delete the gathered data for all ${thisDeliveryStatusReport.sessionCount} session(s) launched so far"
                    />
                  </c:when>
                  <c:otherwise>
                    Selected for this launch
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <h4><a href="${utils:escapeLink(assessmentRouting['show'])}"><c:out value="${assessment.name}"/></a></h4>
                <span class="title"><c:out value="${assessment.title}"/></span>
              </td>
              <td class="center">
                <c:choose>
                  <c:when test="${assessment.assessmentType=='ASSESSMENT_ITEM'}">Item</c:when>
                  <c:otherwise>Test</c:otherwise>
                </c:choose>
              </td>
              <td class="center">
                <c:out value="${utils:formatDayDateAndTime(assessment.creationTime)}"/>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not uploaded any assessments yet.</p>
    </c:otherwise>
  </c:choose>
</page:ltipage>
