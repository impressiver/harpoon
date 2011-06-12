(function ($) {
    var options = {
        browsermob: {
            show: true
        }
    };

    function init(harlet) {
        // Hook callbacks
        var doDraw = function (data) {
            var instance = harlet.getInstance(),
                har = harlet.getHar(),
                tbody = $("tbody.harlet-net-body", harlet.getPlaceholder());

            if(har.log["_nameValuePairs"] && har.log["_nameValuePairs"].length) {
                for(var i = 0, dL = har.log["_nameValuePairs"].length; i < dL; ++i) {
                    var pair = har.log["_nameValuePairs"][i],
                        tr = $("<tr id=\"harlet-data-row-" + instance + "-tx-" + i + "\" class=\"data-row\">");

                    tr.append("<td>" + pair.name + ":</td>");
                    tr.append("<td colspan=\"4\">" + pair.value + "</td>");

                    tbody.prepend(tr);
                }
            }

            for(var i = 0, pL = data.pages.length; i < pL; ++i) {
                var page = data.pages[i];

                if(page["_nameValuePairs"] && page["_nameValuePairs"].length) {
                    for(var j = 0, dL = page["_nameValuePairs"].length; j < dL; ++j) {
                        var pair = page["_nameValuePairs"][j],
                            tr = $("<tr id=\"harlet-data-row-" + instance + "-" + i + "-" + j + "\" class=\"data-row\">");

                        tr.append("<td>" + pair.name + ":</td>");
                        tr.append("<td colspan=\"4\">" + pair.value + "</td>");

                        $("#harlet-page-row-" + instance + "-" + i, tbody).after(tr);
                    }
                }
            }
        };

        // Register the hooks
        harlet.hook("draw", doDraw);
    }

    $.harlet.registerPlugin({
        init: init,
        options: options,
        name: 'browsermob',
        version: '1.0'
    });
})(jQuery);
