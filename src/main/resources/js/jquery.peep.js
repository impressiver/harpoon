// Show the full contents of an overflowed div on rollover
(function($) {
    var counter = 0;

    $.fn.extend({
        peep: function (selector, options) {
            var options = options;
            if(typeof selector == "object") {
                options = selector;
                selector = null;
            }

            options = $.extend(true, {
                attr: null,
                css: {
                    "background-color": "#fff",
                    padding: "3px"
                },
                offset: [-3, -3],
                onclick: null,
                selector: null
            }, options);

            function doMouseenter() {
                $("div.peep").remove();

                var containerWidth = $(this).parent().width(),
                    width = $(this).width();

                if(containerWidth && (width <= containerWidth)) {
                    return;
                }

                var content;
                if(options.attr) {
                    content = $(this).attr(options.attr);
                } else {
                    content = $(this).clone(true);
                    content.unbind("mouseenter", doMouseenter);
                }

                var div = $("<div class=\"peep\">");
                div.html(content);
                div.css($.extend({
                    position: "absolute",
                    left: $(this).offset().left + options.offset[0],
                    top: $(this).offset().top + options.offset[1]
                }, options.css));

                $("body").append(div);
            }

            function doMouseleave() {
                $("div.peep").remove();
            }

            return this.each(function() {
                if(selector) {
                    $(this).delegate(selector, "mouseenter", doMouseenter);
                } else {
                    $(this).mouseenter(doMouseenter);
                }

                $("div.peep").live("mouseleave", doMouseleave);
                if(options.onclick) {
                    $("div.peep").live("click", function () {
                        options.onclick.apply(this);
                    });
                }
            });
        }
    });
})(jQuery);
