ProfileSettingsBasicInfoEditor = Class.create({
  initialize : function(editorContainer) {
    this._editorContainer = editorContainer;
    this._updateProfileButtonClickListener = this._onUpdateProfileButtonClick.bindAsEventListener(this);
    this._changeProfilePictureButtonClickListener = this._onUpdateProfilePictureButtonClick.bindAsEventListener(this);
    this._closeModalButtonClickListener = this._onCloseModalButtonClick.bindAsEventListener(this);
    this._imageLoadedListener = this._onImageLoaded.bindAsEventListener(this);
//    this._validEmailMask = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
    this.setup();
  },
  deinitialize: function () {
    Event.stopObserving(this._saveButton, "click", this._updateProfileButtonClickListener);
    Event.stopObserving($('_uploadFrame'), "load", this._imageLoadedListener);
  },
  setup: function () {
    this._saveButton = this._editorContainer.down('input[name="updateProfileButton"]');
    Event.observe(this._saveButton, "click", this._updateProfileButtonClickListener);
    
    this._profilePicture = $("profilePicture");
    this._changeProfilePictureButton = this._profilePicture.down(".changeProfilePictureButton");
    Event.observe(this._changeProfilePictureButton, "click", this._changeProfilePictureButtonClickListener);

    this._closeModalButton = this._profilePicture.down('.changeProfilePictureCloseModalButton');
    Event.observe(this._closeModalButton, "click", this._closeModalButtonClickListener);
    
    Event.observe($('_uploadFrame'), "load", this._imageLoadedListener);
  },
  hideEditor: function () {
    var dialogElement = this._profilePicture.down('.changeProfilePictureModalOverlay');
    var contentContainer = this._profilePicture.down('.changeProfilePictureModalContainer');

    dialogElement.hide();
    contentContainer.hide();
    
    var fileInput = this._profilePicture.down('input[name="imageData"]');
    if (fileInput)
      fileInput.value = '';
  },
  _onUpdateProfileButtonClick: function (event) {
    Event.stop(event);
    
    var parameters = {
      userId: this._editorContainer.down('input[name="userId"]').value,
      firstName: this._editorContainer.down('input[name="firstName"]').value,
      lastName: this._editorContainer.down('input[name="lastName"]').value,
      nickname: this._editorContainer.down('input[name="nickname"]').value,
      emailId: this._editorContainer.down('input[name="emailId"]').value,
      email: this._editorContainer.down('input[name="email"]').value,
      commentMail: this._editorContainer.down('input[name="commentMail"]').checked ? '1' : '0'
   };
//    var _this = this;
    
    startLoadingOperation("profile.block.savingProfile");
    JSONUtils.request(CONTEXTPATH + '/profile/saveprofile.json', {
      parameters: parameters,
      onComplete: function (transport) {
        endLoadingOperation();
      }
    });
  },
  _onUpdateProfilePictureButtonClick: function (event) {
    Event.stop(event);

    var dialogElement = this._profilePicture.down('.changeProfilePictureModalOverlay');
    var contentContainer = this._profilePicture.down('.changeProfilePictureModalContainer');
    dialogElement.show();
    contentContainer.show();
  },
  _onCloseModalButtonClick: function (event) {
    this.hideEditor();
  },
  _onImageLoaded: function (event) {
    var jsonDocument = $('_uploadFrame').contentDocument || $('_uploadFrame').contentWindow.document;
    if (jsonDocument.body && jsonDocument.body.firstChild) {
      var jsonResponse = eval('(' + jsonDocument.body.firstChild.innerHTML + ')');
      JSONUtils.showMessages(jsonResponse);
      var imageContainer = $("profilePicture");
      if (imageContainer) {
        imageUrl = CONTEXTPATH + '/user/picture.binary?userId=' + $('profileUserIdElement').value + '&time=' + new Date().getTime();
        imageContainer.setStyle({
          backgroundImage : 'url("' + imageUrl + '")'
        }); 
      }
      this.hideEditor();
    }
  }
});

ProfileSettingsPasswordEditor = Class.create({
  initialize : function(editorContainer) {
    this._editorContainer = editorContainer;
    this._updatePasswordButtonClickListener = this._onUpdatePasswordButtonClick.bindAsEventListener(this);
    this.setup();
  },
  deinitialize: function () {
    Event.stopObserving(this._saveButton, "click", this._updatePasswordButtonClickListener);
  },
  setup: function () {
    this._saveButton = this._editorContainer.down('input[name="updatePasswordButton"]');
    Event.observe(this._saveButton, "click", this._updatePasswordButtonClickListener);
  },
  _onUpdatePasswordButtonClick: function (event) {
    Event.stop(event);

    var parameters = {
      oldPassword: hex_md5(this._editorContainer.down('input[name="oldPassword"]').value),
      newPassword: hex_md5(this._editorContainer.down('input[name="newPassword2"]').value),
      userId: this._editorContainer.down('input[name="passwordUserId"]').value
    };
    var _this = this;
    startLoadingOperation("profile.block.savingPassword");
    JSONUtils.request(CONTEXTPATH + '/profile/savepassword.json', {
      parameters: parameters,
      onComplete: function (transport) {
        endLoadingOperation();
      },
      onSuccess : function(jsonResponse) {
        _this._editorContainer.down('input[name="oldPassword"]').value = '';
        _this._editorContainer.down('input[name="newPassword1"]').value = '';
        _this._editorContainer.down('input[name="newPassword2"]').value = '';
        
        if (parameters.newPassword != "") {
          $('oldPasswordContainer').show();
          $('noPasswordMessageContainer').hide();
        } else {
          $('oldPasswordContainer').hide();
          $('noPasswordMessageContainer').show();
        }
        
        JSONUtils.showMessages(jsonResponse);
      }
    });
  }
});

document.observe("dom:loaded", function(event) {
  new ProfileSettingsBasicInfoEditor($("profileSettingsForm"));
  new ProfileSettingsPasswordEditor($("profilePasswordForm"));
});
