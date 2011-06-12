/*!
 * Harlet jQuery Extension v0.6
 *
 * Copyright 2011, Ian White
 *
 * Date: Fri June 10 10:32:26 2011 -0700
 */

(function($) {
    function Harlet(placeholder, inputData, options, plugins) {
        var options = $.extend({
            timeSliceInterval: 1000,
            autoInit: true
        }, options);

        // local vars
        var harlet = this,
            har = null,
            hooks = {
                init: [],
                render: [],
                parse: [],
                draw: [],
                tooltip: []
            },
            guid = 0,
            instance = ++Harlet.instances,
            initialized = false;

        // public attributes

        // public functions
        harlet.getInstance = function() { return instance; };
        harlet.getPlaceholder = function() { return placeholder; };
        harlet.getHar = function() { return har; };
        harlet.init = init;
        harlet.load = load;
        harlet.setHar = setHar;
        harlet.draw = draw;
        harlet.hook = hook;
        harlet.unhook = unhook;

        // initialize
        initPlugins();
        if(options.autoInit) {
            init();
        }

        // functions
        function initPlugins() {
            for (var i = 0, l = plugins.length; i < l; ++i) {
                var p = plugins[i];
                p.init(harlet);
                if (p.options)
                    $.extend(true, options, p.options);
            }
        }

        function init() {
            if(initialized) return;
            initialized = true;

            render();
            if(typeof(inputData) == "string") {
                load(inputData);
            } else if(typeof(inputData) == "object") {
                setHar(inputData);
            }

            $.each(hooks.init, function (i, callback) {
                callback.call(harlet);
            });
        }

        function hook(type, callback) {
            if(hooks[type] === undefined) return;

            if (!callback.guid) {
                callback.guid = guid++;
            }

            hooks[type].push(callback);
        }

        function unhook(type, callback) {
            for(var i = 0, l = hooks[type].length; i < l; ++i) {
                var handler = hooks[type][i];
                if(callback.guid && (callback.guid == handler.guid)) {
                    hooks[type].splice(i, 1);
                    return;
                }
            }
        }

        function load(url, callback) {
            jQuery.get(url, null, function (data, textStatus, XMLHttpRequest) {
                setHar(data);
                if(callback) {
                    callback.apply(this);
                }
            }, "json");
        }

        function setHar(json) {
            har = json;
            draw();
        }

        function parseData() {
            var harr = $.extend(true, {}, har),
                data = {
                    pages: [],
                    fileCount: 0, totalSize: 0, cachedSize: 0, totalTime: 0
                },
                sliceInterval = options.timeSliceInterval || 1000;

            for(var i = 0; i < harr.log.pages.length; ++i) {
                var page = harr.log.pages[i];

                page.slices = [];
                page.startedTime = parseISO8601(page.startedDateTime),
                page.onLoadTime = (page.pageTimings && (page.pageTimings.onLoad != -1)) ? page.pageTimings.onLoad : 0;

                data.pages.push(page);
            }

            var entriesByPage = {};
            if(har.log.entries && har.log.entries.length) {
                // sort entries by startedDateTime (oldest first)
                harr.log.entries.sort(function(a, b) {
                    return parseISO8601(a.startedDateTime) - parseISO8601(b.startedDateTime);
                });

                // populate page entries
                for(var i = 0; i < harr.log.entries.length; ++i) {
                    var entry = harr.log.entries[i];

                    var pageref = entry.pageref || "none";
                    if(!entriesByPage[entry.pageref]) entriesByPage[entry.pageref] = [];

                    entriesByPage[entry.pageref].push(entry);
                }

                if(entriesByPage["none"])
                    data.pages.unshift({id: null, slices: []});
            }

            // split entries into slices
            for(var i = 0; i < data.pages.length; ++i) {
                var page = data.pages[i],
                pageEntries = entriesByPage[page.id] || [],
                slice = null;

                for(var j = 0; j < pageEntries.length; ++j) {
                    var entry = pageEntries[j];

                    var startedTime = parseISO8601(entry.startedDateTime);
                    var sliceLastStartTime = slice ? parseISO8601(slice.entries[slice.entries.length - 1].startedDateTime) : 0;

                    if (!slice || ((startedTime - sliceLastStartTime) >= sliceInterval)
                            && (startedTime > (page.startedTime + page.onLoadTime))) {
                        slice = {entries: []};
                        page.slices.push(slice);
                    }

                    if (slice.startedTime == undefined || slice.startedTime > startedTime)
                        slice.startedTime = startedTime;

                    // entry.time represents total elapsed time of the request.
                    if (slice.endTime == undefined || slice.endTime < startedTime + entry.time)
                        slice.endTime = startedTime + entry.time;

                    if (slice == page.slices[0] && slice.endTime < page.startedTime + page.onLoadTime)
                        slice.endTime = page.startedTime + page.onLoadTime;

                    slice.entries.push(entry);
                }
            }

            // now go back over and setup all the page/slice/entry timing
            // pages
            for(var i = 0, pL = data.pages.length; i < pL; ++i) {
                var page = data.pages[i];

                page.fileCount = page.totalSize = page.cachedSize = page.totalTime = 0;

                // slices
                for(var j = 0, sL = page.slices.length; j < sL; ++j) {
                    var slice = page.slices[j],
                        minTime = 0, maxTime = 0;

                    slice.fileCount = slice.totalSize = slice.cachedSize = slice.totalTime = 0;
                    slice.elapsedTime = slice.endTime - slice.startedTime;

                    // onContentLoad (e.g. DOMContentLoad for Firefox)
                    var onContentLoad = (page.pageTimings && page.pageTimings.onContentLoad != -1) ? page.pageTimings.onContentLoad : 0;
                    if (j == 0 && onContentLoad > 0)
                        slice.contentLoadBarOffset = Math.floor(
                            ((page.startedTime + onContentLoad - slice.startedTime) / slice.elapsedTime) * 100);

                    // onLoad (e.g. onLoad for Firefox)
                    var onLoad = page.pageTimings.onLoad;
                    if (j == 0 && onLoad > 0)
                        slice.windowLoadBarOffset = Math.floor(
                            ((page.startedTime + onLoad - slice.startedTime) / slice.elapsedTime) * 100);

                    // entries
                    for(var k = 0, eL = slice.entries.length; k < eL; ++k) {
                        var entry = slice.entries[k];

                        // Individual phases of a request:
                        //
                        // 1) Blocking          HTTP-ON-MODIFY-REQUEST -> (STATUS_RESOLVING || STATUS_CONNECTING_TO)
                        // 2) DNS               STATUS_RESOLVING -> STATUS_CONNECTING_TO
                        // 3) Connecting        STATUS_CONNECTING_TO -> (STATUS_CONNECTED_TO || STATUS_SENDING_TO)
                        // 4) Sending           STATUS_SENDING_TO -> STATUS_WAITING_FOR
                        // 5) Waiting           STATUS_WAITING_FOR -> STATUS_RECEIVING_FROM
                        // 6) Receiving         STATUS_RECEIVING_FROM -> ACTIVITY_SUBTYPE_RESPONSE_COMPLETE
                        //
                        // Note that HTTP-ON-EXAMINE-RESPONSE should not be used since the time isn't passed
                        // along with this event and so, it could break the timing. Only the HTTP-ON-MODIFY-REQUEST
                        // is used to get begining of the request and compute the blocking time. Hopefully this
                        // will work or there is better mechanism.
                        //
                        // If the response comes directly from the browser cache, there is only one state.
                        // HTTP-ON-MODIFY-REQUEST -> HTTP-ON-EXAMINE-CACHED-RESPONSE

                        // Compute end of each phase since the request start.
                        if (!entry.timings) {
                            var resolving = 0;
                            var connecting = 0;
                            var blocking = 0;
                            var sending = 0;
                            var waiting = 0;
                            var receiving = 0;
                        } else {
                            var resolving = ((!entry.timings.dns || entry.timings.dns < 0) ? 0 : entry.timings.dns);
                            var connecting = resolving + ((!entry.timings.connect || entry.timings.connect < 0) ? 0 : entry.timings.connect);
                            var blocking = connecting + ((!entry.timings.blocked || entry.timings.blocked < 0) ? 0 : entry.timings.blocked);
                            var sending = blocking + ((!entry.timings.send || entry.timings.send < 0) ? 0 : entry.timings.send);
                            var waiting = sending + ((!entry.timings.wait || entry.timings.wait < 0) ? 0 : entry.timings.wait);
                            var receiving = waiting + ((!entry.timings.receive || entry.timings.receive < 0) ? 0 : entry.timings.receive);
                        }

                        var startedTime = parseISO8601(entry.startedDateTime);

                        entry.barOffset = Math.floor(((startedTime - slice.startedTime) / slice.elapsedTime) * 100);

                        // Compute size of each bar. Left side of each bar starts at the
                        // beginning. The first bar is on top of all and the last one is
                        // at the bottom (z-index).
                        entry.barResolving = ((resolving / slice.elapsedTime) * 100);
                        entry.barConnecting = ((connecting / slice.elapsedTime) * 100);
                        entry.barBlocking = ((blocking / slice.elapsedTime) * 100);
                        entry.barSending = ((sending / slice.elapsedTime) * 100);
                        entry.barWaiting = ((waiting / slice.elapsedTime) * 100);
                        entry.barReceiving = ((receiving / slice.elapsedTime) * 100);

                        // Slice stats
                        ++slice.fileCount;
                        slice.totalSize += entry.response.content.size;

                        if (entry.response.status == 304)
                            slice.cachedSize += entry.response.content.size;

                        if (!minTime || startedTime < minTime)
                            minTime = startedTime;

                        var entryEndTime = startedTime + entry.time;
                        if (entryEndTime > maxTime)
                            maxTime = entryEndTime;
                    }

                    slice.totalTime = maxTime - minTime;

                    page.fileCount += slice.fileCount;
                    page.totalSize += slice.totalSize;
                    page.cachedSize += slice.cachedSize;
                    page.totalTime += slice.totalTime;
                }

                data.fileCount += page.fileCount;
                data.totalSize += page.totalSize;
                data.cachedSize += page.cachedSize;
                data.totalTime += page.totalTime;
            }

            $.each(hooks["parse"], function (i, callback) {
                callback.call(harlet, data);
            });

            return data;
        }

        function render() {
            // Prepare the container
            placeholder.hide();
            placeholder.html("");

            var table = $("<table id=\"harlet-net-" + instance + "\" class=\"harlet-net\" width=\"100%\">");
            table.append("<col width=\"30%\">");
            table.append("<col width=\"10%\">");
            table.append("<col width=\"10%\">");
            table.append("<col width=\"10%\">");
            table.append("<col width=\"40%\">");

            var thead = $("<thead></thead>");
            var tr = $("<tr></tr>");
            tr.append("<th>Filename</th>");
            tr.append("<th>Status</th>");
            tr.append("<th>Host</th>");
            tr.append("<th>Bytes</th>");
            tr.append("<th></th>");
            thead.append(tr);
            table.append(thead);

            var tbody = $("<tbody id=\"harlet-net-body-" + instance + "\" class=\"harlet-net-body\"></tbody>");
            table.append(tbody);

            placeholder.append(table);
            $(table).peep("tr.entry-row td.request a", {offset: [-2, -3], css: {padding: "2px"}});

            $.each(hooks["render"], function (i, callback) {
                callback.call(harlet);
            });
        }

        function draw() {
            var data = parseData();

            // Prepare the container
            placeholder.hide();

            var tbody = $("tbody.harlet-net-body", placeholder);
            tbody.html("");

            // Render pages
            for(var i = 0; i < data.pages.length; i++) {
                var page = data.pages[i];

                // Page header
                var tr = $("<tr id=\"harlet-page-row-" + instance + "-" + i + "\" class=\"page-row\"></tr>");
                tr.append("<td>" + page.title + "</td>");
                tr.append("<td colspan=\"2\">" + page.fileCount + " requests</td>");
                tr.append("<td class=\"bytes-header\">" + formatBytes(page.totalSize) + "</td>");
                tr.append("<td class=\"response-time-header\">" + formatResponseTime(page.totalTime, 2) + "</td>");
                tbody.append(tr);

                // Page entries
                for(var j = 0; j < page.slices.length; ++j) {
                    var slice = page.slices[j];

                    for(var k = 0; k < slice.entries.length; ++k) {
                        var entry = slice.entries[k];

                        var tr = $("<tr id=\"harlet-entry-row-" + instance + "-" + i + "-" + j + "-" + k + "\" class=\"entry-row\"></tr>");
                        var td = $("<td class=\"request\"></td>");
                        var a = $("<a class=\"link\">" + entry.request.method + " " + entry.request.url + "</a>");
                        td.append(a);
                        tr.append(td);

                        tr.append("<td class=\"status-code" + ((entry.response.status >= 400 || entry.response.status < 200) ? " error-code" : "") + "\">" + entry.response.status + " " + entry.response.statusText + "</td>");
                        tr.append("<td class=\"host\">" + getPrettyDomain(entry.request.url) + "</td>");
                        tr.append("<td class=\"bytes\">" + formatBytes(entry.response.bodySize) + "</td>");

                        // Net bar
                        var chartTd = $("<td class=\"chart\"></td>");
                        var div = $("<div class=\"net-bar clearfix\"></div>");
                        /* div.append("<div class=\"net-total-bar\"style=\"left:0;width:" + (dnsLen + ttfbLen + ttlbLen) + "%\"></div>"); */
                        div.append("<div class=\"net-resolving-bar\"style=\"left:" + entry.barOffset + "%;width:" + (entry.barResolving || 0) + "%\"></div>");
                        div.append("<div class=\"net-connecting-bar\"style=\"left:" + entry.barOffset + "%;width:" + (entry.barConnecting || 0) + "%\"></div>");
                        div.append("<div class=\"net-blocking-bar\"style=\"left:" + entry.barOffset + "%;width:" + (entry.barBlocking || 0) + "%\"></div>");
                        div.append("<div class=\"net-sending-bar\"style=\"left:" + entry.barOffset + "%;width:" + (entry.barSending || 0) + "%\"></div>");
                        div.append("<div class=\"net-waiting-bar\"style=\"left:" + entry.barOffset + "%;width:" + (entry.barWaiting || 0) + "%\"></div>");
                        div.append("<div class=\"net-receiving-bar\"style=\"left:" + entry.barOffset + "%;width:" + (entry.barReceiving || 0) + "%\"></div>");
                        div.append("<div class=\"net-time\"style=\"left:" + (entry.barOffset + (entry.barReceiving || 0)) + "%\">" + formatResponseTime(entry.time) + "</div>");

                        div.mouseenter(function(entry) {
                            return function(e) {
                                e = $.event.fix(e);
                                showTooltip(e.pageX, e.pageY, entry);
                            };
                        }(entry));

                        //div.mousemove(doMoveTooltip);
                        div.mouseleave(doHideTooltip);

                        chartTd.append(div);
                        tr.append(chartTd);
                        tbody.append(tr);
                    }
                }
            }

            $.each(hooks["draw"], function (i, callback) {
                callback.call(harlet, data);
            });

            placeholder.show();
        }

        function showTooltip (x, y, entry) {
            doHideTooltip();

            var content = "";
            if(entry.serverIPAddress) content += "<div><strong>IP Address: </strong> " + entry.serverIPAddress + "</div>";
            if(entry.timings.blocked != null && entry.timings.blocked > 0) content += "<div><strong>Blocking: </strong> " + formatResponseTime(entry.timings.blocked) + "</div>";
            if(entry.timings.dns != null && entry.timings.dns > 0) content += "<div><strong>DNS Lookup: </strong> " + formatResponseTime(entry.timings.dns) + "</div>";
            if(entry.timings.send != null && entry.timings.send > 0) content += "<div><strong>Sending: </strong> " + formatResponseTime(entry.timings.send) + "</div>";
            if(entry.timings.connect != null && entry.timings.connect > 0) content += "<div><strong>Connecting: </strong> " + formatResponseTime(entry.timings.connect) + "</div>";
            if(entry.timings.ssl != null && entry.timings.ssl > 0) content += "<div><strong>&nbsp;&nbsp;-&nbsp;SSL Handshaking: </strong> " + formatResponseTime(entry.timings.ssl) + "</div>";
            content += "<div><strong>Waiting: </strong> " + formatResponseTime(entry.timings.wait) + "</div>";
            content += "<div><strong>Receiving: </strong> " + formatResponseTime(entry.timings.receive) + "</div>";

            $.each(hooks["tooltip"], function (i, callback) {
                content += callback.call(harlet, x, y, entry);
            });

            $('<div id="harlet-tooltip" class="tooltip"></div>').append(content).css({
                position: 'absolute',
                top: y + 5,
                left: x + 5
            }).appendTo("body").fadeIn(200);
        }

        function doHideTooltip() {
            $("#harlet-tooltip").remove();
        }

        function doMoveTooltip(e) {
            e = $.event.fix(e);

            $("#harlet-tooltip").css({
                top: e.pageX + 5,
                left: e.pageY + 5
            });
        }

        // Helpers
        function formatBytes(value) {
            if (value < 1024) {
                return value  + " B";
            }

            value = value / 1024;
            if (value < 1024) {
                if (value < 10) {
                    return formatNumber(value, 2) + " KB";
                } else {
                    return formatNumber(value, 1) + " KB";
                }
            }

            value = value / 1024;

            if (value < 1024) {
                if (value < 10) {
                    return formatNumber(value, 2) + " MB";
                } else {
                    return formatNumber(value, 1) + " MB";
                }
            }

            value = value / 1024;

            if (value < 10) {
                return formatNumber(value, 2) + " GB";
            } else {
                return formatNumber(value, 1) + " GB";
            }
        }

        function formatResponseTime(value, round) {
            var round = (round === true);

            if (value < 1000) {
                return value  + " ms";
            }

            value = value / 1000;
            if (value < 10) {
                return formatNumber(value, round ? 0 : 2) + " secs";
            }

            if (value < 60) {
                return formatNumber(value, round ? 0 : 1) + " secs";
            }

            value = value / 60;
            if(value < 60) {
                return formatNumber(value, round ? 0 : 1) + " min";
            }

            value = value / 60;
            if(value < 24) {
                return formatNumber(value, round ? 0 : 1) + " hrs";
            }

            value = value / 24;
            return formatNumber(value, round ? 0 : 1) + " days";
        }

        function formatNumber(value, decimalPlaces) {
            value = new Number(value);
            value = decimalPlaces ? value.toFixed(decimalPlaces) : Math.round(value);

            return addCommas(new String(value));
        }

        function addCommas(nStr) {
            nStr += '';
            var x = nStr.split('.');
            var x1 = x[0];
            var x2 = x.length > 1 ? '.' + x[1] : '';
            var rgx = /(\d+)(\d{3})/;
            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1' + ',' + '$2');
            }
            return x1 + x2;
        }

        function getPrettyDomain(url){
            var m=/[^:]+:\/{1,3}(www\.)?([^\/]+)/.exec(url);
            return m?m[2]:"";
        }

        function parseISO8601(text) {
            // Date time pattern: YYYY-MM-DDThh:mm:ss.sTZD
            // eg 1997-07-16T19:20:30.451+01:00
            // http://www.w3.org/TR/NOTE-datetime
            var regex = /(\d\d\d\d)(-)?(\d\d)(-)?(\d\d)(T)?(\d\d)(:)?(\d\d)(:)?(\d\d)(\.\d+)?(Z|([+-])(\d\d)(:)?(\d\d))/;
            var reg = new RegExp(regex);
            var m = text.toString().match(new RegExp(regex));
            if (!m)
                return null;

            var date = new Date();
            date.setUTCDate(1);
            date.setUTCFullYear(parseInt(m[1], 10));
            date.setUTCMonth(parseInt(m[3], 10) - 1);
            date.setUTCDate(parseInt(m[5], 10));
            date.setUTCHours(parseInt(m[7], 10));
            date.setUTCMinutes(parseInt(m[9], 10));
            date.setUTCSeconds(parseInt(m[11], 10));

            if (m[12])
                date.setUTCMilliseconds(parseFloat(m[12]) * 1000);
            else
                date.setUTCMilliseconds(0);

            if (m[13] != 'Z') {
                var offset = (m[15] * 60) + parseInt(m[17], 10);
                offset *= ((m[14] == '-') ? -1 : 1);
                date.setTime(date.getTime() - offset * 60 * 1000);
            }

            return date.getTime();
        }
    }
    Harlet.instances = 0;

    $.harlet = function(placeholder, har, options) {
        return new Harlet($(placeholder), har, options, $.harlet.plugins);
    };

    $.harlet.plugins = [];

    $.harlet.registerPlugin = function (plugin) {
        this.plugins.push(plugin);
    };
})(jQuery);
