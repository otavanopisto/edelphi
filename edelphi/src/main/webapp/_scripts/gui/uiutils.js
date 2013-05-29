function startBlockingOperation() {
  window._blockingOverlay = new S2.UI.Overlay();
  window._blockingOverlay.toElement().addClassName("globalBlockingOverlay");
  $(document.body).insert(window._blockingOverlay);    
}

function endBlockingOperation() {
  if (window._blockingOverlay) {
    window._blockingOverlay.destroy();
  }
}

function startLoadingOperation(messageKey) {
  startBlockingOperation();
  
  window._loadingQueueItem = getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText(messageKey), {
    className: "eventQueueLoadingItem"
  }));
}

function endLoadingOperation() {
  if (window._loadingQueueItem) {
    getGlobalEventQueue().removeItem(window._loadingQueueItem);
    window._loadingQueueItem = undefined;
  }
  
  endBlockingOperation();
}