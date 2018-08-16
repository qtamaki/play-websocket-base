$ ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "stateupdate"
        $("#cur-image").attr("src", "/assets/images/miku/miku_"+message.state+".jpg")
        console.log(message.state)
      else
        console.dir(message)

  $("#addsymbolform").submit (event) ->
    event.preventDefault()
    # send the message to watch the stock
    ws.send(JSON.stringify({state: $("#addsymboltext").val()}))
    # reset the form
    $("#addsymboltext").val("")

  $(".face-images").on "click", (e) ->
    ws.send(JSON.stringify({state: $(e.target).data("image-number")}))
