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
package fr.paris.lutece.plugins.fdw.modules.wizard.rights;

import fr.paris.lutece.plugins.form.service.FormResourceIdService;

import java.util.HashMap;
import java.util.Map;


public class MappingJspPermission
{
    //CONSTANTS
    public static final String PERMISSION_MODIFY = "MODIFY";
    public static final String KEY_ID_RESOURCE_FORM = "FORM_FORM_TYPE";
    public static final String KEY_ID_RESOURCE_DIRECTORY = "DIRECTORY_DIRECTORY_TYPE";
    public static final String KEY_ID_RESOURCE_WORKFLOW = "WORKFLOW_WORKFLOW_TYPE";
    public static final Map<String, PermissionResourceType> MAPPING_JSP_PERMISSIONS;

    static
    {
        MAPPING_JSP_PERMISSIONS = new HashMap<String, PermissionResourceType>(  );

        //Modify Form
        MAPPING_JSP_PERMISSIONS.put( "ModifyForm.jsp",
            new PermissionResourceType( KEY_ID_RESOURCE_FORM, FormResourceIdService.PERMISSION_MODIFY ) );

        //Modify Directory
        MAPPING_JSP_PERMISSIONS.put( "ModifyDirectory.jsp",
            new PermissionResourceType( KEY_ID_RESOURCE_DIRECTORY, FormResourceIdService.PERMISSION_MODIFY ) );
    }
}
