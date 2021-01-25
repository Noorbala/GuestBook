<%@ include file="../init.jsp" %>

<portlet:renderURL var="addEntryURL">
	<portlet:param name="mvcPath" value="/guestbook/edit_entry.jsp"></portlet:param>
</portlet:renderURL>

<aui:button-row>
	<aui:button value="Add Entry" onClick="<%= addEntryURL.toString() %>" ></aui:button>
</aui:button-row>

