(function ($) {
    var options = {
        harpoon: {
            show: true
        }
    };

    function doGetRequest() {
        var request = $(this).hasClass("entry-row") ? $("td.request", this) : $(this);
        var str = request.text();
        if (str && str.indexOf(" ") != -1) {
            var uri = str.substring(str.indexOf(" ") + 1);
            getRequestHistory(uri);
        }
    }

    function init(harlet) {
        // Hook callbacks
        var doDraw = function (data) {
            var instance = harlet.getInstance(),
                har = harlet.getHar(),
                tbody = $("tbody.harlet-net-body", harlet.getPlaceholder());

            $("tr.entry-row, tr.entry-row td.request a.link", tbody).click(doGetRequest);
        };

        // Register the hooks
        harlet.hook("draw", doDraw);
    }

    $.harlet.registerPlugin({
        init: init,
        options: options,
        name: 'harpoon',
        version: '1.0'
    });
})(jQuery);
