/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.fdw.modules.wizard.service;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.fdw.modules.wizard.exception.DuplicationException;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.method.MethodUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.iterators.EntrySetMapIterator;


/**
 *
 * WizardService
 *
 */
public class WizardService
{
    public static final String BEAN_SERVICE = "fdw-wizard.wizardService";
    private static WizardService _singleton;

    //services
    private IWorkflowService _workflowService;
    private IActionService _actionService;
    private IStateService _stateService;
    private ITaskService _taskService;

    /**
     * Initialize the service
     *
     */
    public void init(  )
    {
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static synchronized WizardService getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new WizardService(  );
        }

        return _singleton;
    }

    /**
     * @param workflowService the workflowService to set
     */
    public void setWorkflowService( IWorkflowService workflowService )
    {
        this._workflowService = workflowService;
    }

    /**
     * @param actionService the actionService to set
     */
    public void setActionService( IActionService actionService )
    {
        this._actionService = actionService;
    }

    /**
     * @param stateService the stateService to set
     */
    public void setStateService( IStateService stateService )
    {
        this._stateService = stateService;
    }

    /**
     * @param taskService the taskService to set
     */
    public void setTaskService( ITaskService taskService )
    {
        this._taskService = taskService;
    }

    /**
     * Gets a directory
     * @param nIdDirectory the id of the directory
     * @param plugin the plugin
     * @return the directory
     */
    public Directory getDirectory( int nIdDirectory, Plugin plugin )
    {
        return DirectoryHome.findByPrimaryKey( nIdDirectory, plugin );
    }

    /**
     * Gets a form
     * @param nIdForm the id of the form
     * @param plugin the plugin
     * @return the form
     */
    public Form getForm( int nIdForm, Plugin plugin )
    {
        return FormHome.findByPrimaryKey( nIdForm, plugin );
    }

    /**
     * Gets a workflow
     * @param nIdWorkflow the id of the workflow
     * @return the workflow
     */
    public Workflow getWorkflow( int nIdWorkflow )
    {
        return _workflowService.findByPrimaryKey( nIdWorkflow );
    }

    /**
     * Copy a given workflow
     * @param workflowToCopy the workflow to copy
     * @param copyName the name of the copy
     * @param locale the locale
     * @return the id of the copy
     * @throws DuplicationException the duplication exception
     */
    public int doCopyWorkflow( Workflow workflowToCopy, String copyName, Locale locale )
        throws DuplicationException
    {
        try
        {
            int nIdWorkflow = workflowToCopy.getId(  );
            Map<Integer, Integer> mapIdStates = new HashMap<Integer, Integer>(  );
            Map<Integer, Integer> mapIdActions = new HashMap<Integer, Integer>(  );

            workflowToCopy.setName( copyName );
            _workflowService.create( workflowToCopy );

            //get all the states of the workflow to copy
            List<State> listStatesOfWorkflow = (List<State>) _workflowService.getAllStateByWorkflow( nIdWorkflow );

            for ( State state : listStatesOfWorkflow )
            {
                state.setWorkflow( workflowToCopy );

                //get the maximum order number in this workflow and set max+1
                int nMaximumOrder = _stateService.findMaximumOrderByWorkflowId( state.getWorkflow(  ).getId(  ) );
                state.setOrder( nMaximumOrder + 1 );

                // Save state to copy id
                Integer nOldIdState = state.getId(  );

                // Create new state (this action will change state id with the new idState)
                _stateService.create( state );

                mapIdStates.put( nOldIdState, state.getId(  ) );
            }

            //get all the actions of the workflow to copy
            ActionFilter actionFilter = new ActionFilter(  );
            actionFilter.setIdWorkflow( nIdWorkflow );

            List<Action> listActionsOfWorkflow = _actionService.getListActionByFilter( actionFilter );

            for ( Action action : listActionsOfWorkflow )
            {
                action.setWorkflow( workflowToCopy );

                //get the maximum order number in this workflow and set max+1
                int nMaximumOrder = _actionService.findMaximumOrderByWorkflowId( action.getWorkflow(  ).getId(  ) );
                action.setOrder( nMaximumOrder + 1 );

                // Change idState to set the new state
                action.getStateBefore(  ).setId( mapIdStates.get( action.getStateBefore(  ).getId(  ) ) );
                action.getStateAfter(  ).setId( mapIdStates.get( action.getStateAfter(  ).getId(  ) ) );

                int nOldIdAction = action.getId(  );

                //get the linked tasks and duplicate them
                List<ITask> listLinkedTasks = _taskService.getListTaskByIdAction( action.getId(  ), locale );

                _actionService.create( action );

                mapIdActions.put( nOldIdAction, action.getId(  ) );

                for ( ITask task : listLinkedTasks )
                {
                    //for each we change the linked action
                    task.setAction( action );

                    //and then we create the new task duplicated
                    this.doCopyTaskWithModifiedParam( task, null );
                }
            }

            //get all the linked actions
            actionFilter = new ActionFilter(  );
            actionFilter.setIdWorkflow( workflowToCopy.getId(  ) );

            List<Action> listActionsOfNewWorkflow = _actionService.getListActionByFilter( actionFilter );

            for ( Action action : listActionsOfNewWorkflow )
            {
                List<Integer> newListIdsLinkedAction = new ArrayList<Integer>(  );

                for ( Integer nIdActionLinked : action.getListIdsLinkedAction(  ) )
                {
                    newListIdsLinkedAction.add( mapIdActions.get( nIdActionLinked ) );
                }

                action.setListIdsLinkedAction( newListIdsLinkedAction );
                _actionService.update( action );
            }
        }
        catch ( InvocationTargetException e )
        {
            throw new DuplicationException( e );
        }
        catch ( NoSuchMethodException e )
        {
            throw new DuplicationException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new DuplicationException( e );
        }

        return workflowToCopy.getId(  );
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
    public void doCopyTaskWithModifiedParam( ITask taskToCopy, Map<String, String> mapParamToChange )
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Save nIdTaskToCopy
        Integer nIdTaskToCopy = taskToCopy.getId(  );

        //get the maximum order number in this workflow and set max+1
        int nMaximumOrder = _taskService.findMaximumOrderByActionId( taskToCopy.getAction( ).getId( ) );
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
                taskConfig.setIdTask( taskToCopy.getId(  ) );

                if ( mapParamToChange != null )
                {
                    EntrySetMapIterator it = new EntrySetMapIterator( mapParamToChange );

                    while ( it.hasNext(  ) )
                    {
                        String key = (String) it.next(  );
                        String value = (String) it.getValue(  );
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
     * @param plugin the plugin
     * @return the id of the copy
     */
    public int doCopyDirectory( Directory directoryToCopy, String copyName, Plugin plugin )
    {
        directoryToCopy.setTitle( copyName );
        DirectoryHome.copy( directoryToCopy, plugin );

        return directoryToCopy.getIdDirectory(  );
    }

    /**
     * Copy a given form
     * @param formToCopy the form to copy
     * @param copyName the name of the copy
     * @param plugin the plugin
     * @return the id of the copy
     */
    public int doCopyForm( Form formToCopy, String copyName, Plugin plugin )
    {
        formToCopy.setTitle( copyName );
        FormHome.copy( formToCopy, plugin );

        return formToCopy.getIdForm(  );
    }

    /**
     * Copy a given directory and its workflow
     * @param directoryToCopy the directory to copy
     * @param directoryCopyName the name of the copy
     * @param workflowCopyName the name of the copy
     * @param plugin the plugin
     * @param locale the locale
     * @return the id of the copy
     * @throws DuplicationException the duplication exception
     */
    public int doCopyDirectoryWithWorkflow( Directory directoryToCopy, String directoryCopyName,
        String workflowCopyName, Plugin plugin, Locale locale )
        throws DuplicationException
    {
        Workflow workflowToCopy = _workflowService.findByPrimaryKey( directoryToCopy.getIdWorkflow(  ) );
        int nIdWorkflowCopy = doCopyWorkflow( workflowToCopy, workflowCopyName, locale );

        directoryToCopy.setIdWorkflow( nIdWorkflowCopy );

        int nIdDirectoryCopy = doCopyDirectory( directoryToCopy, directoryCopyName, plugin );

        return nIdDirectoryCopy;
    }

    /**
     * Gets the directory associted to a given form
     * @param form the form
     * @return the directory or null if no directory found
     */
    public Directory getDirectoryAssociatedToForm( Form form )
    {
        Directory directory = null;

        for ( IFormDirectoryAssociationService associationService : SpringContextService.getBeansOfType( 
                IFormDirectoryAssociationService.class ) )
        {
            Directory tmpDirectory = associationService.getDirectoryAssociatedToForm( form );

            if ( tmpDirectory != null )
            {
                directory = tmpDirectory;
            }
        }

        return directory;
    }

    /**
     * Deletes a given workflow
     * @param nIdWorkflow the id of the workflow
     */
    public void deleteWorkflow( int nIdWorkflow )
    {
        _workflowService.remove( nIdWorkflow );
    }
}
