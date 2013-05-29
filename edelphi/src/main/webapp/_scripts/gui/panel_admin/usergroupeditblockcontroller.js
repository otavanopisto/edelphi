UserGroupEditBlockController = Class.create(BlockController, {
  initialize : function($super) {
    $super();

    this._userGroupId = null;
    
    this._createNewUserGroupListener = this._onCreateNewUserGroupClicked.bindAsEventListener(this);
    this._userGroupClickedListener = this._onUserGroupClicked.bindAsEventListener(this);
    this._selectUserClickedListener = this._onSelectUserClicked.bindAsEventListener(this);
    this._filterAvailableUsersKeyUpListener = this._onFilterAvailableUsersKeyUp.bindAsEventListener(this);
    this._filterAvailableUsersFocusListener = this._onFilterAvailableUsersFocus.bindAsEventListener(this);
    this._filterAvailableUsersBlurListener = this._onFilterAvailableUsersBlur.bindAsEventListener(this);
    this._addUsersClickListener = this._onAddUsersClick.bindAsEventListener(this);
    this._removeUsersClickListener = this._onRemoveUsersClick.bindAsEventListener(this);
    this._filterGroupUsersKeyUpListener = this._onFilterGroupUsersKeyUp.bindAsEventListener(this);
    this._filterGroupUsersFocusListener = this._onFilterGroupUsersFocus.bindAsEventListener(this);
    this._filterGroupUsersBlurListener = this._onFilterGroupUsersBlur.bindAsEventListener(this);
    this._saveClickListener = this._onSaveClick.bindAsEventListener(this);
    
    if ((typeof JSDATA) != 'undefined') {
      if (JSDATA['panelUsers'])
        this._panelUsers = JSDATA['panelUsers'].evalJSON();
    }
    if (!this._panelUsers) {
      this._panelUsers = new Array();
    }
  },
  setup: function ($super) {
    $super($('panelAdminManagePanelUsergroupViewBlockContent'));
    
    this._isLatestStamp = JSDATA['activeStampId'] === JSDATA['latestStampId']; 

    // Listeners for usergrouplistblockcontorller.js
    
    Event.observe(document, "ed:createNewUserGroupClicked", this._createNewUserGroupListener);
    Event.observe(document, "ed:userGroupClicked", this._userGroupClickedListener);

    // Available panelists filter
    
    this._availableFilterField = $('filterAvailableUsers');
    Event.observe(this._availableFilterField, "keyup", this._filterAvailableUsersKeyUpListener);
    Event.observe(this._availableFilterField, "focus", this._filterAvailableUsersFocusListener);
    Event.observe(this._availableFilterField, "blur", this._filterAvailableUsersBlurListener);
    this._availableFilterHelpText = getLocale().getText('panel.admin.managePanelUserGroups.searchAvailableUsers');
    this._availableFilterField.value = this._availableFilterHelpText;
    
    // Available panelists
    
    var panelUsersContainer = $('usergroupView_availableUsersListContainer');
    for (var i = 0, l = this._panelUsers.length; i < l; i++) {
      var userElement = new Element("div", { id: "usergroupView_user_" + this._panelUsers[i].userId, className: "usergroupView_userContainer"});
      if (this._panelUsers[i].name) {
        var nameElement = new Element("div", { className: "usergroupView_username"});
        nameElement.update(this._panelUsers[i].name);
        userElement.appendChild(nameElement);
      }
      if (this._panelUsers[i].mail) {
        var mailElement = new Element("div", { className: "usergroupView_useremail"});
        mailElement.update(this._panelUsers[i].mail);
        userElement.appendChild(mailElement);
      }
      panelUsersContainer.appendChild(userElement);
      if (this._isLatestStamp) {
        Event.observe(userElement, "click", this._selectUserClickedListener);
      }
    }
    
    // Add and remove buttons
    
    this._addUserButton = $('addUserGroupUser');
    this._removeUserButton = $('removeUserGroupUser');
    if (this._isLatestStamp) {
      Event.observe(this._addUserButton, "click", this._addUsersClickListener);
      Event.observe(this._removeUserButton, "click", this._removeUsersClickListener);
    }

    // User group panelists filter
    
    this._groupFilterField = $('filterGroupUsers');
    Event.observe(this._groupFilterField, "keyup", this._filterGroupUsersKeyUpListener);
    Event.observe(this._groupFilterField, "focus", this._filterGroupUsersFocusListener);
    Event.observe(this._groupFilterField, "blur", this._filterGroupUsersBlurListener);
    this._groupFilterHelpText = getLocale().getText('panel.admin.managePanelUserGroups.searchUserGroupUsers');
    this._groupFilterField.value = this._groupFilterHelpText;
    
    // Save button
    
    this._saveButton = this.getBlockElement().down('input[name="save"]');
    if (this._isLatestStamp) {
      Event.observe(this._saveButton, "click", this._saveClickListener);
    }
    else {
      $('userGroupName').disabled = true;
      this._saveButton.disabled = true;
      this._saveButton.addClassName("disabledButton");
    }
    
    this.getBlockElement().hide();
  },
  deinitialize : function() {
    Event.stopObserving(document, "ed:createNewUserGroup", this._createNewUserGroupListener);
    Event.stopObserving(document, "ed:userGroupClicked", this._userGroupClickedListener);
    Event.stopObserving(this._availableFilterField, "keyup", this._filterAvailableUsersKeyUpListener);
    Event.stopObserving(this._availableFilterField, "focus", this._filterAvailableUsersFocusListener);
    Event.stopObserving(this._availableFilterField, "blur", this._filterAvailableUsersBlurListener);
    Event.stopObserving(this._groupFilterField, "keyup", this._filterGroupUsersKeyUpListener);
    Event.stopObserving(this._groupFilterField, "focus", this._filterGroupUsersFocusListener);
    Event.stopObserving(this._groupFilterField, "blur", this._filterGroupUsersBlurListener);
    if (this._isLatestStamp) {
      $$('.usergroupView_userContainer').each(function (node) {
        Event.stopObserving(node, "click", this._selectUserButtonClickListener);
      });
      Event.stopObserving(this._addUserButton, "click", this._addUsersClickListener);
      Event.stopObserving(this._removeUserButton, "click", this._removeUsersClickListener);
      Event.stopObserving(this._saveButton, "click", this._saveClickListener);
    }
  },
  _onCreateNewUserGroupClicked: function(event) {
    this.getBlockElement().show();
    this._userGroupId = null;
    $('userGroupName').value = '';
    this._resetGroupUsers();
    this._resetFiltersAndSelections();
    revalidateAll(true);
  },
  _onUserGroupClicked: function(event) {
    this.getBlockElement().show();
    var _this = this;
    this._userGroupId = event.memo.userGroupId;
    $('userGroupName').value = event.memo.userGroupName;
    JSONUtils.request(CONTEXTPATH + '/panel/admin/listusergroupusers.json', {
      parameters: {
        userGroupId: this._userGroupId
      },
      onSuccess: function (jsonResponse) {
        _this._resetGroupUsers();
        _this._resetFiltersAndSelections();
        var targetContainer = $('usergroupView_usergroupUsersListContainer');
        var users = jsonResponse.users;
        for (var i = 0, l = users.length; i < l; i++) {
          var availableUser = _this._findAvailableUserByUserId(users[i].id);
          if (availableUser != null) {
            targetContainer.insert(availableUser);
          }
          else {
            var userElement = new Element("div", { id: "usergroupView_user_" + users[i].id, className: "usergroupView_userContainer"});
            if (users[i].name) {
              var nameElement = new Element("div", { className: "usergroupView_username"});
              nameElement.update(users[i].name);
              userElement.appendChild(nameElement);
            }
            if (users[i].mail) {
              var mailElement = new Element("div", { className: "usergroupView_useremail"});
              mailElement.update(users[i].mail);
              userElement.appendChild(mailElement);
            }
            targetContainer.appendChild(userElement);
            Event.observe(userElement, "click", _this._selectUserClickedListener);
          }
        }
        revalidateAll(true);
      }
    });
  },
  _findAvailableUserByUserId: function(userId) {
    var availableUsers = $('usergroupView_availableUsersListContainer').select('.usergroupView_userContainer');
    for (var i = 0, l = availableUsers.length; i < l; i++) {
      var availableUserId = availableUsers[i].id.split("_");
      availableUserId = availableUserId[availableUserId.length - 1];
      if (availableUserId == userId) {
        return availableUsers[i];
      }
    }
    return null;
  },
  _onFilterAvailableUsersKeyUp: function(event) {
    this._filterContainer($('usergroupView_availableUsersListContainer'), $('filterAvailableUsers').value);
  },
  _onFilterAvailableUsersBlur: function(event) {
    var filterElement = $('filterAvailableUsers');
    if (filterElement.value == "") {
      filterElement.value = this._availableFilterHelpText;
    }
  },
  _onFilterAvailableUsersFocus: function(event) {
    var filterElement = $('filterAvailableUsers');
    if (filterElement.value == this._availableFilterHelpText) {
      filterElement.value = '';
    }
    filterElement.select();
  },
  _onFilterGroupUsersKeyUp: function(event) {
    this._filterContainer($('usergroupView_usergroupUsersListContainer'),  $('filterGroupUsers').value);
  },
  _onFilterGroupUsersBlur: function(event) {
    var filterElement = $('filterGroupUsers');
    if (filterElement.value == "") {
      filterElement.value = this._groupFilterHelpText;
    }
  },
  _onFilterGroupUsersFocus: function(event) {
    var filterElement = $('filterGroupUsers');
    if (filterElement.value == this._groupFilterHelpText) {
      filterElement.value = '';
    }
    filterElement.select();
  },
  _matchesFilter: function(filter, userContainer) {
    if (filter == "") {
      return true;
    }
    filter = filter.toLowerCase();
    var nameElement = userContainer.down('.usergroupView_username');
    if (nameElement && nameElement.innerHTML) {
      var name = nameElement.innerHTML.toLowerCase();
      if (name.indexOf(filter) != -1) {
        return true;
      }
    }
    var mailElement = userContainer.down('.usergroupView_useremail');
    if (mailElement && mailElement.innerHTML) {
      var mail = mailElement.innerHTML.toLowerCase();
      if (mail.indexOf(filter) != -1) {
        return true;
      }
    }
    return false;
  },
  _onSelectUserClicked: function(event) {
    var userElement;
    var element = Event.element(event);
    if (element.hasClassName('usergroupView_userContainer')) {
      userElement = element;
    }
    else {
      userElement = element.up('.usergroupView_userContainer');
    }
    if (userElement.hasClassName('usergroupView_userSelected')) {
      userElement.removeClassName('usergroupView_userSelected');
    }
    else {
      userElement.addClassName('usergroupView_userSelected');
    }
    var userIsAvailable = userElement.descendantOf('usergroupView_availableUsersListContainer');
    if (userIsAvailable) {
      this._toggleAddButton();
    }
    else {
      this._toggleRemoveButton();
    }
  },
  _resetGroupUsers: function() {
    var groupUsers = $('usergroupView_usergroupUsersListContainer').select('.usergroupView_userContainer');
    var targetContainer = $('usergroupView_availableUsersListContainer');
    for (var i = 0, l = groupUsers.length; i < l; i++) {
      var insertElement = this._getInsertElement(groupUsers[i], targetContainer);
      if (insertElement == null) {
        targetContainer.insert(groupUsers[i]);
      }
      else {
        targetContainer.insertBefore(groupUsers[i], insertElement);
      }
    }
  },
  _onAddUsersClick: function(event) {
    if (Event.element(event).hasClassName('actionEnabled')) {
      var sourceContainer = $('usergroupView_availableUsersListContainer');
      var targetContainer = $('usergroupView_usergroupUsersListContainer');
      var selectedUsers = sourceContainer.select('.usergroupView_userSelected');
      for (var i = 0, l = selectedUsers.length; i < l; i++) {
        var insertElement = this._getInsertElement(selectedUsers[i], targetContainer);
        if (insertElement == null) {
          targetContainer.insert(selectedUsers[i]);
        }
        else {
          targetContainer.insertBefore(selectedUsers[i], insertElement);
        }
      }
      // reset the available user filter to ensure all removed users are visible
      $('filterGroupUsers').value = this._groupFilterHelpText; 
      this._filterContainer($('usergroupView_usergroupUsersListContainer'), '');
      // enable/disable add/remove buttons accordingly
      this._toggleAddButton();
      this._toggleRemoveButton();
    }
  },
  _onRemoveUsersClick: function(event) {
    if (Event.element(event).hasClassName('actionEnabled')) {
      var sourceContainer = $('usergroupView_usergroupUsersListContainer');
      var targetContainer = $('usergroupView_availableUsersListContainer');
      var selectedUsers = sourceContainer.select('.usergroupView_userSelected');
      for (var i = 0, l = selectedUsers.length; i < l; i++) {
        var insertElement = this._getInsertElement(selectedUsers[i], targetContainer);
        if (insertElement == null) {
          targetContainer.insert(selectedUsers[i]);
        }
        else {
          targetContainer.insertBefore(selectedUsers[i], insertElement);
        }
      }
      // reset the available user filter to ensure all removed users are visible
      $('filterAvailableUsers').value = this._availableFilterHelpText; 
      this._filterContainer($('usergroupView_availableUsersListContainer'), '');
      // enable/disable add/remove buttons accordingly
      this._toggleAddButton();
      this._toggleRemoveButton();
    }
  },
  _onSaveClick: function(event) {
    Event.stop(event);
    startLoadingOperation("panel.admin.managePanelUserGroups.savingUserGroup");
    var userIds = "";
    var usersContainer = $('usergroupView_usergroupUsersListContainer');
    usersContainer.select('.usergroupView_userContainer').each(function (node) {
      var userId = node.id.split("_");
      userId = userId[userId.length - 1];
      if (userIds != "") {
        userIds += ",";
      }
      userIds += userId;
    });
    var name = $('userGroupName').value;
    var parameters = {};
    if (this._userGroupId) {
      parameters = {
        userGroupId: this._userGroupId,
        userIds: userIds,
        name: name
      };
    }
    else {
      parameters = {
        userIds: userIds,
        name: name
      };
    }
    var _this = this;
    JSONUtils.request(CONTEXTPATH + '/panel/admin/saveusergroup.json', {
      parameters: parameters,
      onComplete: function (transport) {
        endLoadingOperation();
      },
      onSuccess: function (jsonResponse) {
        if (!_this._userGroupId) {
          _this._userGroupId = jsonResponse.userGroup.id;
        }
        document.fire('ed:userGroupSaved', {
          'id': _this._userGroupId,
          'name' : jsonResponse.userGroup.name,
          'created' : jsonResponse.userGroup.created,
          'modified' : jsonResponse.userGroup.modified
        });
      }
    });
  },
  _toggleAddButton: function() {
    var addElement = $('addUserGroupUser');
    var usersContainer = $('usergroupView_availableUsersListContainer');
    var selectedUsers = usersContainer.select('.usergroupView_userSelected');
    if (selectedUsers.length > 0) {
      if (!addElement.hasClassName('actionEnabled')) {
        addElement.addClassName('actionEnabled');
      }
    }
    else {
      if (addElement.hasClassName('actionEnabled')) {
        addElement.removeClassName('actionEnabled');
      }
    }
  },
  _toggleRemoveButton: function() {
    var removeElement = $('removeUserGroupUser');
    var usersContainer = $('usergroupView_usergroupUsersListContainer');
    var selectedUsers = usersContainer.select('.usergroupView_userSelected');
    if (selectedUsers.length > 0) {
      if (!removeElement.hasClassName('actionEnabled')) {
        removeElement.addClassName('actionEnabled');
      }
    }
    else {
      if (removeElement.hasClassName('actionEnabled')) {
        removeElement.removeClassName('actionEnabled');
      }
    }
  },
  _getInsertElement: function(userElement, userContainer) {
    var element = null;
    var userStr = this._extractUserString(userElement);
    var users = userContainer.select('.usergroupView_userContainer');
    for (var i = 0, l = users.length; i < l; i++) {
      var cmpStr = this._extractUserString(users[i]);
      if (cmpStr.localeCompare(userStr) >= 0) {
        element = users[i];
        break;
      }
    }
    return element;
  },
  _extractUserString: function(userElement) {
    var str = '';
    var nameElement = userElement.down('.usergroupView_username');
    if (nameElement && nameElement.innerHTML) {
      str = nameElement.innerHTML;
    }
    var emailElement = userElement.down('.usergroupView_useremail');
    if (emailElement && emailElement.innerHTML) {
      if (str != '') {
        str += ' ';
      }
      str += emailElement.innerHTML;
    }
    return str.toLowerCase();
  },
  _filterContainer: function(userContainer, filterText) {
    var _this = this;
    userContainer.select('.usergroupView_userContainer').each(function (node) {
      if (_this._matchesFilter(filterText, node)) {
        node.show();
      }
      else {
        node.hide();
      }
    });
  },
  _resetFiltersAndSelections: function() {
    $('filterAvailableUsers').value = this._availableFilterHelpText;
    this._filterContainer($('usergroupView_availableUsersListContainer'), '');
    $('filterGroupUsers').value = this._groupFilterHelpText;
    this._filterContainer($('usergroupView_usergroupUsersListContainer'), '');
    $('usergroupView_availableUsersListContainer').select('.usergroupView_userSelected').each(function (node) {
      node.removeClassName('usergroupView_userSelected');      
    });
    $('usergroupView_usergroupUsersListContainer').select('.usergroupView_userSelected').each(function (node) {
      node.removeClassName('usergroupView_userSelected');      
    });
  }
});

addBlockController(new UserGroupEditBlockController());