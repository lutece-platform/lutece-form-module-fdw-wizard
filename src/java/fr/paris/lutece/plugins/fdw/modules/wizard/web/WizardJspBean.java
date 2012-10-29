/**
 * 
 */
package fr.paris.lutece.plugins.fdw.modules.wizard.web;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.fdw.modules.wizard.business.FormWithDirectory;
import fr.paris.lutece.plugins.fdw.modules.wizard.rights.Rights;
import fr.paris.lutece.plugins.fdw.modules.wizard.service.DuplicationManager;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.modules.exportdirectory.business.FormConfiguration;
import fr.paris.lutece.plugins.form.modules.exportdirectory.business.FormConfigurationHome;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.method.MethodUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.iterators.EntrySetMapIterator;
import org.apache.commons.lang.StringUtils;


/**
 * 
 *
 */
public class WizardJspBean extends PluginAdminPageJspBean
{
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
    private static final String COPY_MODE_DIRECTORY_ONLY = "directoryOnly";
    private static final String COPY_MODE_DIRECTORY_WITH_WORKFLOW = "directoryWithWorkflow";
    private static final String COPY_MODE_FORM_ONLY = "formOnly";
    private static final String COPY_MODE_FORM_WITH_DIRECTORY = "formWithDirectory";
    private static final String COPY_MODE_FORM_WITH_DIRECTORY_AND_WORKFLOW = "formWithDirectoryAndWorkflow";

    private static final String MARK_DATA_TABLE_DIRECTORY = "dataTableDirectory";
    private static final String MARK_DATA_TABLE_FORM = "dataTableForm";
    private static final String MARK_DATA_TABLE_WORKFLOW = "dataTableWorkflow";
    private static final String MARK_WORKFLOW = "workflow";
    private static final String MARK_DIRECTORY = "directory";
    private static final String MARK_FORM = "form";
    private static final String MARK_FORM_WITH_DIRECTORY = "formWithDirectory";
    private static final String MARK_FROM_CHOICE = "fromChoice";
    private static final String MARK_COPY_MODE = "copyMode";

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

    private static final String JSP_MANAGE_WIZARD = "jsp/admin/plugins/fdw/modules/wizard/ManageWizard.jsp";
    private static final String JSP_DUPLICATION_SUCCESS_WORKFLOW = "jsp/admin/plugins/fdw/modules/wizard/DuplicateWorkflowSuccess.jsp";
    private static final String JSP_DUPLICATION_SUCCESS_DIRECTORY_SIMPLE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectorySimpleSuccess.jsp";
    private static final String JSP_DUPLICATION_SUCCESS_DIRECTORY_WITH_WORKFLOW = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectoryWithWorkflowSuccess.jsp";
    private static final String JSP_DUPLICATE_DIRECTORY_SIMPLE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectorySimple.jsp";
    private static final String JSP_DUPLICATE_DIRECTORY_WITH_WORKFLOW = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectoryWithWorkflow.jsp";
    private static final String JSP_DUPLICATE_DIRECTORY_CHOICE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateDirectoryChoice.jsp";
    private static final String JSP_DUPLICATION_SUCCESS_FORM = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormSuccess.jsp";
    private static final String JSP_DUPLICATE_FORM_SIMPLE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormSimple.jsp";
    private static final String JSP_DUPLICATE_FORM_WITH_DIRECTORY = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormWithDirectory.jsp";
    private static final String JSP_DUPLICATE_FORM_WITH_DIRECTORY_AND_WORKFLOW = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormWithDirectoryAndWorkflow.jsp";
    private static final String JSP_DUPLICATE_FORM_CHOICE = "jsp/admin/plugins/fdw/modules/wizard/DuplicateFormChoice.jsp";

    private IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private IStateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );

    /**
     * Return management wizard
     * @param request The Http request
     * @return Html directory
     */
    public String getManageWizard( HttpServletRequest request )
    {

        Map<String, Object> model = new HashMap<String, Object>( );

        // directory
        DataTableManager<Directory> dataTableDirectory = new DataTableManager<Directory>( "", "", 10, true );
        dataTableDirectory.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.directory.row_title", "title", false );
        dataTableDirectory.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.directory.row_description",
                "description", false );
        dataTableDirectory.addFreeColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.directory.row_actions",
                MACRO_COLUMN_ACTIONS_DIRECTORY );

        DirectoryFilter filterDirectory = new DirectoryFilter( );
        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filterDirectory, getPlugin( ) );
        listDirectory = (List<Directory>) AdminWorkgroupService.getAuthorizedCollection( listDirectory, getUser( ) );

        dataTableDirectory.filterSortAndPaginate( request, listDirectory );
        model.put( MARK_DATA_TABLE_DIRECTORY, dataTableDirectory );

        // workflow
        DataTableManager<Workflow> dataTableWorkflow = new DataTableManager<Workflow>( "", "", 10, true );
        dataTableWorkflow.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.workflow.row_title", "name", false );
        dataTableWorkflow.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.workflow.row_description",
                "description", false );
        dataTableWorkflow.addFreeColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.workflow.row_actions",
                MACRO_COLUMN_ACTIONS_WORKFLOW );

        WorkflowFilter filterWorkflow = new WorkflowFilter( );
        List<Workflow> listWorkflow = _workflowService.getListWorkflowsByFilter( filterWorkflow );
        listWorkflow = (List<Workflow>) AdminWorkgroupService.getAuthorizedCollection( listWorkflow, getUser( ) );

        dataTableWorkflow.filterSortAndPaginate( request, listWorkflow );
        model.put( MARK_DATA_TABLE_WORKFLOW, dataTableWorkflow );

        // form
        DataTableManager<FormWithDirectory> dataTableForm = new DataTableManager<FormWithDirectory>( "", "", 10, true );
        dataTableForm.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.form.row_title", "form.title", false );
        dataTableForm.addColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.form.row_description", "form.description",
                false );
        dataTableForm.addFreeColumn( "module.fdw.wizard.manage_plugin_fdw-wizard.form.row_actions",
                MACRO_COLUMN_ACTIONS_FORM );

        FormFilter filterForm = new FormFilter( );
        List<Form> listForm = FormHome.getFormList( filterForm, getPlugin( ) );
        listForm = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForm, getUser( ) );

        List<FormWithDirectory> listFormWithDirectory = new ArrayList<FormWithDirectory>( );
        for ( Form form : listForm )
        {
            FormWithDirectory formWithDirectory = new FormWithDirectory( );
            formWithDirectory.setForm( form );

            // retrieve the directory associated to the form if it exists
            Directory directory = getDirectoryAssociatedTo( form );
            formWithDirectory.setDirectory( directory );

            listFormWithDirectory.add( formWithDirectory );
        }

        dataTableForm.filterSortAndPaginate( request, listFormWithDirectory );
        model.put( MARK_DATA_TABLE_FORM, dataTableForm );

        Rights rights = new Rights( );
        rights.init( request );
        model.put( "rights", rights );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_WIZARD, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Return workflow duplication page
     * @param request The Http request
     * @return Html workflow duplication page
     * @throws AccessDeniedException
     */
    public String getDuplicateWorkflow( HttpServletRequest request ) throws AccessDeniedException
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

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_WORKFLOW, workflow );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_WORKFLOW, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Duplicates a workflow
     * @param request the request
     * @return The URL of the duplication success page
     * @throws AccessDeniedException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public String doDuplicateWorkflow( HttpServletRequest request ) throws AccessDeniedException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException
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
            throw new AccessDeniedException( "Workflow not found for ID " + nIdWorkflow );
        }

        if ( request.getParameter( PARAMETER_DUPLICATE ) != null )
        {
            // simple copy
            String strWorkflowCopyTitle = request.getParameter( PARAMETER_WORKFLOW_TITLE );
            doCopyWorkflow( workflow, strWorkflowCopyTitle );
            
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATION_SUCCESS_WORKFLOW );
            url.addParameter( PARAMETER_ID_WORKFLOW, nIdWorkflow );
        }
        else
        {
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
        }

        return url.getUrl( );
    }

    /**
     * Return workflow duplication success page
     * @param request The Http request
     * @return Html workflow duplication success page
     * @throws AccessDeniedException
     */
    public String getDuplicateWorkflowSuccess( HttpServletRequest request ) throws AccessDeniedException
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

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_WORKFLOW, workflow );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_WORKFLOW_SUCCESS, getLocale( ),
                model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Return directory duplication page
     * @param request The Http request
     * @return Html directory duplication page
     * @throws AccessDeniedException
     */
    public String getDuplicateDirectorySimple( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin( ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_DIRECTORY, directory );
        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_DIRECTORY_SIMPLE, getLocale( ),
                model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Duplicates a directory
     * @param request the request
     * @return The URL of the duplication success page
     * @throws AccessDeniedException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public String doDuplicateDirectory( HttpServletRequest request ) throws AccessDeniedException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        UrlItem url = null;

        if ( nIdDirectory != DirectoryUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin( ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        if ( request.getParameter( PARAMETER_DUPLICATE ) != null )
        {
            String strCopyMode = request.getParameter( PARAMETER_COPY_MODE );
            String strDirectoryCopyTitle = request.getParameter( PARAMETER_DIRECTORY_TITLE );

            if ( COPY_MODE_DIRECTORY_ONLY.equals( strCopyMode ) )
            {
                // simple copy
                int nIdDirectoryCopy = doCopyDirectory( directory, strDirectoryCopyTitle );

                doExtraDuplication( DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.CONSTANT_ID_NULL, nIdDirectory,
                        nIdDirectoryCopy, DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.CONSTANT_ID_NULL );

            }
            else if ( COPY_MODE_DIRECTORY_WITH_WORKFLOW.equals( strCopyMode ) )
            {
                // copy with workflow
                int nIdWorkflowToCopy = directory.getIdWorkflow( );
                String strWorkflowCopyTitle = request.getParameter( PARAMETER_WORKFLOW_TITLE );
                int nIdDirectoryCopy = doCopyDirectoryWithWorkflow( directory, strDirectoryCopyTitle,
                        strWorkflowCopyTitle );
                int nIdWorkflowCopy = directory.getIdWorkflow( );

                doExtraDuplication( DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.CONSTANT_ID_NULL, nIdDirectory,
                        nIdDirectoryCopy, nIdWorkflowToCopy, nIdWorkflowCopy );
            }

            // success
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATION_SUCCESS_DIRECTORY_SIMPLE );
            url.addParameter( PARAMETER_ID_DIRECTORY, nIdDirectory );
            url.addParameter( PARAMETER_COPY_MODE, strCopyMode );
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

        return url.getUrl( );
    }

    /**
     * Return directory duplication success page
     * @param request The Http request
     * @return Html directory duplication success page
     * @throws AccessDeniedException
     */
    public String getDuplicateDirectorySimpleSuccess( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        String strCopyMode = request.getParameter( PARAMETER_COPY_MODE );

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin( ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_COPY_MODE, strCopyMode );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_DIRECTORY_SIMPLE_SUCCESS,
                getLocale( ),
                model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Return directory duplication choice page
     * @param request The Http request
     * @return Html directory duplication page
     * @throws AccessDeniedException
     */
    public String getDuplicateDirectoryChoice( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin( ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_DIRECTORY, directory );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_DIRECTORY_CHOICE, getLocale( ),
                model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Chooses a duplication method
     * @param request the request
     * @return The URL of the duplication page
     * @throws AccessDeniedException
     */
    public String doDuplicateDirectoryChoice( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        UrlItem url = null;

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin( ) );
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
                url.addParameter( PARAMETER_ID_DIRECTORY, nIdDirectory );
                url.addParameter( PARAMETER_FROM_CHOICE, "true" );
            }
        }
        else
        {
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
        }

        return url.getUrl( );
    }

    /**
     * Return directory duplication page
     * @param request The Http request
     * @return Html directory duplication page
     * @throws AccessDeniedException
     */
    public String getDuplicateDirectoryWithWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdDirectory != WorkflowUtils.CONSTANT_ID_NULL )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin( ) );
        }

        if ( directory == null )
        {
            throw new AccessDeniedException( "Directory not found for ID " + nIdDirectory );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_DIRECTORY, directory );
        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_DIRECTORY_WITH_WORKFLOW,
                getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the directory associted to a given form
     * @param form the form
     * @return the directory or null if no directory found
     */
    private Directory getDirectoryAssociatedTo( Form form )
    {
        Directory directory = null;
        FormConfiguration formConfiguration = FormConfigurationHome.findByPrimaryKey( form.getIdForm( ), getPlugin( ) );

        if ( formConfiguration != null )
        {
            directory = DirectoryHome.findByPrimaryKey( formConfiguration.getIdDirectory( ), getPlugin( ) );
        }

        return directory;
    }

    /**
     * Return form duplication page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException
     */
    public String getDuplicateFormSimple( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin( ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FORM, form );
        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService
                .getTemplate( TEMPLATE_DUPLICATE_FORM_SIMPLE, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Duplicates a form
     * @param request the request
     * @return The URL of the duplication success page
     * @throws AccessDeniedException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public String doDuplicateForm( HttpServletRequest request ) throws AccessDeniedException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        UrlItem url = null;

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin( ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        if ( request.getParameter( PARAMETER_DUPLICATE ) != null )
        {
            String strCopyMode = request.getParameter( PARAMETER_COPY_MODE );
            String strFormCopyTitle = request.getParameter( PARAMETER_FORM_TITLE );

            if ( COPY_MODE_FORM_ONLY.equals( strCopyMode ) )
            {
                // simple copy
                doCopyForm( form, strFormCopyTitle );
            }
            else if ( COPY_MODE_FORM_WITH_DIRECTORY.equals( strCopyMode ) )
            {
                String strDirectoryCopyTitle = request.getParameter( PARAMETER_DIRECTORY_TITLE );

                doCopyFormWithDirectory( form, strFormCopyTitle, strDirectoryCopyTitle );
            }
            else if ( COPY_MODE_FORM_WITH_DIRECTORY_AND_WORKFLOW.equals( strCopyMode ) )
            {
                String strDirectoryCopyTitle = request.getParameter( PARAMETER_DIRECTORY_TITLE );
                String strWorkflowCopyTitle = request.getParameter( PARAMETER_WORKFLOW_TITLE );

                doCopyFormWithDirectoryAndWorkflow( form, strFormCopyTitle, strDirectoryCopyTitle, strWorkflowCopyTitle );
            }

            // success
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DUPLICATION_SUCCESS_FORM );
            url.addParameter( PARAMETER_ID_FORM, nIdForm );
            url.addParameter( PARAMETER_COPY_MODE, strCopyMode );
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

        return url.getUrl( );
    }

    /**
     * Return form duplication success page
     * @param request The Http request
     * @return Html form duplication success page
     * @throws AccessDeniedException
     */
    public String getDuplicateFormSuccess( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        String strCopyMode = request.getParameter( PARAMETER_COPY_MODE );

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin( ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FORM, form );
        model.put( MARK_COPY_MODE, strCopyMode );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_FORM_SUCCESS,
                getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Return form duplication choice page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException
     */
    public String getDuplicateFormChoice( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin( ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        FormWithDirectory formWithDirectory = new FormWithDirectory( );
        formWithDirectory.setForm( form );
        formWithDirectory.setDirectory( getDirectoryAssociatedTo( form ) );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FORM, form );
        model.put( MARK_FORM_WITH_DIRECTORY, formWithDirectory );

        HtmlTemplate templateList = AppTemplateService
                .getTemplate( TEMPLATE_DUPLICATE_FORM_CHOICE, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Chooses a duplication method
     * @param request the request
     * @return The URL of the duplication page
     * @throws AccessDeniedException
     */
    public String doDuplicateFormChoice( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        UrlItem url = null;

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin( ) );
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
                    url = new UrlItem( AppPathService.getBaseUrl( request )
                            + JSP_DUPLICATE_FORM_WITH_DIRECTORY_AND_WORKFLOW );
                }
                url.addParameter( PARAMETER_ID_FORM, nIdForm );
                url.addParameter( PARAMETER_FROM_CHOICE, "true" );
            }
        }
        else
        {
            url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_WIZARD );
        }

        return url.getUrl( );
    }

    /**
     * Return form duplication page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException
     */
    public String getDuplicateFormWithDirectory( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin( ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FORM, form );
        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DUPLICATE_FORM_WITH_DIRECTORY,
                getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Return form duplication page
     * @param request The Http request
     * @return Html form duplication page
     * @throws AccessDeniedException
     */
    public String getDuplicateFormWithDirectoryAndWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        Form form = null;
        String strFromChoice = request.getParameter( PARAMETER_FROM_CHOICE );

        if ( nIdForm != WorkflowUtils.CONSTANT_ID_NULL )
        {
            form = FormHome.findByPrimaryKey( nIdForm, getPlugin( ) );
        }

        if ( form == null )
        {
            throw new AccessDeniedException( "Form not found for ID " + nIdForm );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FORM, form );
        if ( StringUtils.isNotBlank( strFromChoice ) )
        {
            model.put( MARK_FROM_CHOICE, strFromChoice );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate(
                TEMPLATE_DUPLICATE_FORM_WITH_DIRECTORY_AND_WORKFLOW, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Copy a given workflow
     * @param workflowToCopy the workflow to copy
     * @param copyName the name of the copy
     * @return the id of the copy
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private int doCopyWorkflow( Workflow workflowToCopy, String copyName ) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {
        int nIdWorkflow = workflowToCopy.getId( );
        Map<Integer, Integer> mapIdStates = new HashMap<Integer, Integer>( );
        Map<Integer, Integer> mapIdActions = new HashMap<Integer, Integer>( );

        workflowToCopy.setName( copyName );
        _workflowService.create( workflowToCopy );

        //get all the states of the workflow to copy
        List<State> listStatesOfWorkflow = (List<State>) _workflowService.getAllStateByWorkflow( nIdWorkflow );

        for ( State state : listStatesOfWorkflow )
        {
            state.setWorkflow( workflowToCopy );

            //get the maximum order number in this workflow and set max+1
            int nMaximumOrder = _stateService.findMaximumOrderByWorkflowId( state.getWorkflow( ).getId( ) );
            state.setOrder( nMaximumOrder + 1 );

            // Save state to copy id
            Integer nOldIdState = state.getId( );

            // Create new state (this action will change state id with the new idState)
            _stateService.create( state );

            mapIdStates.put( nOldIdState, state.getId( ) );
        }

        //get all the actions of the workflow to copy
        ActionFilter actionFilter = new ActionFilter( );
        actionFilter.setIdWorkflow( nIdWorkflow );

        List<Action> listActionsOfWorkflow = _actionService.getListActionByFilter( actionFilter );

        for ( Action action : listActionsOfWorkflow )
        {
            action.setWorkflow( workflowToCopy );

            //get the maximum order number in this workflow and set max+1
            int nMaximumOrder = _actionService.findMaximumOrderByWorkflowId( action.getWorkflow( ).getId( ) );
            action.setOrder( nMaximumOrder + 1 );

            // Change idState to set the new state
            action.getStateBefore( ).setId( mapIdStates.get( action.getStateBefore( ).getId( ) ) );
            action.getStateAfter( ).setId( mapIdStates.get( action.getStateAfter( ).getId( ) ) );

            int nOldIdAction = action.getId( );

            //get the linked tasks and duplicate them
            List<ITask> listLinkedTasks = _taskService.getListTaskByIdAction( action.getId( ), this.getLocale( ) );

            _actionService.create( action );

            mapIdActions.put( nOldIdAction, action.getId( ) );

            for ( ITask task : listLinkedTasks )
            {
                //for each we change the linked action
                task.setAction( action );

                //and then we create the new task duplicated
                this.doCopyTaskWithModifiedParam( task, null );
            }
        }

        //get all the linked actions
        actionFilter = new ActionFilter( );
        actionFilter.setIdWorkflow( workflowToCopy.getId( ) );

        List<Action> listActionsOfNewWorkflow = _actionService.getListActionByFilter( actionFilter );

        for ( Action action : listActionsOfNewWorkflow )
        {
            List<Integer> newListIdsLinkedAction = new ArrayList<Integer>( );

            for ( Integer nIdActionLinked : action.getListIdsLinkedAction( ) )
            {
                newListIdsLinkedAction.add( mapIdActions.get( nIdActionLinked ) );
            }

            action.setListIdsLinkedAction( newListIdsLinkedAction );
            _actionService.update( action );
        }
        return workflowToCopy.getId( );
    }

    /**
     * Copy the task whose key is specified in the Http request and update param
     * if exists
     * @param taskToCopy the task to copy
     * @param mapParamToChange the map<String, String> of <Param, Value> to
     *            change
     * @throws NoSuchMethodException NoSuchMethodException the
     *             {@link NoSuchMethodException}
     * @throws IllegalAccessException IllegalAccessException the
     *             {@link IllegalAccessException}
     * @throws InvocationTargetException InvocationTargetException the
     *             {@link InvocationTargetException}
     */
    private void doCopyTaskWithModifiedParam( ITask taskToCopy, Map<String, String> mapParamToChange )
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Save nIdTaskToCopy
        Integer nIdTaskToCopy = taskToCopy.getId( );

        //get the maximum order number in this workflow and set max+1
        int nMaximumOrder = _taskService.findMaximumOrderByWorkflowId( taskToCopy.getAction( ).getId( ) );
        taskToCopy.setOrder( nMaximumOrder + 1 );

        // Create the new task (taskToCopy id will be update with the new idTask)
        _taskService.create( taskToCopy );

        // get all taskConfigService
        List<ITaskConfigService> listTaskConfigService = SpringContextService.getBeansOfType( ITaskConfigService.class );

        // For each taskConfigService, update parameter if exists
        for ( ITaskConfigService taskConfigService : listTaskConfigService )
        {
            ITaskConfig taskConfig = taskConfigService.findByPrimaryKey( nIdTaskToCopy );

            if ( taskConfig != null )
            {
                taskConfig.setIdTask( taskToCopy.getId( ) );

                if ( mapParamToChange != null )
                {
                    EntrySetMapIterator it = new EntrySetMapIterator( mapParamToChange );

                    while ( it.hasNext( ) )
                    {
                        String key = (String) it.next( );
                        String value = (String) it.getValue( );
                        MethodUtil.set( taskConfig, key, value );
                    }
                }

                taskConfigService.create( taskConfig );
            }
        }
    }

    /**
     * Copy a given directory
     * @param directoryToCopy the directory to copy
     * @param copyName the name of the copy
     * @return the id of the copy
     */
    private int doCopyDirectory( Directory directoryToCopy, String copyName )
    {
        directoryToCopy.setTitle( copyName );
        DirectoryHome.copy( directoryToCopy, getPlugin( ) );

        return directoryToCopy.getIdDirectory( );
    }

    /**
     * Copy a given form
     * @param formToCopy the form to copy
     * @param copyName the name of the copy
     * @return the id of the copy
     */
    private int doCopyForm( Form formToCopy, String copyName )
    {
        formToCopy.setTitle( copyName );
        FormHome.copy( formToCopy, getPlugin( ) );

        return formToCopy.getIdForm( );
    }

    /**
     * Copy a given directory and its workflow
     * @param directoryToCopy the directory to copy
     * @param copyName the name of the copy
     * @return the id of the copy
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private int doCopyDirectoryWithWorkflow( Directory directoryToCopy, String directoryCopyName,
            String workflowCopyName ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Workflow workflowToCopy = _workflowService.findByPrimaryKey( directoryToCopy.getIdWorkflow( ) );
        int nIdWorkflowCopy = doCopyWorkflow( workflowToCopy, workflowCopyName );

        directoryToCopy.setIdWorkflow( nIdWorkflowCopy );
        int nIdDirectoryCopy = doCopyDirectory( directoryToCopy, directoryCopyName );

        return nIdDirectoryCopy;
    }

    /**
     * Copy a given form with its directory
     * @param formToCopy the form to copy
     * @param copyName the name of the copy
     * @return the id of the copy
     */
    private int doCopyFormWithDirectory( Form formToCopy, String formCopyName, String directoryCopyName )
    {
        Directory directoryToCopy = getDirectoryAssociatedTo( formToCopy );
        int nIdDirectoryCopy = doCopyDirectory( directoryToCopy, directoryCopyName );

        int nIdFormCopy = doCopyForm( formToCopy, formCopyName );

        // TODO copy export-directory

        return nIdFormCopy;
    }

    /**
     * Copy a given form with its directory and workflow
     * @param formToCopy the form to copy
     * @param copyName the name of the copy
     * @return the id of the copy
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private int doCopyFormWithDirectoryAndWorkflow( Form formToCopy, String formCopyName, String directoryCopyName,
            String workflowCopyName ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Directory directoryToCopy = getDirectoryAssociatedTo( formToCopy );
        int nIdDirectoryCopy = doCopyDirectoryWithWorkflow( directoryToCopy, directoryCopyName, workflowCopyName );

        int nIdFormCopy = doCopyForm( formToCopy, formCopyName );

        // TODO copy export-directory

        return nIdFormCopy;
    }

    /**
     * Performs extra duplications, parameters must be set to -1 if not needed
     * @param nIdFormToCopy
     * @param nIdCopyOfForm
     * @param nIdDirectoryToCopy
     * @param nIdCopyOfDirectory
     * @param nIdWorkflowToCopy
     * @param nIdCopyOfWorkflow
     */
    private void doExtraDuplication( int nIdFormToCopy, int nIdCopyOfForm, int nIdDirectoryToCopy,
            int nIdCopyOfDirectory, int nIdWorkflowToCopy, int nIdCopyOfWorkflow )
    {
        Form formToCopy = null;
        Form copyOfForm = null;
        Directory directoryToCopy = null;
        Directory copyOfDirectory = null;
        Workflow workflowToCopy = null;
        Workflow copyOfWorkflow = null;

        if ( nIdFormToCopy > 0 )
        {
            formToCopy = FormHome.findByPrimaryKey( nIdFormToCopy, getPlugin( ) );
        }
        if ( nIdCopyOfForm > 0 )
        {
            copyOfForm = FormHome.findByPrimaryKey( nIdCopyOfForm, getPlugin( ) );
        }

        if ( nIdDirectoryToCopy > 0 )
        {
            directoryToCopy = DirectoryHome.findByPrimaryKey( nIdDirectoryToCopy, getPlugin( ) );
        }
        if ( nIdCopyOfDirectory > 0 )
        {
            copyOfDirectory = DirectoryHome.findByPrimaryKey( nIdCopyOfDirectory, getPlugin( ) );
        }

        if ( nIdWorkflowToCopy > 0 )
        {
            workflowToCopy = _workflowService.findByPrimaryKey( nIdWorkflowToCopy );
        }
        if ( nIdCopyOfWorkflow > 0 )
        {
            copyOfWorkflow = _workflowService.findByPrimaryKey( nIdCopyOfWorkflow );
        }

        DuplicationManager.doDuplicate( formToCopy, copyOfForm, directoryToCopy, copyOfDirectory, workflowToCopy,
                copyOfWorkflow );
    }
}
