<%@ include file="../init.jsp" %>

<%

    long entryId = ParamUtil.getLong(renderRequest, "entryId");

    GuestbookEntry entry = null;
    if (entryId > 0) {
        entry = GuestbookEntryLocalServiceUtil.getGuestbookEntry(entryId);
    }

    long guestbookId = ParamUtil.getLong(renderRequest, "guestbookId");

%>

<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/guestbook/view.jsp"></portlet:param>
</portlet:renderURL>