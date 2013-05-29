CKEDITOR.plugins.add('fnidynlist', {
  onLoad : function() { 
    var dynListElement = function(dialog, elementDefinition, htmlList) {
      var columns = elementDefinition.columns;
      var rows = elementDefinition.rows;
      
      if (elementDefinition.onRowClick)
        this._onRowClickFN = CKEDITOR.tools.addFunction(elementDefinition.onRowClick, dialog);
      
      var myHTML = new Array();
      var domId = elementDefinition.id || CKEDITOR.tools.getNextNumber() + '_uiElement';
      this.rowCount = 0;
      
      myHTML.push('<div class="fniDynListHeaderRow">');
      
      for (var i = 0; i < columns.length; i++) {
        var column = columns[i];
        var title = column.title;
        
        if (title) {
          myHTML.push('<div');
          var headerStyle = new Array();
          
          if (column.width) { 
            var width = column.width;
            
            if (column.paddings) {
              if (column.paddings.left) {
                width += column.paddings.left;                
              }
              if (column.paddings.right)
                width +=column.paddings.right;
            }
            
            headerStyle.push('width:' + width + 'px');
          }
          
          if ((i < (columns.length - 1)) && (columns[i + 1].paddings) && (columns[i + 1].paddings.left))
            headerStyle.push('padding-right:' + columns[i + 1].paddings.left + 'px');

          if (headerStyle.length > 0)
            myHTML.push('style="' + headerStyle.join(';') + '"');
          
          myHTML.push('class="fniDynListHeader">' + title + '</div>');
        }
      }
      
      myHTML.push('</div>');
            
      
      myHTML.push('<div id="' + domId + '_content" class="fniDynListContent"');
      var contentStyle = new Array();
      if (elementDefinition.contentHeight)
        contentStyle.push("height: " + elementDefinition.contentHeight + 'px');
      if (elementDefinition.contentWidth)
        contentStyle.push("width: " + elementDefinition.contentWidth + 'px');
      if (contentStyle.length > 0)
        myHTML.push('style="' + contentStyle.join(';') + '"');
      myHTML.push('>');
      
      myHTML.push('</div>');
      
      var className = "fniDynList";
      if (elementDefinition.useHoverEffect == true)
        className += ' fniDynListHover';
      
      CKEDITOR.ui.dialog.uiElement.call(this, dialog, elementDefinition, htmlList, 'div', {
        
      }, {
        "class": className,
        "id": domId
      }, myHTML.join(' '));
      
      if (this.rows) {
        var _this = this;
        dialog.on( 'load', function() {
          for (var j = 0; j < _this.rows.length; j++)
            _this.addRow(_this.rows[j]);  
        });      
      }
    };
    
    dynListElement.prototype = new CKEDITOR.ui.dialog.uiElement;
    
    CKEDITOR.tools.extend(dynListElement.prototype, {
      addRow: function (data) {
        return this.addRows([data]);
      },
      addRows: function (rows) {
        var myHTML = new Array();
        
        for (var x = 0; x < rows.length; x++) {
          var data = rows[x];
          
          var rowId = this.rowCount;  
          
          myHTML.push('<div class="fniDynListRow"');
          
          if (this.onRowClick) {
            myHTML.push('onclick="CKEDITOR.tools.callFunction(' + this._onRowClickFN + ', {rowId: ' + rowId + '})"');
          }
          
          if (this.rowStyle) 
            myHTML.push('style="' + this.rowStyle + '"');
          
          myHTML.push('id="' + this.domId + '.row.' + rowId + '">');
          
          for (var i = 0; i < this.columns.length; i++) {
            var column = this.columns[i];
            var style = new Array();
            
            if (column.width)
              style.push('width: ' + column.width + 'px');
            
            if (column.type == 'hidden')
              style.push('display: none');
            
            if (column.paddings) {
              if (column.paddings.left)
                style.push('padding-left: ' + column.paddings.left + 'px');
              if (column.paddings.right)
                style.push('padding-right: ' + column.paddings.right + 'px');
              if (column.paddings.top)
                style.push('padding-top: ' + column.paddings.top + 'px');
              if (column.paddings.bottom)
                style.push('padding-bottom: ' + column.paddings.bottom + 'px');
            }
            
            if (column.margins) {
              if (column.margins.left)
                style.push('margin-left: ' + column.margins.left + 'px');
              if (column.margins.right)
                style.push('margin-right: ' + column.margins.right + 'px');
              if (column.margins.top)
                style.push('margin-top: ' + column.margins.top + 'px');
              if (column.margins.bottom)
                style.push('margin-bottom: ' + column.margins.bottom + 'px');
            }
            
            if (column.style) {
              style.push(column.style);
            }
              
            
            myHTML.push('<div');
            
            if (style.length > 0)
              myHTML.push('style="' + style.join(';') + '"');
            
            var className = "fniDynListCell";
            if (column.className)
              className += ' ' + column.className;
            
            myHTML.push('class="' + className + '">');
            
            var cellId = this.domId + '.cell.' + i + '.' + rowId;
            
            switch (column.type) {
              case 'checkbox':
                myHTML.push('<input id="' + cellId + '" class="fniDynListCheckbox" type="checkbox"' + (data[i] == true ? 'checked="checked"' : '') + '/>');
              break;
              case 'hidden':
                myHTML.push('<input id="' + cellId + '" type="hidden" value="' + data[i] + '"/>');
              break;
              case 'icon':
                myHTML.push('<img id="' + cellId + '" type="hidden" src="' + data[i] + '"/>');
              break;
              case 'link':
                myHTML.push('<a href="javascript:void(null)"');
                
                if (column.onClick)
                  myHTML.push('onclick="CKEDITOR.tools.callFunction(' + CKEDITOR.tools.addFunction(column.onClick, this._.dialog) + ', {row: ' + rowId + '})"');
                
                myHTML.push('>' + data[i] + '</a>');
              break;
              default:
                myHTML.push('<span id="' + cellId + '">' + data[i] + '</span>');
              break;
            }
            
            myHTML.push('</div>');
          }
          
          if (this.buttons) {
            for (var b = 0; b < this.buttons.length; b++) {
              var button = this.buttons[b];
              myHTML.push('<div class="fniDynListCell fniDynListButtonCell"><div');
              var className = "fniDynListButton";
              
              myHTML.push('id="' + this.domId + '.' + button.id + '.' + rowId + '"');
              
              if (button.className)
                className += ' ' + button.className;
              
              myHTML.push('class="' + className + '"');
              
              if (button.style)
                myHTML.push('style="' + button.style + '"');
              
              if (button.onClick) {
                myHTML.push('onclick="CKEDITOR.tools.callFunction(' + CKEDITOR.tools.addFunction(button.onClick, this._.dialog) + ', {row: ' + rowId + '})"');
              }
              
              myHTML.push('></div></div>');
            }
          }
          
          myHTML.push('</div>');
          this.rowCount++;
        }
        
        this.getContentElement().innerHTML += myHTML.join(' ');
        return this.rowCount - 1;
      },
      hideButton: function (row, id) {
        this.getButtonElement(row, id).setStyle({
          display: 'none'
        });
      },
      showButton: function (row, id) {
        this.getButtonElement(row, id).setStyle({
          display: ''
        });
      },
      getButtonElement: function (row, id) {
        return $(this.domId + '.' + id + '.' + row);
      },
      getRowElement: function (row) {
        return $(this.domId + '.row.' + row);
      },
      getContentElement: function () {
        return $(this.domId + '_content');
      },
      getDOMElement: function () {
        return $(this.domId);
      },
      getRowCount: function () {
        return this.rowCount;
      },
      getCellValue: function (row, column) {
        switch (this.columns[column].type) {
          case 'checkbox':
            return $(this.domId + '.cell.' + column + '.' + row).checked;
          break;
          case 'hidden':
            return $(this.domId + '.cell.' + column + '.' + row).value;            
          break;
          case 'icon':
            return $(this.domId + '.cell.' + column + '.' + row).src;            
          break;
          default:
            throw new Error("Cannot get value from text cell");
          break;
        }
      },
      setCellValue: function (row, column, value) {
        switch (this.columns[column].type) {
          case 'checkbox':
            $(this.domId + '.cell.' + column + '.' + row).checked = value;
          break;
          case 'hidden':
            $(this.domId + '.cell.' + column + '.' + row).value = value;            
          break;
          case 'icon':
            $(this.domId + '.cell.' + column + '.' + row).src = value;
          break;
          default:
            $(this.domId + '.cell.' + column + '.' + row).innerHTML = value;
          break;
        }
      },
      removeRow: function (row) {
        this.fire("beforeRemoveRow", {
          row: row
        });
        
        this.getRowElement(row).remove();
        
        for (var i = row + 1; i < this.rowCount; i++)
          this._changeRowId(i, i - 1);
        
        this.rowCount--;
        
        this.fire("rowRemoved", {
          row: row
        });
      },
      removeRows: function () {
        while (this.rowCount > 0)
          this.removeRow(this.rowCount - 1);
      },
      getIndexById: function (id) {
        for (var i = 0; i < this.columns.length; i++) {
          if (this.columns[i].id == id)
            return i;
        }
        
        return null;
      },
      startLoading: function () {
        this.getDOMElement().addClassName("fniDynListLoading");
        if (this._emptyNode) {
          this._emptyNode.remove();
          this._emptyNode = undefined;
        }
      },
      stopLoading: function () {
        this.getDOMElement().removeClassName("fniDynListLoading");
        if ((this.getRowCount() == 0) && (this.onEmptyText)) {
          this._emptyNode = document.createElement('div');
          this._emptyNode.setAttribute("class", "fniDynListEmpty");
          this._emptyNode.innerHTML = this.onEmptyText;
          this.getContentElement().appendChild(this._emptyNode);
        }  
      },
      setEmptyText: function (text) {
        this.onEmptyText = text;
      },
      _changeRowId: function (oldId, newId) {
        this.getRowElement(oldId).id = this.domId + '.row.' + newId;
        for (var i = 0; i < this.columns.length; i++) {
          var valueElement = $(this.domId + '.cell.' + i + '.' + oldId);
          if (valueElement)
            valueElement.id = this.domId + '.cell.' + i + '.' + newId;
        }
        
        if (this.buttons) {
          for (var b = 0; b < this.buttons.length; b++) {
            var button = this.buttons[b];
            var buttonElement = $(this.domId + '.' + button.id + '.' + oldId);
            buttonElement.id = this.domId + '.' + button.id + '.' + newId;
            
            if (button.onClick)
              buttonElement.setAttribute("onclick", 'CKEDITOR.tools.callFunction(' + CKEDITOR.tools.addFunction(button.onClick, this._.dialog) + ', {row: ' + newId + '})');            
          }
        }
      }
    });

    CKEDITOR.dialog.addUIElement('dynList', { 
      build : function( dialog, elementDefinition, output ) {
        return new dynListElement( dialog, elementDefinition, output);
      }
    });
  },
  requires: ['dialog']
});