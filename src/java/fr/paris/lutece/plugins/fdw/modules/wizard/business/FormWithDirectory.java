/**
 * 
 */
package fr.paris.lutece.plugins.fdw.modules.wizard.business;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.form.business.Form;


/**
 * 
 *
 */
public class FormWithDirectory
{
    private Form _form = null;
    private Directory _directory = null;

    /**
     * @return the form
     */
    public Form getForm( )
    {
        return _form;
    }

    /**
     * @param form the form to set
     */
    public void setForm( Form form )
    {
        this._form = form;
    }

    /**
     * @return the directory
     */
    public Directory getDirectory( )
    {
        return _directory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory( Directory directory )
    {
        this._directory = directory;
    }

}
