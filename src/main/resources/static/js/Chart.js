$(document).ready(function(){
$getJSON('http://localhost:8080/api/charts/default'function(json_data){
 // Get the context of the canvas element we want to select
    var ctx=document.getElementById("weeklyStepChart").getContext("2d");

  // Instantiate a new chart using 'data'
    var chart new Chart(ctx,data);
    });

    $ajax({
    type:'POST',
    url: 'http://localhost:8080/api/auth/oauth',
    data: '{"url"   :"'+document.URL+'"}',
    success:function(data) {console.log('data: '+data);},
    contentType:"application/json",
    dataType: 'json'
    });

});