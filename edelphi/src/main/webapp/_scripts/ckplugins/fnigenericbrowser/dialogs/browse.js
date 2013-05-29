(function() {
  
  API = {
    doGet : function(url, onSuccess, onFailure) {
      var response = CKEDITOR.ajax.load(url);
      var responseJson = eval("(" + response + ")");
      
      if (responseJson.status == 'OK') {
        onSuccess(responseJson);
      } else {
        onFailure();
      }
    },
    listMaterials: function (editor, dialogName, parent, callback) {
      var connectorUrl = editor.config.fniGenericBrowser.connectorUrl;
      var url = connectorUrl + (connectorUrl.indexOf("?") > 0 ? '&' : '?') + "dialog=" + dialogName +  '&action=LIST_MATERIALS';
      
      this.doGet(url, function (responseJson) {
        callback(responseJson.materials);
      }, function () {
        alert('Could not execute listMaterials method');
      });
    }  
  };
  
  function loadMaterials(dialog, parent) {
    var rootDialog = dialog._.editor._fniGenericBrowserDialog;
    var dialogName = rootDialog._.name;
    
    var lang = dialog._.editor.lang.fnigenericbrowser.browseDialog;

    var materialsList = dialog.getContentElement("materials", "list");
    materialsList.removeRows();

    if (parent != null) {
      materialsList.addRow([CKEDITOR.plugins.getPath('fnigenericbrowser') + 'images/go-up.png', lang.materialsListOpenParentFolder, 'ParentFolder', '..', parent ]);
    }

    materialsList.startLoading();
    API.listMaterials(dialog._.editor, dialogName, parent, function (materials){
      var rows = new Array();
      
      for (var i = 0, l = materials.length; i < l; i++) {
        var id = materials[i].id;
        var name = materials[i].name;
        var type = materials[i].type;
        var path = materials[i].path;
        var iconUrl = materials[i].iconUrl;
        
        rows.push( [ iconUrl, name, type, path, id, parent ]);
      }
      
      materialsList.addRows(rows);
      materialsList.stopLoading();
    });
  };

  CKEDITOR.dialog.add("fnigenericbrowser_browse", function(editor) {
    var lang = editor.lang.fnigenericbrowser.browseDialog;

    return {
      title : lang.title,
      minWidth : 400,
      minHeight : 440,
      contents : [ {
        id : 'materials',
        label : lang.materialsTabLabel,
        expand : false,
        padding : 0,
        elements : [ {
          type : 'dynList',
          id : 'list',
          label : lang.materialsListTitle,
          contentHeight : 400,
          contentWidth: 400,
          onEmptyText : lang.materialsEmptyFolder,
          useHoverEffect : true,
          rowStyle : "cursor: pointer",
          onRowClick : function(event) {
            var dialog = this;
            var list = dialog.getContentElement("materials", "list");
            var resourceType = list.getCellValue(event.rowId, 2);
            var resourcePath = list.getCellValue(event.rowId, 3);
            var resourceId = list.getCellValue(event.rowId, 4);

            switch (resourceType) {
            case 'ParentFolder':
              list.startLoading();
              // TODO: Folder up
              break;
            case 'Folder':
              loadMaterials(dialog, resourceId);
              break;
            default:
              var editor = dialog._.editor;
              CKEDITOR.tools.callFunction(editor._._fniGenericBrowserSetResult, {
                value : resourcePath
              });
              dialog.hide();
              break;
            }
          },
          columns : [ {
            title : '&nbsp;',
            type : 'icon'
          }, {
            title : lang.materialsListNameColumnTitle
          }, {
            type : 'hidden'
          }, {
            type : 'hidden'
          }, {
            type : 'hidden'
          } ]
        } ]
      }],
      buttons : [ CKEDITOR.dialog.cancelButton ],
      onShow : function(event) {
        var dialog = event.sender;
        loadMaterials(dialog, null);
      }
    };
  });
})();