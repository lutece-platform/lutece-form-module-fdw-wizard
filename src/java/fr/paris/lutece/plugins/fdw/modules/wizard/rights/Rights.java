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
package fr.paris.lutece.plugins.fdw.modules.wizard.rights;

import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.rbac.RBACService;

import javax.servlet.http.HttpServletRequest;


/**
 * Class Rights
 *
 */
public class Rights
{
    //CONSTANTS
    public static final String PERMISSIONS = "PERMISSIONS";
    private static final String PERMISSION_JOKER = "*";

    //REQUEST
    private HttpServletRequest _request;

    /**
     * Initialize Rights
     * @param request http
     */
    public void init( HttpServletRequest request )
    {
        this._request = request;
    }

    /**
     * Return true if the user has the given permission
     * @param resourceType type of resource (use case)
     * @param permission key of the permission
     * @param idResource the id of the resource
     * @return authorized or not (true or false)
     */
    public boolean isAuthorized( String resourceType, String permission, String idResource )
    {
        boolean authorizedPermission = RBACService.isAuthorized( resourceType, idResource, permission,
                AdminUserService.getAdminUser( _request ) );

        return authorizedPermission;
    }

    /**
     * Return true if the user has the right to get into the given jsp (form)
     * Uses the mapping jsp -> permission
     * @param jsp the jsp url
     * @param idResource the id of the resource
     * @return authorized or not (true or false)
     */
    public boolean isAuthorizedJspForm( String jsp, String idResource )
    {
        final int LONG_PREFIX_PATH = 23; //length of jsp/admin/plugins/form/
        Boolean authorized = false;

        if ( jsp.equals( "#" ) || jsp.equals( PERMISSION_JOKER ) )
        {
            authorized = true;
        }
        else
        {
            if ( jsp.length(  ) > LONG_PREFIX_PATH )
            {
                jsp = jsp.substring( LONG_PREFIX_PATH );
            }

            PermissionResourceType permissionResource = MappingJspPermission.MAPPING_JSP_PERMISSIONS.get( jsp );

            if ( permissionResource != null )
            {
                authorized = isAuthorized( permissionResource.getResourceType(  ),
                        permissionResource.getPermission(  ), idResource );
            }
            else
            {
                authorized = false;
            }
        }

        return authorized;
    }

    /**
     * Return true if the user has the right to get into the given jsp
     * (directory)
     * Uses the mapping jsp -> permission
     * @param jsp the jsp url
     * @param idResource the id resource
     * @return authorized or not (true or false)
     */
    public boolean isAuthorizedJspDirectory( String jsp, String idResource )
    {
        final int LONG_PREFIX_PATH = 28; //length of jsp/admin/plugins/directory/
        Boolean authorized = false;

        if ( jsp.equals( "#" ) || jsp.equals( PERMISSION_JOKER ) )
        {
            authorized = true;
        }
        else
        {
            if ( jsp.length(  ) > LONG_PREFIX_PATH )
            {
                jsp = jsp.substring( LONG_PREFIX_PATH );
            }

            PermissionResourceType permissionResource = MappingJspPermission.MAPPING_JSP_PERMISSIONS.get( jsp );

            if ( permissionResource != null )
            {
                authorized = isAuthorized( permissionResource.getResourceType(  ),
                        permissionResource.getPermission(  ), idResource );
            }
            else
            {
                authorized = false;
            }
        }

        return authorized;
    }
}
