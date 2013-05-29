CKEDITOR.plugins.addExternal('fnigenericbrowser', '../ckplugins/fnigenericbrowser/');
CKEDITOR.plugins.addExternal('fnidynlist', '../ckplugins/fnidynlist/');

CKEDITOR.config.scayt_autoStartup = false;
CKEDITOR.config.entities = false;
CKEDITOR.config.autoGrow_onStartup = true;
CKEDITOR.config.autoGrow_minHeight = 300;
CKEDITOR.config.autoGrow_maxHeight = 600;
CKEDITOR.config.forcePasteAsPlainText = true;

CKEDITOR.config.extraPlugins = 'autogrow,mediaembed,fnigenericbrowser';

CKEDITOR.config.removePlugins = 'elementspath';
CKEDITOR.config.toolbarCanCollapse = false;
CKEDITOR.config.resize_dir = 'vertical';

//var defaultToolbar = [
//  ['Cut','Copy','Paste','PasteText','-', 'Scayt'],
//  ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
//  ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
//  ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
//  ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
//  ['Link','Unlink'],
//  ['Image','MediaEmbed','Table','HorizontalRule','SpecialChar'],
//  '/',
//  ['Format','Font','FontSize'],
//  ['TextColor','BGColor'],
//  ['Maximize', 'ShowBlocks','-','About']
//];

CKEDITOR.config.toolbar_materialToolbar = [
  ['Cut','Copy','Paste','PasteText','-', 'Scayt'],
  ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
  ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
  ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
  ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
  ['Link','Unlink'],
  ['Image','MediaEmbed','Flash','Table','HorizontalRule','SpecialChar'],
  ['FontSize'],
  ['Maximize', 'ShowBlocks','-','About']
];

CKEDITOR.config.toolbar_thesisDescriptionToolbar = [
  ['Bold','Italic','Underline'],['Link','Unlink'],['Image','MediaEmbed'],['Table'],['NumberedList','BulletedList']
];

CKEDITOR.config.toolbar_simpleToolbar = [
  ['Bold','Italic','Underline']
];
