<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />
<jsp:useBean id="fdwWizard" scope="session" class="fr.paris.lutece.plugins.fdw.modules.wizard.web.WizardJspBean" />
<% 
	fdwWizard.init( request, fr.paris.lutece.plugins.fdw.modules.wizard.web.ManageWizardJspBean.RIGHT_MANAGE_WIZARD);
%>
<%= fdwWizard.getDuplicateWorkflow( request ) %>
<%@ include file="../../../../AdminFooter.jsp" %>