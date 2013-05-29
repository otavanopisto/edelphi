BulletinListBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._deleteClickListener = this._onDeleteClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelBulletinsBlockContent'));
    
    var _this = this;
    this.getBlockElement().select('.blockContextualLink.delete').each(function (linkElement) {
      Event.observe(linkElement, "click", _this._deleteClickListener);
    });
  },
  deinitialize: function ($super) {
    $super();
    this.getBlockElement().select('.blockContextualLink.delete').invoke("purge");
  },
  _onDeleteClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var bulletinId = hashParams.get("bulletinId");
    
    var titleContainer = new Element("div", { className: "modalPopupTitleContent" });
    titleContainer.update(getLocale().getText('panelAdmin.block.bulletins.list.deleteBulletinDialogText'));

    var popup = new ModalPopup({
      content: titleContainer,
      buttons: [
        {
          text: getLocale().getText('panelAdmin.block.bulletins.list.deleteBulletinDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panelAdmin.block.bulletins.list.deleteBulletinDialogDeleteButton'),
          classNames: "modalPopupButtonRed",
          action: function(instance) {
            instance.close(true);
            
            JSONUtils.request(CONTEXTPATH + '/panel/admin/archivebulletin.json', {
              parameters: {
                bulletinId: bulletinId
              },
              onSuccess : function(jsonRequest) {
                var row = linkElement.up(".panelAdminBulletinRow");
                if (row != null)
                  row.remove();
              }
            });
          }
        }
      ]
    });

    popup.open(linkElement);
  }
});

addBlockController(new BulletinListBlockController());