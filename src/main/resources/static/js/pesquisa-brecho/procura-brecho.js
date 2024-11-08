var table = $('#brecho_tbl').DataTable();

$(document).ready(function() {
    table.DataTable({
        "ajax": {
            "url": "/api/v1/brecho",
            "dataSrc": ""
        },
        "columns": [
            { "data": "id" },
            { "data": "name" },
            { "data": "email" },
            { "data": "age" }
        ]
    });

});