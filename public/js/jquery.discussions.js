//load data
$(document).ready(function(){
    $.get("/discussion/88bb9936-3e58-432c-bba9-3ed5aeb7d7cb", function(data){

        var discussion_title = $("#discussion_title");

        //add title
        var titles = data.title.split(".");
        for (i = 0; i < titles.length; i++){
            discussion_title.append("<h2>" + titles[i] + "</h2>");
        }

    //add date
            discussion_title.append("<hr>")
            .append("<p>" + data.date + "</p>");

    //add comments
            var row
            for(i = 0; i < data.comments.length; i++){
                if (i % 3 == 0){
                    row = $("<div class=\"row\"></div>").appendTo("#comments")
                }

                var col = $("<div class=\"col-md-4\"></div>").appendTo(row)
                $("<h4>" + data.comments[i].userName + "</h4>").appendTo(col)
                $("<p class=\"time\">" + data.comments[i].time + "</p>").appendTo(col)
                $("<p>" + data.comments[i].message + "</p>").appendTo(col)
            }
         });
});

//post a comment
$(document).ready(function(){
    $("#form_opinion").submit(function(event){
        /*Stop the usual form submission event*/
        event.preventDefault();

        /* post method */
        $.ajax({
            url: "/opinion/add",
            type: "POST",
            dataType: "xml/html/script/json",
            contentType: "application/json",
            data: JSON.stringify({
                uuid: "88bb9936-3e58-432c-bba9-3ed5aeb7d7cb",
                name: $("#name").val(),
                comment: $("#comment").val()
            }),
            success: location.reload() // does a page refresh
        });
    });
});
