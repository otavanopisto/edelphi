ManagePanelUsersBlockController = Class.create(BlockController, {
  initialize : function() {
    this._selectUserButtonClickListener = this._onSelectUserButtonClick.bindAsEventListener(this);
    this._toggleExpertiseClickListener = this._onExpertiseToggleClick.bindAsEventListener(this);
    this._saveUserButtonClickListener = this._onSaveUserButtonClick.bindAsEventListener(this);
    this._removeUserLinkClickListener = this._onRemoveUserLinkClick.bindAsEventListener(this);
  },
  deinitialize: function () {
    Event.stopObserving(this._saveUserButton, "click", this._saveUserButtonClickListener);

    $$('.managePanelUsers_panelUser').each(function (node) {
      Event.stopObserving(node, "click", this._selectUserButtonClickListener);
    });
    
    $$('.managePanelUsers_panelUser_removeFromPanel').each(function (node) {
      if (!node.hasClassName("managePanelUsers_panelUser_removeFromPanelDisabled"))
        Event.stopObserving(node, "click", this._removeUserLinkClickListener);
    });

    $$('.managePanelUsers_panelExpertsMatrixGroupCell').each(function (node) {
      Event.stopObserving(node, "click", this._toggleExpertiseClickListener);
    });
  },
  setup: function ($super, blockElement) {
    var blockElement = $('manageUsersListColumn');
    $super(blockElement);
    this._matrixContainer = $('manageUsersUserViewColumn_panelExpertsMatrix');
    this._expertiseClasses = JSDATA['panelExpertiseClasses'].evalJSON();
    this._intressClasses = JSDATA['panelIntressClasses'].evalJSON();
    this._panelExpertiseGroups = JSDATA['panelExpertiseGroups'].evalJSON();
    this._loggedUserId = JSDATA['loggedUserId'];
    
    this._isLatestStamp = JSDATA['activeStampId'] === JSDATA['latestStampId']; 
    
    var panelUsers = JSDATA['panelUsers'].evalJSON();
    
    this._managerListElement = $('manageUsersManagerList');
    this._userListElement = $('manageUsersPanelistList');
    
    for (var i = 0, l = panelUsers.length; i < l; i++) {
      var userElement = new Element("div", { className: "managePanelUsers_panelUser", id: "managePanelUsers_panelUser_" + panelUsers[i].id });

      var panelUser_image = new Element("div", { className: "managePanelUsers_panelUser_image" });
      var panelUser_created = new Element("div", { className: "managePanelUsers_panelUser_created" });
      var panelUser_expertise_title = new Element("div", { className: "managePanelUsers_panelUser_expertise_title" });
      var panelUser_expertise = new Element("div", { className: "managePanelUsers_panelUser_expertise" });
      var panelUser_removeFromPanel = null;
      if (this._isLatestStamp) {
        panelUser_removeFromPanel = new Element("div", { className: "managePanelUsers_panelUser_removeFromPanel" });
      }
      
      userElement._panelUserId = panelUsers[i].id;

      var createdDate = new Date();
      createdDate.setTime(panelUsers[i].created);
      
      var expertisestext = "";
      if (panelUsers[i].expertises.length > 0) {
        expertisestext = panelUsers[i].expertises[0].expertiseName + " (" + panelUsers[i].expertises[0].intressName + ")";
        for (var e = 1, eLen = panelUsers[i].expertises.length; e < eLen; e++) {
          expertisestext += ", " + panelUsers[i].expertises[e].expertiseName + " (" + panelUsers[i].expertises[e].intressName + ")";
        }
      }
      
      var panelUserNameStr = "" + (panelUsers[i].lastName ? panelUsers[i].lastName + ", " : "") + (panelUsers[i].firstName ? panelUsers[i].firstName : "");
      
      var panelUser_name = null;
      var panelUser_email = null;
      if (panelUserNameStr == "") {
        panelUser_name = new Element("div", { className: "managePanelUsers_panelUser_emailAsName" });
        panelUser_name.update(panelUsers[i].email);
      }
      else {
        panelUser_name = new Element("div", { className: "managePanelUsers_panelUser_name" });
        panelUser_email = new Element("div", { className: "managePanelUsers_panelUser_email" });
        panelUser_name.update(panelUserNameStr);
        panelUser_email.update(panelUsers[i].email);
      }
      
      panelUser_created.update(getLocale().getText("panel.admin.managePanelUsers.userList.userCreated") + " " + getLocale().getDate(createdDate));
      panelUser_expertise_title.update(getLocale().getText("panel.admin.managePanelUsers.userList.userExpertise"));
      panelUser_expertise.update(expertisestext);
      
      userElement.appendChild(panelUser_image);
      userElement.appendChild(panelUser_name);
      userElement.appendChild(panelUser_created);
      if (panelUser_email != null) {
        userElement.appendChild(panelUser_email);
      }
      userElement.appendChild(panelUser_expertise_title);
      userElement.appendChild(panelUser_expertise);
      if (this._isLatestStamp) {
        userElement.appendChild(panelUser_removeFromPanel);
      
        var panelUser_contextualLinksContainer = new Element("div", { className: "contextualLinks" });
        var panelUser_contextualLinkContainer = new Element("div", { className: "blockContextualLink" });
        
        panelUser_removeFromPanel.appendChild(panelUser_contextualLinksContainer);
        panelUser_contextualLinksContainer.appendChild(panelUser_contextualLinkContainer);
        
        var panelUser_removeTooltipContainer = new Element("span", { className: "blockContextualLinkTooltip" });
        var panelUser_removeTooltipText = new Element("span", { className: "blockContextualLinkTooltipText" });
        var panelUser_removeTooltipArrow = new Element("span", { className: "blockContextualLinkTooltipArrow" });
        
        panelUser_contextualLinkContainer.appendChild(panelUser_removeTooltipContainer);
        panelUser_removeTooltipContainer.appendChild(panelUser_removeTooltipText);
        panelUser_removeTooltipText.update(getLocale().getText("panel.admin.managePanelUsers.userList.removeUserFromPanel"));
        panelUser_removeTooltipContainer.appendChild(panelUser_removeTooltipArrow);

        if (panelUsers[i].userId != this._loggedUserId)
          Event.observe(panelUser_removeFromPanel, "click", this._removeUserLinkClickListener);
        else
          panelUser_removeFromPanel.addClassName("managePanelUsers_panelUser_removeFromPanelDisabled");
      }

      var roleId = panelUsers[i].roleId;
      var parentNode = $('manageUsersList_' + roleId);
      
      parentNode.appendChild(userElement);
      
      userElement._user = panelUsers[i];
      Event.observe(userElement, "click", this._selectUserButtonClickListener);
    }
    
    this.setupMatrix();
    
    this._saveUserButton = $("manageUsersUserViewColumn_saveUserButton");
    if (!this._isLatestStamp) {
      this._saveUserButton.disabled = true;
      this._saveUserButton.addClassName("disabledButton");
    }

    Event.observe(this._saveUserButton, "click", this._saveUserButtonClickListener);
  },
  setupMatrix: function() {
    var tableElem = new Element("table", { className: "managePanelUsers_panelExpertsMatrixTable" });
    this._matrixContainer.appendChild(tableElem);
    var rowDiv = new Element("tr", { className: "managePanelUsers_panelExpertsMatrixHeaderRow" });
    tableElem.appendChild(rowDiv);

    var topLeftSpacer = new Element("td", { className: "managePanelUsers_panelExpertsMatrixCell" });
    topLeftSpacer.update(' ');
    rowDiv.appendChild(topLeftSpacer);

    for (var x = 0, xLen = this._expertiseClasses.length; x < xLen; x++) {
      var elem = new Element("td", { className: "managePanelUsers_panelExpertsMatrixCell managePanelUsers_panelExpertsMatrixHeaderCell" });
      
      elem.update(this._expertiseClasses[x].name);
      elem._expertiseClassId = this._expertiseClasses[x].id;
      
      rowDiv.appendChild(elem);
    }
    
    for (var y = 0, yLen = this._intressClasses.length; y < yLen; y++) {
      var rowDiv = new Element("tr", { className: "managePanelUsers_panelExpertsMatrixRow" });
      rowDiv._intressClassId = this._intressClasses[y].id;
      tableElem.appendChild(rowDiv);

      var elem = new Element("td", { className: "managePanelUsers_panelExpertsMatrixCell managePanelUsers_panelExpertsMatrixHeaderCell" });
      elem.update(this._intressClasses[y].name);
      elem._intressClassId = this._intressClasses[y].id;
      rowDiv.appendChild(elem);
      
      for (var x = 0, xLen = this._expertiseClasses.length; x < xLen; x++) {
        var group = this._findExpertiseGroup(this._expertiseClasses[x].id, this._intressClasses[y].id);
        var groupId = group ? group.id : null; // old stamps don't necessarily have a group for the expertise/interest combo
        var groupCell = new Element("td", { className: "managePanelUsers_panelExpertsMatrixCell managePanelUsers_panelExpertsMatrixGroupCell", id: "managePanelUsers_panelExpertsMatrixGroupCell_" + groupId });
        
        groupCell._expertiseClassId = this._expertiseClasses[x].id;
        groupCell._intressClassId = this._intressClasses[y].id;
        groupCell._expertiseGroupId = groupId;
        
        if (this._isLatestStamp) {
          Event.observe(groupCell, "click", this._toggleExpertiseClickListener);
        }
        
        rowDiv.appendChild(groupCell);
      }
    }
  },
  _findExpertiseGroup: function (expertiseClassId, intressClassId) {
    for (var i = 0, l = this._panelExpertiseGroups.length; i < l; i++) {
      if ((this._panelExpertiseGroups[i].expertiseClassId == expertiseClassId) && 
          (this._panelExpertiseGroups[i].intressClassId == intressClassId)) {
        return this._panelExpertiseGroups[i];
      }
    }
  },
  _selectUser: function(userElement) {
    var user = userElement._user;
    this._selectedUser = user;
    
    if (!this._isLatestStamp || user.userId == this._loggedUserId) {
      $('manageUsersUserViewColumn_userInformation_role').down("select").disable();
    } else {
      $('manageUsersUserViewColumn_userInformation_role').down("select").enable();
    }
    
    $$('.managePanelUsers_panelUser_selected').each(function (node) {
      node.removeClassName('managePanelUsers_panelUser_selected');
    });
    userElement.addClassName('managePanelUsers_panelUser_selected');
    
    var userNameStr = "" + (user.lastName ? user.lastName + ", " : "") + (user.firstName ? user.firstName : "");
    
    $('manageUsersUserViewColumn_userName').update(userNameStr);
    $('manageUsersUserViewColumn_userInformation_name').update(userNameStr);
    $('manageUsersUserViewColumn_userInformation_email').update(user.email);
    $('manageUsersUserViewColumn_userInformation_role').down("select").value = user.roleId;
    $('manageUsersUserViewColumnContent').show();
    
    if (!this._isLatestStamp) {
      $('manageUsersUserViewColumn_userInformation_firstName').disabled = true;  
      $('manageUsersUserViewColumn_userInformation_lastName').disabled = true;
    }
    
    var authstext = "";
    if (user.auths.length > 0) {
      authstext = user.auths[0].authName;
      
      for (var i = 1, l = user.auths.length; i < l; i++) {
        authstext += ", " + user.auths[i].authName;
      }
    }
    $('manageUsersUserViewColumn_userInformation_auth').update(authstext);
    
    $$('.managePanelUsers_panelExpertsMatrixGroupCell').each(function (node) {
      node.update(' ');
    });
    
    for (var i = 0, l = user.expertises.length; i < l; i++) {
      var groupCell = $('managePanelUsers_panelExpertsMatrixGroupCell_' + user.expertises[i].groupId);
      
      if (groupCell != null) {
        var selectedExpertiseNode = new Element("div", { className: "managePanelUsers_panelExpertsSelectedExpertiseCell" });
        selectedExpertiseNode.update(' ');
        selectedExpertiseNode._panelGroupUserId = user.expertises[i].groupUserId;
        groupCell.appendChild(selectedExpertiseNode);
      }
    }
  },
  _updateUserListUser: function(user) {
    // TODO properly updated name and email
    var userDiv = $('managePanelUsers_panelUser_' + user.id);

    var panelUser_expertise = userDiv.down(".managePanelUsers_panelUser_expertise");
    
    var expertisestext = "";
    if (user.expertises.length > 0) {
      expertisestext = user.expertises[0].expertiseName + " (" + user.expertises[0].intressName + ")";
      for (var e = 1, eLen = user.expertises.length; e < eLen; e++) {
        expertisestext += ", " + user.expertises[e].expertiseName + " (" + user.expertises[e].intressName + ")";
      }
    }

    panelUser_expertise.update(expertisestext);
    
    var roleId = user.roleId;
    var newParentNode = $('manageUsersList_' + roleId);
    
    if (userDiv.parentNode != newParentNode) {
      userDiv.remove();
      newParentNode.appendChild(userDiv);
    }
  },
  _findIntressClassName: function (id) {
    for (var i = 0, l = this._intressClasses.length; i < l; i++) {
      if (this._intressClasses[i].id == id) {
        return this._intressClasses[i].name;
      }
    }
    return undefined;
  },
  _findExpertiseClassName: function (id) {
    for (var i = 0, l = this._expertiseClasses.length; i < l; i++) {
      if (this._expertiseClasses[i].id == id) {
        return this._expertiseClasses[i].name;
      }
    }
    return undefined;
  },
  _saveUser: function (user) {
    var panelUserId = user.id;
    var newRoleId = $('manageUsersUserViewColumn_userInformation_role').down("select[name='selectedUserRole']").value;

    var _this = this;
    
    JSONUtils.request(CONTEXTPATH + '/panel/admin/updatepaneluser.json', {
      parameters: {
        panelUserId: panelUserId,
        newRoleId: newRoleId
      },
      onSuccess: function (jsonRequest) {
        user.roleId = newRoleId;
        _this._updateUserListUser(user);
      }
    });
  },
  _removeUser: function (user, removeElement) {
    var panelUserId = user.id;
    var userElement = $('managePanelUsers_panelUser_' + panelUserId);
    var _this = this;

    var popup = new ModalPopup({
      content: getLocale().getText('panel.admin.managePanelUsers.removeUserFromPanelText'),
      buttons: [
        {
          text: getLocale().getText('panel.admin.managePanelUsers.removeUserFromPanelCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panel.admin.managePanelUsers.removeUserFromPanelRemoveButton'),
          classNames: "modalPopupButtonRed",
          action: function(instance) {
            instance.close();
    
            JSONUtils.request(CONTEXTPATH + '/panel/admin/archivepaneluser.json', {
              parameters: {
                panelUserId: panelUserId
              },
              onSuccess: function (jsonResponse) {
                Event.stopObserving(userElement, "click", _this._selectUserButtonClickListener);
                Event.stopObserving(userElement.down('.managePanelUsers_panelUser_removeFromPanel'), "click", _this._removeUserLinkClickListener);
                
                userElement.remove();

                JSONUtils.showMessages(jsonResponse);
              }
            });
          }
        }
      ]
    });

    popup.open(removeElement);
  },
  _onSelectUserButtonClick: function(event) {
    var userElement;
    var element = Event.element(event);
    
    if (element.hasClassName('managePanelUsers_panelUser'))
      userElement = element;
    else
      userElement = element.up('.managePanelUsers_panelUser');

    this._selectUser(userElement);
  },
  _onExpertiseToggleClick: function(event) {
    if (this._selectedUser) {
      var groupCell = Event.element(event);
      
      if (!groupCell.hasClassName("managePanelUsers_panelExpertsMatrixGroupCell")) {
        groupCell = groupCell.up(".managePanelUsers_panelExpertsMatrixGroupCell");
      }
      
      if (groupCell != null) {
        var expertiseGroupId = groupCell._expertiseGroupId;
        var panelUserId = this._selectedUser.id;
        var selectedExpertiseNode = groupCell.down(".managePanelUsers_panelExpertsSelectedExpertiseCell");
        var _this = this;
        
        if (selectedExpertiseNode != undefined) {
          // Delete
          
          var panelGroupUserId = selectedExpertiseNode._panelGroupUserId;
          
          JSONUtils.request(CONTEXTPATH + '/panel/removepanelexpertgroupuser.json', {
            parameters: {
              panelExpertGroupUserId: panelGroupUserId 
            },
            onSuccess: function (jsonRequest) {
              selectedExpertiseNode.remove();
              
              for (var i = _this._selectedUser.expertises.length - 1; i >= 0; i--) {
                if (_this._selectedUser.expertises[i].groupUserId == panelGroupUserId) {
                  _this._selectedUser.expertises.splice(i, 1);
                }
              }
              
              // Update User List...
              _this._updateUserListUser(_this._selectedUser);
            }
          });
          
        } else {
          // Create
          
          JSONUtils.request(CONTEXTPATH + '/panel/createpanelexpertgroupuser.json', {
            parameters: {
              expertiseGroupId: expertiseGroupId,
              panelUserId: panelUserId
            },
            onSuccess: function (jsonRequest) {
              var panelGroupUserId = jsonRequest.id;

              var intressId = groupCell._intressClassId;
              var expertiseId = groupCell._expertiseClassId;
              
              _this._selectedUser.expertises.push({
                groupUserId: panelGroupUserId,
                groupId: expertiseGroupId,
                intressId: intressId,
                expertiseId: expertiseId,
                intressName: _this._findIntressClassName(intressId),
                expertiseName: _this._findExpertiseClassName(expertiseId)
              });

              selectedExpertiseNode = new Element("div", { className: "managePanelUsers_panelExpertsSelectedExpertiseCell" });
              selectedExpertiseNode.update(' ');
              selectedExpertiseNode._panelGroupUserId = panelGroupUserId;
              groupCell.appendChild(selectedExpertiseNode);

              // Update User List...
              _this._updateUserListUser(_this._selectedUser);
            }
          });
        }
      }
    }
  },
  _onSaveUserButtonClick: function (event) {
    this._saveUser(this._selectedUser);
  },
  _onRemoveUserLinkClick: function (event) {
    Event.stop(event);
    
    var userElement;
    var element = Event.element(event);
    
    if (element.hasClassName('managePanelUsers_panelUser'))
      userElement = element;
    else
      userElement = element.up('.managePanelUsers_panelUser');

    if (userElement && userElement._user)
      this._removeUser(userElement._user, element);
  }
});

addBlockController(new ManagePanelUsersBlockController());