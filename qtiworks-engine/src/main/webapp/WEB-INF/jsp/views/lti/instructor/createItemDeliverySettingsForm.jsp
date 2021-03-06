<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a new Item Delivery settings

Model:

itemDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Create new Item Delivery Settings">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Delivery Settings Manager</a> &#xbb;
    <a href="${utils:escapeLink(primaryRouting['listItemDeliverySettings'])}">Item Delivery Settings</a> &#xbb;
    </nav>
    <h2>Create Item Delivery Settings</h2>
    <div class="hints">
      <p>
        This form lets you create a new set of Delivery Settings for use with deliveries of single Assessment Items.
      </p>
    </div>
  </header>

  <%@ include file="/WEB-INF/jsp/includes/instructor/itemDeliverySettingsForm.jspf" %>

</page:ltipage>
