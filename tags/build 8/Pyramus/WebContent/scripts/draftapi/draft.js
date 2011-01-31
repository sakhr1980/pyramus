var INTERVAL = 60 * 1000;

var DRAFTAPI;
var DRAFTUI;
var __STOPDRAFTING = false;

function storeLatestDraftDataHash(draftData) {
  var hash = hex_md5(draftData);
  $(document.body).getStorage().set("latestDraftDataHash", hash);
  return hash;
}

function getLatestDraftDataHash() {
  var hash = $(document.body).getStorage().get("latestDraftDataHash");  
  if (!hash) {
    hash = storeLatestDraftDataHash(DRAFTAPI.createFormDraft());
  }
  
  return hash;
}

function isDraftEqualToLatestDraft(draftData) {
  var hash = getLatestDraftDataHash();
  return hex_md5(draftData) == hash;
}

function initDrafting(options) {
  DRAFTAPI = new IxDraftAPI();
  DRAFTUI = new IxDraftUI(options);
};

function startDraftSaving() {
  setTimeout("saveFormDraft();", INTERVAL);
}

function saveFormDraft() {
  if (__STOPDRAFTING != true) {
    var draftData = DRAFTAPI.createFormDraft();
    if (!isDraftEqualToLatestDraft(draftData)) {
      DRAFTUI.updateDraftStart();
      
      storeLatestDraftDataHash(draftData);
      
      JSONRequest.request("drafts/saveformdraft.json", {
        parameters: {
          draftData: draftData
        },
        onSuccess: function (jsonResponse) {
          try {
            if (jsonResponse.draftModified)
              DRAFTUI.updateDraftEnd(jsonResponse.draftModified.time);
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

function deleteFormDraft(onSuccess) {
  DRAFTUI.deleteDraftStart();
  JSONRequest.request("drafts/deleteformdraft.json", {
    onSuccess: function (jsonResponse) {
      DRAFTUI.deleteDraftEnd();
      if (onSuccess) {
        onSuccess();
      }
    }
  });
}