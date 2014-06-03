$(document).ready(function() {
  
  $("div[class*='-icon']").click(function() {

    var clickedIcon = $(this);
    var actionWrapper = clickedIcon.parent();
    var actionContainer = $(actionWrapper).children("[class*='-container']");
    
    // Remove icon-selected class from every other icon first which are specific to report in hand
    clickedIcon.parentsUntil("div[class*='GUI_selectedQueryReportWrapper']").find("div[class*='-icon']").removeClass("icon-selected");
    
    // Add icon-selected class to the clicked icon
    clickedIcon.addClass("icon-selected");
    
    // Hide other containers first which are specific to report  in hand
    clickedIcon.parentsUntil("div[class*='GUI_selectedQueryReportWrapper']").find("div[class*='-container']").animate({
      height : 0,
      opacity : 0
      }, 300, 
      function() {
        $(this).hide().removeClass("expanded");
    });
    
    // Animate the container specific to the clicked icon
    if (actionContainer.hasClass("expanded")) {
      clickedIcon.removeClass("icon-selected");
    } else {
      actionContainer.show().stop()
      .animate({ 
        height : 300, 
        opacity:1 
        }, 400, 
        function() {
          actionContainer.addClass("expanded");
          clickedIcon.addClass("icon-selected");
      });
    }

  });

});