var INTERVAL = 60 * 1000;

var DRAFTAPI;
var __STOPDRAFTING = false;

function storeLatestDraftDataHash(draftData) {
  var hash = checksum(draftData);
  $(document.body).getStorage().set("latestDraftDataHash", hash);
  return hash;
}

function getLatestDraftDataHash() {
  var hash = $(document.body).getStorage().get("latestDraftDataHash");  
  if (!hash)
    hash = storeLatestDraftDataHash(DRAFTAPI.createFormDraft());
  return hash;
}

function isDraftEqualToLatestDraft(draftData) {
  var hash = getLatestDraftDataHash();
  return checksum(draftData) == hash;
}

function initDrafting(rootElement) {
  DRAFTAPI = new fi.internetix.draft.DraftAPI(rootElement);
  
  JSONUtils.request(CONTEXTPATH + "/drafts/retrieveformdraft.json", {
    onSuccess: function (jsonResponse) {
      if (!jsonResponse.draftDeleted) {
        var draftModified = jsonResponse.draftModified;
        
        var dialog = new S2.UI.Dialog({
          title: getLocale().getText('generic.draft.confirmDraftRestoreDialogTitle'),
          zIndex: 2000,
          content: getLocale().getText('generic.draft.confirmDraftRestoreDialogContent', [ getLocale().getTime(draftModified.time) ]),
          buttons: [
            {
              label: getLocale().getText('generic.draft.confirmDraftRestoreDialogDiscardButton'),
              action: function(instance) {
                instance.close(false);
                deleteFormDraft(true);
              }
            },
            {
              label: getLocale().getText('generic.draft.confirmDraftRestoreDialogRestoreButton'),
              primary: true,
              action: function(instance) {
                instance.close(true);

                var draftData = jsonResponse.draftData;
                
                
                DRAFTAPI.restoreFormDraft(draftData);
                
                $H(CKEDITOR.instances).each(function (i) {
                  i.value.setData(i.value.element.value);
                });
                
                getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText("generic.draft.draftRestored"), {
                  className: "eventQueueSuccessItem",
                  timeout: 1000 * 3
                }));
                
                storeLatestDraftDataHash(DRAFTAPI.createFormDraft());
                startDraftSaving();
              }
            }
          ]
        });

        dialog.open();
      } else {
        startDraftSaving();
      }
    } 
  });
};

function startDraftSaving() {
  setTimeout("saveFormDraft();", INTERVAL);
}

function saveFormDraft() {
  if (__STOPDRAFTING != true) {
    if (CKEDITOR) {
      $H(CKEDITOR.instances).each(function (i) {
        i.value.updateElement();
      });   
    }
    
    document.fire("draft:beforeCheck"); 
    
    var draftData = DRAFTAPI.createFormDraft();
    if (!isDraftEqualToLatestDraft(draftData)) {
      document.fire("draft:beforeSave"); 
      var savingDraftQueueItem = getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText("generic.draft.savingDraft"), {
        className: "eventQueueLoadingItem"
      }));;
      
      storeLatestDraftDataHash(draftData);
      
      JSONUtils.request(CONTEXTPATH + "/drafts/saveformdraft.json", {
        parameters: {
          draftData: draftData
        },
        onSuccess: function (jsonResponse) {
          document.fire("draft:afterSave"); 
          try {
            if (jsonResponse.draftModified) {
              getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText("generic.draft.draftSaved", [ getLocale().getTime(jsonResponse.draftModified.time) ]), {
                className: "eventQueueSuccessItem",
                timeout: 1000 * 3
              }));
              
              getGlobalEventQueue().removeItem(savingDraftQueueItem);
            }
          } finally {
            setTimeout("saveFormDraft();", INTERVAL);
          }
        },
        onFailure: function () {
          setTimeout("saveFormDraft();", INTERVAL);
        }
      });
    } else {
      setTimeout("saveFormDraft();", INTERVAL);
    }
  }
};

function deleteFormDraft(showMessage, onSuccess) {
  this.deleteFormDraftByStrategy(showMessage, "URL_AND_USER", onSuccess);
}

function deleteFormDraftByStrategy(showMessage, strategy, onSuccess) {
  var deletingDraftQueueItem = null;

  if (showMessage !== false) {
    deletingDraftQueueItem = getGlobalEventQueue().addItem(
      new EventQueueItem(getLocale().getText('generic.draft.deletingDraft'), {
        className : "eventQueueLoadingItem"
      }));
  }

  JSONUtils.request(CONTEXTPATH + "/drafts/deleteformdraft.json", {
    parameters : {
      strategy : strategy
    },
    onSuccess : function(jsonResponse) {
      if (showMessage !== false) {
        getGlobalEventQueue().addItem(
          new EventQueueItem(getLocale().getText("generic.draft.draftDeleted"),
            {
              className : "eventQueueSuccessItem",
              timeout : 1000 * 3
            }));

        getGlobalEventQueue().removeItem(deletingDraftQueueItem);
      }

      if (onSuccess) {
        onSuccess();
      }
    }
  });
}

function checksum(s) {
  var i;
  var chk = s.length;
  for (i = 0, l = s.length; i < l; i++)
    chk += (s.charCodeAt(i) * i);
  return chk;
}
