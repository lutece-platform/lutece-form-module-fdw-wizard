/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.fdw.modules.wizard.web;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.fdw.modules.wizard.business.DuplicationContext;
import fr.paris.lutece.plugins.fdw.modules.wizard.business.FormWithDirectory;
import fr.paris.lutece.plugins.fdw.modules.wizard.rights.Rights;
import fr.paris.lutece.plugins.fdw.modules.wizard.service.DuplicationManager;
import fr.paris.lutece.plugins.fdw.modules.wizard.service.WizardService;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * WizardJspBean
 *
 */
public class WizardJspBean extends PluginAdminPageJspBean
{
    //parameters
    private static final String PARAMETER_ID_WORKFLOW = "id_workflow";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_CHOICE = "choice";
    private static final String PARAMETER_DUPLICATE = "duplicate";
    private static final String PARAMETER_NEXT = "next";
    private static final String PARAMETER_PREVIOUS = "previous";
    private static final String PARAMETER_FROM_CHOICE = "from_choice";
    private static final String PARAMETER_DIRECTORY_TITLE = "directory_title";
    private static final String PARAMETER_WORKFLOW_TITLE = "workflow_title";
    private static final String PARAMETER_FORM_TITLE = "form_title";
    private static final String PARAMETER_COPY_MODE = "copyMode";
    private static final String VALUE_DIRECTORY_CHOICE_SIMPLE = "DuplicationSimple";
    private static final String VALUE_DIRECTORY_CHOICE_WITH_WORKFLOW = "DuplicationWithWorkflow";
    private static final String VALUE_FORM_CHOICE_SIMPLE = "DuplicationSimple";
    private static final String VALUE_FORM_CHOICE_WITH_DIRECTORY = "DuplicationWithDirectory";
    private static final String VALUE_FORM_CHOICE_WITH_DIRECTORY_AND_WORKFLOW = "DuplicationWithDirectoryAndWorkflow";
    private static final String PARAMETER_MESSAGE_SUCCESS_SIMPLE_COPY_DIRECTORY = "module.fdw.wizard.duplication.directory.success";
    private static final String PARAMETER_MESSAGE_SUCCESS_SIMPLE_COPY_WORKFLOW = "module.fdw.wizard.duplication.workflow.success";
    private static final String PARAMETER_MESSAGE_SUCCESS_SIMPLE_COPY_FORM = "module.fdw.wizard.duplication.form.success";
    private static final String PARAMETER_MESSAGE_SUCCESS_COPY_DIRECTORY_WITH_WORKFLOW = "module.fdw.wizard.duplication.directory.with_workflow.success";
    private static final String PARAMETER_MESSAGE_SUCCESS_COPY_FORM_WITH_DIRECTORY_AND_EXPORT = "module.fdw.wizard.duplication.form.with_directory.success";
    private static final String PARAMETER_MESSAGE_SUCCESS_COPY_FORM_WITH_DIRECTORY_AND_WORKFLOW = "module.fdw.wizard.duplication.form.with_directory_and_workflow.success";

    //copy modes
    private static final String COPY_MODE_DIRECTORY_ONLY = "directoryOnly";
    private static final String COPY_MODE_DIRECTORY_WITH_WORKFLOW = "directoryWithWorkflow";
    private static final String COPY_MODE_FORM_ONLY = "formOnly";
    private static final String COPY_MODE_FORM_WITH_DIRECTORY = "formWithDirectory";
    private static final String COPY_MODE_FORM_WITH_DIRECTORY_AND_WORKFLOW = "formWithDirectoryAndWorkflow";

    // marks
    private static final String MARK_DATA_TABLE_DIRECTORY = "dataTableDirectory";
    private static final String MARK_DATA_TABLE_FORM = "dataTableForm";
    private static final String MARK_DATA_TABLE_WORKFLOW = "dataTableWorkflow";
    private static final String MARK_WORKFLOW = "workflow";
    private static final String MARK_DIRECTORY = "directory";
    private static final String MARK_FORM = "form";
    private static final String MARK_FORM_WITH_DIRECTORY = "formWithDirectory";
    private static final String MARK_FROM_CHOICE = "fromChoice";
    private static final String MARK_COPY_MODE = "copyMode";
    private static final String MARK_MESSAGE_SUCCESS = "messageSuccess";
    private static final String MARK_STACK_ERROR = "stackError";

    //macro column names
    private static final String MACRO_COLUMN_ACTIONS_DIRECTORY = "columnActionsDirectory";
    private static final String MACRO_COLUMN_ACTIONS_FORM = "columnActionsForm";
    private static final String MACRO_COLUMN_ACTIONS_WORKFLOW = "columnActionsWorkflow";

    //templates
    private static final String TEMPLATE_MANAGE_WIZARD = "admin/plugins/fdw/modules/wizard/manage_wizard.html";
    private static final String TEMPLATE_DUPLICATE_WORKFLOW = "admin/plugins/fdw/modules/wizard/duplicate_workflow.html";
    private static final String TEMPLATE_DUPLICATE_WORKFLOW_SUCCESS = "admin/plugins/fdw/modules/wizard/duplicate_workflow_success.html";
    private static final String TEMPLATE_DUPLICATE_DIRECTORY_SIMPLE = "admin/plugins/fdw/modules/wizard/duplicate_directory_simple.html";
    private static final String TEMPLATE_DUPLICATE_DIRECTORY_SIMPLE_SUCCESS = "admin/plugins/fdw/modules/wizard/duplicate_directory_simple_success.html";
    private static final String TEMPLATE_DUPLICATE_DIRECTORY_CHOICE = "admin/plugins/fdw/modules/wizard/duplicate_directory_choice.html";
    private static final String TEMPLATE_DUPLICATE_DIRECTORY_WITH_WORKFLOW = "admin/plugins/fdw/modules/wizard/duplicate_directory_with_workflow.html";
    private static final String TEMPLATE_DUPLICATE_FORM_SIMPLE = "admin/plugins/fdw/modules/wizard/duplicate_form_simple.html";
    private static final String TEMPLATE_DUPLICATE_FORM_SUCCESS = "admin/plugins/fdw/modules/wizard/duplicate_form_success.html";
    private static final String TEMPLATE_DUPLICATE_FORM_CHOICE = "admin/plugins/fdw/modules/wizard/duplicate_form_choice.html";
    private static final String TEMPLATE_DUPLICATE_FORM_WITH_DIRECTORY = "admin/plugins/fdw/modules/wizard/duplicate_form_with_directory.html";
    private static final String TEMPLATE_DUPLICATE_FORM_WITH_DIRECTORY_AND_WORKFLOW = "admin/plugins/fdw/modules/wizard/duplicate_form_with_directory_and_workflow.html";
    private static final String TEMPLATE_DUPLICATION_ERROR = "admin/plugins/fdw/modules/wizard/duplication_error.html";

    //jsp
    private static final String JSP_MANAGE_WIZARD = "jsp/admin/plugins/fdw/modules/wizard/ManageWizard.jsp";
    private static final String JSP_DUPLICATION_SUCCESS_WORKFLOW = "jsp/admin/plugins/fdw/modules/wizard/DuplicateWorkflowSuccess.jsp";
    private static final String JSP_DUPLICATION_SUCCESS_DIRECTORY = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectorySuccess.jsp";
    private static final String JSP_DUPLICATE_DIRECTORY_SIMPLE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectorySimple.jsp";
    private static final String JSP_DUPLICATE_DIRECTORY_WITH_WORKFLOW = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectoryWithWorkflow.jsp";
    private static final String JSP_DUPLICATE_DIRECTORY_CHOICE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectoryChoice.jsp";
    private static final String JSP_DUPLICATION_SUCCESS_FORM = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormSuccess.jsp";
    private static final String JSP_DUPLICATE_FORM_SIMPLE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormSimple.jsp";
    private static final String JSP_DUPLICATE_FORM_WITH_DIRECTORY = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormWithDirectory.jsp";
    private static final String JSP_DUPLICATE_FORM_WITH_DIRECTORY_AND_WORKFLOW = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormWithDirectoryAndWorkflow.jsp";
    private static final String JSP_DUPLICATE_FORM_CHOICE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormChoice.jsp";
    private static final String JSP_ERROR_DUPLICATION = "jsp/admin/plugins/fdw/modules/wizard/ErrorDuplication.jsp";

    //field name
    private static final String FIELD_WORKFLOW_TITLE_WORKFLOW = "module.fdw.wizard.duplication.workflow.input_title";
    private static final String FIELD_DIRECTORY_TITLE_DIRECTORY = "module.fdw.wizard.duplication.directory.input_title";
    private static final String FIELD_DIRECTORY_TITLE_WORKFLOW = "module.fdw.wizard.duplication.directory.input_title_workflow";
    private static final String FIELD_FORM_TITLE_FORM = "module.fdw.wizard.duplication.form.input_title";
    private static final String FIELD_FORM_TITLE_DIRECTORY = "module.fdw.wizard.duplication.form.input_directory_title";
    private static final String FIELD_FORM_TITLE_WORKFLOW = "module.fdw.wizard.duplication.form.input_workflow_title";

    //mandatory field error message
    private static final String MESSAGE_MANDATORY_FIELD = "module.fdw.wizard.duplication.error.mandatory_field";

    //error
    private Exception _duplicationErrorMessage;

    //services
    private IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private WizardService _wizardService = SpringContextService.getBean( WizardService.BEAN_SERVICE );

    /**
     * Return management wizard
     * @param request The Http request
     * @return Html directory
     */
    public String getManageWizard( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        // directory
        DataTableManager<Directory> dataTableDirectory = new DataTableManager<Directory>( JSP_MANAGE_WIZARD, "", 10,
                true );
        dataTableDirectory.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.directory.row_title", "title", false );
        dataTableDirectory.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.directory.row_description",
            "description", false );
        dataTableDirectory.addFreeColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.directory.row_actions",
            MACRO_COLUMN_ACTIONS_DIRECTORY );

        DirectoryFilter filterDirectory = new DirectoryFilter(  );
        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filterDirectory, getPlugin(  ) );
        listDirectory = (List<Directory>) AdminWorkgroupService.getAuthorizedCollection( listDirectory, getUser(  ) );

        dataTableDirectory.filterSortAndPaginate( request, listDirectory );
        model.put( MARK_DATA_TABLE_DIRECTORY, dataTableDirectory );

        // workflow
        DataTableManager<Workflow> dataTableWorkflow = new DataTableManager<Workflow>( JSP_MANAGE_WIZARD, "", 10, true );
        dataTableWorkflow.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.workflow.row_title", "name", false );
        dataTableWorkflow.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.workflow.row_description",
            "description", false );
        dataTableWorkflow.addFreeColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.workflow.row_actions",
            MACRO_COLUMN_ACTIONS_WORKFLOW );

        WorkflowFilter filterWorkflow = new WorkflowFilter(  );
        List<Workflow> listWorkflow = _workflowService.getListWorkflowsByFilter( filterWorkflow );
        listWorkflow = (List<Workflow>) AdminWorkgroupService.getAuthorizedCollection( listWorkflow, getUser(  ) );

        dataTableWorkflow.filterSortAndPaginate( request, listWorkflow );
        model.put( MARK_DATA_TABLE_WORKFLOW, dataTableWorkflow );

        // form
        DataTableManager<Form> dataTableForm = new DataTableManager<Form>( JSP_MANAGE_WIZARD, "", 10, true );
        dataTableForm.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.form.row_title", "title", false );
        dataTableForm.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.form.row_description", "description", false );
        dataTableForm.addFreeColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.form.row_actions",
            MACRO_COLUMN_ACTIONS_FORM );

        FormFilter filterForm = new FormFilter(  );
        List<Form> listForm = FormHome.getFormList( filterForm, getPlugin(  ) );
        listForm = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForm, getUser(  ) );

        dataTableForm.filterSortAndPaginate( request, listForm );
        model.put( MARK_DATA_TABLE_FORM, dataTableForm );

        Rights rights = new Rights(  );
        rights.init( request );
        model.put( "rights", rights );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_WIZARD, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return workflow duplication page
     * @param request The Http request
     * @return Html workflow duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateWorkflow( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        }

        if ( workflow == null )
        {
            throw new AccessDeniedException( "Workflow not found for ID " + nIdWorkflow );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WORKFLOW, workflow );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_WORKFLOW, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Duplicates a workflow
     * @param request the request
     * @return The URL of the duplication success page
     */
    public String doDuplicateWorkflow( HttpServletRequest request )
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;
        UrlItem url = null;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        }

        if ( workflow == null )
        {
            //go to the error page
            _duplicationErrorMessage = new AccessDeniedException( "Workflow not found for ID " + nIdWorkflow );
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_ERROR_DUPLICATION );
        }
        else
        {
            if ( request.getParameter( PARAMETER_DUPLICATE ) != null )
            {
                int nIdWorkflowCopy = WorkflowUtils.CONSTANT_ID_NULL;
                String strWorkflowCopyTitle = request.getParameter( PARAMETER_WORKFLOW_TITLE );
                String strFieldError = null;

                if ( StringUtils.isBlank( strWorkflowCopyTitle ) )
                {
                    strFieldError = FIELD_WORKFLOW_TITLE_WORKFLOW;
                }

                if ( StringUtils.isNotBlank( strFieldError ) )
                {
                    Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                        AdminMessage.TYPE_STOP );
                }

                try
                {
                    // simple copy
                    nIdWorkflowCopy = _wizardService.doCopyWorkflow( workflow, strWorkflowCopyTitle, this.getLocale(  ) );

                    DuplicationContext context = new DuplicationContext(  );
                    context.setLocale( this.getLocale(  ) );
                    context.setPlugin( getPlugin(  ) );
                    context.setWorkflowDuplication( true );
                    context.setWorkflowToCopy( _wizardService.getWorkflow( nIdWorkflow ) );
                    context.setWorkflowCopy( _wizardService.getWorkflow( nIdWorkflowCopy ) );
                    DuplicationManager.doDuplicate( context );

                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATION_SUCCESS_WORKFLOW );
                    url.addParameter( PARAMETER_ID_WORKFLOW, nIdWorkflow );
                }
                catch ( Exception e )
                {
                    //rollback - delete copied workflow
                    if ( nIdWorkflowCopy > 0 )
                    {
                        _workflowService.remove( nIdWorkflowCopy );
                    }

                    //go to the error page
                    _duplicationErrorMessage = e;
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_ERROR_DUPLICATION );
                }
            }
            else
            {
                url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
            }
        }

        return url.getUrl(  );
    }

    /**
     * Return workflow duplication success page
     * @param request The Http request
     * @return Html workflow duplication success page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateWorkflowSuccess( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;
        String strWorkflowName = StringUtils.EMPTY;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
            strWorkflowName = workflow.getName(  );
        }

        if ( workflow == null )
        {
            throw new AccessDeniedException( "Workflow not found for ID " + nIdWorkflow );
        }

        String strMessageSuccess = MessageFormat.format( I18nService.getLocalizedString( 
                    PARAMETER_MESSAGE_SUCCESS_SIMPLE_COPY_WORKFLOW, request.getLocale(  ) ), strWorkflowName );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WORKFLOW, workflow );
        model.put( MARK_MESSAGE_SUCCESS, strMessageSuccess );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_WORKFLOW_SUCCESS, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return directory duplication page
     * @param request The Http request
     * @return Html directory duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateDirectorySimple( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_DIRECTORY, directory );

        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_DIRECTORY_SIMPLE, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Duplicates a directory
     * @param request the request
     * @return The URL of the duplication success page
     */
    public String doDuplicateDirectory( HttpServletRequest request )
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        UrlItem url = null;

        if ( nIdDirectory != DirectoryUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        }

        if ( directory == null )
        {
            //go to the error page
            _duplicationErrorMessage = new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_ERROR_DUPLICATION );
        }
        else
        {
            if ( request.getParameter( PARAMETER_DUPLICATE ) != null )
            {
                String strCopyMode = request.getParameter( PARAMETER_COPY_MODE );
                String strDirectoryCopyTitle = request.getParameter( PARAMETER_DIRECTORY_TITLE );
                int nIdDirectoryCopy = DirectoryUtils.CONSTANT_ID_NULL;
                int nIdWorkflowCopy = WorkflowUtils.CONSTANT_ID_NULL;
                String strFieldError = null;

                if ( StringUtils.isBlank( strDirectoryCopyTitle ) )
                {
                    strFieldError = FIELD_DIRECTORY_TITLE_DIRECTORY;
                }

                if ( StringUtils.isNotBlank( strFieldError ) )
                {
                    Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale( ) ) };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                            AdminMessage.TYPE_STOP );
                }

                try
                {
                    if ( COPY_MODE_DIRECTORY_ONLY.equals( strCopyMode ) )
                    {
                        // simple copy
                        directory.setIdWorkflow( DirectoryUtils.CONSTANT_ID_NULL );

                        nIdDirectoryCopy = _wizardService.doCopyDirectory( directory, strDirectoryCopyTitle,
                                getPlugin( ) );

                        DuplicationContext context = new DuplicationContext( );
                        context.setLocale( this.getLocale( ) );
                        context.setPlugin( getPlugin( ) );
                        context.setDirectoryDuplication( true );
                        context.setDirectoryToCopy( _wizardService.getDirectory( nIdDirectory, getPlugin( ) ) );
                        context.setDirectoryCopy( _wizardService.getDirectory( nIdDirectoryCopy, getPlugin( ) ) );
                        DuplicationManager.doDuplicate( context );
                    }
                    else if ( COPY_MODE_DIRECTORY_WITH_WORKFLOW.equals( strCopyMode ) )
                    {
                        // copy with workflow
                        int nIdWorkflowToCopy = directory.getIdWorkflow( );
                        String strWorkflowCopyTitle = request.getParameter( PARAMETER_WORKFLOW_TITLE );

                        if ( StringUtils.isBlank( strWorkflowCopyTitle ) )
                        {
                            strFieldError = FIELD_DIRECTORY_TITLE_WORKFLOW;
                        }

                        if ( StringUtils.isNotBlank( strFieldError ) )
                        {
                            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale( ) ) };

                            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD,
                                    tabRequiredFields, AdminMessage.TYPE_STOP );
                        }

                        nIdDirectoryCopy = _wizardService.doCopyDirectoryWithWorkflow( directory,
                                strDirectoryCopyTitle, strWorkflowCopyTitle, getPlugin( ), this.getLocale( ) );
                        nIdWorkflowCopy = directory.getIdWorkflow( );

                        DuplicationContext context = new DuplicationContext( );
                        context.setLocale( this.getLocale( ) );
                        context.setPlugin( getPlugin( ) );
                        context.setDirectoryDuplication( true );
                        context.setWorkflowDuplication( true );
                        context.setDirectoryToCopy( _wizardService.getDirectory( nIdDirectory, getPlugin( ) ) );
                        context.setDirectoryCopy( _wizardService.getDirectory( nIdDirectoryCopy, getPlugin( ) ) );
                        context.setWorkflowToCopy( _wizardService.getWorkflow( nIdWorkflowToCopy ) );
                        context.setWorkflowCopy( _wizardService.getWorkflow( nIdWorkflowCopy ) );
                        DuplicationManager.doDuplicate( context );
                    }

                    // success
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATION_SUCCESS_DIRECTORY );
                    url.addParameter( PARAMETER_ID_DIRECTORY, nIdDirectory );
                    url.addParameter( PARAMETER_COPY_MODE, strCopyMode );
                }
                catch ( Exception e )
                {
                    //rollback - delete copied directory and workflow
                    if ( nIdDirectoryCopy > 0 )
                    {
                        DirectoryHome.remove( nIdDirectoryCopy, getPlugin( ) );
                    }

                    if ( nIdWorkflowCopy > 0 )
                    {
                        _workflowService.remove( nIdWorkflowCopy );
                    }

                    //go to the error page
                    _duplicationErrorMessage = e;
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_ERROR_DUPLICATION );
                }
            }
            else if ( request.getParameter( PARAMETER_PREVIOUS ) != null )
            {
                url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATE_DIRECTORY_CHOICE );
                url.addParameter( PARAMETER_ID_DIRECTORY, nIdDirectory );
                url.addParameter( PARAMETER_FROM_CHOICE, "true" );
            }
            else
            {
                url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
            }
        }

        return url.getUrl(  );
    }

    /**
     * Return directory duplication success page
     * @param request The Http request
     * @return Html directory duplication success page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateDirectorySuccess( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        String strCopyMode = request.getParameter( PARAMETER_COPY_MODE );
        String strDirectoryName = StringUtils.EMPTY;

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
            strDirectoryName = directory.getTitle(  );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        String strMessageSuccess = initSuccessMessageForDirectoryDuplication( request, strCopyMode, strDirectoryName );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_COPY_MODE, strCopyMode );
        model.put( MARK_MESSAGE_SUCCESS, strMessageSuccess );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_DIRECTORY_SIMPLE_SUCCESS,
                getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return directory duplication choice page
     * @param request The Http request
     * @return Html directory duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateDirectoryChoice( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_DIRECTORY, directory );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_DIRECTORY_CHOICE, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Chooses a duplication method
     * @param request the request
     * @return The URL of the duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String doDuplicateDirectoryChoice( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        UrlItem url = null;

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        if ( request.getParameter( PARAMETER_NEXT ) != null )
        {
            String strChoice = request.getParameter( PARAMETER_CHOICE );

            if ( StringUtils.isNotBlank( strChoice ) )
            {
                if ( VALUE_DIRECTORY_CHOICE_SIMPLE.equals( strChoice ) )
                {
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATE_DIRECTORY_SIMPLE );
                }
                else if ( VALUE_DIRECTORY_CHOICE_WITH_WORKFLOW.equals( strChoice ) )
                {
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATE_DIRECTORY_WITH_WORKFLOW );
                }
                else
                {
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
                }

                url.addParameter( PARAMETER_ID_DIRECTORY, nIdDirectory );
                url.addParameter( PARAMETER_FROM_CHOICE, "true" );
            }
            else
            {
                url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
            }
        }
        else
        {
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
        }

        return url.getUrl(  );
    }

    /**
     * Return directory duplication page
     * @param request The Http request
     * @return Html directory duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateDirectoryWithWorkflow( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_DIRECTORY, directory );

        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_DIRECTORY_WITH_WORKFLOW,
                getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return form duplication page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateFormSimple( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, form );

        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_FORM_SIMPLE, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Duplicates a form
     * @param request the request
     * @return The URL of the duplication success page
     */
    public String doDuplicateForm( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        UrlItem url = null;

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
        }

        if ( form == null )
        {
            //go to the error page
            _duplicationErrorMessage = new AccessDeniedException( "Form not found for ID " + nIdForm );
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_ERROR_DUPLICATION );
        }
        else
        {

            if ( request.getParameter( PARAMETER_DUPLICATE ) != null )
            {
                String strCopyMode = request.getParameter( PARAMETER_COPY_MODE );
                String strFormCopyTitle = request.getParameter( PARAMETER_FORM_TITLE );
                int nIdFormCopy = FormUtils.CONSTANT_ID_NULL;
                String strFieldError = null;

                if ( StringUtils.isBlank( strFormCopyTitle ) )
                {
                    strFieldError = FIELD_FORM_TITLE_FORM;
                }

                if ( StringUtils.isNotBlank( strFieldError ) )
                {
                    Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale( ) ) };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                            AdminMessage.TYPE_STOP );
                }

                try
                {
                    if ( COPY_MODE_FORM_ONLY.equals( strCopyMode ) )
                    {
                        // simple copy
                        nIdFormCopy = _wizardService.doCopyForm( form, strFormCopyTitle, getPlugin( ) );

                        DuplicationContext context = new DuplicationContext( );
                        context.setLocale( this.getLocale( ) );
                        context.setPlugin( getPlugin( ) );
                        context.setFormDuplication( true );
                        context.setFormToCopy( _wizardService.getForm( nIdForm, getPlugin( ) ) );
                        context.setFormCopy( _wizardService.getForm( nIdFormCopy, getPlugin( ) ) );
                        DuplicationManager.doDuplicate( context );
                    }
                    else if ( COPY_MODE_FORM_WITH_DIRECTORY.equals( strCopyMode ) )
                    {
                        String strDirectoryCopyTitle = request.getParameter( PARAMETER_DIRECTORY_TITLE );

                        if ( StringUtils.isBlank( strDirectoryCopyTitle ) )
                        {
                            strFieldError = FIELD_FORM_TITLE_DIRECTORY;
                        }

                        if ( StringUtils.isNotBlank( strFieldError ) )
                        {
                            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale( ) ) };

                            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD,
                                    tabRequiredFields, AdminMessage.TYPE_STOP );
                        }

                        nIdFormCopy = _wizardService.doCopyForm( form, strFormCopyTitle, getPlugin( ) );

                        DuplicationContext context = new DuplicationContext( );
                        context.setLocale( this.getLocale( ) );
                        context.setPlugin( getPlugin( ) );
                        context.setFormDuplication( true );
                        context.setDirectoryDuplication( true );
                        context.setDirectoryCopyName( strDirectoryCopyTitle );
                        context.setFormToCopy( _wizardService.getForm( nIdForm, getPlugin( ) ) );
                        context.setFormCopy( _wizardService.getForm( nIdFormCopy, getPlugin( ) ) );
                        DuplicationManager.doDuplicate( context );
                    }
                    else if ( COPY_MODE_FORM_WITH_DIRECTORY_AND_WORKFLOW.equals( strCopyMode ) )
                    {
                        String strDirectoryCopyTitle = request.getParameter( PARAMETER_DIRECTORY_TITLE );
                        String strWorkflowCopyTitle = request.getParameter( PARAMETER_WORKFLOW_TITLE );

                        if ( StringUtils.isBlank( strDirectoryCopyTitle ) )
                        {
                            strFieldError = FIELD_FORM_TITLE_DIRECTORY;
                        }
                        else if ( StringUtils.isBlank( strWorkflowCopyTitle ) )
                        {
                            strFieldError = FIELD_FORM_TITLE_WORKFLOW;
                        }

                        if ( StringUtils.isNotBlank( strFieldError ) )
                        {
                            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale( ) ) };

                            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD,
                                    tabRequiredFields, AdminMessage.TYPE_STOP );
                        }

                        nIdFormCopy = _wizardService.doCopyForm( form, strFormCopyTitle, getPlugin( ) );

                        DuplicationContext context = new DuplicationContext( );
                        context.setLocale( this.getLocale( ) );
                        context.setPlugin( getPlugin( ) );
                        context.setFormDuplication( true );
                        context.setDirectoryDuplication( true );
                        context.setWorkflowDuplication( true );
                        context.setDirectoryCopyName( strDirectoryCopyTitle );
                        context.setWorkflowCopyName( strWorkflowCopyTitle );
                        context.setFormToCopy( _wizardService.getForm( nIdForm, getPlugin( ) ) );
                        context.setFormCopy( _wizardService.getForm( nIdFormCopy, getPlugin( ) ) );
                        DuplicationManager.doDuplicate( context );
                    }

                    // success
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATION_SUCCESS_FORM );
                    url.addParameter( PARAMETER_ID_FORM, nIdForm );
                    url.addParameter( PARAMETER_COPY_MODE, strCopyMode );
                }
                catch ( Exception e )
                {
                    //rollback - delete copied form
                    if ( nIdFormCopy > 0 )
                    {
                        FormHome.remove( nIdFormCopy, getPlugin( ) );
                    }

                    //go to the error page
                    _duplicationErrorMessage = e;
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_ERROR_DUPLICATION );
                }
            }
            else if ( request.getParameter( PARAMETER_PREVIOUS ) != null )
            {
                url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATE_FORM_CHOICE );
                url.addParameter( PARAMETER_ID_FORM, nIdForm );
                url.addParameter( PARAMETER_FROM_CHOICE, "true" );
            }
            else
            {
                url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
            }
        }

        return url.getUrl(  );
    }

    /**
     * Return form duplication success page
     * @param request The Http request
     * @return Html form duplication success page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateFormSuccess( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        String strCopyMode = request.getParameter( PARAMETER_COPY_MODE );
        String strFormName = StringUtils.EMPTY;

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
            strFormName = form.getTitle(  );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        String strMessageSuccess = initSuccessMessageForFormDuplication( request, strCopyMode, strFormName );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, form );
        model.put( MARK_COPY_MODE, strCopyMode );
        model.put( MARK_MESSAGE_SUCCESS, strMessageSuccess );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_FORM_SUCCESS, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return form duplication choice page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateFormChoice( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        FormWithDirectory formWithDirectory = new FormWithDirectory(  );
        formWithDirectory.setForm( form );
        formWithDirectory.setDirectory( _wizardService.getDirectoryAssociatedToForm( form ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, form );
        model.put( MARK_FORM_WITH_DIRECTORY, formWithDirectory );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_FORM_CHOICE, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Chooses a duplication method
     * @param request the request
     * @return The URL of the duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String doDuplicateFormChoice( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        UrlItem url = null;

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        if ( request.getParameter( PARAMETER_NEXT ) != null )
        {
            String strChoice = request.getParameter( PARAMETER_CHOICE );

            if ( StringUtils.isNotBlank( strChoice ) )
            {
                if ( VALUE_FORM_CHOICE_SIMPLE.equals( strChoice ) )
                {
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATE_FORM_SIMPLE );
                }
                else if ( VALUE_FORM_CHOICE_WITH_DIRECTORY.equals( strChoice ) )
                {
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATE_FORM_WITH_DIRECTORY );
                }
                else if ( VALUE_FORM_CHOICE_WITH_DIRECTORY_AND_WORKFLOW.equals( strChoice ) )
                {
                    url = new UrlItem( AppPathService.getBaseUrl( request ) +
                            JSP_DUPLICATE_FORM_WITH_DIRECTORY_AND_WORKFLOW );
                }
                else
                {
                    url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
                }
                url.addParameter( PARAMETER_ID_FORM, nIdForm );
                url.addParameter( PARAMETER_FROM_CHOICE, "true" );
            }
            else
            {
                url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
            }
        }
        else
        {
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
        }

        return url.getUrl(  );
    }

    /**
     * Return form duplication page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateFormWithDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, form );

        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_FORM_WITH_DIRECTORY,
                getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return form duplication page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateFormWithDirectoryAndWorkflow( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, form );

        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_FORM_WITH_DIRECTORY_AND_WORKFLOW,
                getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Inits the success message for directory duplication, depends on the type
     * of duplication
     * @param request the http request
     * @param strCopyMode the copy mode
     * @param strDirectoryName the directory name
     * @return the success message
     */
    private String initSuccessMessageForDirectoryDuplication( HttpServletRequest request, String strCopyMode,
        String strDirectoryName )
    {
        String strMessageSuccess = StringUtils.EMPTY;

        if ( strCopyMode.equals( COPY_MODE_DIRECTORY_ONLY ) )
        {
            //simple duplication
            strMessageSuccess = MessageFormat.format( I18nService.getLocalizedString( 
                        PARAMETER_MESSAGE_SUCCESS_SIMPLE_COPY_DIRECTORY, request.getLocale(  ) ), strDirectoryName );
        }
        else if ( strCopyMode.equals( COPY_MODE_DIRECTORY_WITH_WORKFLOW ) )
        {
            //duplication with workflow
            strMessageSuccess = MessageFormat.format( I18nService.getLocalizedString( 
                        PARAMETER_MESSAGE_SUCCESS_COPY_DIRECTORY_WITH_WORKFLOW, request.getLocale(  ) ),
                    strDirectoryName );
        }

        return strMessageSuccess;
    }

    /**
     * Inits the success message for form duplication, depends on the type
     * of duplication
     * @param request the http request
     * @param strCopyMode the copy mode
     * @param strFormName the form name
     * @return the success message
     */
    private String initSuccessMessageForFormDuplication( HttpServletRequest request, String strCopyMode,
        String strFormName )
    {
        String strMessageSuccess = StringUtils.EMPTY;

        if ( strCopyMode.equals( COPY_MODE_FORM_ONLY ) )
        {
            //simple duplication
            strMessageSuccess = MessageFormat.format( I18nService.getLocalizedString( 
                        PARAMETER_MESSAGE_SUCCESS_SIMPLE_COPY_FORM, request.getLocale(  ) ), strFormName );
        }
        else if ( strCopyMode.equals( COPY_MODE_FORM_WITH_DIRECTORY ) )
        {
            //duplication with directory and export configuration
            strMessageSuccess = MessageFormat.format( I18nService.getLocalizedString( 
                        PARAMETER_MESSAGE_SUCCESS_COPY_FORM_WITH_DIRECTORY_AND_EXPORT, request.getLocale(  ) ),
                    strFormName );
        }
        else if ( strCopyMode.equals( COPY_MODE_FORM_WITH_DIRECTORY_AND_WORKFLOW ) )
        {
            //duplication with directory and workflow
            strMessageSuccess = MessageFormat.format( I18nService.getLocalizedString( 
                        PARAMETER_MESSAGE_SUCCESS_COPY_FORM_WITH_DIRECTORY_AND_WORKFLOW, request.getLocale(  ) ),
                    strFormName );
        }

        return strMessageSuccess;
    }

    /**
     * Return directory duplication page
     * @param request The Http request
     * @return Html directory duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        if ( directory.getIdWorkflow(  ) > 0 )
        {
            return getDuplicateDirectoryChoice( request );
        }
        else
        {
            return getDuplicateDirectorySimple( request );
        }
    }

    /**
     * Return form duplication page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException the acces denied exception
     */
    public String getDuplicateForm( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        Directory directory = _wizardService.getDirectoryAssociatedToForm( form );

        if ( directory != null )
        {
            return getDuplicateFormChoice( request );
        }
        else
        {
            return getDuplicateFormSimple( request );
        }
    }

    /**
     * Gets the duplication error page
     * @param request the request
     * @return the duplication error page url
     */
    public String getErrorDuplicationPage( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_STACK_ERROR, _duplicationErrorMessage.getMessage(  ) );

        _duplicationErrorMessage = null;

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATION_ERROR, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }
}
