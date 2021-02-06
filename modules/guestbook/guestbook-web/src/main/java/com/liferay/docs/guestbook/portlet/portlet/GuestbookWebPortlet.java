package com.liferay.docs.guestbook.portlet.portlet;

import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.docs.guestbook.model.GuestbookEntry;
import com.liferay.docs.guestbook.portlet.constants.GuestbookWebPortletKeys;
import com.liferay.docs.guestbook.service.GuestbookEntryLocalService;
import com.liferay.docs.guestbook.service.GuestbookEntryLocalServiceUtil;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author noor
 */
@Component(
        immediate = true,
        property = {
                "com.liferay.portlet.display-category=category.sample",
                "com.liferay.portlet.instanceable=false",
                "com.liferay.portlet.scopeable=true",
                "javax.portlet.display-name=Guestbook",
                "javax.portlet.expiration-cache=0",
                "javax.portlet.init-param.template-path=/",
                "javax.portlet.init-param.view-template=/guestbook/view.jsp",
                "javax.portlet.name=" + GuestbookWebPortletKeys.GUESTBOOK,
                "javax.portlet.resource-bundle=content.Language",
                "javax.portlet.security-role-ref=power-user,user",
                "javax.portlet.supports.mime-type=text/html"
        },
        service = Portlet.class
)
public class GuestbookWebPortlet extends MVCPortlet {

    public void addEntry(ActionRequest request, ActionResponse response)
            throws PortalException {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                GuestbookEntry.class.getName(), request);

        String userName = ParamUtil.getString(request, "name");
        String email = ParamUtil.getString(request, "email");
        String message = ParamUtil.getString(request, "message");
        long guestbookId = ParamUtil.getLong(request, "guestbookId");
        long entryId = ParamUtil.getLong(request, "entryId");

        //===========================================================
        System.out.println("addEntry.guestbookId = " + guestbookId);
        //===========================================================

        if (entryId > 0) {

            try {

                _guestbookEntryLocalService.updateGuestbookEntry(
                        serviceContext.getUserId(), guestbookId, entryId, userName,
                        email, message, serviceContext);

                response.setRenderParameter(
                        "guestbookId", Long.toString(guestbookId));

            } catch (Exception e) {
                System.out.println(e);

                PortalUtil.copyRequestParameters(request, response);

                response.setRenderParameter(
                        "mvcPath", "/guestbook/edit_entry.jsp");
            }

        } else {

            try {
                _guestbookEntryLocalService.addGuestbookEntry(
                        serviceContext.getUserId(), guestbookId, userName, email,
                        message, serviceContext);

                response.setRenderParameter(
                        "guestbookId", Long.toString(guestbookId));

            } catch (Exception e) {
                System.out.println(e);

                PortalUtil.copyRequestParameters(request, response);

                response.setRenderParameter(
                        "mvcPath", "/guestbook/edit_entry.jsp");
            }
        }
    }

    public void deleteEntry(ActionRequest request, ActionResponse response) throws PortalException {
        long entryId = ParamUtil.getLong(request, "entryId");
        long guestbookId = ParamUtil.getLong(request, "guestbookId");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                GuestbookEntry.class.getName(), request);

        try {

            response.setRenderParameter(
                    "guestbookId", Long.toString(guestbookId));

            _guestbookEntryLocalService.deleteGuestbookEntry(entryId);
        } catch (Exception e) {
            Logger.getLogger(GuestbookWebPortlet.class.getName()).log(
                    Level.SEVERE, null, e);
        }
    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(
                    Guestbook.class.getName(), renderRequest);

            long groupId = serviceContext.getScopeGroupId();

            long guestbookId = ParamUtil.getLong(renderRequest, "guestbookId");

            List<Guestbook> guestbooks = _guestbookLocalService.getGuestbooks(
                    groupId);

            if (guestbooks.isEmpty()) {
                Guestbook guestbook = _guestbookLocalService.addGuestbook(
                        serviceContext.getUserId(), "Main", serviceContext);

                guestbookId = guestbook.getGuestbookId();
            }

            if (guestbookId == 0) {
                guestbookId = guestbooks.get(0).getGuestbookId();
            }

            //==========================
            System.out.println("groupId: " + groupId);
            System.out.println("guestbookId: " + guestbookId);
            //==========================

            List<GuestbookEntry> entries = GuestbookEntryLocalServiceUtil.getGuestbookEntries(groupId,
                    guestbookId, 0, 10);

            //System.out.println("size:   " + entries.size());

            renderRequest.setAttribute("guestbookId", guestbookId);
        } catch (Exception e) {
            throw new PortletException(e);
        }

        super.render(renderRequest, renderResponse);
    }

    @Reference
    private GuestbookEntryLocalService _guestbookEntryLocalService;

    @Reference
    private GuestbookLocalService _guestbookLocalService;
}