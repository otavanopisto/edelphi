function touchLock(resourceId) {
  setTimeout(function() {
    try {
      JSONUtils.request(CONTEXTPATH + "/resources/touchresourcelock.json", {
        parameters : {
          resourceId : resourceId
        },
        onSuccess : Prototype.emptyFunction,
        onFailure : Prototype.emptyFunction
      });
    } finally {
      touchLock(resourceId);
    }
  }, 1000 * 15);
}

function startLockToucher(resourceId) {
  touchLock(resourceId);
}