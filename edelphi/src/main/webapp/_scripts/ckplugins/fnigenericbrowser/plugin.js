(function() {
  
  function openBrowseView(evt) {
    var browseButton = evt.sender;
    var dialog = browseButton._.dialog;    
    var editor = dialog._.editor;
    editor._fniGenericBrowserTarget = browseButton.filebrowser.target.split(':');
    editor._fniGenericBrowserDialog = dialog;
    editor.openDialog("fnigenericbrowser_browse");
  }
  
  function setResult(data) {
    var dialog = this._fniGenericBrowserDialog;
    var target = this._fniGenericBrowserTarget;
    
    this._fniGenericBrowserTarget = undefined;
    this._fniGenericBrowserDialog = undefined;
    
    var element = dialog.getContentElement(target[0],target[1]);
    if (element) {
      element.setValue(data.value);
      dialog.selectPage(target[0]);
    }
  }
  
  function isBrowserEnabledInDialog(editor, dialogName) {
    var conf = editor.config.fniGenericBrowser;
    var enabledInDialogs = conf.enabledInDialogs;
    for (var i = 0, l = enabledInDialogs.length; i < l; i++) {
      if (enabledInDialogs[i] == dialogName)
        return true;
    }
    
    return false;
  }

  function attachBrowser(editor, dialogName, definition, elements) {
    var element;

    for ( var i in elements) {
      element = elements[i];

      if (element.type == 'hbox' || element.type == 'vbox')
        attachBrowser(editor, dialogName, definition, element.children);

      if (!element.filebrowser)
        continue;
      
      element.onClick = openBrowseView;
      element.hidden = false;
    }
  }

  CKEDITOR.plugins.add('fnigenericbrowser', {
    lang : [ 'fi', 'en' ],
    requires: ['ajax','fnidynlist'],
    init : function(editor, pluginPath) {
      CKEDITOR.dialog.add('fnigenericbrowser_browse', this.path + 'dialogs/browse.js');
    
      editor._._fniGenericBrowserSetResult = CKEDITOR.tools.addFunction(setResult, editor);
      
      CKEDITOR.on('dialogDefinition', function(evt) {
        for ( var i in evt.data.definition.contents) {
          var dialogName = evt.data.name;
          var definition = evt.data.definition;
          var tab = definition.contents[i];
          if (isBrowserEnabledInDialog(evt.editor, dialogName))
            attachBrowser(evt.editor, dialogName, definition, tab.elements);
        }
      });
    }
  });
})();
