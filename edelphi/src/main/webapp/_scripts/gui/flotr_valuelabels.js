Flotr.addType('valuelabels', {
  options: {
    show: false,
    margin: 5,
    position: 'se',   
    labelFormatter: function (obj) {
      return obj.x + ',' + obj.y;
    }
  },
  
  createValueLabel: function (series, position, x, y, content) {
    var style = 'opacity:0.7;background-color:#000;color:#fff;display:none;position:absolute;padding:2px 8px;-moz-border-radius:4px;border-radius:4px;white-space:nowrap;';
    
    var m = series.valuelabels.margin;
    var plotOffset = this.plotOffset;
    
    if (position.charAt(0) == 'n')
      style += 'bottom:' + (m - plotOffset.top - y + this.canvasHeight) + 'px;top:auto;';
    else if (position.charAt(0) == 's')
      style += 'top:' + (m + plotOffset.top + y) + 'px;bottom:auto;';

    if (position.charAt(1) == 'e')
      style += 'left:' + (m + plotOffset.left + x) + 'px;right:auto;';
    else if (position.charAt(1) == 'w')
      style += 'right:' + (m - plotOffset.left - x + this.canvasWidth) + 'px;left:auto;';
         
    var e = new Element("div", {
      className: "flotr-value-label",
      style: style
    }).update(content).show();
    
    this.el.appendChild(e);
  },
  
  draw: function (series) {
    var xa = series.xaxis;
    var ya = series.yaxis;
    var data = series.data;
    
    for (var i = data.length - 1; i > -1; --i) {
      var x = xa.d2p(data[i][0]);
      var y = ya.d2p(data[i][1]);
      var text = series.valuelabels.labelFormatter({
        x: data[i][0], 
        y: data[i][1],
        series: series
      });
      
      var position = null;
      if ((typeof series.valuelabels.position) == 'function') {
        position = series.valuelabels.position({
          x: data[i][0], 
          y: data[i][1],
          series: series
        });
      } else {
        position = series.valuelabels.position;
      }

      this.valuelabels.createValueLabel(series, position, x, y, text);
    }
    
  }
});