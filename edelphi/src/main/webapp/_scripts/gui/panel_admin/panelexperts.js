/**
 * GUI_panelExpertsUser
 * 
 * Presents a user in matrix or in user list. Draggable.
 * - _panelUserId         PanelUser.id
 * - _management          PanelExpertsMatrixController
 * - _panelGroupUserId    PanelExpertiseGroupUser.id, if available
 * 
 * GUI_panelExpertsMatrixGroupCell
 * 
 * A cell in matrix that a user can be dropped into. Droppable.
 * - _expertiseClassId    PanelUserExpertiseClass.id
 * - _intressClassId      PanelUserIntressClass.id
 * - _expertiseGroupId    PanelUserExpertiseGroup.id
 * 
 * GUI_panelExpertsMatrixRow
 * 
 * A Row in matrix.
 * - _intressClassId      PanelUserIntressClass.id
 */

PanelExpertsMatrixController = Class.create({
  initialize : function(matrixContainer, usersListContainer) {
    this._matrixContainer = matrixContainer;
    this._usersListContainer = usersListContainer;

    this._panel = JSDATA['panel'].evalJSON();
    this._panelUsers = JSDATA['panelUsers'].evalJSON();
    this._expertiseClasses = JSDATA['panelExpertiseClasses'].evalJSON();
    this._intressClasses = JSDATA['panelIntressClasses'].evalJSON();
    this._panelExpertiseGroups = JSDATA['panelExpertiseGroups'].evalJSON();
    this._panelExpertiseGroupUsers = JSDATA['panelExpertiseGroupUsers'].evalJSON();
    
    this._newIntressButtonClickListener = this._onNewIntressButtonClick.bindAsEventListener(this);
    this._newExpertiseButtonClickListener = this._onNewExpertiseButtonClick.bindAsEventListener(this);
    this._removeExpertButtonClickListener = this._onRemoveExpertButtonClick.bindAsEventListener(this);
    
    this._editExpertiseClassButtonClickListener = this._onEditExpertiseClassButtonClick.bindAsEventListener(this);
    this._deleteExpertiseClassButtonClickListener = this._onDeleteExpertiseClassButtonClick.bindAsEventListener(this);
    
    this._newIntressButton = $('newIntressButton');
    this._newExpertiseButton = $('newExpertiseButton');
    Event.observe(this._newIntressButton, "click", this._newIntressButtonClickListener);
    Event.observe(this._newExpertiseButton, "click", this._newExpertiseButtonClickListener);
    
    this.setup();
  },
  deinitialize : function() {
    Event.stopObserving(this._newIntressButton, "click", this._newIntressButtonClickListener);
    Event.stopObserving(this._newExpertiseButton, "click", this._newExpertiseButtonClickListener);
    
    this._matrixContainer.select(".panelExpertsEditExpertiseClassButton").each(function (button) {
      Event.stopObserving(button, "click", this._editExpertiseClassButtonClickListener);
    });

    this._matrixContainer.select(".panelExpertsDeleteExpertiseClassButton").each(function (button) {
      Event.stopObserving(button, "click", this._deleteExpertiseClassButtonClickListener);
    });
  },
  setup: function() {
    this.setupUsersPanel();
    this.setupMatrix();
  },
  setupUsersPanel: function () {
    for (var i = 0, l = this._panelUsers.length; i < l; i++) {
      var userElem = this._createUserElement(this._panelUsers[i].id, this._panelUsers[i].name, this._panelUsers[i].email);
      this._usersListContainer.appendChild(userElem);
    }
  },
  _appendClassMgmtButtons: function (elem, inUse) {
  	var contextualLinksWrapper = new Element("div", { className: "panelExpertsContextualLinkWrapper"});
  	var contextualLinks = new Element("div", { className: "contextualLinksPanelExperts"});
  	
  	var blockContextualEditLinkContainer = new Element("div", { className: "blockContextualLink edit"});
  	var blockContextualDeleteLinkContainer = new Element("div", { className: "blockContextualLink delete"});
	
    var editButton = new Element("a", { className: "panelExpertsEditExpertiseClassButton" });
    var deleteButton = null;
    
    if (inUse) {
      deleteButton = new Element("a", { className: "panelExpertsDeleteExpertiseClassButton panelExpertsDeleteExpertiseClassButtonDisabled" });
    } else {
      deleteButton = new Element("a", { className: "panelExpertsDeleteExpertiseClassButton" });
    }
    
    var editTooltipContainer = new Element("span", { className: "blockContextualLinkTooltip"});
    var editTooltipText = new Element("span", { className: "blockContextualLinkTooltipText"});
    var editTooltipArrow = new Element("span", { className: "blockContextualLinkTooltipArrow"});
    
    var deleteTooltipContainer = new Element("span", { className: "blockContextualLinkTooltip"});
    var deleteTooltipText = new Element("span", { className: "blockContextualLinkTooltipText"});
    var deleteTooltipArrow = new Element("span", { className: "blockContextualLinkTooltipArrow"});
    
    editButton.appendChild(editTooltipContainer);
    editTooltipContainer.appendChild(editTooltipText);
    editTooltipContainer.appendChild(editTooltipArrow);
    editTooltipText.update(getLocale().getText("panels.admin.panelExperts.editClassPopupEditButtonTooltip"));
    
    deleteButton.appendChild(deleteTooltipContainer);
    
    deleteTooltipContainer.appendChild(deleteTooltipText);
    deleteTooltipContainer.appendChild(deleteTooltipArrow);
    if (inUse)
      deleteTooltipText.update(getLocale().getText("panels.admin.panelExperts.cannotDeleteClassPopupDeleteButtonTooltip"));
    else
      deleteTooltipText.update(getLocale().getText("panels.admin.panelExperts.deleteClassPopupDeleteButtonTooltip"));
        
    elem.appendChild(contextualLinksWrapper);
    
    contextualLinksWrapper.appendChild(contextualLinks);
    
    contextualLinks.appendChild(blockContextualEditLinkContainer);
    contextualLinks.appendChild(blockContextualDeleteLinkContainer);

    blockContextualEditLinkContainer.appendChild(editButton);
    blockContextualDeleteLinkContainer.appendChild(deleteButton);

    Event.observe(editButton, "click", this._editExpertiseClassButtonClickListener);
    if (!inUse) {
      Event.observe(deleteButton, "click", this._deleteExpertiseClassButtonClickListener);
    }
  },
  setupMatrix: function () {
    var rowDiv = new Element("div", { className: "panelExpertsMatrixHeaderRow" });
    this._matrixContainer.appendChild(rowDiv);

    var topLeftSpacer = new Element("div", { className: "panelExpertsMatrixCell" });
    topLeftSpacer.update(' ');
    rowDiv.appendChild(topLeftSpacer);

    for (var x = 0, xLen = this._expertiseClasses.length; x < xLen; x++) {
      var elem = new Element("div", { className: "panelExpertsMatrixCell panelExpertsMatrixHeaderCell" });
      
      var captionElem = new Element("div", { className: "panelExpertsMatrixHeaderCaption" });
      captionElem.update(this._expertiseClasses[x].name);
      this._appendClassMgmtButtons(elem, this._expertiseClasses[x].inUse); 
      elem.appendChild(captionElem);
      
      elem._expertiseClassId = this._expertiseClasses[x].id;
           
      rowDiv.appendChild(elem);
    }
    
    for (var y = 0, yLen = this._intressClasses.length; y < yLen; y++) {
      var rowDiv = new Element("div", { className: "panelExpertsMatrixRow" });
      rowDiv._intressClassId = this._intressClasses[y].id;
      this._matrixContainer.appendChild(rowDiv);

      var elem = new Element("div", { className: "panelExpertsMatrixCell panelExpertsMatrixHeaderCell" });
      
      var captionElem = new Element("div", { className: "panelExpertsMatrixHeaderCaption" });
      captionElem.update(this._intressClasses[y].name);
      this._appendClassMgmtButtons(elem, this._intressClasses[y].inUse);
      elem.appendChild(captionElem);

      elem._intressClassId = this._intressClasses[y].id;
      
      rowDiv.appendChild(elem);
      
      for (var x = 0, xLen = this._expertiseClasses.length; x < xLen; x++) {
        var groupCell = new Element("div", { className: "panelExpertsMatrixCell panelExpertsMatrixGroupCell" });
        var group = this._findExpertiseGroup(this._expertiseClasses[x].id, this._intressClasses[y].id);
        
        groupCell._expertiseClassId = this._expertiseClasses[x].id;
        groupCell._intressClassId = this._intressClasses[y].id;
        groupCell._expertiseGroupId = group.id;
        
        rowDiv.appendChild(groupCell);
        
        Droppables.add(groupCell, {
          onDrop: this._onDroppedName
        });
        
        var groupUsers = this._panelExpertiseGroupUsers[group.id];
        for (var u = 0, uLen = groupUsers.length; u < uLen; u++) {
          var groupUser = groupUsers[u];
          var groupUserElem = this._createUserElement(groupUser.panelUserId, groupUser.name, groupUser.email, groupUser.id);
          groupCell.appendChild(groupUserElem);
        }
      }
    }
  },
  _onDroppedName: function (draggable, droparea) {
    var mgmt = draggable._management;

    var userExists = false;
    var expertiseGroupId = droparea._expertiseGroupId;
    var groupUsersList = mgmt._panelExpertiseGroupUsers[expertiseGroupId];
    var panelUserId = draggable._panelUserId;
    var panelId = mgmt._panel.id;
    
    for (var i = 0, l = groupUsersList.length; i < l; i++) {
      if (groupUsersList[i].panelUserId == panelUserId) {
        userExists = true;
        break;
      }
    }
    
    if (!userExists) {
      if (draggable._panelGroupUserId) {
        JSONUtils.request(CONTEXTPATH + '/panel/updatepanelexpertgroupuser.json', {
          parameters: {
            panelId: panelId,
            panelExpertGroupUserId: draggable._panelGroupUserId, 
            newExpertiseGroupId: expertiseGroupId
          },
          onSuccess: function (jsonRequest) {
            var oldGroupElement = draggable.up(".panelExpertsMatrixGroupCell");
            var oldGroupId = oldGroupElement._expertiseGroupId;
            var oldGroupUsersList = mgmt._panelExpertiseGroupUsers[oldGroupId];
            var panelGroupUserId = jsonRequest.id;
            var name = jsonRequest.name;
            var email = jsonRequest.email;

            for (var i = oldGroupUsersList.length - 1; i >= 0; i--) {
              if (oldGroupUsersList[i].panelUserId == panelUserId) {
                oldGroupUsersList.splice(i, 1);
              }
            }
            
            groupUsersList.push({
              id: panelGroupUserId,
              panelUserId: panelUserId,
              name: name,
              email: email
            });

            mgmt._removeUserElement(draggable);
            
            var elem = mgmt._createUserElement(panelUserId, name, email, panelGroupUserId);
            droparea.appendChild(elem);
          }
        });
      } else {
        JSONUtils.request(CONTEXTPATH + '/panel/createpanelexpertgroupuser.json', {
          parameters: {
            panelId: panelId,
            expertiseGroupId: expertiseGroupId,
            panelUserId: panelUserId
          },
          onSuccess: function (jsonRequest) {
            var panelGroupUserId = jsonRequest.id;
            var name = jsonRequest.name;
            var email = jsonRequest.email;
            
            groupUsersList.push({
              id: panelGroupUserId,
              panelUserId: panelUserId,
              name: name,
              email: email
            });
            
            var elem = mgmt._createUserElement(panelUserId, name, email, panelGroupUserId);
            droparea.appendChild(elem);
          }
        });
      }
    }
  },
  _createUserElement: function (panelUserId, name, email, panelGroupUserId) {
    var elem = new Element("div", { className: "panelExpertsUser" });
    var elemUsername = new Element("div", { className: "panelExpertsUserName" });
    elem.appendChild(elemUsername);
    elemUsername.update(name ? name : email);	
    
    elem._panelUserId = panelUserId;
    elem._panelGroupUserId = panelGroupUserId;
    elem._management = this;
    
    // TODO: poista ghosting (?)
    elem._dragIntf = new Draggable(elem, {
      revert: true,
      ghosting: true,
      endeffect: Prototype.emptyFunction
    });
    
    var removeBtn = new Element("div", { className: "panelExpertsUserRemoveButton" });
    removeBtn.update(' ');
    elem.appendChild(removeBtn);
    Event.observe(removeBtn, "click", this._removeExpertButtonClickListener);

    if (name && email) {
      var tooltipSpan = new Element("span", { className: "blockContextInfoTooltip"});
      var tooltipText = new Element("span", { className: "blockContextInfoTooltipText"});
      tooltipText.update(email);
      var tooltipArrow = new Element("span", { className: "blockContextInfoTooltipArrow"});
      tooltipSpan.appendChild(tooltipText);
      tooltipSpan.appendChild(tooltipArrow);
      elem.appendChild(tooltipSpan);
    }
    
    return elem;
  },
  _removeUserElement: function (element) {
    var removeBtn = element.down(".panelExpertsUserRemoveButton");
    Event.stopObserving(removeBtn, "click", this._removeExpertButtonClickListener);
    element._dragIntf.destroy();
    element.remove();
  },
  _findExpertiseGroup: function (expertiseClassId, intressClassId) {
    for (var i = 0, l = this._panelExpertiseGroups.length; i < l; i++) {
      if ((this._panelExpertiseGroups[i].expertiseClassId == expertiseClassId) && 
          (this._panelExpertiseGroups[i].intressClassId == intressClassId)) {
        return this._panelExpertiseGroups[i];
      }
    }
  },
  _findUser: function (userId) {
    for (var i = 0, l = this._panelUsers.length; i < l; i++) {
      if (this._panelUsers[i].id === userId)
        return this._panelUsers[i];
    }
    return undefined;
  },
  _onNewIntressButtonClick: function (event) {
    Event.stop(event);
    var newIntressName = $('newIntressName').value;
    var _this = this;
    
    JSONUtils.request(CONTEXTPATH + '/panel/createpanelintress.json', {
      parameters: {
        panelId: _this._panel.id,
        newIntressName: newIntressName
      },
      onSuccess: function (jsonRequest) {
    	$('newIntressName').value = '';
      $('newIntressName').validate(true, true);

        var intressId = jsonRequest.id;
        var groupIds = jsonRequest.newExpertiseGroups;

        _this._intressClasses.push({
          id: intressId,
          name: newIntressName
        });
        
        // New row
        var rowDiv = new Element("div", { className: "panelExpertsMatrixRow" });
        rowDiv._intressClassId = intressId;
        _this._matrixContainer.appendChild(rowDiv);

        // New row title cell
        var elem = new Element("div", { className: "panelExpertsMatrixCell panelExpertsMatrixHeaderCell" });

        var captionElem = new Element("div", { className: "panelExpertsMatrixHeaderCaption" });
        captionElem.update(newIntressName);

        elem._intressClassId = jsonRequest.id;
        _this._appendClassMgmtButtons(elem, false);
        elem.appendChild(captionElem);
        rowDiv.appendChild(elem);
        
        // New row cells that represent groups
        for (var x = 0, xLen = _this._expertiseClasses.length; x < xLen; x++) {
          var expertiseClassId = _this._expertiseClasses[x].id;
          var expertiseGroupId = groupIds[_this._expertiseClasses[x].id];

          // Update lists with new group
          _this._panelExpertiseGroups.push({
            id: expertiseGroupId,
            expertiseClassId: expertiseClassId,
            intressClassId: intressId
          });
          _this._panelExpertiseGroupUsers[expertiseGroupId] = new Array();

          // New group cell
          var groupCell = new Element("div", { className: "panelExpertsMatrixCell panelExpertsMatrixGroupCell" });
          
          groupCell._expertiseClassId = expertiseClassId;
          groupCell._intressClassId = intressId;
          groupCell._expertiseGroupId = expertiseGroupId;
          
          rowDiv.appendChild(groupCell);
          
          Droppables.add(groupCell, {
            onDrop: _this._onDroppedName
          }); 
        }
      }
    });
  },
  _onNewExpertiseButtonClick: function (event) {
    Event.stop(event);
    var _this = this;
    var newExpertiseName = $('newExpertiseName').value;
    
    JSONUtils.request(CONTEXTPATH + '/panel/createpanelexpertise.json', {
      parameters: {
        panelId: _this._panel.id,
        newExpertiseName: newExpertiseName
      },
      onSuccess: function (jsonRequest) {
    	$('newExpertiseName').value = '';
    	$('newExpertiseName').validate(true, true);

        var newExpertiseClassId = jsonRequest.id;
        var groupIds = jsonRequest.newExpertiseGroups;

        _this._expertiseClasses.push({
          id: newExpertiseClassId,
          name: newExpertiseName
        });
        
        _this._matrixContainer.select('.panelExpertsMatrixHeaderRow').each(function (headerRow) {
          var newHeader = new Element("div", { className: "panelExpertsMatrixCell panelExpertsMatrixHeaderCell" });

          var captionElem = new Element("div", { className: "panelExpertsMatrixHeaderCaption" });
          captionElem.update(newExpertiseName);

          newHeader._expertiseClassId = newExpertiseClassId;
          _this._appendClassMgmtButtons(newHeader, false);
          newHeader.appendChild(captionElem);
          headerRow.appendChild(newHeader);
        });
        
        _this._matrixContainer.select('.panelExpertsMatrixRow').each(function (rowDiv) {
          var groupCell = new Element("div", { className: "panelExpertsMatrixCell panelExpertsMatrixGroupCell" });
          
          var intressClassId = rowDiv._intressClassId;
          var expertiseGroupId = groupIds[rowDiv._intressClassId];

          // Update lists with new group
          _this._panelExpertiseGroups.push({
            id: expertiseGroupId,
            expertiseClassId: newExpertiseClassId,
            intressClassId: intressClassId
          });
          _this._panelExpertiseGroupUsers[expertiseGroupId] = new Array();
          
          groupCell._expertiseClassId = newExpertiseClassId;
          groupCell._intressClassId = intressClassId;
          groupCell._expertiseGroupId = expertiseGroupId;
          
          rowDiv.appendChild(groupCell);
          
          Droppables.add(groupCell, {
            onDrop: _this._onDroppedName
          }); 
        });
      }
    });
  },
  _onRemoveExpertButtonClick: function (event) {
    var removeBtn = Event.element(event);
    Event.stopObserving(removeBtn, "click", this._removeExpertButtonClickListener);
    
    var userElement = removeBtn.up(".panelExpertsUser");
    var groupElement = userElement.up(".panelExpertsMatrixGroupCell");
    
    var expertiseGroupId = groupElement._expertiseGroupId;
    var panelGroupUserId = userElement._panelGroupUserId;
    
    var _this = this;
    
    JSONUtils.request(CONTEXTPATH + '/panel/removepanelexpertgroupuser.json', {
      parameters: {
        panelId: _this._panel.id,
        panelExpertGroupUserId: panelGroupUserId 
      },
      onSuccess: function (jsonRequest) {
        userElement.remove();

        var groupUsersList = _this._panelExpertiseGroupUsers[expertiseGroupId];
        
        for (var i = groupUsersList.length - 1; i >= 0; i--) {
          if (groupUsersList[i].id == panelGroupUserId) {
            groupUsersList.splice(i, 1);
          }
        }
      }
    });
  },
  _onEditExpertiseClassButtonClick: function (event) {
    Event.stop(event);
    var element = Event.element(event);
    var _this = this;

    if (element.hasClassName("panelExpertsEditExpertiseClassButton")) {
      // Fetch the old name and set the text field
      var name = element.up(".panelExpertsMatrixHeaderCell").down(".panelExpertsMatrixHeaderCaption").innerHTML;
      
      var content = new Element("div", { className: "panelExpertsEditExpertisePopupTextContainer" });
      var inputContainer = new Element("div", { className: "panelExpertsEditExpertisePopupInputContainer" });
      var input = new Element("input", { type: "text", name: "modifiedClassName", value: name, className: "modalPopupTextfield formField formTextField" });
      var textContainer = new Element("div", { className: "modalPopupTitleContent" });
      textContainer.update(getLocale().getText('panels.admin.panelExperts.editExpertiseClassPopupMessage'));
      inputContainer.appendChild(input);
      content.appendChild(textContainer);
      content.appendChild(inputContainer);
    
      var popup = new ModalPopup({
        content: content,
        buttons: [
          {
            text: getLocale().getText('panels.admin.panelExperts.editClassPopupCancelButtonCaption'),
            action: function(instance) {
              instance.close();
            }
          },
          {
            text: getLocale().getText('panels.admin.panelExperts.editClassPopupEditButtonCaption'),
            classNames: "modalPopupButtonGreen",
            action: function(instance) {
              instance.close();
  
              var expertiseClassCell = element.up(".panelExpertsMatrixHeaderCell");
              var newName = instance.getFrame().down("input[name='modifiedClassName']").value;
              
              var classType = undefined;
              var classId = undefined;
  
              if (expertiseClassCell._expertiseClassId) {
                classType = "EXP";
                classId = expertiseClassCell._expertiseClassId;
              } else {
                if (expertiseClassCell._intressClassId) {
                  classType = "INT";
                  classId = expertiseClassCell._intressClassId;
                }
              }
              
              if (classType === "EXP") {
                JSONUtils.request(CONTEXTPATH + '/panel/admin/updateexpertisename.json', {
                  parameters: {
                    expertiseClassId: classId,
                    name: newName 
                  },
                  onSuccess: function (jsonRequest) {
                    expertiseClassCell.down(".panelExpertsMatrixHeaderCaption").update(newName);
                    
                    for (var i = 0, l = _this._expertiseClasses.length; i < l; i++) {
                      if (_this._expertiseClasses[i].id == classId) {
                        _this._expertiseClasses[i].name = newName;
                        break;
                      }
                    }
                  }
                });
              } else {
                if (classType === "INT") {
                  JSONUtils.request(CONTEXTPATH + '/panel/admin/updateinterestname.json', {
                    parameters: {
                      interestClassId: classId,
                      name: newName 
                    },
                    onSuccess: function (jsonRequest) {
                      expertiseClassCell.down(".panelExpertsMatrixHeaderCaption").update(newName);
  
                      for (var i = 0, l = _this._intressClasses.length; i < l; i++) {
                        if (_this._intressClasses[i].id == classId) {
                          _this._intressClasses[i].name = newName;
                          break;
                        }
                      }
                    }
                  });
                }
              }
            }
          }
        ]
      });
  
      popup.open(element);
    }    
  },
  _onDeleteExpertiseClassButtonClick: function (event) {
    Event.stop(event);
    var element = Event.element(event);
    var _this = this;
    
    var titleContainer = new Element("div", { className: "modalPopupTitleContent" });
    titleContainer.update(getLocale().getText('panels.admin.panelExperts.deleteExpertiseClassPopupMessage'));

    var popup = new ModalPopup({
      content: titleContainer,
      buttons: [
        {
          text: getLocale().getText('panels.admin.panelExperts.deleteClassPopupCancelButtonCaption'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panels.admin.panelExperts.deleteClassPopupDeleteButtonCaption'),
          classNames: "modalPopupButtonRed",
          action: function(instance) {
            instance.close();

            var expertiseClassCell = element.up(".panelExpertsMatrixHeaderCell");
            
            var classType = undefined;
            var classId = undefined;

            if (expertiseClassCell._expertiseClassId) {
              classType = "EXP";
              classId = expertiseClassCell._expertiseClassId;
            } else {
              if (expertiseClassCell._intressClassId) {
                classType = "INT";
                classId = expertiseClassCell._intressClassId;
              }
            }
            
            
            if (classType === "EXP") {
              JSONUtils.request(CONTEXTPATH + '/panel/admin/deleteexpertise.json', {
                parameters: {
                  expertiseClassId: classId
                },
                onSuccess: function (jsonRequest) {
                  _this._matrixContainer.select(".panelExpertsMatrixGroupCell").each(function (cell) {
                    if (cell._expertiseClassId == classId) {
                      // Remove Users gracefully
                      cell.select(".panelExpertsUser").each(function (userNode) {
                        _this._removeUserElement(userNode);
                      });
                      
                      // Remove GroupCell from droppables
                      Droppables.remove(cell);
                      
                      cell.remove();
                    }
                  });
                  
                  Event.stopObserving(expertiseClassCell.down(".panelExpertsEditExpertiseClassButton"), "click", this._editExpertiseClassButtonClickListener);
                  Event.stopObserving(expertiseClassCell.down(".panelExpertsDeleteExpertiseClassButton"), "click", this._deleteExpertiseClassButtonClickListener);
                  expertiseClassCell.remove(); 
            
                  for (var i = _this._expertiseClasses.length - 1; i > 0; i--) {
                    if (_this._expertiseClasses[i].id == classId) {
                      _this._expertiseClasses.splice(i, 1);
                      break;
                    }
                  }
                }
              });
            } else {
              if (classType === "INT") {
                JSONUtils.request(CONTEXTPATH + '/panel/admin/deleteinterest.json', {
                  parameters: {
                    interestClassId: classId
                  },
                  onSuccess: function (jsonRequest) {
                    var rowElement = expertiseClassCell.up(".panelExpertsMatrixRow");

                    rowElement.select(".panelExpertsMatrixGroupCell").each(function (cell) {
                      // Remove Users gracefully
                      cell.select(".panelExpertsUser").each(function (userNode) {
                        _this._removeUserElement(userNode);
                      });
                      
                      // Remove GroupCell from droppables
                      Droppables.remove(cell);
                    });
                    
                    // Remove button listeners
                    Event.stopObserving(expertiseClassCell.down(".panelExpertsEditExpertiseClassButton"), "click", this._editExpertiseClassButtonClickListener);
                    Event.stopObserving(expertiseClassCell.down(".panelExpertsDeleteExpertiseClassButton"), "click", this._deleteExpertiseClassButtonClickListener);
                    
                    rowElement.remove();
                    
                    for (var i = _this._intressClasses.length - 1; i > 0; i--) {
                      if (_this._intressClasses[i].id == classId) {
                        _this._intressClasses.splice(i, 1);
                        break;
                      }
                    }
                  }
                });
              }
            }
          }
        }
      ]
    });

    popup.open(element);
  }
});

document.observe("dom:loaded", function(event) {
  new PanelExpertsMatrixController($('panelExpertsMatrixContainer'), $('panelExpertsUsersContainer'));
});
