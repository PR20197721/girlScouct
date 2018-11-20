girlscouts.components.VTKResourcesIconPickerer= CQ.Ext.extend(CQ.form.CompositeField, {

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":2
        };
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.VTKResourcesIconPickerer.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        girlscouts.components.VTKResourcesIconPickerer.superclass.initComponent.call(this);

        this.comboField = new CQ.Ext.form.ComboBox({
            mode: 'local',
            anyMatch: true,
            hiddenName: this.name,
            store: new CQ.Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'myId',  // numeric value is the key
                    'displayText'
                ],
                data: [['icon-empty-mug', 'empty-mug'], ['icon-cookie', 'cookie'], ['icon-girlscouts', 'girl-scouts'], ['icon-phone', 'phone'], ['icon-mobile', 'mobile'], ['icon-tablet', 'tablet'], ['icon-screen', 'screen'], ['icon-tv', 'tv'], ['icon-mouse', 'mouse'], ['icon-keyboard', 'keyboard'], ['icon-printer', 'printer'], ['icon-compass', 'compass'], ['icon-crosshair', 'crosshair'], ['icon-direction', 'direction'], ['icon-location', 'location'], ['icon-map-location', 'map-location'], ['icon-map-route', 'map-route'], ['icon-address-directions', 'address-directions'], ['icon-pin', 'pin'], ['icon-flashlight', 'flashlight'], ['icon-nautical-star', 'nautical-star'], ['icon-security-shield', 'security-shield'], ['icon-infinity', 'infinity'], ['icon-mail', 'mail'], ['icon-paperplane', 'paperplane'], ['icon-at-symbol', 'at-symbol'], ['icon-outbox', 'outbox'], ['icon-inbox', 'inbox'], ['icon-box', 'box'], ['icon-outgoing', 'outgoing'], ['icon-incoming', 'incoming'], ['icon-paperbox', 'paperbox'], ['icon-pencil', 'pencil'], ['icon-pen', 'pen'], ['icon-feather', 'feather'], ['icon-create-new', 'create-new'], ['icon-paperclip', 'paperclip'], ['icon-export', 'export'], ['icon-reply', 'reply'], ['icon-reply-to-all', 'reply-to-all'], ['icon-forward', 'forward'], ['icon-upload', 'upload'], ['icon-download', 'download'], ['icon-brush', 'brush'], ['icon-eyedropper', 'eyedropper'], ['icon-magic-wand', 'magic-wand'], ['icon-vector-curves', 'vector-curves'], ['icon-painting-palette', 'painting-palette'], ['icon-scissors', 'scissors'], ['icon-ruler', 'ruler'], ['icon-bucket', 'bucket'], ['icon-settings', 'settings'], ['icon-tools', 'tools'], ['icon-screwdriver', 'screwdriver'], ['icon-wrench', 'wrench'], ['icon-gears', 'gears'], ['icon-gear', 'gear'], ['icon-vertical-sliders', 'vertical-sliders'], ['icon-horizontal-sliders', 'horizontal-sliders'], ['icon-thermometer', 'thermometer'], ['icon-temperature', 'temperature'], ['icon-drops', 'drops'], ['icon-single-drop', 'single-drop'], ['icon-fire', 'fire'], ['icon-bell', 'bell'], ['icon-link', 'link'], ['icon-love', 'love'], ['icon-flag', 'flag'], ['icon-network', 'network'], ['icon-share', 'share'], ['icon-filled-heart', 'filled-heart'], ['icon-empty-heart', 'empty-heart'], ['icon-empty-star', 'empty-star'], ['icon-filled-star', 'filled-star'], ['icon-like-thumbs-up', 'like-thumbs-up'], ['icon-unlike', 'unlike'], ['icon-awards-cup', 'awards-cup'], ['icon-award-star', 'award-star'], ['icon-console', 'console'], ['icon-opening-tag', 'opening-tag'], ['icon-closing-tag', 'closing-tag'], ['icon-bug-debugging', 'bug-debugging'], ['icon-leaf', 'leaf'], ['icon-home', 'home'], ['icon-globe', 'globe'], ['icon-quotes', 'quotes'], ['icon-magnet', 'magnet'], ['icon-lifebuoy', 'lifebuoy'], ['icon-eye', 'eye'], ['icon-traffic-cone', 'traffic-cone'], ['icon-save-floppy', 'save-floppy'], ['icon-key', 'key'], ['icon-lock', 'lock'], ['icon-unlocked', 'unlocked'], ['icon-horn-promote', 'horn-promote'], ['icon-factory', 'factory'], ['icon-anchor', 'anchor'], ['icon-lightbulb', 'lightbulb'], ['icon-bomb', 'bomb'], ['icon-flash', 'flash'], ['icon-lightning', 'lightning'], ['icon-socket', 'socket'], ['icon-funnel-filter', 'funnel-filter'], ['icon-label-tag', 'label-tag'], ['icon-question-mark', 'question-mark'], ['icon-questions-answers', 'questions-answers'], ['icon-notice-info-announcement', 'notice-info-announcement'], ['icon-urgent-extra-notice-info-announcement', 'urgent-extra-notice-info-announcement'], ['icon-information-info-i', 'information-info-i'], ['icon-help-info', 'help-info'], ['icon-trash-delete-remove', 'trash-delete-remove'], ['icon-search-magnifying-glass', 'search-magnifying-glass'], ['icon-zoom-in', 'zoom-in'], ['icon-zoom-out', 'zoom-out'], ['icon-gauge-speed', 'gauge-speed'], ['icon-gauge', 'gauge'], ['icon-time-processing-hourglass', 'time-processing-hourglass'], ['icon-time-clock', 'time-clock'], ['icon-time-alarm', 'time-alarm'], ['icon-night-crescent-moon', 'night-crescent-moon'], ['icon-daytime-sun-light-up', 'daytime-sun-light-up'], ['icon-light-down', 'light-down'], ['icon-light-adjust', 'light-adjust'], ['icon-battery-full-charge', 'battery-full-charge'], ['icon-battery-half-charge', 'battery-half-charge'], ['icon-low-battery', 'low-battery'], ['icon-empty-battery', 'empty-battery'], ['icon-sound-volume-full', 'sound-volume-full'], ['icon-sound-volume-high', 'sound-volume-high'], ['icon-sound-volume-lower', 'sound-volume-lower'], ['icon-sound-volume-lowest', 'sound-volume-lowest'], ['icon-sound-volume-mute', 'sound-volume-mute'], ['icon-sound-record-microphone', 'sound-record-microphone'], ['icon-music-note', 'music-note'], ['icon-music-notes', 'music-notes'], ['icon-voicemail', 'voicemail'], ['icon-headphones-music-listen', 'headphones-music-listen'], ['icon-game-console', 'game-console'], ['icon-video-movie', 'video-movie'], ['icon-film-movie', 'film-movie'], ['icon-video-camera', 'video-camera'], ['icon-web-camera-webcam', 'web-camera-webcam'], ['icon-photo-camera', 'photo-camera'], ['icon-image', 'image'], ['icon-picture', 'picture'], ['icon-podcast', 'podcast'], ['icon-signal', 'signal'], ['icon-router', 'router'], ['icon-rss', 'rss'], ['icon-wifi', 'wifi'], ['icon-cloud', 'cloud'], ['icon-cloud-up', 'cloud-up'], ['icon-cloud-energy', 'cloud-energy'], ['icon-cloud-disconnected', 'cloud-disconnected'], ['icon-cloud-sync', 'cloud-sync'], ['icon-database-stack', 'database-stack'], ['icon-hard-drive-storage-box', 'hard-drive-storage-box'], ['icon-arrow-drive', 'arrow-drive'], ['icon-cup', 'cup'], ['icon-browser', 'browser'], ['icon-arrow-browser', 'arrow-browser'], ['icon-line-chart', 'line-chart'], ['icon-growth-chart', 'growth-chart'], ['icon-chart-growth', 'chart-growth'], ['icon-pie-chart', 'pie-chart'], ['icon-bar-chart', 'bar-chart'], ['icon-bar-chart-growth', 'bar-chart-growth'], ['icon-bar-chart-decrease', 'bar-chart-decrease'], ['icon-chart-time', 'chart-time'], ['icon-pulse', 'pulse'], ['icon-strategy', 'strategy'], ['icon-layout-list', 'layout-list'], ['icon-layout-grid', 'layout-grid'], ['icon-layout-images', 'layout-images'], ['icon-layout-blocks', 'layout-blocks'], ['icon-layout-text', 'layout-text'], ['icon-layout-add', 'layout-add'], ['icon-layout-document', 'layout-document'], ['icon-layout-new-blank', 'layout-new-blank'], ['icon-layout-template', 'layout-template'], ['icon-layout-popup', 'layout-popup'], ['icon-resize-minify', 'resize-minify'], ['icon-resize-maximize', 'resize-maximize'], ['icon-resize-min', 'resize-min'], ['icon-resize-max', 'resize-max'], ['icon-logout-signout', 'logout-signout'], ['icon-login-signin', 'login-signin'], ['icon-shuffle', 'shuffle'], ['icon-back-previous', 'back-previous'], ['icon-level-down', 'level-down'], ['icon-retweet-repost', 'retweet-repost'], ['icon-loop', 'loop'], ['icon-level-up', 'level-up'], ['icon-switch', 'switch'], ['icon-sync-cycle-repeat', 'sync-cycle-repeat'], ['icon-cw-clockwise', 'cw-clockwise'], ['icon-ccw-counterclockwise', 'ccw-counterclockwise'], ['icon-back-in-time', 'back-in-time'], ['icon-exchange', 'exchange'], ['icon-arrow-left', 'arrow-left'], ['icon-arrow-down', 'arrow-down'], ['icon-arrow-right', 'arrow-right'], ['icon-arrow-up', 'arrow-up'], ['icon-button-arrow-left', 'button-arrow-left'], ['icon-button-arrow-right', 'button-arrow-right'], ['icon-button-arrow-down', 'button-arrow-down'], ['icon-button-arrow-up', 'button-arrow-up'], ['icon-eject', 'eject'], ['icon-play', 'play'], ['icon-pause', 'pause'], ['icon-record', 'record'], ['icon-stop', 'stop'], ['icon-ff-fast-forward', 'ff-fast-forward'], ['icon-fb-fast-back', 'fb-fast-back'], ['icon-to-start', 'to-start'], ['icon-to-end', 'to-end'], ['icon-volume', 'volume'], ['icon-power', 'power'], ['icon-check', 'check'], ['icon-cross', 'cross'], ['icon-square-minus-min', 'square-minus-min'], ['icon-square-plus-max-medical', 'square-plus-max-medical'], ['icon-square-cross', 'square-cross'], ['icon-button-circle-minus', 'button-circle-minus'], ['icon-button-circle-plus', 'button-circle-plus'], ['icon-button-circle-cross', 'button-circle-cross'], ['icon-minus', 'minus'], ['icon-plus', 'plus'], ['icon-label-close-erase', 'label-close-erase'], ['icon-block', 'block'], ['icon-flow-cascade', 'flow-cascade'], ['icon-flow-branch', 'flow-branch'], ['icon-flow-tree', 'flow-tree'], ['icon-flow-line', 'flow-line'], ['icon-flow-parallel', 'flow-parallel'], ['icon-arrow-left2', 'arrow-left2'], ['icon-arrow-right2', 'arrow-right2'], ['icon-arrow-down2', 'arrow-down2'], ['icon-arrow-up2', 'arrow-up2'], ['icon-arrow-left3', 'arrow-left3'], ['icon-arrow-right3', 'arrow-right3'], ['icon-arrow-down3', 'arrow-down3'], ['icon-arrow-up3', 'arrow-up3'], ['icon-arrow-up-down-combo', 'arrow-up-down-combo'], ['icon-speech-bubbles', 'speech-bubbles'], ['icon-speech-bubble', 'speech-bubble'], ['icon-smiley-sad', 'smiley-sad'], ['icon-smiley-happy', 'smiley-happy'], ['icon-profile-vcard', 'profile-vcard'], ['icon-user-userpic-avatar-portrait', 'user-userpic-avatar-portrait'], ['icon-user-avatar', 'user-avatar'], ['icon-user-profile', 'user-profile'], ['icon-users', 'users'], ['icon-add-user', 'add-user'], ['icon-remove-user', 'remove-user'], ['icon-star-user', 'star-user'], ['icon-arrow-user', 'arrow-user'], ['icon-user-info-profile-details', 'user-info-profile-details'], ['icon-male-mars-arrow', 'male-mars-arrow'], ['icon-female-venus-symbol', 'female-venus-symbol'], ['icon-head-question-faq', 'head-question-faq'], ['icon-head-idea-lightbulb', 'head-idea-lightbulb'], ['icon-head-exclamation-mark', 'head-exclamation-mark'], ['icon-address-contact-book', 'address-contact-book'], ['icon-address-book-contacts', 'address-book-contacts'], ['icon-address-book', 'address-book'], ['icon-briefcase', 'briefcase'], ['icon-checklist', 'checklist'], ['icon-checklist-todo', 'checklist-todo'], ['icon-calendar', 'calendar'], ['icon-archive-storage', 'archive-storage'], ['icon-folder', 'folder'], ['icon-add-folder', 'add-folder'], ['icon-remove-folder', 'remove-folder'], ['icon-erase-folder', 'erase-folder'], ['icon-private-folder', 'private-folder'], ['icon-open-book', 'open-book'], ['icon-bookmark', 'bookmark'], ['icon-bookmarks', 'bookmarks'], ['icon-bookmark-label-tag', 'bookmark-label-tag'], ['icon-fonts-aa-capitalized-case', 'fonts-aa-capitalized-case'], ['icon-pilcrow', 'pilcrow'], ['icon-file', 'file'], ['icon-list-file', 'list-file'], ['icon-text-file', 'text-file'], ['icon-new-file', 'new-file'], ['icon-add-file', 'add-file'], ['icon-remove-file', 'remove-file'], ['icon-move-file', 'move-file'], ['icon-upload-file', 'upload-file'], ['icon-download-file', 'download-file'], ['icon-files', 'files'], ['icon-doc-file-extension', 'doc-file-extension'], ['icon-xls-excel-file-extension', 'xls-excel-file-extension'], ['icon-zip-archive-file-extension', 'zip-archive-file-extension'], ['icon-pdf-file-extension', 'pdf-file-extension'], ['icon-psd-file-extension', 'psd-file-extension'], ['icon-ai-illustrator-file-extension', 'ai-illustrator-file-extension'], ['icon-eps-illustrator-file-extension', 'eps-illustrator-file-extension'], ['icon-html-document-file-extension', 'html-document-file-extension'], ['icon-css-styles-file-extension', 'css-styles-file-extension'], ['icon-js-javascript-file-extension', 'js-javascript-file-extension'], ['icon-php-file-extension', 'php-file-extension'], ['icon-jpg-file-extension', 'jpg-file-extension'], ['icon-png-file-extension', 'png-file-extension'], ['icon-gif-file-extension', 'gif-file-extension'], ['icon-credit-card', 'credit-card'], ['icon-currency-usd-dollar', 'currency-usd-dollar'], ['icon-money-dollar', 'money-dollar'], ['icon-money-finance', 'money-finance'], ['icon-savings', 'savings'], ['icon-currency-usd-dolar', 'currency-usd-dolar'], ['icon-currency-gbp-pound', 'currency-gbp-pound'], ['icon-currency-yen', 'currency-yen'], ['icon-currency-euro', 'currency-euro'], ['icon-calculator', 'calculator'], ['icon-abacus', 'abacus'], ['icon-shopping-cart-ecommerce', 'shopping-cart-ecommerce'], ['icon-barcode', 'barcode'], ['icon-shopping-bag', 'shopping-bag'], ['icon-gifts-giftbox', 'gifts-giftbox'], ['icon-delivery-shipping', 'delivery-shipping'], ['icon-open-sign', 'open-sign'], ['icon-badge-new', 'badge-new'], ['icon-badge-free', 'badge-free'], ['icon-badge-sale', 'badge-sale'], ['icon-sale-label', 'sale-label'], ['icon-plane', 'plane'], ['icon-luggage-travel-case', 'luggage-travel-case'], ['icon-weather-sun', 'weather-sun'], ['icon-weather-wind', 'weather-wind'], ['icon-weather-rain', 'weather-rain'], ['icon-weather-snow', 'weather-snow'], ['icon-weather-cloud', 'weather-cloud'], ['icon-weather-umbrella', 'weather-umbrella'], ['icon-coffee', 'coffee'], ['icon-tea-cup', 'tea-cup'], ['icon-coffee-paper-cup', 'coffee-paper-cup'], ['icon-food-chicken', 'food-chicken'], ['icon-food-chicken-leg', 'food-chicken-leg'], ['icon-food-sppon-fork-cafe', 'food-sppon-fork-cafe'], ['icon-drink-wine-glass-wineglass', 'drink-wine-glass-wineglass'], ['icon-soda-drink', 'soda-drink'], ['icon-food-apple', 'food-apple'], ['icon-food-cupcake', 'food-cupcake'], ['icon-food-hotdog', 'food-hotdog'], ['icon-drinks-beer', 'drinks-beer'], ['icon-icecream', 'icecream'], ['icon-scales', 'scales'], ['icon-bow', 'bow'], ['icon-crown', 'crown'], ['icon-lab-flask', 'lab-flask'], ['icon-atom', 'atom'], ['icon-radiation-fallout', 'radiation-fallout'], ['icon-graduation-cap', 'graduation-cap'], ['icon-dna', 'dna'], ['icon-siringe-medicine', 'siringe-medicine'], ['icon-microscope', 'microscope'], ['icon-dumbbell', 'dumbbell'], ['icon-competition', 'competition'], ['icon-football', 'football'], ['icon-soccer-football', 'soccer-football'], ['icon-bike-bicycle', 'bike-bicycle'], ['icon-ticket', 'ticket'], ['icon-trumpet', 'trumpet'], ['icon-boombox', 'boombox'], ['icon-yinyang-chinese', 'yinyang-chinese'], ['icon-heart-valentine-love', 'heart-valentine-love'], ['icon-horseshoe', 'horseshoe'], ['icon-ribbon', 'ribbon'], ['icon-flower', 'flower'], ['icon-tree', 'tree'], ['icon-palm-tree', 'palm-tree'], ['icon-fir-tree', 'fir-tree'], ['icon-baloon', 'baloon'], ['icon-rocket', 'rocket'], ['icon-sheriff-star', 'sheriff-star'], ['icon-pipe', 'pipe'], ['icon-sneaker', 'sneaker'], ['icon-diamond-ruby', 'diamond-ruby'], ['icon-glasses-mustache', 'glasses-mustache'], ['icon-skull', 'skull'], ['icon-alien', 'alien'], ['icon-face-woman', 'face-woman'], ['icon-face-boy', 'face-boy'], ['icon-face-man', 'face-man'], ['icon-face-robot', 'face-robot'], ['icon-social-facebook', 'social-facebook'], ['icon-social-google-gplus', 'social-google-gplus'], ['icon-social-twitter-tweet-bird', 'social-twitter-tweet-bird'], ['icon-social-foursquare', 'social-foursquare'], ['icon-social-instagram', 'social-instagram'], ['icon-social-pinterest', 'social-pinterest'], ['icon-social-delicious', 'social-delicious'], ['icon-social-dribbble', 'social-dribbble'], ['icon-social-behance', 'social-behance'], ['icon-social-github-octocat', 'social-github-octocat'], ['icon-social-linkedin', 'social-linkedin'], ['icon-social-vimeo', 'social-vimeo'], ['icon-social-skype', 'social-skype'], ['icon-platform-windows', 'platform-windows'], ['icon-platform-apple', 'platform-apple'], ['icon-platform-android', 'platform-android']]
            }),
            valueField: 'myId',
            displayField: 'displayText',
            triggerAction: 'all',
            listeners:{
                scope: this,
                'select': this.updateIcon
            }
        });
        this.add(this.comboField);

        this.labelField = new CQ.Ext.form.Label({
        	    style: "font-size: 50px;"
        });
        this.add(this.labelField);

    },
    
    updateIcon: function() {
        $(this.labelField.el.dom).attr('class', this.comboField.getValue() + ' icon-picker-label');
    },
    
    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        this.comboField.setValue(value);
        this.updateIcon();
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
    	return this.comboField.getValue();
    },
});

// register xtype
CQ.Ext.reg('vtkresourcesiconpicker', girlscouts.components.VTKResourcesIconPickerer);
