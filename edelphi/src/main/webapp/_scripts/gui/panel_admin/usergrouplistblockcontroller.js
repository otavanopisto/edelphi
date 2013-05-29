UserGroupListBlockController = Class.create(BlockController, {
  initialize : function($super) {
    if ((typeof JSDATA) != 'undefined') {
      if (JSDATA['userGroups'])
        this._userGroups = JSDATA['userGroups'].evalJSON();
    }
    if (!this._userGroups) {
      this._userGroups = new Array();
    }
    this._newUserGroupClickListener = this._onNewuserGroupClicked.bindAsEventListener(this);
    this._userGroupClickListener = this._onUserGroupClicked.bindAsEventListener(this);
    this._removeUserGroupClickListener = this._onRemoveUserGroupClicked.bindAsEventListener(this);
    this._userGroupSavedListener = this._onUserGroupSaved.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelAdminUserGroupListBlock'));
    this._isLatestStamp = JSDATA['activeStampId'] === JSDATA['latestStampId']; 
    // Event listeners
    if (this._isLatestStamp) {
      var createLink = this.getBlockElement().down('.CREATEUSERGROUP').down('a');
      Event.observe(createLink, 'click', this._newUserGroupClickListener);
      Event.observe(document, 'ed:userGroupSaved', this._userGroupSavedListener);
    }
    // User groups
    var userGroupContainer = $('panelAdminManagePanelUsergroupListingBlockContent');
    for (var i = 0, l = this._userGroups.length; i < l; i++) {
      var groupElement = this._createUserGroupElement(this._userGroups[i].id, this._userGroups[i].name, this._userGroups[i].created, this._userGroups[i].modified);
      userGroupContainer.appendChild(groupElement);
      Event.observe(groupElement, 'click', this._userGroupClickListener);
    }
  },
  deinitialize : function() {
    var _this = this;
    if (this._isLatestStamp) {
      var createLink = this.getBlockElement().down('.CREATEUSERGROUP').down('a');
      Event.stopObserving(createLink, 'click', this._newUserGroupClickListener);
      Event.stopObserving(document, 'ed:userGroupSaved', this._userGroupSavedListener);
    }
    $$('.managePanelUsergroups_panelUsergroup').each(function (node) {
      Event.stopObserving(node, 'click', _this._userGroupClickListener);
    });
    $$('.managePanelUsergroups_panelUsergroup_removeFromPanel').each(function (node) {
      Event.stopObserving(node, 'click', _this._removeUserGroupClickListener);
    });
  },
  _createUserGroupElement: function(id, name, created, modified) {

    // Elements
    
    var groupElement = new Element("div", { id: "managePanelUsergroups_panelUsergroup_" + id, className: "managePanelUsergroups_panelUsergroup"});
    var nameElement = new Element("div", { className: "managePanelUsergroups_panelUsergroup_name"});
    var createdElement = new Element("div", { className: "managePanelUsergroups_panelUsergroup_created"});
    var modifiedElement = new Element("div", { className: "managePanelUsergroups_panelUsergroup_modified"});
    var removeContainer = new Element("div", { className: "managePanelUsergroups_panelUsergroup_removeFromPanel"});
    var contextLinksContainer = new Element("div", { className: "contextualLinks"});
    var contextLinkContainer = new Element("div", { className: "blockContextualLink"});
    var tooltipContainer = new Element("span", { className: "blockContextualLinkTooltip"});
    var tooltipTextContainer = new Element("span", { className: "blockContextualLinkTooltipText"});
    var tooltipArrowContainer = new Element("span", { className: "blockContextualLinkTooltipArrow"});
    
    // Hierarchy
    
    groupElement.appendChild(nameElement);
    groupElement.appendChild(createdElement);
    groupElement.appendChild(modifiedElement);
    if (this._isLatestStamp) {
      groupElement.appendChild(removeContainer);
      removeContainer.appendChild(contextLinksContainer);
      contextLinksContainer.appendChild(contextLinkContainer);
      contextLinkContainer.appendChild(tooltipContainer);
      tooltipContainer.appendChild(tooltipTextContainer);
      tooltipContainer.appendChild(tooltipArrowContainer);
    }
    
    // Contents
    
    nameElement.update(name);
    var createdDate = new Date();
    createdDate.setTime(created);
    createdElement.update(getLocale().getText('panel.admin.managePanelUserGroups.userGroupCreatedLabel') + ": " + getLocale().getDate(createdDate));
    var modifiedDate = new Date();
    modifiedDate.setTime(modified);
    modifiedElement.update(getLocale().getText('panel.admin.managePanelUserGroups.userGroupModifiedLabel') + ": " + getLocale().getDate(modifiedDate));
    tooltipTextContainer.update(getLocale().getText('panel.admin.managePanelUserGroups.removeUserGroupButtonCaption'));
    
    // Listeners
    
    Event.observe(removeContainer, 'click', this._removeUserGroupClickListener);
    
    return groupElement;
  },
  _onNewuserGroupClicked: function(event) {
    Event.stop(event);
    if (this._selectedGroupElement != null) {
      this._selectedGroupElement.removeClassName('managePanelUsergroups_panelUsergroup_selected');
      this._selectedGroupElement = null;
    }
    document.fire('ed:createNewUserGroupClicked');
  },
  _onUserGroupClicked: function(event) {
    if (this._selectedGroupElement != null) {
      this._selectedGroupElement.removeClassName('managePanelUsergroups_panelUsergroup_selected');
    }
    var element = Event.element(event);
    if (element.hasClassName('managePanelUsergroups_panelUsergroup')) {
      this._selectedGroupElement = element;
    }
    else {
      this._selectedGroupElement = element.up('.managePanelUsergroups_panelUsergroup');
    }
    this._selectedGroupElement.addClassName('managePanelUsergroups_panelUsergroup_selected');
    var userGroupId = this._selectedGroupElement.id.split("_");
    userGroupId = userGroupId[userGroupId.length - 1];
    document.fire("ed:userGroupClicked", {
      "userGroupId": userGroupId,
      "userGroupName" : this._selectedGroupElement.down('.managePanelUsergroups_panelUsergroup_name').innerHTML
    });
  },
  _onRemoveUserGroupClicked: function(event) {
    Event.stop(event);
    var userGroupContainer = Event.element(event).up('.managePanelUsergroups_panelUsergroup');
    var userGroupId = userGroupContainer.id.split("_");
    userGroupId = userGroupId[userGroupId.length - 1];
    var userGroupName = userGroupContainer.down('.managePanelUsergroups_panelUsergroup_name').innerHTML;
    var _this = this;
    var popup = new ModalPopup({
      content: getLocale().getText('panel.admin.managePanelUserGroups.removeUserGroupDialogText', [userGroupName]),
      buttons: [
        {
          text: getLocale().getText('panel.admin.managePanelUserGroups.removeUserGroupDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panel.admin.managePanelUserGroups.removeUserGroupDialogDeleteButton'),
          classNames: "modalPopupButtonRed",
          action: function(instance) {
            startLoadingOperation('panel.admin.managePanelUserGroups.removingUserGroup');
            instance.close(true);
            JSONUtils.request(CONTEXTPATH + '/panel/admin/archiveusergroup.json', {
              parameters: {
                userGroupId: userGroupId
              },
              onComplete : function(transport) {
                endLoadingOperation();
              },
              onSuccess: function (jsonRequest) {
                userGroupContainer.remove();
                if (_this._selectedGroupElement != null) {
                  _this._selectedGroupElement.removeClassName('managePanelUsergroups_panelUsergroup_selected');
                  _this._selectedGroupElement = null;
                  document.fire('ed:createNewUserGroupClicked');
                }
              }
            });
          }
        }
      ]
    });
    popup.open(Event.element(event));
  },
  _onUserGroupSaved: function(event) {
    var oldName = '';
    var userGroupContainer = $('panelAdminManagePanelUsergroupListingBlockContent');
    if (this._selectedGroupElement) {
      oldName = this._selectedGroupElement.down('.managePanelUsergroups_panelUsergroup_name').innerHTML;
      this._selectedGroupElement.down('.managePanelUsergroups_panelUsergroup_name').update(event.memo.name);
      var createdDate = new Date();
      createdDate.setTime(event.memo.created);
      var createdElement = this._selectedGroupElement.down('.managePanelUsergroups_panelUsergroup_created'); 
      createdElement.update(getLocale().getText('panel.admin.managePanelUserGroups.userGroupCreatedLabel') + ": " + getLocale().getDate(createdDate));
      var modifiedDate = new Date();
      modifiedDate.setTime(event.memo.modified);
      var modifiedElement = this._selectedGroupElement.down('.managePanelUsergroups_panelUsergroup_modified'); 
      modifiedElement.update(getLocale().getText('panel.admin.managePanelUserGroups.userGroupModifiedLabel') + ": " + getLocale().getDate(modifiedDate));
    }
    else {
      var groupElement = this._createUserGroupElement(event.memo.id, event.memo.name, event.memo.created, event.memo.modified);
      Event.observe(groupElement, "click", this._userGroupClickListener);
      this._selectedGroupElement = groupElement;
      this._selectedGroupElement.addClassName('managePanelUsergroups_panelUsergroup_selected');
    }
    if (oldName != event.memo.name) {
      var element = null;
      var groups = userGroupContainer.select('.managePanelUsergroups_panelUsergroup');
      for (var i = 0, l = groups.length; i < l; i++) {
        if (groups[i] == this._selectedGroupElement) {
          continue;
        }
        var cmpStr = groups[i].down('.managePanelUsergroups_panelUsergroup_name').innerHTML.toLowerCase();
        if (cmpStr > event.memo.name.toLowerCase()) {
          element = groups[i];
          break;
        }
      }
      if (element == null) {
        userGroupContainer.insert(this._selectedGroupElement);
      }
      else if (element != this._selectedGroupElement) {
        userGroupContainer.insertBefore(this._selectedGroupElement, element);
      }
    }
  }
});

addBlockController(new UserGroupListBlockController());