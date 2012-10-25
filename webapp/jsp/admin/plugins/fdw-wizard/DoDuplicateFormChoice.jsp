<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="fdwWizard" scope="session" class="fr.paris.lutece.plugins.fdw.modules.wizard.web.WizardJspBean" />
<% 
	fdwWizard.init( request, fr.paris.lutece.plugins.fdw.modules.wizard.web.ManageWizardJspBean.RIGHT_MANAGE_WIZARD); 
	response.sendRedirect( fdwWizard.doDuplicateFormChoice( request ) );
%>