// Center an element on the screen
(function($) {
    $.fn.extend({
        center: function (options) {
            var options = $.extend({
                window: false,      // Center to viewport
                horizontal: true,
                vertical: true,
                minX: 0,
                minY: 0,
                transition: 0
            }, options);

            return this.each(function() {
                var parent = options.window ? $(window) : $(this).offsetParent(),
                    top, left, disp;

                $(this).css({position: 'absolute'});

                top = (parent.height() - $(this).outerHeight()) / 2;
                left = (parent.width() - $(this).outerWidth()) / 2;

                if(options.window) {
                    top +=  parent.scrollTop();
                    left += parent.scrollLeft();
                }

                var pos = {};
                if(options.horizontal) pos.left = (((options.minX !== false) && (left > options.minX)) ? left : options.minX);
                if(options.vertical) pos.top = (((options.minY !== false) && (top > options.minY)) ? top : options.minY);

                if(options.transition) {
                    $(this).animate(pos, options.transition);
                } else {
                    $(this).css(pos);
                }

                return $(this);
            });
        }
    });
})(jQuery);
