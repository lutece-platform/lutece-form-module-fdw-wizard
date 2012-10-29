-- add the admin right for the module fdw wizard
INSERT INTO core_admin_right (id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order)
VALUES ('FDW_WIZARD_MANAGEMENT', 'module.fdw.wizard.adminFeature.fdw-wizard_management.name', '2', 'jsp/admin/plugins/fdw/modules/wizard/ManageWizard.jsp', 'module.fdw.wizard.adminFeature.fdw-wizard_management.description', '0', 'fdw-wizard', NULL, NULL, 'jsp/admin/documentation/AdminDocumentation.jsp?doc=admin-directory', '4');

-- add the role for admin for the module fdw wizard
INSERT INTO core_user_right VALUES ('FDW_WIZARD_MANAGEMENT',1);
INSERT INTO core_user_right VALUES ('FDW_WIZARD_MANAGEMENT',2);