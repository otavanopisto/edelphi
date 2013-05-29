if ((typeof fi) == 'undefined') {
  /**
   * @namespace fi package
   */
  var fi = {};
};

if ((typeof fi.internetix) == 'undefined') {
  /**
   * @namespace fi.internetix package
   */
  fi.internetix = {};
};

if ((typeof fi.internetix.draft) == 'undefined') {
  /**
   * @namespace fi.internetix.draft package
   */
  fi.internetix.draft = {};
};

fi.internetix.draft.DraftTaskVault = {
  getTaskClassFor: function (element) {
    for (var i = this._taskTypes.length - 1; i >= 0; i--) {
      for (var j = 0; j < this._taskTypes[i].supports.length; j++) {
        var selector = new Selector(this._taskTypes[i].supports[j]);
        if (selector.match(element))
          return this._taskTypes[i];
      }
    }
  },
  getTaskClassById: function (taskId) {
    return this._tasksById.get(taskId);
  },
  registerCustomTask: function (customTask) {
    this._customTasks.push(customTask);
  },
  getCustomTasks: function () {
    return this._customTasks;
  },
  getCustomTaskById: function (id) {
    for (var i = 0, l = this._customTasks.length; i < l; i++) {
      if (id == this._customTasks[i].getId())
        return this._customTasks[i];
    }
    
    return null;
  },
  _registerTaskType: function (clazz, taskId) {
    this._taskTypes.push(clazz);
    this._tasksById.set(taskId, clazz);
  },
  _taskTypes: new Array(),
  _tasksById: new Hash(),
  _customTasks: new Array()
};

fi.internetix.draft.AbstractDraftTask = Class.create({
  initialize : function() {
    
  },
  createDraftData: function () {
    throw new Error("Not implemented"); 
  },
  restoreDraftData: function () { 
    throw new Error("Not implemented");
  },
  _compress: function (value) {
    return value;
  },
  _uncompress: function (draftData) {
    return draftData;
  }
});

fi.internetix.draft.InputFieldDraftTask = Class.create(fi.internetix.draft.AbstractDraftTask, {
  initialize : function($super) {
    $super();
  },
  createDraftData: function (element) {
    var elementName = element.name;
    
    if (elementName.blank())
      return null;

    switch (element.type) {
      case 'checkbox':
        return new fi.internetix.draft.ElementDraft('inputField', elementName, this._compress(element.checked));
      break;
      case 'radio':
        if (element.checked) {
          var inputs = document.getElementsByName(elementName);
          for (var i = 0, l = inputs.length; i < l; i++) {
            if (inputs[i].checked) {
              return new fi.internetix.draft.ElementDraft('inputField', elementName, inputs[i].value, i);
            }
          }
        }
      break;
      default:
        return new fi.internetix.draft.ElementDraft('inputField', elementName, this._compress(element.value));
      break;
    }
    
    return null;
  },
  restoreDraftData: function (elementDraft) {
    var name = elementDraft.getName();
    var index = elementDraft.getIndex();
    var elements = document.getElementsByName(name);
    if (elements.length > index) {
      switch (elements[index].type) {
        case 'checkbox':
          elements[index].checked = this._uncompress(elementDraft.getData());
        break;
        case 'radio':
          if (elements[index].value == this._uncompress(elementDraft.getData()))
            elements[index].checked = true;
        break;
        default:
          elements[index].value = this._uncompress(elementDraft.getData());
        break;
      }
    } 
  }
});

Object.extend(fi.internetix.draft.InputFieldDraftTask, {
  supports: ['input[type="checkbox"]','input[type="hidden"]','input[type="password"]','input[type="radio"]','input[type="text"]']
});

fi.internetix.draft.DraftTaskVault._registerTaskType(fi.internetix.draft.InputFieldDraftTask, 'inputField');

fi.internetix.draft.SelectFieldDraftTask = Class.create(fi.internetix.draft.AbstractDraftTask, {
  initialize : function($super) {
    $super();
  },
  createDraftData: function (element) {
    var elementName = element.name;
    var elementValue = element.selectedIndex;
    
    if (elementName.blank())
      return null;
    else
      return new fi.internetix.draft.ElementDraft('selectField', elementName, this._compress(elementValue));
  },
  restoreDraftData: function (elementDraft) {
    var name = elementDraft.getName();
    var index = elementDraft.getIndex();
    var elements = document.getElementsByName(name);
    var selectedValue = this._uncompress(elementDraft.getData());
    
    if (elements.length > index) {
      var element = elements[index];
      element.selectedIndex = selectedValue;
    }
  }
});

Object.extend(fi.internetix.draft.SelectFieldDraftTask, {
  supports: ['select']
});

fi.internetix.draft.DraftTaskVault._registerTaskType(fi.internetix.draft.SelectFieldDraftTask, 'selectField');

fi.internetix.draft.TextAreaFieldDraftTask = Class.create(fi.internetix.draft.AbstractDraftTask, {
  initialize : function($super) {
    $super();
  },
  createDraftData: function (element) {
    var elementName = element.name;
    var elementValue = element.value;
    
    if (elementName.blank())
      return null;
    else
      return new fi.internetix.draft.ElementDraft('textareaField', elementName, this._compress(elementValue));
  },
  restoreDraftData: function (elementDraft) {
    var name = elementDraft.getName();
    var index = elementDraft.getIndex();
    var elements = document.getElementsByName(name);
    if (elements.length > index) {
      elements[index].value = this._uncompress(elementDraft.getData());
    } 
  }
});

Object.extend(fi.internetix.draft.TextAreaFieldDraftTask, {
  supports: ['textarea']
});

fi.internetix.draft.DraftTaskVault._registerTaskType(fi.internetix.draft.TextAreaFieldDraftTask, 'textareaField');

fi.internetix.draft.CustomDraftTask = Class.create(fi.internetix.draft.AbstractDraftTask, {
  getId: function () {
    throw new Error("Not implemented"); 
  }
});

fi.internetix.draft.ElementDraft = Class.create({
  initialize : function(draftTaskId, name, data,index) {
    this._data = data;
    this._name = name;
    this._draftTaskId = draftTaskId;
    this._index = index||0;
  },
  getData: function () {
    return this._data;
  },
  getName: function () {
    return this._name;
  },
  getIndex: function () {
    return this._index;
  },
  getDraftTaskId: function () {
    return this._draftTaskId;
  }
});

fi.internetix.draft.DraftAPI = Class.create({
  initialize: function (rootElement) {
    this._rootElement = rootElement;
  },
  createFormDraft: function () {
    var elementDrafts = new Array();
    this._draftChildElements(this._rootElement, elementDrafts);
    var draftData = new Hash();
    draftData.set("elements", elementDrafts);
    
    var customDrafts = new Array();
    for (var i = 0, l = fi.internetix.draft.DraftTaskVault.getCustomTasks().length; i < l; i++) {
      var customTask = fi.internetix.draft.DraftTaskVault.getCustomTasks()[i];
      var customDraft = customTask.createDraftData();
      if (customDraft) {
        customDrafts.push({
          "name": customDraft.getName(),
          "taskId": customDraft.getDraftTaskId(),
          "data": customDraft.getData()
        });
      }
    }
    
    if (customDrafts.length > 0) {
      draftData.set("custom", customDrafts);
    }
    
    return Object.toJSON(draftData);
  },
  restoreFormDraft: function (restoreData) {
    var draftData = Object.isString(restoreData) ? restoreData.evalJSON() : restoreData;

    var elementDrafts = draftData.elements;
    for (var i = 0, l = elementDrafts.length; i < l; i++) {
      var elementDraft = new fi.internetix.draft.ElementDraft(elementDrafts[i].taskId, elementDrafts[i].name, elementDrafts[i].data, elementDrafts[i].index);
      var draftTaskClass = fi.internetix.draft.DraftTaskVault.getTaskClassById(elementDraft.getDraftTaskId());
      if (draftTaskClass) {
        var draftTask = new draftTaskClass();
        draftTask.restoreDraftData(elementDraft);
      }
    }
    
    var customDrafts = draftData.custom;
    if (customDrafts) {
      for (var i = 0, l = customDrafts.length; i < l; i++) {
        var customDraft = new fi.internetix.draft.ElementDraft(customDrafts[i].taskId, customDrafts[i].name, customDrafts[i].data);
        var customTask = fi.internetix.draft.DraftTaskVault.getCustomTaskById(customDraft.getDraftTaskId());
        if (customTask)
          customTask.restoreDraftData(customDraft);
      }
    }
    
    document.fire("draft:draftRestore");
  },
  _draftChildElements: function (element, draftData) {
    var elements = element.childNodes;
    
    for (var i = 0, l = elements.length; i < l; i++) {
      if (elements[i].nodeType == 1) {
        var draftTaskClass = fi.internetix.draft.DraftTaskVault.getTaskClassFor(elements[i]);
        if (draftTaskClass) {
          var draftTask = new draftTaskClass();
          var elementDraft = draftTask.createDraftData(elements[i]);
          if ((elementDraft != null) && (elementDraft.getData() != undefined) && (elementDraft.getData() != null)) {
            var elementData = new Hash();
            elementData.set("name", elementDraft.getName());
            elementData.set("taskId", elementDraft.getDraftTaskId());
            elementData.set("data", elementDraft.getData());
            elementData.set("index", elementDraft.getIndex());
            draftData.push(elementData);
          }
        } else {
          this._draftChildElements(elements[i], draftData);
        }
      }
    } 
  }
});