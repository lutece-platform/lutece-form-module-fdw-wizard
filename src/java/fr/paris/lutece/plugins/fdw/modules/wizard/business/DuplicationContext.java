/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.fdw.modules.wizard.business;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Locale;


/**
 * The context associated to a duplication operation.
 * Contains all the elements needed to perform a duplication.
 *
 */
public class DuplicationContext
{
    private Directory _directoryToCopy;
    private Directory _directoryCopy;
    private Form _formToCopy;
    private Form _formCopy;
    private Workflow _workflowToCopy;
    private Workflow _workflowCopy;
    private Plugin _plugin;
    private Locale _locale;
    private boolean _directoryDuplication;
    private boolean _formDuplication;
    private boolean _workflowDuplication;
    private String _directoryCopyName;
    private String _formCopyName;
    private String _workflowCopyName;

    /**
     * @return the directoryToCopy
     */
    public Directory getDirectoryToCopy(  )
    {
        return _directoryToCopy;
    }

    /**
     * @param directoryToCopy the directoryToCopy to set
     */
    public void setDirectoryToCopy( Directory directoryToCopy )
    {
        this._directoryToCopy = directoryToCopy;
    }

    /**
     * @return the directoryCopy
     */
    public Directory getDirectoryCopy(  )
    {
        return _directoryCopy;
    }

    /**
     * @param directoryCopy the directoryCopy to set
     */
    public void setDirectoryCopy( Directory directoryCopy )
    {
        this._directoryCopy = directoryCopy;
    }

    /**
     * @return the formToCopy
     */
    public Form getFormToCopy(  )
    {
        return _formToCopy;
    }

    /**
     * @param formToCopy the formToCopy to set
     */
    public void setFormToCopy( Form formToCopy )
    {
        this._formToCopy = formToCopy;
    }

    /**
     * @return the formCopy
     */
    public Form getFormCopy(  )
    {
        return _formCopy;
    }

    /**
     * @param formCopy the formCopy to set
     */
    public void setFormCopy( Form formCopy )
    {
        this._formCopy = formCopy;
    }

    /**
     * @return the workflowToCopy
     */
    public Workflow getWorkflowToCopy(  )
    {
        return _workflowToCopy;
    }

    /**
     * @param workflowToCopy the workflowToCopy to set
     */
    public void setWorkflowToCopy( Workflow workflowToCopy )
    {
        this._workflowToCopy = workflowToCopy;
    }

    /**
     * @return the workflowCopy
     */
    public Workflow getWorkflowCopy(  )
    {
        return _workflowCopy;
    }

    /**
     * @param workflowCopy the workflowCopy to set
     */
    public void setWorkflowCopy( Workflow workflowCopy )
    {
        this._workflowCopy = workflowCopy;
    }

    /**
     * @return the plugin
     */
    public Plugin getPlugin(  )
    {
        return _plugin;
    }

    /**
     * @param plugin the plugin to set
     */
    public void setPlugin( Plugin plugin )
    {
        this._plugin = plugin;
    }

    /**
     * @return the locale
     */
    public Locale getLocale(  )
    {
        return _locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale( Locale locale )
    {
        this._locale = locale;
    }

    /**
     * @return the directoryDuplication
     */
    public boolean isDirectoryDuplication(  )
    {
        return _directoryDuplication;
    }

    /**
     * @param directoryDuplication the directoryDuplication to set
     */
    public void setDirectoryDuplication( boolean directoryDuplication )
    {
        this._directoryDuplication = directoryDuplication;
    }

    /**
     * @return the formDuplication
     */
    public boolean isFormDuplication(  )
    {
        return _formDuplication;
    }

    /**
     * @param formDuplication the formDuplication to set
     */
    public void setFormDuplication( boolean formDuplication )
    {
        this._formDuplication = formDuplication;
    }

    /**
     * @return the workflowDuplication
     */
    public boolean isWorkflowDuplication(  )
    {
        return _workflowDuplication;
    }

    /**
     * @param workflowDuplication the workflowDuplication to set
     */
    public void setWorkflowDuplication( boolean workflowDuplication )
    {
        this._workflowDuplication = workflowDuplication;
    }

    /**
     * @return the directoryCopyName
     */
    public String getDirectoryCopyName(  )
    {
        return _directoryCopyName;
    }

    /**
     * @param directoryCopyName the directoryCopyName to set
     */
    public void setDirectoryCopyName( String directoryCopyName )
    {
        this._directoryCopyName = directoryCopyName;
    }

    /**
     * @return the formCopyName
     */
    public String getFormCopyName(  )
    {
        return _formCopyName;
    }

    /**
     * @param formCopyName the formCopyName to set
     */
    public void setFormCopyName( String formCopyName )
    {
        this._formCopyName = formCopyName;
    }

    /**
     * @return the workflowCopyName
     */
    public String getWorkflowCopyName(  )
    {
        return _workflowCopyName;
    }

    /**
     * @param workflowCopyName the workflowCopyName to set
     */
    public void setWorkflowCopyName( String workflowCopyName )
    {
        this._workflowCopyName = workflowCopyName;
    }
}
