function ping() {
  setTimeout(function() {
    try {
      JSONUtils.request(CONTEXTPATH + "/ping.json", {
        parameters : {
        },
        onSuccess : Prototype.emptyFunction,
        onFailure : Prototype.emptyFunction
      });
    } finally {
      ping();
    }
  }, 300000);
}

function startPing() {
  ping();
}