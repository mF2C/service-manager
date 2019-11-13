window.onload=function(){
	var services = getServices()['services'];
	var catalog = document.getElementById('catalog');
	var r=0;
	var width = $(window).width();
	var comlumns = 4;
	var columnSize = "l3"
	if(width<1080){
		comlumns = 2;
		columnSize = "m6"
	}
	if(width<600){
		comlumns = 1;
		columnSize = "s12"
	}
	var rows = Math.floor(services.length/comlumns);
	var remainder = services.length % comlumns;
	var counter = 0;
	while(r<rows){
		var c=0;
		createRow(catalog, r);
		while(c<comlumns){
			var service = services[counter];
			var id = r+""+c;
			createColumn(catalog, id, r, service, columnSize);
			c++;
			counter++;
		}
		r++;
	}
	if(remainder>0){
		var c=0;
		createRow(catalog, r);
		while(c<remainder){
			var service = services[counter];
			var id = r+""+c;
			createColumn(catalog, id, r, service, columnSize);
			c++;
			counter++;
		}
	}
    var ele = document.createElement("button");
	ele.setAttribute("class","button button-register");
	ele.setAttribute("onClick","location.href='registration.html'");
	ele.innerHTML="New service";
	catalog.appendChild(ele);
};

function getServices()
{
	try
	{
		var message = null;
		$.ajax
		({
			url:   "api",
			type:  "GET",
			async: false,
			success: function(ans)
			{
				message = ans;
			}
		});
		return message;
	}
	catch (e){return 0;}
}

function launchService(service)
{
    var serviceInstanceJson = JSON.stringify({
        service_id: service['id']
    });
	try {
		var response = null;
		$.ajax
		({
			data: serviceInstanceJson,
			url:   "api/gui/service-instance",
			type:  "POST",
			contentType: "application/json",
			async: false,
			success: function(ans) { showResponseFromLM(ans); },
			error: function (xhr, ajaxOptions, thrownError) {
                 alert(xhr.responseText);
           }
		});
	}
	catch (e){return 0;}
}

function showResponseFromLM(response){
    if (response != null){
        if (response['status'] == 200){
            alert(response['message']);
            window.location.href = "index.html";
        } else{
            alert(response['message']);
        }
    }
}

function deleteService(serviceId){
    try {
    		var response = null;
    		$.ajax
    		({
    			url:   "api/gui/" + serviceId,
    			type:  "DELETE",
    			async: false,
    			success: function(ans)
    			{
    				response = ans;
    			}
    		});
    		if (response != null){
                if(response['status'] == 200) {
                    window.location.href = "index.html";
                } else {
                    alert("Error: " + response['message']);
                }
    		}
    	}
    	catch (e){return 0;}
}


function createRow(catalog, id){

	var ele = document.createElement("div");
	ele.setAttribute("id","row"+id);
	ele.setAttribute("class","w3-row w3-container");
	catalog.appendChild(ele);
}

function createColumn(catalog, id, r, service, columnSize){

	var col = document.createElement("div");
	col.setAttribute("id","col"+id);
	col.setAttribute("class","w3-col w3-center "+ columnSize);
	document.getElementById("row"+r).appendChild(col);

	var container = document.createElement("div");
	container.setAttribute("id","container"+id);
	container.setAttribute("class","w3-container");
	container.setAttribute("style","width:100%");
	document.getElementById("col"+id).appendChild(container);

	var card = document.createElement("div");
	card.setAttribute("id","card"+id);
	card.setAttribute("class","w3-card-4 w3-white");
	document.getElementById("container"+id).appendChild(card);

	var header = document.createElement("header");
	header.setAttribute("id","header"+id);
	header.setAttribute("class","w3-container w3-container-header w3-blue");
	document.getElementById("card"+id).appendChild(header);

	var title = document.createElement("h2");
	title.setAttribute("id","title"+id);
	var titleString;
	var maxLength = 15;
	if(service['name'].length > maxLength){
		titleString = service['name'].substring(0, maxLength) + " ...";
	}
	else{
		titleString = service['name'];
	}
	title.innerHTML = titleString;
	document.getElementById("header"+id).appendChild(title);

	var content = document.createElement("div");
	content.setAttribute("id","content"+id);
	content.setAttribute("class","w3-container w3-center w3-container-fixed");
	document.getElementById("card"+id).appendChild(content);

	var section = document.createElement("div");
	section.setAttribute("id","section"+id);
	section.setAttribute("class","w3-section w3-padding-8");
	document.getElementById("content"+id).appendChild(section);

	var description = document.createElement("h6");
	description.setAttribute("id","description-"+id);
	description.innerHTML= service['description'];
	document.getElementById("content"+id).appendChild(description);

    var type = document.createElement("h6");
    type.setAttribute("id","exec-"+id);
    var execString;
    if(service['exec'].length > maxLength){
    	execString = service['exec'].substring(0, maxLength) + " ...";
    }
    else{
    	execString = service['exec'];
    }
    type.innerHTML= "exec: " + execString;
    document.getElementById("content"+id).appendChild(type);

    var type = document.createElement("h6");
	type.setAttribute("id","exec_type-"+id);
	type.innerHTML= "exec_type: " + service['exec_type'];
	document.getElementById("content"+id).appendChild(type);

    var type = document.createElement("h6");
    type.setAttribute("id","agent_type-"+id);
    type.innerHTML= "agent_type: " + service['agent_type'];
    document.getElementById("content"+id).appendChild(type);

	var launchButton = document.createElement("button");
	launchButton.setAttribute("id","button_launch"+id);
	launchButton.setAttribute("class","button button-launch");
	launchButton.onclick = function() {launchService(service);}
	launchButton.innerHTML="Launch";
	document.getElementById("card"+id).appendChild(launchButton);

	var deleteButton = document.createElement("button");
	deleteButton.setAttribute("id","button_delete"+id);
	deleteButton.setAttribute("class","button button-delete");
	deleteButton.onclick = function() {deleteService(service['id']);}
	deleteButton.innerHTML="Delete";
	document.getElementById("card"+id).appendChild(deleteButton);
}

