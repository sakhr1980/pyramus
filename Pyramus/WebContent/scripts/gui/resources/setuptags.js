function setupTags() {
  JSONRequest.request("tags/getalltags.json", {
    onSuccess : function(jsonResponse) {
      new Autocompleter.Local("tags", "tags_choices", jsonResponse.tags, {
        tokens : [ ',', '\n', ' ' ]
      });
    }
  });
}

function onLoad(event) {
//  var tabControl = new IxProtoTabs($('tabs'));

  setupTags();
};