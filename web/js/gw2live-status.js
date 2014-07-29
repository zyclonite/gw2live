/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
function parseSize(size) {
    var suffix = ["bytes", "KB", "MB", "GB", "TB", "PB"],
            tier = 0;
    while (size >= 1024) {
        size = size / 1024;
        tier++;
    }
    return Math.round(size * 10) / 10 + " " + suffix[tier];
}

function getStatus() {
        $.getJSON("admin/jvm")
                .done(function(data) {
            $('#threadcount').text("Threadcount: " + data.threadCount + " threads");
            $('#heap > div.progress > div.bar').width(((data.usedHeap / data.commitedHeap) * 100) + "%");
            $('#heap > span').text("Heap " + parseSize(data.usedHeap) + "/" + parseSize(data.commitedHeap));
            $('#nonheap > div.progress > div.bar').width(((data.usedNonHeap / data.commitedNonHeap) * 100) + "%");
            $('#nonheap > span').text("NonHeap " + parseSize(data.usedNonHeap) + "/" + parseSize(data.commitedNonHeap));

            $('#pss > div.progress > div.bar').width(((data.memInfo['PS Survivor Space'].usage.used / data.memInfo['PS Survivor Space'].usage.committed) * 100) + "%");
            $('#pss > span').text("Survivor Space " + parseSize(data.memInfo['PS Survivor Space'].usage.used) + "/" + parseSize(data.memInfo['PS Survivor Space'].usage.committed));
            $('#pog > div.progress > div.bar').width(((data.memInfo['PS Old Gen'].usage.used / data.memInfo['PS Old Gen'].usage.committed) * 100) + "%");
            $('#pog > span').text("Old Gen " + parseSize(data.memInfo['PS Old Gen'].usage.used) + "/" + parseSize(data.memInfo['PS Old Gen'].usage.committed));
            $('#pes > div.progress > div.bar').width(((data.memInfo['PS Eden Space'].usage.used / data.memInfo['PS Eden Space'].usage.committed) * 100) + "%");
            $('#pes > span').text("Eden Space " + parseSize(data.memInfo['PS Eden Space'].usage.used) + "/" + parseSize(data.memInfo['PS Eden Space'].usage.committed));
            $('#cc > div.progress > div.bar').width(((data.memInfo['Code Cache'].usage.used / data.memInfo['Code Cache'].usage.committed) * 100) + "%");
            $('#cc > span').text("Code Cache " + parseSize(data.memInfo['Code Cache'].usage.used) + "/" + parseSize(data.memInfo['Code Cache'].usage.committed));
            $('#ccs > div.progress > div.bar').width(((data.memInfo['Compressed Class Space'].usage.used / data.memInfo['Compressed Class Space'].usage.committed) * 100) + "%");
            $('#ccs > span').text("Class Space " + parseSize(data.memInfo['Compressed Class Space'].usage.used) + "/" + parseSize(data.memInfo['Compressed Class Space'].usage.committed));
            $('#ms > div.progress > div.bar').width(((data.memInfo['Metaspace'].usage.used / data.memInfo['Metaspace'].usage.committed) * 100) + "%");
            $('#ms > span').text("Meta Space " + parseSize(data.memInfo['Metaspace'].usage.used) + "/" + parseSize(data.memInfo['Metaspace'].usage.committed));
        });
        $.getJSON("admin/pvecache")
                .done(function(data) {
            $('#pvec').text("PvE Cache: " + data.ownedEntryCount + " entries (Memory: " + parseSize(data.ownedEntryMemoryCost) + ")");
        });
        $.getJSON("admin/wvwcache")
                .done(function(data) {
            $('#wvwc').text("WvW Cache: " + data.ownedEntryCount + " entries (Memory: " + parseSize(data.ownedEntryMemoryCost) + ")");
        });
        $.getJSON("admin/channelcache")
                .done(function(data) {
            $('#chc').text("Channel Cache: " + data.ownedEntryCount + " entries (Memory: " + parseSize(data.ownedEntryMemoryCost) + ")");
        });
}

$(function() {
    setInterval(getStatus, 10000);
    getStatus();
});
