if ((typeof fi) == 'undefined') {
  /**
   * @namespace fi package
   */
  var fi = {};
};if ((typeof fi.internetix) == 'undefined') {
  /**
   * @namespace fi.internetix package
   */
  fi.internetix = {};
};if ((typeof fi.internetix.datagrid) == 'undefined') {
  /**
   * @namespace fi.internetix.datagrid package
   */
  fi.internetix.datagrid = {};
};var _dataGrids = new Hash();

function getDataGridById(id) {
  return _dataGrids.get(id);
};

function getDataGrids() {
  return _dataGrids.values();
};DataGrid = Class.create({
  initialize : function(parentNode, options) {
    this._rowClickListener = this._onRowClick.bindAsEventListener(this);
    this._headerClickListener = this._onHeaderClick.bindAsEventListener(this);
    this._activeRows = new Hash();
    this._rowElements = new Hash();
    this._filters = new Array();
  
    this._headerRowContent = new Element("div", {
      className : "dataGridRowContent"
    });
    
    this._headerRow = new Element("div", {
      className : "dataGridHeaderRow"
    });
    this._headerRow.appendChild(this._headerRowContent);

    this._content = new Element("div", {
      className : "dataGridContent"
    });
    
    this._rowCount = new Element("input", {
      type: 'hidden',
      name: options.id + '.rowCount',
      value: 0
    });
    
    var classNames = "dataGrid";
    
    if (options.rowHoverEffect === true)
      classNames += " dataGridRowHoverEffect";
    
    this.domNode = new Element("div", {
      className : classNames
    });
    this.domNode.appendChild(this._headerRow);
    this.domNode.appendChild(this._content);
    this.domNode.appendChild(this._rowCount);
    
    this._headerCells = new Object();
    this._cellEditors = new Hash();
    parentNode.appendChild(this.domNode);
    
    this._hasHeader = false;
    
    this.options = options;
    for (var i = 0; i < options.columns.length; i++) {
      var column = options.columns[i];
      
      this._hasHeader = this._hasHeader || !((column.header == '') || (!column.header));
      
      var headerContent = undefined;

      if (column.headerimg) {
        var headerImgClassNames = "dataGridHeaderCellImage";
        if (Object.isFunction(column.headerimg.onclick))
          headerImgClassNames = headerImgClassNames + " dataGridHeaderCellImageButton";
        
        headerContent = new Element("img", { 
          src: column.headerimg.imgsrc, 
          title: column.headerimg.tooltip ? column.headerimg.tooltip : '', 
          className: headerImgClassNames
        });
        
        if (Object.isFunction(column.headerimg.onclick)) {
          headerContent._headerOnClick = column.headerimg.onclick;
          headerContent._columnIndex = i;
          Event.observe(headerContent, "click", this._headerClickListener);
        }
      } else {
        headerContent = new Element("div", {
          className : "dataGridHeaderCellText"
        }).update(column.header);
      }
      
      var headerCell = new Element("div", {
        className : "dataGridHeaderCell",
        title: column.headerTooltip ? column.headerTooltip : ''
      });
      headerCell.appendChild(headerContent);
      

      this._clearColumnFiltersClickListener = this._onClearColumnFiltersClickListener.bindAsEventListener(this);
      var clearFilterButton = new Element("span", { className : "dataGridClearFilterButton" });
      clearFilterButton._column = i;
      headerCell.appendChild(clearFilterButton);
      Event.observe(clearFilterButton, "click", this._clearColumnFiltersClickListener);
      
      if (column.sortAttributes) {
        this._sortColumnClickListener = this._onSortColumnClick.bindAsEventListener(this);

        if (column.sortAttributes.sortAscending) {
          var sortAscendingBtn = new Element("span", { className : "dataGridHeaderSortButton dataGridHeaderSortButtonAscending", title: column.sortAttributes.sortAscending.toolTip });
          
          sortAscendingBtn._sortAction = new column.sortAttributes.sortAscending.sortAction(i, "asc");
          headerCell.appendChild(sortAscendingBtn);

          Event.observe(sortAscendingBtn, "click", this._sortColumnClickListener);
        }
        if (column.sortAttributes.sortDescending) {
          var sortDescendingBtn = new Element("span", { className : "dataGridHeaderSortButton dataGridHeaderSortButtonDescending", title: column.sortAttributes.sortDescending.toolTip });
          
          sortDescendingBtn._sortAction = new column.sortAttributes.sortDescending.sortAction(i, "desc");
          headerCell.appendChild(sortDescendingBtn);

          Event.observe(sortDescendingBtn, "click", this._sortColumnClickListener);
        }
      }
      
      var measurementUnit = 'px';
      if (column.measurementUnit != undefined) {
        measurementUnit = column.measurementUnit;
      }
      
      if ((column.left != undefined) && (column.left != NaN)) {
        headerCell.setStyle( {
          left : column.left + measurementUnit
        });
      };

      if ((column.right != undefined) && (column.right != NaN)) {
        headerCell.setStyle( {
          right : column.right + measurementUnit
        });
      };
      
      if ((column.width != undefined) && (column.width != NaN)) {
        headerCell.setStyle( {
          width : column.width + measurementUnit
        });
      };

      this._headerCells[i] = headerCell;

      this._headerRowContent.appendChild(headerCell);
    }
    
    this._headerRow.setStyle({
      display: 'none'
    });
    
    if (options.id) {
      _dataGrids.set(options.id, this);
      document.fire("ix:dataGridAdd", {
        dataGridComponent: this 
      });
      
      this.domNode.setAttribute("dataGridId", options.id);
    }

    this._contextMenuButtonClickListener = this._onContextMenuButtonClick.bindAsEventListener(this);
    this._contextMenuItemClickListener = this._onContextMenuItemClick.bindAsEventListener(this);
  },
  deinitialize: function () {
    try {
      this.detachFromDom();
      this.deleteAllRows();
      this.domNode.descendants().invoke('purge');
      this.domNode.purge();
      this.domNode.remove();
      this.removeAllListeners();
    } catch (e) {
    }
  },
  getId: function () {
    return this.domNode.getAttribute("dataGridId");
  },
  addRow : function(values, editable) {
    return this._addRows([values], editable);
  },
  addRows: function (rowDatas, editable) {
    this.detachFromDom();
    var rowCount = this._addRows(rowDatas, editable);
    this.reattachToDom();
    return rowCount;
  },
  _addRows: function (rowDatas, editable) {
    var rowNumber = this.getRowCount() - 1;
    var rowElements = new Array(); 
    var columnCount = this.options.columns.length;
    
    for (var rowIndex = 0, rowCount = rowDatas.length; rowIndex < rowCount; rowIndex++) {
      rowNumber++;
      
      var values = rowDatas[rowIndex];
      
      if (values.length != this.options.columns.length) {
        throw new Error("Value array length (" + values.length + ") != data grid columns length (" + this.options.columns.length + ")");
      }
      
      var rowContent = new Element("div", { className : "dataGridRowContent" });
      var row = new Element("div", { className : "dataGridRow" });
      row._rowNumber = rowNumber;
      row.appendChild(rowContent);
      this._rowElements.set(rowNumber, row);
      
      if (this.options.rowClasses) {
        for (var i = 0, l = this.options.rowClasses.length; i < l; i++) {
          row.addClassName(this.options.rowClasses[i]);
        }
      }

      for (var i = 0; i < columnCount; i++) {
        var column = this.options.columns[i];
        var name = this.options.id ? this.options.id + '.' + rowNumber + '.' + (column.paramName ? column.paramName : i) : '';
        
        var cell = new Element("div", { className : "dataGridCell" });
        cell._column = i;
        
        var cellStyles = {};
        var hasStyles = false;
        
        var measurementUnit = 'px';
        if (column.measurementUnit != undefined) {
          measurementUnit = column.measurementUnit;
        }

        if ((column.left != undefined) && (column.left != NaN)) {
          cellStyles.left = column.left + measurementUnit;
          hasStyles = true;
        }
        
        if ((column.right != undefined) && (column.right != NaN)) {
          cellStyles.right = column.right + measurementUnit;
          hasStyles = true;
        }
        
        if ((column.width != undefined) && (column.width != NaN)) {
          cellStyles.width = column.width + measurementUnit;
          hasStyles = true;
        }
        
        if (hasStyles)
          cell.setStyle(cellStyles);
        
        var cellContentHandler = this._createCellContentHandler(name, column, editable); 
        rowContent.appendChild(cell);
        var cellController = DataGridControllers.getController(column.dataType);
        cellController.attachContentHandler(this, cell, cellContentHandler);
        cellController.setEditorValue(cellContentHandler, values[i]);
        
        if (this._hasHeader == true) {
          this._headerRow.setStyle({
            display: ''
          });
        }
      }    
      
      this._setRowCount(this.getRowCount() + 1);
      
      Event.observe(row, "click", this._rowClickListener);
      
      rowElements.push(row);
    }
    
    for (var i = 0, l = rowElements.length; i < l; i++) {
      this._content.appendChild(rowElements[i]);
      var rowNumber = rowElements[i]._rowNumber;
      
      this.fire("rowAdd", {
        dataGridComponent: this,
        row: rowNumber
      });
      
      for (var j = 0; j < columnCount; j++) {
        this.fire("cellValueChange", {
          dataGridComponent: this,
          row: rowNumber,
          column: j, 
          value: rowDatas[i][j]
        });
      }
    }
    
    return rowNumber;
  },
  deleteRow: function (rowNumber) {
    this._deleteRow(rowNumber);
    this._redoFilters();
    this._redoSort();
  },
  hideRow: function (rowNumber) {
    var rowElement = this.getRowElement(rowNumber);
    var doHide = rowElement.visible();

    if (doHide && this.fire("beforeRowVisibilityChange", {
      dataGridComponent: this,
      rows: [ rowNumber ],
      hidden: false
    })) {
      rowElement.hide();

      if (this.getVisibleRowCount() == 0 && this._hasHeader == true && this._filters.size() == 0) {
        this._headerRow.setStyle({
          display: 'none'
        });
      }
      
      this.fire("afterRowVisibilityChange", {
        dataGridComponent: this,
        rows: [ rowNumber ],
        hidden: true
      });
    }
  },
  hideRows: function (rowNumbers) {
    var hideRows = new Array();
    
    for (var i = 0, len = rowNumbers.length; i < len; i++) {
      var rowElem = this.getRowElement(rowNumbers[i]);
      if (rowElem.visible())
        hideRows.push(rowElem); 
    }
    
    var doHide = hideRows.length > 0;

    if (doHide && this.fire("beforeRowVisibilityChange", {
      dataGridComponent: this,
      rows: hideRows,
      hidden: false
    })) {
      this.detachFromDom();
      hideRows.invoke("hide");
      this.reattachToDom();
  
      if (this.getVisibleRowCount() == 0 && this._hasHeader == true && this._filters.size() == 0) {
        this._headerRow.setStyle({
          display: 'none'
        });
      }
  
      this.fire("afterRowVisibilityChange", {
        dataGridComponent: this,
        rows: rowNumbers,
        hidden: true
      });
    }
  },
  showRow: function (rowNumber) {
    var rowElement = this.getRowElement(rowNumber);
    var doShow = !rowElement.visible();
    
    if (doShow && this.fire("beforeRowVisibilityChange", {
      dataGridComponent: this,
      rows: [ rowNumber ],
      hidden: true
    })) {
      rowElement.show();

      this._headerRow.setStyle({
        display: ''
      });
      
      this.fire("afterRowVisibilityChange", {
        dataGridComponent: this,
        rows: [ rowNumber ],
        hidden: false
      });
    }
  },
  showAllRows: function () {
    var rowNumbers = new Array();
    var rowElements = new Array();

    for (var i = 0, len = this.getRowCount(); i < len; i++) {
      var rowElement = this.getRowElement(i); 
      if (!rowElement.visible()) {
        rowNumbers.push(i);
        rowElements.push(rowElement);
      }
    }

    var doShow = rowElements.length > 0;
    
    if (doShow && this.fire("beforeRowVisibilityChange", {
      dataGridComponent: this,
      rows: rowNumbers,
      hidden: true
    })) {
      this.detachFromDom();
      rowElements.invoke("show");
      this.reattachToDom();
  
      if (this.getVisibleRowCount() > 0 && this._hasHeader == true) {
        this._headerRow.setStyle({
          display: ''
        });
      }
  
      this.fire("afterRowVisibilityChange", {
        dataGridComponent: this,
        rows: rowNumbers,
        hidden: false
      });
    }
  },
  isRowVisible: function (rowNumber) {
    return this.getRowElement(rowNumber).visible();
  },
  getVisibleRowCount: function () {
    var result = 0;
    
    for (var i = 0, l = this.getRowCount(); i < l; i++) {
      if (this.isRowVisible(i))
        result++;
    }
    
    return result;
  },
  getNamedColumnIndex: function (name) {
    for (var i = 0; i < this.options.columns.length; i++) {
      var column = this.options.columns[i];
      if (column.paramName == name)
        return i;
    }
    
    return -1;
  },
  deleteAllRows: function () {
    while (this.getRowCount() > 0)
      this._deleteRow(this.getRowCount() - 1);
    this.clearFilters();
  },
  getCellEditor: function (row, column) {
    return this._cellEditors.get(row + '.' + column);
  },
  getCellValue: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return DataGridControllers.getController(handlerInstance._dataType).getEditorValue(handlerInstance);
  },
  setCellValue: function (row, column, value) {
    var handlerInstance = this.getCellEditor(row, column);
    var controller = DataGridControllers.getController(handlerInstance._dataType);
    var oldValue = controller.getEditorValue(handlerInstance);
    
    if (oldValue !== value) {
      controller.setEditorValue(handlerInstance, value);
      this.fire("cellValueChange", {
        dataGridComponent: this,
        row: row,
        column: column, 
        value: value
      });
    }
  },
  copyCellValue: function(column, fromRow, toRow) {
    var fromInstance = this.getCellEditor(fromRow, column);
    var toInstance = this.getCellEditor(toRow, column);
    
    DataGridControllers.getController(toInstance._dataType).copyCellValue(toInstance, fromInstance);
  },
  disableCellEditor: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return DataGridControllers.getController(handlerInstance._dataType).disableEditor(handlerInstance);
  },
  enableCellEditor: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return DataGridControllers.getController(handlerInstance._dataType).enableEditor(handlerInstance);
  },
  hideCell: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    DataGridControllers.getController(handlerInstance._dataType).hide(handlerInstance);
  },
  showCell: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    DataGridControllers.getController(handlerInstance._dataType).show(handlerInstance);
  },
  isCellVisible: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return DataGridControllers.getController(handlerInstance._dataType).isVisible(handlerInstance);
  },
  isCellDisabled: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return DataGridControllers.getController(handlerInstance._dataType).isDisabled(handlerInstance);
  },
  preventCellSelection: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    
  },
  allowCellSelection: function (row, column) {
    
  },
  getHeaderCell: function (column) {
    return this._headerCells[column];
  },
  getColumnCount: function () {
    return this.options.columns.length;
  },
  disableRow: function (row) {
    for (var column = 0; column < this.options.columns.length; column++)
      this.disableCellEditor(row, column);
  },
  enableRow: function (row) {
    for (var column = 0; column < this.options.columns.length; column++)
      this.enableCellEditor(row, column);
  },
  getRowCount: function () {
    return parseInt(this._rowCount.value);
  },
  isCellEditable: function (row, column) {
    var editor = this.getCellEditor(row, column);
    return editor._editable;
  },
  setCellEditable: function (row, column, editable) {
    var editor = this.getCellEditor(row, column);
    var controller = DataGridControllers.getController(editor._dataType);
    
    if (controller.getEditable(editor) != editable) {
      controller.setEditable(editor, editable);
      this.fire("cellEditableChanged", {
        dataGridComponent: this,
        row: row,
        column: column,
        editable: editable
      });
    }
  },
  setCellDataType: function (row, column, dataType) {
    var editor = this.getCellEditor(row, column);
    if (editor._dataType != dataType) {
      var oldController = DataGridControllers.getController(editor._dataType);
      var newController = DataGridControllers.getController(dataType);
      
      var value = oldController.getEditorValue(editor);
      var editable = editor._editable;
      var name = editor._name;
      var columnDefinition = editor._columnDefinition;
      var column = this._getCellEditorColumn(editor);
      var row = this._getCellEditorRow(editor);
      var cell = editor._cell;
      
      oldController.detachContentHandler(editor);
      oldController.destroyHandler(editor);
      
      if (editable) {
        var editor = newController.buildEditor(name, columnDefinition);
        newController.attachContentHandler(this, cell, editor);
        newController.setEditorValue(editor, value);
      } else {
        var viewer = newController.buildViewer(name, columnDefinition);
        newController.attachContentHandler(this, cell, viewer);
        newController.setEditorValue(viewer, value);
      }
      
      this.fire("cellDataTypeChanged", {
        dataGridComponent: this,
        row: row,
        column: column,
        dataType: dataType
      });
    }
  },
  focusCell: function (row, column) {
    var editor = this.getCellEditor(row, column);
    var controller = DataGridControllers.getController(editor._dataType);
    controller.focus(editor);
  },
  getCellDataType: function (row, column) {
    var editor = this.getCellEditor(row, column);
    return editor._dataType;
  },
  setActiveRows: function (rows) {
    while (this.getActiveRows().length > 0) {
      this.removeActiveRow(this.getActiveRows()[0]);
    }
    
    for (var i = 0; i < rows.length; i++) {
      this.addActiveRow(rows[i]);
    }
  },
  getActiveRows: function () {
    return this._activeRows.keys();
  },
  isActiveRow: function (rowNumber) {
    return this._activeRows.get(rowNumber) == true;
  },
  addActiveRow: function (rowNumber) {
    this._activeRows.set(rowNumber, true);
    this.getRowElement(rowNumber).addClassName("ixActiveTableRow");
  },
  removeActiveRow: function (rowNumber) {
    this._activeRows.unset(rowNumber);
    this.getRowElement(rowNumber).removeClassName("ixActiveTableRow");
  },
  getRowElement: function (rowNumber) {
    return this._rowElements.get(rowNumber);
  },
  isDetachedFromDom: function () {
    return this._detached == true;
  },
  detachFromDom: function() {
    if (!this.isDetachedFromDom()) {
      if (this.fire("beforeDetachFromDom", { dataGridComponent: this})) {
        this._detachedParent = this.domNode.parentNode;
        this._detachedNextSibling = this.domNode.next();
        
        this.domNode.remove();
        this._detached = true;
        this._detachedCount = 0;
        
        this.fire("afterDetachFromDom", { dataGridComponent: this});
      }
    }
    this._detachedCount++;
  },
  reattachToDom: function() {
    if (this.isDetachedFromDom()) {
      this._detachedCount--;
      
      if (this._detachedCount == 0) {
        if (this.fire("beforeReattachToDom", { dataGridComponent: this})) {
          if (this._detachedNextSibling) {
            this._detachedParent.insertBefore(this.domNode, this._detachedNextSibling);
          } else {
            this._detachedParent.appendChild(this.domNode);
          }
          this._detachedParent = undefined;
          this._detachedNextSibling = undefined;
          this._detached = false;
        
          this.fire("afterReattachToDom", { dataGridComponent: this}); 
        }
      }
    }
  },  
  addFilter: function (filter) {
    if (this.fire("beforeFiltering", { dataGridComponent: this })) {
      this._filters.push(filter);
      filter.execute({ 
        dataGridComponent: this 
      });
      
      if (Object.isFunction(filter.getColumn)) {
        var column = filter.getColumn();
        
        if ((column != undefined) && (column >= 0)) {
          var columnHeaderCell = this._headerCells[column];
          
          if (columnHeaderCell)
            columnHeaderCell.addClassName("dataGridColumnHeaderFiltered");
        }
      }
      this.fire("afterFiltering", { dataGridComponent: this });
    }
  },
  applyFilters: function () {
    this._redoFilters();
  },
  _redoFilters: function () {
    if (this.fire("beforeFiltering", { dataGridComponent: this })) {
      this.detachFromDom();
  
      this.showAllRows();
  
      var _this = this;
      this._filters.each(function(filter) {
        filter.execute({ 
          dataGridComponent: _this 
        });
      });
      
      this.reattachToDom();
      this.fire("afterFiltering", { dataGridComponent: this });
    }
  },
  clearFilters: function () {
    if (this.fire("beforeFiltering", { dataGridComponent: this })) {
      this._filters.clear();
      
      for (var i = 0, len = this.options.columns.length; i < len; i++)
        this._headerCells[i].removeClassName("dataGridColumnHeaderFiltered");
      
      this.showAllRows();
      this.fire("afterFiltering", { dataGridComponent: this });
    }
  },
  _clearColumnFilter: function (column) {
    if (this.fire("beforeFiltering", { dataGridComponent: this })) {
      for (var i = this._filters.size() - 1; i >= 0; i--) {
        var filter = this._filters[i];
        if (filter.getColumn() === column) {
          this._filters.splice(i, 1);
        }
      }
      var columnHeaderCell = this._headerCells[column];
      
      if (columnHeaderCell)
        columnHeaderCell.removeClassName("dataGridColumnHeaderFiltered");
      this.fire("afterFiltering", { dataGridComponent: this });
    }
  },
  _setSortMethod: function (sortMethod) {
    this._sortMethod = sortMethod;
    this._redoSort();
  },
  _redoSort: function () {
    if (this._sortMethod) {
      var rows = this._rowElements.values().clone();
      var event = { dataGridComponent: this };
      
      for (var i = 1, len = rows.length; i < len; i++) {
        var j = i;
        
        var row1 = rows[j - 1]._rowNumber;
        var row2 = rows[j]._rowNumber;
        
        while ((j > 0) && (this._sortMethod.compare(event, row1, row2) > 0)) {
          var row = rows[j];
          rows[j] = rows[j - 1]; 
          rows[j - 1] = row;
          j--;

          if (j > 0) {
            row1 = rows[j - 1]._rowNumber;
            row2 = rows[j]._rowNumber;
          }
        }
      }
    
      this.detachFromDom();
      
      for (var i = 0, len = rows.length; i < len; i++) {
        this._content.appendChild(rows[i]);
      }
      
      this.reattachToDom();
    }
  },
  _deleteRow: function (rowNumber) {
    this.fire("beforeRowDelete", {
      dataGridComponent: this,
      row: rowNumber
    });
    
    for (var row = rowNumber; row < (this.getRowCount() - 1); row++) {
      for (var column = 0; column < this.options.columns.length; column++) {
        var cellEditor = this.getCellEditor(row, column);
        var nextCellEditor = this.getCellEditor(row + 1, column); 
        DataGridControllers.getController(cellEditor._dataType)._copyState(cellEditor, nextCellEditor);
      }
    }
    
    var rowNumber = this.getRowCount() - 1;
    var rowElement = this._rowElements.get(rowNumber);

    for (var i = 0, len = this.options.columns.length; i < len; i++) {
      if (this.options.columns[i].contextMenu) {
        var cell = this._getCellEditorCell(this.getCellEditor(rowNumber, i));
        var contextMenuButton = cell.down(".dataGridCellContextMenuButton");
        Event.stopObserving(contextMenuButton, "click", this._contextMenuButtonClickListener);
      }
    }

    rowElement.remove();
    
    this._setRowCount(this.getRowCount() - 1);
    
    this.fire("rowDelete", {
      dataGridComponent: this, 
      row: rowNumber
    });

    if (this.getVisibleRowCount() == 0 && this._hasHeader == true && this._filters.size() == 0) {
      this._headerRow.setStyle({
        display: 'none'
      });
    }
  },
  _getContextButtonCell: function (contextMenuButton) {
    return $(contextMenuButton.parentNode.parentNode);
  },
  _getCellEditorCell: function (editorInstance) {
    return $(editorInstance._cell);
  },
  _getCellEditorRow: function (editorInstance) {
    return this._getCellRow(this._getCellEditorCell(editorInstance));
  },
  _getCellEditorColumn: function (editorInstance) {
    return this._getCellColumn(this._getCellEditorCell(editorInstance));
  },
  _getCellRow: function (cell) {
    var rowContent = $(cell.parentNode);
    if (rowContent) {
      var rowElement = $(rowContent.parentNode);
      if (rowElement)
        return rowElement._rowNumber;
    }
    
    return -1;
  },
  _getCellColumn: function (cell) {
    return cell._column;
  },  
  changeId: function (newId) {
    var oldId = this.options.id;
    
    if (this.fire("dataGridIdChange", { oldId: oldId, newId: newId})) {
      // Update options id
      this.options.id = newId;
      // Update global hash
      _dataGrids.unset(oldId);
      _dataGrids.set(newId, this);
      // Update rowCount element
      this._rowCount.name = newId + '.rowCount';
      // Update dataGridId attribute
      this.domNode.setAttribute("dataGridId", newId);
      // Update field ids
      for (var i = 0; i < this.options.columns.length; i++) {
        var column = this.options.columns[i];
        for (var row = 0; row < this.getRowCount(); row++) {
          var cellEditor = this.getCellEditor(row, i);
          var name = newId ? newId + '.' + row + '.' + (column.paramName ? column.paramName : i) : '';
          DataGridControllers.getController(cellEditor._dataType).changeParamName(cellEditor, name);
        }
      }
    };
  },
  _setRowCount: function (rowCount) {
    this._rowCount.value = rowCount; 
  },
  _onRowClick: function (event) {
    var row = Event.element(event);
    if (!row.hasClassName("dataGridRow"))
      row = row.up(".dataGridRow");
    if (row) {
      this.fire("rowClick", {
        dataGridComponent: this,
        row: row._rowNumber
      });
    }
  },
  _onHeaderClick: function (event) {
    var elem = Event.element(event);

    if (Object.isFunction(elem._headerOnClick)) {
      elem._headerOnClick({
        dataGridComponent: this,
        columnIndex: elem._columnIndex
      });
    }
  },
  _createCellContentHandler: function (name, columnDefinition, editable) {
    var controller = DataGridControllers.getController(columnDefinition.dataType);
    
    var cellEditable = editable||columnDefinition.editable;
    
    if (controller.getMode() == DataGridControllers.EDITMODE_NOT_EDITABLE)
      cellEditable = false;
    else if (controller.getMode() == DataGridControllers.EDITMODE_ONLY_EDITABLE)
      cellEditable = true;
    
    if (cellEditable) {
      var editor = controller.buildEditor(name, columnDefinition);
      return editor;
    } else {
      var viewer = controller.buildViewer(name, columnDefinition);
      return viewer;
    }
  },
  _setCellContentHandler: function (row, column, handlerInstance) {
    this._cellEditors.set(row + '.' + column, handlerInstance);
  },
  _unsetCellContentHandler: function (row, column) {
    this._cellEditors.unset(row + '.' + column);
  },
  _onClearColumnFiltersClickListener: function (event) {
    var clearBtn = Event.element(event);
    if (clearBtn._column) {
      this._clearColumnFilter(clearBtn._column);
      
      
      this._redoFilters();
    }
  },
  _onSortColumnClick: function (event) {
    var sortButton = Event.element(event);
    if (sortButton._sortAction) {
      this._setSortMethod(sortButton._sortAction);
    }
  },
  _onContextMenuButtonClick: function (event) {
    var contextMenuButton = Event.element(event);
    var cell = this._getContextButtonCell(contextMenuButton);
    var row = this._getCellRow(cell);
    var column = this._getCellColumn(cell);
    
    var columnOptions = this.options.columns[column];
    if (columnOptions && columnOptions.contextMenu) {
      var menuContainer = new Element("div", {className: "dataGridCellContextMenu"} );
      
      for (var i = 0, l = columnOptions.contextMenu.length; i < l; i++) {
        var menuItem = columnOptions.contextMenu[i];
        var menuElement = new Element("div");
        menuElement._menuItem = menuItem;

        if (!("-" === menuItem.text)) {
          menuElement.addClassName("dataGridCellContextMenuItem");
          menuElement.update(menuItem.text);
          Event.observe(menuElement, "click", this._contextMenuItemClickListener);
        } else {
          menuElement.addClassName("dataGridCellContextMenuItemSpacer");
        }
        menuContainer.appendChild(menuElement);
      }
      
      var _this = this;
      var windowMouseMove = function (event) {
        var element = Event.element(event);
        var overMenu = element.hasClassName('dataGridCellContextMenu');
        if (!overMenu) {
          if (element.up('.dataGridCellContextMenu'))
            overMenu = true;
        }
      
        if (!overMenu) {
          $$('.dataGridCellContextMenu').forEach(function (menu) {
            $(menu).select('.dataGridCellContextMenuItem').forEach(function (menuItem) {
              if (!menuItem.hasClassName("dataGridCellContextMenuItemSpacer"))
                Event.stopObserving(menuItem, "click", _this._contextMenuItemClickListener);
            }); 
            
            $(menu).remove();
          });
          
          Event.stopObserving(Prototype.Browser.IE ? document : window, "mousemove", windowMouseMove);
        }
      };
      
      Event.observe(Prototype.Browser.IE ? document : window, "mousemove", windowMouseMove);
      
      cell.appendChild(menuContainer);
    }
  },
  _onContextMenuItemClick: function (event) {
    var menuElement = Event.element(event);
    var contextMenu = menuElement.parentNode;
    
    var menuItem = menuElement._menuItem;
    var cell = $(contextMenu.parentNode);
    var row = this._getCellRow(cell);
    var column = this._getCellColumn(cell);

    var _this = this;
    contextMenu.select('.dataGridCellContextMenuItem').forEach(function (menuItem) {
      Event.stopObserving(menuItem, "click", _this._contextMenuItemClickListener);
    }); 

    contextMenu.remove();
    
    menuItem.onclick.execute({
      dataGridComponent: this,
      row: row,
      column: column,
      menuItem: menuItem
    });
  }
});

Object.extend(DataGrid.prototype,fni.events.FNIEventSupport);DataGridControllers = {
  registerController: function (controller) {
    this._controllers.set(controller.getDataType(), controller);
  },
  getController: function (dataType) {
    return this._controllers.get(dataType);
  },
  EDITMODE_EDITABLE: 0,
  EDITMODE_NOT_EDITABLE: 1, 
  EDITMODE_ONLY_EDITABLE: 2,
  _controllers: new Hash()  
};DataGridEditorController = Class.create({
  buildEditor: function (name, columnDefinition) { },
  buildViewer: function (name, columnDefinition) { },
  attachContentHandler: function (dataGrid, cell, handlerInstance) {
    handlerInstance._dataGrid = dataGrid;
    handlerInstance._cell = cell;
    
    if (handlerInstance._columnDefinition.contextMenu) {
      var contextMenuButtonContainer = new Element("div", {className: "dataGridCellContextMenuButtonContainer"});
      var contextMenuButton = new Element("span", {className: "dataGridCellContextMenuButton"});
      var editorContainer = new Element("div", {className: "dataGridCellEditorContainer"});
        cell.addClassName('dataGridContextMenuCell');
      
      Event.observe(contextMenuButton, "click", dataGrid._contextMenuButtonClickListener);

      contextMenuButtonContainer.appendChild(contextMenuButton);
      editorContainer.appendChild(handlerInstance);
      cell.appendChild(editorContainer);
      cell.appendChild(contextMenuButtonContainer);
    } else {
      cell.appendChild(handlerInstance);
    }
    
    var row = this.getEditorRow(handlerInstance);
    var column = this.getEditorColumn(handlerInstance);
    dataGrid._setCellContentHandler(row, column, handlerInstance);
    
    if (handlerInstance._columnDefinition.hidden == true) 
      this.hide(handlerInstance);
    else
      this.show(handlerInstance);
    
    var selectable = handlerInstance._columnDefinition.selectable;
    if (selectable == false)
      this.setSelectable(handlerInstance, false);
    
    return handlerInstance;
  },
  detachContentHandler: function (handlerInstance) {
    var row = this.getEditorRow(handlerInstance);
    var column = this.getEditorColumn(handlerInstance);
    handlerInstance._dataGrid._unsetCellContentHandler(row, column);
    handlerInstance._dataGrid = undefined;
    var cell = handlerInstance._cell;

    var children = cell.childElements();
    
    for (var i = children.length - 1; i >= 0; i--) {
      var child = children[i];
      child.parentNode.removeChild(child);
    }
    // For unknown reason the following line doesn't work in all cases.
//    cell.childElements().invoke('remove');
  },  
  destroyHandler: function (handlerInstance) { 
    handlerInstance._editable = undefined;
    handlerInstance._dataType = undefined;
    handlerInstance._name = undefined;
    handlerInstance._cell = undefined;
    handlerInstance._columnDefinition = undefined;
    
    if (handlerInstance._fieldValue) {
      handlerInstance.removeChild(handlerInstance._fieldValue);
      handlerInstance._fieldValue = undefined;
    }
    
    if (handlerInstance._fieldContent) {
      handlerInstance.removeChild(handlerInstance._fieldContent);
      handlerInstance._fieldContent = undefined;
    }
  },
  getEditable: function (handlerInstance) {
    return handlerInstance._editable;
  },
  setEditable: function (handlerInstance, editable) {
    if (handlerInstance._editable == editable)
      return handlerInstance;
    if ((this.getMode(handlerInstance) == DataGridControllers.EDITMODE_ONLY_EDITABLE) && (editable == false))
      return handlerInstance;
    if ((this.getMode(handlerInstance) == DataGridControllers.EDITMODE_NOT_EDITABLE) && (editable == true))
      return handlerInstance;
    
    var dataGrid = handlerInstance._dataGrid;
    var cell = handlerInstance._cell;
    var visible = this.isVisible(handlerInstance); 
    var cellValue = this.getEditorValue(handlerInstance);
    
    this.detachContentHandler(handlerInstance);
    
    var newHandler = editable == true ? this.buildEditor(handlerInstance._name, handlerInstance._columnDefinition) : this.buildViewer(handlerInstance._name, handlerInstance._columnDefinition);
    this.attachContentHandler(dataGrid, cell, newHandler);
    
    if (visible) 
      this.show(newHandler);
    else 
      this.hide(newHandler);
    
    this.setEditorValue(newHandler, cellValue);
    this.destroyHandler(handlerInstance);

    return newHandler;
  },
  getEditorValue: function (handlerInstance) {},
  setEditorValue: function (handlerInstance, value) {},
  getDisplayValue: function (handlerInstance) {
    if (this.getEditable(handlerInstance) != true)
      return handlerInstance._fieldContent.innerHTML;
    else
      return this.getEditorValue(handlerInstance);
  },
  disableEditor: function (handlerInstance) {},
  enableEditor: function (handlerInstance) {},
  enableEditor: function (handlerInstance) {},
  isDisabled: function (handlerInstance) {},
  hide: function (handlerInstance) {
    handlerInstance._cell.hide();
  },
  show: function (handlerInstance) {
    handlerInstance._cell.show();
  },
  isVisible: function (handlerInstance) {
    return handlerInstance._cell.visible();
  },
  setSelectable: function (handlerInstance, selectable) {
    if (selectable == true) {
      handlerInstance.onselectstart = undefined;
      handlerInstance.unselectable = "off";
      handlerInstance.style.MozUserSelect = "text";
    } else {
      handlerInstance.onselectstart = function(){ return false; };
      handlerInstance.unselectable = "on";
      handlerInstance.style.MozUserSelect = "none";
    }
  },
  getMode: function () { },
  getDataType: function () { },
  changeParamName: function (handlerInstance, name) {
    if (handlerInstance._editable) {
      handlerInstance.name = name;
    } else {
      handlerInstance._name = name;
      if (handlerInstance._fieldValue) {
        handlerInstance._fieldValue.name = name;
      }
    }
  },
  focus: function (handlerInstance) {
    Form.Element.focus(handlerInstance);
  },
  getDataGridComponent: function (handlerInstance) {
    return handlerInstance._dataGrid;
  },
  getEditorRow: function (handlerInstance) {
    return handlerInstance._dataGrid._getCellEditorRow(handlerInstance);
  },
  getEditorColumn: function (handlerInstance) {
    return handlerInstance._dataGrid._getCellEditorColumn(handlerInstance);
  },
  _createEditorElement: function (elementName, name, className, attributes, columnDefinition) {
    var editor = new Element(elementName, Object.extend(attributes||{}, {className: "dataGridCellEditor" + (className ? ' ' + className : '')}));
    
    if (columnDefinition.editorClassNames) {
      var classNames = columnDefinition.editorClassNames.split(' ');
      for (var i = 0, l = classNames.length; i < l; i++) {
        editor.addClassName(classNames[i]);
      }
    }
    
    editor._editable = true;
    editor._dataType = this.getDataType();
    editor._name = name;
    editor._columnDefinition = columnDefinition;
    return editor;
  },
  _createViewerElement: function (elementName, name, className, attributes, columnDefinition) {
    var viewer = new Element(elementName, Object.extend(attributes||{}, {className: "dataGridCellViewer" + (className ? ' ' + className : '')}));
    
    if (columnDefinition.viewerClassNames) {
      var classNames = columnDefinition.viewerClassNames.split(' ');
      for (var i = 0, l = classNames.length; i < l; i++) {
        viewer.addClassName(classNames[i]);
      }
    }
    
    viewer._editable = false;
    viewer._dataType = this.getDataType();
    viewer._name = name;
    viewer._columnDefinition = columnDefinition;
    
    viewer._fieldValue = new Element("input", {type: "hidden", name: name});
    viewer._fieldContent = new Element("span"); 
    viewer.appendChild(viewer._fieldValue);
    viewer.appendChild(viewer._fieldContent); 
    
    return viewer;
  },
  _setViewerValue: function (viewer, value, displayValue) {
    if (value == undefined||value==null) {
      viewer._fieldValue.value = '';
      viewer._fieldContent.innerHTML = '';
    } else {
      viewer._fieldValue.value = value;
      viewer._fieldContent.innerHTML = displayValue ? displayValue : String(value).escapeHTML();
    }
  },
  _getViewerValue: function (viewer) {
    return viewer._fieldValue.value;
  },
  _fireValueChange: function (handlerInstance, newValue) {
    handlerInstance._dataGrid.fire("cellValueChange", {
      dataGridComponent: handlerInstance._dataGrid,
      fieldType: handlerInstance._dataType,
      column: this.getEditorColumn(handlerInstance),
      row: this.getEditorRow(handlerInstance),
      value: newValue
    });
  },
  _addDisabledHiddenElement: function (handlerInstance) {
    if (handlerInstance.parentNode) {
      var value = this.getEditorValue(handlerInstance);
      if (handlerInstance._disabledHiddenElement) {
        handlerInstance._disabledHiddenElement.value = value;
      } else {
        var hiddenElement = new Element("input", {type: 'hidden', value: value, name: handlerInstance._name});
        handlerInstance._disabledHiddenElement = hiddenElement;
        handlerInstance._cell.appendChild(hiddenElement);
      }
    } else {
      // TODO: Onko tälläisiäkin tilanteita ????
    }
  },
  _removeDisabledHiddenElement: function (handlerInstance) {
    if (handlerInstance._disabledHiddenElement) {
      handlerInstance._disabledHiddenElement.remove();
      delete handlerInstance._disabledHiddenElement;
    }
  },
  _updateDisabledHiddenElement: function (handlerInstance, value) {
    if (handlerInstance._disabledHiddenElement) {
      handlerInstance._disabledHiddenElement.value = value;
    }
  },
  copyCellValue: function(target, source) {
    this.setEditorValue(target, this.getEditorValue(source));
  },
  _copyState: function (target, source) {
    this.copyCellValue(target, source);
    return this.setEditable(target, this.getEditable(source));
    // TODO: disabled, datatype yms tiedot
  },
  _unescapeHtmlEntities: function(value) {
    if (value) {
      var tmp = document.createElement("pre");
      tmp.innerHTML = value;
      value = tmp.firstChild.nodeValue;
    }
    return value;
  }
});

Object.extend(DataGridEditorController.prototype,fni.events.FNIEventSupport);ButtonTableEditorButtonController = Class.create(DataGridEditorController, {
  buildViewer: function ($super, name, columnDefinition) {
    if (columnDefinition.imgsrc) {  
      var cellViewer = new Element("img", { src: columnDefinition.imgsrc, title: columnDefinition.tooltip ? columnDefinition.tooltip : '', className: "dataGridCellViewer dataGridCellEditorButton"});
      
      if (columnDefinition.viewerClassNames) {
        var classNames = columnDefinition.viewerClassNames.split(' ');
        for (var i = 0, l = classNames.length; i < l; i++) {
          cellViewer.addClassName(classNames[i]);
        }
      }
      
      cellViewer._editable = false;
      cellViewer._dataType = this.getDataType();
      cellViewer._name = name;
      cellViewer._columnDefinition = columnDefinition;
      
      return cellViewer;
    } else {
      throw new Error("Unable to build button without image");
    }
  },
  attachContentHandler: function ($super, dataGrid, cell, handlerInstance) {
    var handlerInstance = $super(dataGrid, cell, handlerInstance);
    handlerInstance._clickListener = this._onClick.bindAsEventListener(this);
    Event.observe(handlerInstance, "click", handlerInstance._clickListener); 
  },
  detachContentHandler: function ($super, handlerInstance) {
    Event.stopObserving(handlerInstance, "click", handlerInstance._clickListener);
    handlerInstance._clickListener = undefined;
    $super(handlerInstance);
  }, 
  disableEditor: function ($super, handlerInstance) {
    handlerInstance._disabled = true;
    handlerInstance.addClassName("dataGridButtonDisabled");
  },
  enableEditor: function ($super, handlerInstance) {
    handlerInstance._disabled = false;
    handlerInstance.removeClassName("dataGridButtonDisabled");
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance._disabled == true;
  },
  getDataType: function ($super) {
    return "button";  
  },
  getMode: function ($super) {
    return DataGridControllers.EDITMODE_NOT_EDITABLE;
  },
  _onClick: function (event) {
    var handlerInstance = Event.element(event);
    if (Object.isFunction(handlerInstance._columnDefinition.onclick)) {
      Event.stop(event);
      
      if (this.isDisabled(handlerInstance) != true) { 
        handlerInstance._columnDefinition.onclick.call(window, {
          dataGridComponent: handlerInstance._dataGrid,
          row: this.getEditorRow(handlerInstance),
          column: this.getEditorColumn(handlerInstance)
        });
      }
    }
  }
});

DataGridControllers.registerController(new ButtonTableEditorButtonController());NumberDataGridEditorController = Class.create(DataGridEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var editor = this._createEditorElement("input", name, "dataGridCellEditorNumber", {type: "text", name: name}, columnDefinition);

    editor.addClassName("float");
    if (columnDefinition.required)
      editor.addClassName("required");
    
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(editor, "change", this._editorValueChangeListener);
    return editor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "dataGridCellViewerNumber", {}, columnDefinition);
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable == false)
      handlerInstance.addClassName("dataGridCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
      handlerInstance.disabled = true;
    }
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable == false)
      handlerInstance.removeClassName("dataGridCellViewerDisabled");
    else {
      handlerInstance.disabled = false;
      this._removeDisabledHiddenElement(handlerInstance);
    }
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return this._getViewerValue(handlerInstance);
    else
      return handlerInstance.value;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    if (handlerInstance._editable != true) 
      this._setViewerValue(handlerInstance, value);
    else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, value);
      
      handlerInstance.value = value;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled == true;
  },
  getDataType: function () {
    return "number";  
  },
  getMode: function () { 
    return DataGridControllers.EDITMODE_EDITABLE;
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    this._fireValueChange(handlerInstance, handlerInstance.value);
  }
});

DataGridControllers.registerController(new NumberDataGridEditorController());CheckboxDataGridEditorController = Class.create(DataGridEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("input", name, "dataGridCellEditorCheckbox", {name: name, value: "1", type: "checkbox"}, columnDefinition);
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(cellEditor, "change", this._editorValueChangeListener);
    return cellEditor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "dataGridCellViewerCheckbox", {}, columnDefinition);
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.addClassName("dataGridCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
      handlerInstance.disabled = true;
    }
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.removeClassName("dataGridCellViewerDisabled");
    else {
      handlerInstance.disabled = false;
      this._removeDisabledHiddenElement(handlerInstance);
    }
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      return this._getViewerValue(handlerInstance) == 1;
    else
      return handlerInstance.checked;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    var isChecked;
    if (Object.isString(value))
      isChecked = value == '1';
    else if (Object.isNumber(value))
      isChecked = value == 1;
    else 
      isChecked = value;
    
    if (handlerInstance._editable != true)
      this._setViewerValue(handlerInstance, isChecked ? 1 : 0, '<div class="dataGridViewerCheckbox' + (isChecked ? 'Checked' : 'NotChecked') + '"/>');
    else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, isChecked ? 1 : 0);
      
      handlerInstance.checked = isChecked;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled == true;
  },
  getDataType: function ($super) {
    return "checkbox";  
  },
  getMode: function ($super) { 
    return DataGridControllers.EDITMODE_EDITABLE;
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    this._fireValueChange(handlerInstance, handlerInstance.value);
  }
});

DataGridControllers.registerController(new CheckboxDataGridEditorController());RadioButtonDataGridEditorController = Class.create(DataGridEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("input", name, "dataGridCellEditorCheckbox", {name: name, value: 'true', type: "radio", title: columnDefinition.tooltip ? columnDefinition.tooltip : ''}, columnDefinition);
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(cellEditor, "change", this._editorValueChangeListener);
    return cellEditor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "dataGridCellViewerCheckbox", {}, columnDefinition);
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.addClassName("dataGridCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
      handlerInstance.disabled = true;
    }
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.removeClassName("dataGridCellViewerDisabled");
    else {
      handlerInstance.disabled = false;
      this._removeDisabledHiddenElement(handlerInstance);
    }
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      return this._getViewerValue(handlerInstance) == 1;
    else
      return handlerInstance.checked;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    var isChecked;
    if (Object.isString(value))
      isChecked = value == '1';
    else if (Object.isNumber(value))
      isChecked = value == 1;
    else 
      isChecked = value;
    
    if (handlerInstance._editable != true)
      this._setViewerValue(handlerInstance, isChecked ? 1 : 0, '<div class="dataGridViewerCheckbox' + (isChecked ? 'Checked' : 'NotChecked') + '"/>');
    else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, isChecked ? 1 : 0);
      
      handlerInstance.checked = isChecked;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled == true;
  },
  getDataType: function ($super) {
    return "radiobutton";  
  },
  getMode: function ($super) { 
    return DataGridControllers.EDITMODE_EDITABLE;
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    var row = this.getEditorRow(handlerInstance);
    var column = this.getEditorColumn(handlerInstance);
    
    if (this.getEditorValue(handlerInstance)) {
      var dataGridComponent = handlerInstance._dataGrid;
      var rows = dataGridComponent.getRowCount();
      for (var i = 0; i < rows; i++) {
        if (i != row) {
          if ((dataGridComponent.getCellDataType(i, column) == 'radiobutton') && (dataGridComponent.isCellEditable(i, column))) {
            dataGridComponent.setCellValue(i, column, false);
          }
        }
      }
    }
    
    this._fireValueChange(handlerInstance, handlerInstance.value);
  }/**,
  _onCellValueChanged: function (event) {
    var dataGridComponent = event.dataGridComponent;
    var handlerInstance = dataGridComponent.getCellEditor(event.row, event.column);
    if (this._isChecked(event.value)) {
      for (var i = 0; i < dataGridComponent.getRowCount(); i++) {
        if (i != event.row) {
          dataGridComponent.setCellValue(i, column, false);
        }
      }
    }
  },
  _isChecked: function(value) {
    if (Object.isString(value)) {
      return value == '1';
    }
    else if (Object.isNumber(value)) {
      return value == 1;
    }
    else {
      return value == true;
    }
  }**/
});

DataGridControllers.registerController(new RadioButtonDataGridEditorController());SelectDataGridEditorController = Class.create(DataGridEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("select", name, "dataGridCellEditorSelect", {name: name}, columnDefinition);
    
    if (columnDefinition.required)
      cellEditor.addClassName("required");
    
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    
    if (columnDefinition.options) {
      this._addOptionsFromArray(cellEditor, columnDefinition.options);
    }
    
    return cellEditor;
  },
  removeAllOptions: function (handlerInstance) {
    if (handlerInstance._editable) {
      for (var i = handlerInstance.options.length - 1; i >= 0; i--) {
        $(handlerInstance.options[i]).remove();
      }  
    }
  },
  addOption: function (handlerInstance, value, text) {
    return this.addOption(handlerInstance, value, text, false);
  },
  addOption: function (handlerInstance, value, text, selected) {
    if (handlerInstance._editable) {
      var optionNode = this._createOption(value, text, selected);
      handlerInstance.appendChild(optionNode);
      return optionNode;
    } else {
      handlerInstance._options.push({
        text: text,
        value: value
      });
      
      if (this.getEditorValue(handlerInstance) == value) {
        this._setViewerValue(handlerInstance, value, text);
      }
    }
  },
  _createOptionGroup: function (text) {
    return new Element("optgroup", {label:text});
  },
  _createOption: function (value, text, selected) {
    var optionNode;
    
    if (!selected)
      optionNode = new Element("option", {value: value === undefined ? '' : value});
    else
      optionNode = new Element("option", {value: value === undefined ? '' : value, selected: "selected"});
    
    if (text)
      optionNode.update(text);
    
    return optionNode;
  },
  attachContentHandler: function ($super, dataGrid, cell, handlerInstance) {
    var result = $super(dataGrid, cell, handlerInstance);
    if (this.getEditable(handlerInstance))
      Event.observe(result, "change", this._editorValueChangeListener);
    return result;
  },
  detachContentHandler: function ($super, handlerInstance) {
    if (this.getEditable(handlerInstance))
      Event.stopObserving(handlerInstance, "change", this._editorValueChangeListener);
    $super(handlerInstance);
  },
  buildViewer: function ($super, name, columnDefinition) {
    var cellViewer = this._createViewerElement("div", name, "dataGridCellViewerSelect", {}, columnDefinition);
    if (this.isDynamicOptions(cellViewer))
      cellViewer._options = new Array();
    
    return cellViewer;
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.addClassName("dataGridCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
      handlerInstance.disabled = true;
    }
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.removeClassName("dataGridCellViewerDisabled");
    else {
      handlerInstance.disabled = false;
      this._removeDisabledHiddenElement(handlerInstance);
    }
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return this._getViewerValue(handlerInstance);
    else
      return handlerInstance.value;
  },
  getDisplayValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return $super(handlerInstance);
    else {
      var index = handlerInstance.selectedIndex;
      var option = handlerInstance.options[index];
      if (option)
        return option.innerHTML;
      else
        return $super(handlerInstance);
    }
  },
  setEditorValue: function ($super, handlerInstance, value) {
    if (handlerInstance._editable != true) {
      var displayValue = value;
      var options = this.isDynamicOptions(handlerInstance) ? handlerInstance._options : handlerInstance._columnDefinition.options;
      
      if (options) {
        for (var i = 0; i < options.length; i++) {
          if (options[i].optionGroup == true) {
            for (var j = 0; j < options[i].options.length;j++) {
              if (options[i].options[j].value == value) {
                displayValue = options[i].options[j].text;
                break;
              }
            }
          } else {
            if (options[i].value == value) {
              displayValue = options[i].text;
              break;
            }
          }
        }
      }
      this._setViewerValue(handlerInstance, value, displayValue);
    } else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, value);
      
      handlerInstance.value = value;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled == true;
  },
  isDynamicOptions: function (handlerInstance) {
    return handlerInstance._columnDefinition.dynamicOptions || false;
  },
  getDataType: function ($super) {
    return "select";  
  },
  getMode: function ($super) { 
    return DataGridControllers.EDITMODE_EDITABLE;
  },
  getOptions: function (handlerInstance) {
    if (this.getEditable(handlerInstance)) {
      return this._readOptionsToArray(handlerInstance);
    } else {
      return handlerInstance._options;
    }
  },
  setOptions: function (handlerInstance, options) {
    if (this.getEditable(handlerInstance)) {
      this.removeAllOptions(handlerInstance);
      this._addOptionsFromArray(handlerInstance, options);
    } else {
      handlerInstance._options = options;
    }
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    this._fireValueChange(handlerInstance, handlerInstance.value);
  },
  setEditable: function ($super, handlerInstance, editable) {
    if (this.getEditable(handlerInstance) == editable)
      return handlerInstance;
    
    if (!this.isDynamicOptions(handlerInstance))
      return $super(handlerInstance, editable);
    
    var value = this.getEditorValue(handlerInstance);
    var options;
    
    if (this.getEditable(handlerInstance)) {
      options = this._readOptionsToArray(handlerInstance);
    } else {
      options = handlerInstance._options;
    }

    var newInstance = $super(handlerInstance, editable);
      
    if (editable) {
      this._addOptionsFromArray(newInstance, options);
    } else {
      newInstance._options = options;
    }
    
    this.setEditorValue(newInstance, value);
    
    return newInstance;
  },
  copyCellValue: function($super, target, source) {
    if (!this.isDynamicOptions(source)) {
      $super(target, source);
    } else {
      var value = this.getEditorValue(source);
      var options;
      
      if (this.getEditable(source)) {
        options = this._readOptionsToArray(source);
      } else {
        options = source._options;
      }
  
      if (this.getEditable(target)) {
        this.removeAllOptions(target);
        this._addOptionsFromArray(target, options);
      } else {
        target._options = options;
      }
      
      this.setEditorValue(target, value);
    }
  },
  _addOptionsFromArray: function (cellEditor, options) {
    var elements = new Array();

    for (var j = 0, l = options.length; j < l; j++) {
      var option = options[j];
      if (option.optionGroup == true) {
        
        var optionGroup = this._createOptionGroup(option.text);

        var groupOptions = option.options;
        for (var groupIndex = 0; groupIndex < groupOptions.length; groupIndex++) {
          var optionElement = this._createOption(groupOptions[groupIndex].value, groupOptions[groupIndex].text, false);
          optionGroup.appendChild(optionElement);
        }
        
        elements.push(optionGroup);
      } else {
        elements.push(this._createOption(option.value, option.text, false));
      }
    }

    for (var i = 0, l = elements.length; i < l;i++) {
      cellEditor.appendChild(elements[i]);
    }
  },
  _readOptionsToArray: function (cellEditor) {
    var options = new Array();
    
    for (var i = 0, sourceChildNodeLen = cellEditor.childNodes.length; i < sourceChildNodeLen; i++) {
      var editorChildNode = cellEditor.childNodes[i];
      if (editorChildNode.tagName == 'OPTGROUP') {
        var groupOptions = new Array();
        
        for (var j = 0, groupChildNodeLen = editorChildNode.childNodes.length; j < groupChildNodeLen; j++) {
          var groupOption = editorChildNode.childNodes[k];
          groupOptions.push({
            text: groupOptionNode.text,
            value: groupOptionNode.value
          });
        }
        
        options.push({
          text: editorChildNode.text,
          optionGroup: true,
          options: groupOptions
        });
        
      } else if (editorChildNode.tagName == 'OPTION') {
        options.push({
          text: editorChildNode.text,
          value: editorChildNode.value
        });
      }
    }
    
    return options;
  } 
});

DataGridControllers.registerController(new SelectDataGridEditorController());TextDataGridEditorController = Class.create(DataGridEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var editor = this._createEditorElement("input", name, "dataGridCellEditorText", {name: name, type: "text"}, columnDefinition);

    if (columnDefinition.required)
      editor.addClassName("required");

    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(editor, "change", this._editorValueChangeListener);
    return editor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "dataGridCellViewerText", {}, columnDefinition);
  },
  attachContentHandler: function ($super, dataGrid, cell, handlerInstance) {
    var handlerInstance = $super(dataGrid, cell, handlerInstance);
    handlerInstance._clickListener = this._onClick.bindAsEventListener(this);
    Event.observe(handlerInstance, "click", handlerInstance._clickListener); 
  },
  detachContentHandler: function ($super, handlerInstance) {
    Event.stopObserving(handlerInstance, "click", handlerInstance._clickListener);
    handlerInstance._clickListener = undefined;
    $super(handlerInstance);
  }, 
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable == false)
      handlerInstance.addClassName("dataGridCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
    }
    handlerInstance.disabled = true;
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.removeClassName("dataGridCellViewerDisabled");
    else {
      this._removeDisabledHiddenElement(handlerInstance);
    }
    handlerInstance.disabled = false;
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return this._getViewerValue(handlerInstance);
    else
      return handlerInstance.value;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    value = this._unescapeHtmlEntities(value);
    if (handlerInstance._editable != true) {
      this._setViewerValue(handlerInstance, value);
    } else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, value);
      handlerInstance.value = value;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled == true;
  },
  getDataType: function ($super) {
    return "text";  
  },
  getMode: function ($super) { 
    return DataGridControllers.EDITMODE_EDITABLE;
  },
  _onClick: function (event) {
    var handlerInstance = Event.element(event);
    
    if (!handlerInstance.hasClassName("dataGridCellEditorText") && !handlerInstance.hasClassName("dataGridCellViewerText")) {
      var e = handlerInstance.up(".dataGridCellViewerText");
      if (e)
        handlerInstance = e;
      else {
        var e = handlerInstance.up(".dataGridCellEditorText");
        if (e)
          handlerInstance = e;
      }
    }
    
    if (Object.isFunction(handlerInstance._columnDefinition.onclick)) {
      if (this.isDisabled(handlerInstance) != true) { 
        handlerInstance._columnDefinition.onclick.call(window, {
          dataGridComponent: handlerInstance._dataGrid,
          row: this.getEditorRow(handlerInstance),
          column: this.getEditorColumn(handlerInstance)
        });
      }
    }
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    this._fireValueChange(handlerInstance, handlerInstance.value);
  }
});

DataGridControllers.registerController(new TextDataGridEditorController());HiddenDataGridEditorController = Class.create(DataGridEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    // Event.observe(cellEditor, "change", this._fieldValueChangeListener);
    return this._createEditorElement("input", name, undefined, {type: "hidden", name: name}, columnDefinition);
  },
  getEditorValue: function ($super, handlerInstance) {
    return handlerInstance.value;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    handlerInstance.value = value == undefined ? '' : this._unescapeHtmlEntities(value);
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  getDataType: function ($super) {
    return "hidden";  
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled == true;
  },
  getMode: function () { 
    return DataGridControllers.EDITMODE_ONLY_EDITABLE;
  }
});

DataGridControllers.registerController(new HiddenDataGridEditorController());_DataGrid_ROWSORT = Class.create({
  initialize : function(column, sortDirection) {
    this._column = column;
    this._sortDirection = sortDirection;
  },
  getColumn: function() {
    return this._column;
  },
  getSortDirection: function() {
    return this._sortDirection;
  },
  compare: function (sortEvent, rowIndex1, rowIndex2) {
    return 0;
  }
});

DataGrid_ROWSTRINGSORT = Class.create(_DataGrid_ROWSORT, {
  compare: function (sortEvent, rowIndex1, rowIndex2) {
    var dataGrid = sortEvent.dataGridComponent;
    var s1 = new String(dataGrid.getCellValue(rowIndex1, this.getColumn())).toLowerCase();
    var s2 = new String(dataGrid.getCellValue(rowIndex2, this.getColumn())).toLowerCase();

    var result = s1 == s2 ? 0 : s1 < s2 ? -1 : 1; 
    
    if (this.getSortDirection() == "desc")
      return result * -1;
    return result;
  }
});

DataGrid_ROWNUMBERSORT = Class.create(_DataGrid_ROWSORT, {
  compare: function (sortEvent, rowIndex1, rowIndex2) {
    var dataGrid = sortEvent.dataGridComponent;
    var n1 = 0 + dataGrid.getCellValue(rowIndex1, this.getColumn());
    var n2 = 0 + dataGrid.getCellValue(rowIndex2, this.getColumn());

    var result = n1 == n2 ? 0 : n1 < n2 ? -1 : 1; 
    
    if (this.getSortDirection() == "desc")
      return result * -1;
    return result;
  }
});

DataGrid_ROWSELECTSORT = Class.create(_DataGrid_ROWSORT, {
  compare: function (sortEvent, rowIndex1, rowIndex2) {
    var dataGrid = sortEvent.dataGridComponent;
    var controller = DataGridControllers.getController("select");

    var s1 = new String(controller.getDisplayValue(dataGrid.getCellEditor(rowIndex1, this.getColumn()))).toLowerCase();
    var s2 = new String(controller.getDisplayValue(dataGrid.getCellEditor(rowIndex2, this.getColumn()))).toLowerCase();

    var result = s1 == s2 ? 0 : s1 < s2 ? -1 : 1; 
    
    if (this.getSortDirection() == "desc")
      return result * -1;
    return result;
  }
});_DataGrid_FILTER = Class.create({
  initialize : function(column) {
    this._column = column; 
  },
  execute: function (event) {
  },
  getColumn: function() {
    return this._column;
  }
});

_DataGrid_DATAGRIDSTRINGFILTER = Class.create(_DataGrid_FILTER, {
  initialize : function($super, column, filterValue, rowFilterableFunc, inclusive) {
    $super(column);
    this._filterValue = filterValue;
    this._rowFilterableFunc = rowFilterableFunc;
    this._inclusive = inclusive;
  },
  execute: function ($super, event) {
    var dataGrid = event.dataGridComponent;
    var filterFunc = this._rowFilterableFunc;
    var hasFilterFunc = !(this._rowFilterableFunc == undefined);
    
    var hideArray = new Array();
    
    for (var i = dataGrid.getRowCount() - 1; i >= 0; i--) {
      var rowValue = dataGrid.getCellValue(i, this.getColumn());
      var match = this._inclusive ? rowValue != this._filterValue : rowValue == this._filterValue; 

      if (match) {
        if ((!hasFilterFunc) || (filterFunc(dataGrid, i) === true))
          hideArray.push(i);
      }
    }

    if (hideArray.size() > 0)
      dataGrid.hideRows(hideArray.toArray());
  }  
});

DataGrid_ROWSTRINGFILTER = Class.create({
  initialize : function(rowFilterableFunc, inclusive) {
    this._rowFilterableFunc = rowFilterableFunc;
    if (inclusive != undefined)
      this._inclusive = inclusive === false ? false : true;
    else
      this._inclusive = true;
  },
  execute: function (event) {
    var dataGrid = event.dataGridComponent;
    var row = event.row;
    var column = event.column;
    var filterValue = dataGrid.getCellValue(row, column);
    var filter = new _DataGrid_DATAGRIDSTRINGFILTER(column, filterValue, this._rowFilterableFunc, this._inclusive);

    dataGrid.addFilter(filter);
  }
});

_DataGrid_DATAGRIDDATEFILTER = Class.create(_DataGrid_FILTER, {
  initialize : function($super, column, filterValue, filterEarlier, rowFilterableFunc) {
    $super(column);
    this._filterValue = filterValue;
    this._filterEarlier = filterEarlier;
    this._rowFilterableFunc = rowFilterableFunc;
  },
  execute: function ($super, event) {
    var dataGrid = event.dataGridComponent;
    var filterFunc = this._rowFilterableFunc;
    var hasFilterFunc = !((filterFunc == undefined) || (filterFunc == null));
    
    var hideArray = new Array();

    if (this._filterEarlier) {
      for (var i = dataGrid.getRowCount() - 1; i >= 0; i--) {
        var rowValue = dataGrid.getCellValue(i, this.getColumn());
        if ((rowValue) && (rowValue > this._filterValue)) { 
          if ((!hasFilterFunc) || (filterFunc(dataGrid, i) === true))
            hideArray.push(i);
        }
      }
    } else {
      for (var i = dataGrid.getRowCount() - 1; i >= 0; i--) {
        var rowValue = dataGrid.getCellValue(i, this.getColumn());
        if ((rowValue) && (rowValue < this._filterValue)) { 
          if ((!hasFilterFunc) || (filterFunc(dataGrid, i) === true))
            hideArray.push(i);
        }
      }
    }
  
    if (hideArray.size() > 0)
      dataGrid.hideRows(hideArray.toArray());
  }
});

DataGrid_ROWDATEFILTER = Class.create({
  initialize : function(filterEarlier, rowFilterableFunc) {
    this._filterEarlier = filterEarlier;
    this._rowFilterableFunc = rowFilterableFunc;
  },
  execute: function (event) {
    var dataGrid = event.dataGridComponent;
    var row = event.row;
    var column = event.column;
    var filterValue = dataGrid.getCellValue(row, column);

    var filter = new _DataGrid_DATAGRIDDATEFILTER(column, filterValue, this._filterEarlier, this._rowFilterableFunc);
    dataGrid.addFilter(filter);
  }
});

DataGrid_ROWCLEARFILTER = Class.create({
  initialize : function() {
  },
  execute: function (event) {
    var dataGrid = event.dataGridComponent;
    dataGrid.clearFilters();
  }  
});DataGrid_COPYVALUESTOCOLUMNACTION = Class.create({
  initialize : function(onlyModifiable) {
    this._onlyModifiable = onlyModifiable;
  },
  execute: function (event) {
    var dataGrid = event.dataGridComponent;
    var column = event.column;
    var row = event.row;
    
    var sourceEditor = dataGrid.getCellEditor(row, column);
    var controller = DataGridControllers.getController(dataGrid.getCellEditor(row, column)._dataType);
    
    for (var i = 0, len = dataGrid.getRowCount(); i < len; i++) {
      if (i != row) {
        var cellEditor = dataGrid.getCellEditor(i, column);
        var settable = this._onlyModifiable ? controller.getEditable(cellEditor) : true; 
  
        if (settable)
          controller.copyCellValue(cellEditor, sourceEditor);
      }
    }
  }
});