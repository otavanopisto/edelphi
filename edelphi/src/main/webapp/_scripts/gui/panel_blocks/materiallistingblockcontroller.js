MaterialListingBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    this._hideLinkClickListener = this._onHideLinkClick.bindAsEventListener(this);
    this._showLinkClickListener = this._onShowLinkClick.bindAsEventListener(this);
    this._deleteClickListener = this._onDeleteClick.bindAsEventListener(this);
    this._createFolderClickListener = this._onCreateFolderClick.bindAsEventListener(this);
    
    this.setup();
  },
  setup: function ($super) {
    var _this = this;
    $$(".materialsBlock").each(function (materialsElement) {
      var listElement = materialsElement.down(".materialsBlockList");
      
      listElement.select('.blockContextualLink.hide').each(function (linkElement) {
        Event.observe(linkElement, "click", _this._hideLinkClickListener);
      });
      
      listElement.select('.blockContextualLink.show').each(function (linkElement) {
        Event.observe(linkElement, "click", _this._showLinkClickListener);
      });

      listElement.select('.blockContextualLink.delete').each(function (linkElement) {
        Event.observe(linkElement, "click", _this._deleteClickListener);
      });
      
      materialsElement.select('.blockContextMenuItem.CREATEFOLDER').each(function (linkElement) {
        Event.observe(linkElement, "click", _this._createFolderClickListener);
      });
      
      if (listElement.hasClassName("sortableMaterialList")) {
        _this._initializeSortable(listElement, materialsElement);
      }
    });
  },
  deinitialize: function ($super) {
    $$(".materialsBlock").each(function (materialsElement) {
      var listElement = materialsElement.down(".materialsBlockList");
      listElement.select('.blockContextualLink.hide').invoke("purge");
      listElement.select('.blockContextualLink.show').invoke("purge");
      listElement.select('.blockContextualLink.delete').invoke("purge");
      materialsElement.select('.blockContextMenuItem.CREATEFOLDER').invoke("purge");
    });
  },
  _initializeSortable: function (listElement, materialsElement) {
    Sortable.create(listElement, { 
      tag: "div",
      overlap: "vertical",
      only: ["materialRow", "materialFolderRow"],
      dropOnEmpty: true,
      constraint: "vertical",
      hoverclass: "materialFolderSortableHover",
      tree: true,
      treeTag: "div.materialFolderChildResources",
      treeOnly: "materialRow",
      endeffect: Prototype.emptyFunction,
      onUpdate: function (container) {
        var folderMap = {};

        var folderId = materialsElement.down("input[name='materialsBlockListParentFolderId']").value;
        
//        container.select(".materialRow, .materialFolderRow").each(function (node) {

        var resourceOrder = "";
        container.childElements().each(function (node) {
          if (node.hasClassName("materialFolderRow") || node.hasClassName("materialRow")) {
            var resourceId = node.down("input[name='resourceId']").value;

            if (node.hasClassName("materialFolderRow")) {
              var subResourceOrder = "";

              node.down(".materialFolderChildResources").select(".materialRow").each(function (subNode) {
                var subResourceId = subNode.down("input[name='resourceId']").value;
                if (subResourceOrder != "")
                  subResourceOrder = subResourceOrder + "," + subResourceId;
                else
                  subResourceOrder = subResourceId;
              });
              
              folderMap[resourceId] = subResourceOrder;
            } 
            
            if (resourceOrder != "")
              resourceOrder = resourceOrder + "," + resourceId;
            else
              resourceOrder = resourceId;
          }
        });

        folderMap[folderId] = resourceOrder;
        
        JSONUtils.request(CONTEXTPATH + '/resources/updateresourceorder.json', {
          parameters: {
//            resourceOrder: resourceOrder, 
            parentFolderId: folderId,
            folderOrder: Object.toJSON(folderMap)
          },
          onSuccess : function(jsonRequest) {
//            jsonRequest.folderId;
          }
        });
      }
    });
  },
  _onHideLinkClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var resourceId = hashParams.get("resourceId");
    
    JSONUtils.request(CONTEXTPATH + '/resources/setresourcevisibility.json', {
      parameters: {
        resourceId: resourceId,
        visible: 0
      },
      onSuccess : function(jsonRequest) {
        var linksContainer = linkElement.up('.contextualLinks');
        linksContainer.down('.hide').addClassName("blockContextualLinkHidden");
        linksContainer.down('.show').removeClassName("blockContextualLinkHidden");
      }
    });
  },
  _onShowLinkClick: function (event) {
    Event.stop(event);
    
    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var resourceId = hashParams.get("resourceId");
    
    JSONUtils.request(CONTEXTPATH + '/resources/setresourcevisibility.json', {
      parameters: {
        resourceId: resourceId,
        visible: 1
      },
      onSuccess : function(jsonRequest) {
        var linksContainer = linkElement.up('.contextualLinks');
        linksContainer.down('.show').addClassName("blockContextualLinkHidden");
        linksContainer.down('.hide').removeClassName("blockContextualLinkHidden");
      }
    });
  },
  _onDeleteClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var resourceId = hashParams.get("resourceId");
    
    var titleContainer = new Element("div", { className: "modalPopupTitleContent" });
    titleContainer.update(getLocale().getText('panelAdmin.block.resources.listing.deleteResourceDialogText'));

    var popup = new ModalPopup({
      content: titleContainer,
      buttons: [
        {
          text: getLocale().getText('panelAdmin.block.resources.listing.deleteResourceDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panelAdmin.block.resources.listing.deleteResourceDialogDeleteButton'),
          classNames: "modalPopupButtonRed",
          action: function(instance) {
            instance.close(true);
            
            JSONUtils.request(CONTEXTPATH + '/resources/archiveresource.json', {
              parameters: {
                resourceId: resourceId
              },
              onSuccess : function(jsonRequest) {
                var resourceElement = linkElement.up(".materialRow");
                if (resourceElement != null)
                  resourceElement.remove();
              }
            });
          }
        }
      ]
    });

    popup.open(linkElement);
  },
  _onCreateFolderClick: function (event) {
    Event.stop(event);
    
    var _this = this;
    
    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var parentFolderId = hashParams.get("parentFolderId");

    var content = new Element("div", { className: "manageMaterialsNewFolderPopupTextContainer" });
    var inputContainer = new Element("div", { className: "manageMaterialsNewFolderPopupInputContainer" });
    var input = new Element("input", { type: "text", name: "name", value: name, className: "manageMaterialsNewFolderPopupInput formField formTextField" });
    var textContainer = new Element("div");
    textContainer.update(getLocale().getText('admin.manageMaterials.newFolderPopupMessage'));
    inputContainer.appendChild(input);
    content.appendChild(textContainer);
    content.appendChild(inputContainer);
    
    var popup = new ModalPopup({
      content: content,
      buttons: [
        {
          text: getLocale().getText('admin.manageMaterials.newFolderDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('admin.manageMaterials.newFolderDialogCreateButton'),
          classNames: "modalPopupButtonGreen",
          action: function(instance) {
            instance.close();

            var name = instance.getFrame().down("input[name='name']").value;
            
            JSONUtils.request(CONTEXTPATH + '/resources/createfolder.json', {
              parameters: {
                parentFolderId: parentFolderId,
                name: name
              },
              onSuccess : function(jsonRequest) {
                var folderId = jsonRequest.folderId;
                
                var folderDiv = new Element("div", { className: "materialFolderRow", id: "resource_" + folderId });
                var folderIdInput = new Element("input", { type: "hidden", name: "resourceId", value: folderId });
                var folderTitleDiv = new Element("div", { className: "panelGenericTitle" });
                var foderChildrenDiv = new Element("div", { className: "materialFolderChildResources", id: "materialFolderChildResources_" + folderId });
                
                folderDiv.appendChild(folderIdInput);
                folderDiv.appendChild(folderTitleDiv);
                folderDiv.appendChild(foderChildrenDiv);
                folderTitleDiv.update(name);
                
                var materialsBlock = linkElement.up(".materialsBlock");
                if (materialsBlock) {
                  var materialsList = materialsBlock.down(".materialsBlockList");
                  if (materialsList) {
                    materialsList.appendChild(folderDiv);
    
                    if (materialsList.hasClassName("sortableMaterialList")) {
                      _this._initializeSortable(materialsList, materialsBlock);
                    }
                  }
                }
              }
            });
          }
        }
      ]
    });

    popup.open(linkElement);
  }
});

document.observe("dom:loaded", function(event) {
  new MaterialListingBlockController();
});